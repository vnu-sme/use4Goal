package org.vnu.sme.goal.transform;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.vnu.sme.goal.maxbpmn.mm.*;
import org.vnu.sme.goal.maxgoal.mm.*;

/**
 * Transforms a MAXGoalModel into a BpmnProcess.
 *
 * Mapping rules:
 *  - Actor           → Lane
 *  - Leaf goal/task  → BpmnTask (in owner's lane)
 *  - SEQ [a,b,c]     → sequence flows a→b→c
 *  - PAR [a,b,c]     → PAR fork → {a,b,c} → PAR join
 *  - XOR [(g,a),...] → XOR fork (with guard labels) → branches → XOR join
 *  - IOR [(g,a),...] → IOR fork → branches → IOR join
 *  - ITER [a] until c → loop gateway → body → back-edge (with until label)
 *  - depend A → B    → MessageFlow from A's BPMN node to B's BPMN node
 */
public final class GoalToBpmnTransformer {

    private final MAXGoalModel      model;
    private final BpmnProcess       process;
    private final AtomicInteger     counter  = new AtomicInteger(0);
    private final Map<String,String> nodeMap = new HashMap<>();  // intentional id → bpmn node id

    private GoalToBpmnTransformer(MAXGoalModel model) {
        this.model   = model;
        this.process = new BpmnProcess(model.getName());
    }

    public static BpmnProcess transform(MAXGoalModel model) {
        return new GoalToBpmnTransformer(model).run();
    }

    private BpmnProcess run() {
        // 1. Create lanes
        for (Actor a : model.getActors()) {
            process.addLane(new Lane(laneId(a.name()), a.name(), a.kind().name().toLowerCase()));
        }

        // 2. Find root intentionals per actor (not referenced as child of any refine)
        Set<String> allChildren = collectAllChildren();

        for (Actor a : model.getActors()) {
            String lane = laneId(a.name());
            List<Intentional> roots = a.intentionals().stream()
                    .filter(i -> !(i instanceof ResourceDef))
                    .filter(i -> !allChildren.contains(i.name()))
                    .toList();

            if (roots.isEmpty()) continue;

            String startId = uid("start_" + a.name());
            String endId   = uid("end_"   + a.name());
            process.addNode(new StartEvent(startId, lane));
            process.addNode(new EndEvent(endId, lane));

            if (roots.size() == 1) {
                Fragment f = transform(roots.get(0).name(), lane);
                addFlow(startId, f.entry(), null);
                addFlow(f.exit(), endId, null);
            } else {
                // implicit PAR for multiple roots
                String forkId = uid("gw_par_fork_" + a.name());
                String joinId = uid("gw_par_join_" + a.name());
                process.addNode(new Gateway(forkId, GatewayType.PAR, GatewayRole.FORK, lane));
                process.addNode(new Gateway(joinId, GatewayType.PAR, GatewayRole.JOIN, lane));
                addFlow(startId, forkId, null);
                addFlow(joinId, endId, null);
                for (Intentional root : roots) {
                    Fragment f = transform(root.name(), lane);
                    addFlow(forkId, f.entry(), null);
                    addFlow(f.exit(), joinId, null);
                }
            }
        }

        // 3. Message flows from depend edges
        for (Dependency d : model.getDependencies()) {
            String srcBpmn = nodeMap.get(d.from());
            String tgtBpmn = nodeMap.get(d.to());
            if (srcBpmn != null && tgtBpmn != null) {
                process.addMessage(new MessageFlow(srcBpmn, tgtBpmn));
            }
        }

        return process;
    }

    // ── Core recursive transformation ───────────────────────────────────────

    private record Fragment(String entry, String exit) {}

    private Fragment transform(String intentionalId, String defaultLane) {
        Optional<Intentional> opt = model.find(intentionalId);
        if (opt.isEmpty()) {
            String phantom = uid("phantom_" + intentionalId);
            process.addNode(new BpmnTask(phantom, intentionalId + "?", defaultLane));
            nodeMap.put(intentionalId, phantom);
            return new Fragment(phantom, phantom);
        }
        Intentional item = opt.get();
        String ownerLane = model.actorOf(intentionalId)
                .map(a -> laneId(a.name())).orElse(defaultLane);

        return switch (item) {
            case GoalDef g when g.refine() != null ->
                    transformRefine(g.refine(), ownerLane);
            case GoalDef g -> {
                String id = uid("gt_" + g.name());
                process.addNode(new BpmnTask(id, g.name(), ownerLane));
                nodeMap.put(g.name(), id);
                yield new Fragment(id, id);
            }
            case TaskDef t when t.refine() != null ->
                    transformRefine(t.refine(), ownerLane);
            case TaskDef t -> {
                String id = uid("tt_" + t.name());
                process.addNode(new BpmnTask(id, t.name(), ownerLane));
                nodeMap.put(t.name(), id);
                yield new Fragment(id, id);
            }
            case ResourceDef r -> {
                // resources don't become nodes — skip
                String id = uid("res_" + r.name());
                process.addNode(new BpmnTask(id, "[" + r.name() + "]", ownerLane));
                nodeMap.put(r.name(), id);
                yield new Fragment(id, id);
            }
        };
    }

    private Fragment transformRefine(RefineSpec spec, String lane) {
        return switch (spec) {
            case RefineSpec.IterRefine it -> {
                Fragment body = transformBodyList(it.children(), lane);
                String loopGwId = uid("gw_iter");
                process.addNode(new Gateway(loopGwId, GatewayType.XOR, GatewayRole.FORK, lane));
                addFlow(body.exit(), loopGwId, it.until());
                addFlow(loopGwId, body.entry(), "loop");
                yield new Fragment(body.entry(), loopGwId);
            }
            case RefineSpec.SeqRefine s -> {
                Fragment first = null, prev = null;
                for (String child : s.children()) {
                    Fragment f = transform(child, lane);
                    if (prev != null) addFlow(prev.exit(), f.entry(), null);
                    if (first == null) first = f;
                    prev = f;
                }
                yield new Fragment(first.entry(), prev.exit());
            }
            case RefineSpec.ParRefine p -> {
                String forkId = uid("gw_par_fork");
                String joinId = uid("gw_par_join");
                process.addNode(new Gateway(forkId, GatewayType.PAR, GatewayRole.FORK, lane));
                process.addNode(new Gateway(joinId, GatewayType.PAR, GatewayRole.JOIN, lane));
                for (String child : p.children()) {
                    Fragment f = transform(child, lane);
                    addFlow(forkId, f.entry(), null);
                    addFlow(f.exit(), joinId, null);
                }
                yield new Fragment(forkId, joinId);
            }
            case RefineSpec.XorRefine x -> {
                String forkId = uid("gw_xor_fork");
                String joinId = uid("gw_xor_join");
                process.addNode(new Gateway(forkId, GatewayType.XOR, GatewayRole.FORK, lane));
                process.addNode(new Gateway(joinId, GatewayType.XOR, GatewayRole.JOIN, lane));
                for (GuardedChild branch : x.branches()) {
                    Fragment f = transform(branch.childId(), lane);
                    addFlow(forkId, f.entry(), branch.condition());
                    addFlow(f.exit(), joinId, null);
                }
                yield new Fragment(forkId, joinId);
            }
            case RefineSpec.IorRefine io -> {
                String forkId = uid("gw_ior_fork");
                String joinId = uid("gw_ior_join");
                process.addNode(new Gateway(forkId, GatewayType.IOR, GatewayRole.FORK, lane));
                process.addNode(new Gateway(joinId, GatewayType.IOR, GatewayRole.JOIN, lane));
                for (GuardedChild branch : io.branches()) {
                    Fragment f = transform(branch.childId(), lane);
                    addFlow(forkId, f.entry(), branch.condition());
                    addFlow(f.exit(), joinId, null);
                }
                yield new Fragment(forkId, joinId);
            }
        };
    }

    private Fragment transformBodyList(List<String> ids, String lane) {
        Fragment first = null, prev = null;
        for (String id : ids) {
            Fragment f = transform(id, lane);
            if (prev != null) addFlow(prev.exit(), f.entry(), null);
            if (first == null) first = f;
            prev = f;
        }
        return new Fragment(first.entry(), prev.exit());
    }

    // ── Utilities ────────────────────────────────────────────────────────────

    private Set<String> collectAllChildren() {
        Set<String> children = new HashSet<>();
        for (Intentional item : model.allIntentionals().values()) {
            RefineSpec r = switch (item) {
                case GoalDef g -> g.refine();
                case TaskDef t -> t.refine();
                case ResourceDef ignored -> null;
            };
            if (r != null) addRefineChildren(r, children);
        }
        return children;
    }

    private void addRefineChildren(RefineSpec r, Set<String> out) {
        switch (r) {
            case RefineSpec.IterRefine it -> out.addAll(it.children());
            case RefineSpec.SeqRefine  s  -> out.addAll(s.children());
            case RefineSpec.ParRefine  p  -> out.addAll(p.children());
            case RefineSpec.XorRefine  x  -> x.branches().forEach(b -> out.add(b.childId()));
            case RefineSpec.IorRefine  io -> io.branches().forEach(b -> out.add(b.childId()));
        }
    }

    private void addFlow(String src, String tgt, String label) {
        process.addFlow(new SequenceFlow(src, tgt, label));
    }

    private String laneId(String actorName) {
        return "lane_" + actorName;
    }

    private String uid(String base) {
        return base + "_" + counter.getAndIncrement();
    }
}
