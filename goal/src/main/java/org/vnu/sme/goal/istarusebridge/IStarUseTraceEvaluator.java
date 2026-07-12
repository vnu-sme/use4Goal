package org.vnu.sme.goal.istarusebridge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.conformance.semantics.IStarPropagation;
import org.vnu.sme.goal.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.istar.mm.Actor;
import org.vnu.sme.goal.istar.mm.Agent;
import org.vnu.sme.goal.istar.mm.AndRefinement;
import org.vnu.sme.goal.istar.mm.Contribution;
import org.vnu.sme.goal.istar.mm.Dependency;
import org.vnu.sme.goal.istar.mm.ForRefinement;
import org.vnu.sme.goal.istar.mm.Goal;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.Obstacle;
import org.vnu.sme.goal.istar.mm.OrRefinement;
import org.vnu.sme.goal.istar.mm.PickRefinement;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.Refinement;
import org.vnu.sme.goal.istar.mm.Resource;
import org.vnu.sme.goal.istar.mm.Role;
import org.vnu.sme.goal.istar.mm.Task;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceCompiler.Checkpoint;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceCompiler.InstanceKey;

/**
 * Builds one instance-level GoalModel (one actor box per real {@code .use} object) for a
 * single {@link Checkpoint} -- the .istar+.use+.soil counterpart of what
 * {@code GoalModelInstanceTranslator} does for {@code .iscn}. An element's occurrence count is
 * driven entirely by {@link ContextResolution#actorTypeOf}: when that governing ActorType
 * differs from the element's own owning actor (either because the element itself is a
 * forall/pick child, or -- restoring the SR structure's plain AND/OR children of such a
 * child, e.g. CollectFromCalendar under TimetableCollected -- because the chain walk finds a
 * quantified ancestor), the element fans out into one occurrence per instance of that
 * governing type, still attached to its declaring actor's box. When two ends of the same
 * refinement/dependency share a governing type, they are synchronized to the same instance
 * per generated edge rather than cartesian-producted (see {@link #bindingsFor}).
 *
 * <p>Known limitation: only a single governing ActorType is resolved per element. A goal
 * genuinely parametrized by two independent ActorTypes at once (e.g. "which Secretary called
 * which Participant") is not yet representable -- such an element's fulfillment must
 * currently be derived through a plain 'depend' link instead of its own ocl clause.
 */
public final class IStarUseTraceEvaluator {

    public record Instantiation(GoalModel instanceModel, IStarMarking instanceMarking,
                                 Map<String, String> actorLabels, Map<String, String> nodeLabels) {}

    private IStarUseTraceEvaluator() {}

    public static Instantiation evaluate(GoalModel source, Checkpoint checkpoint) {
        ContextResolution resolution = ContextResolution.of(source);

        Map<String, List<String>> instancesByType = new LinkedHashMap<>();
        for (InstanceKey key : checkpoint.markings().keySet()) {
            instancesByType.computeIfAbsent(key.actorType(), k -> new ArrayList<>()).add(key.objectName());
        }

        GoalModel out = new GoalModel(source.getName() + "UseTrace");
        Map<String, String> actorLabels = new LinkedHashMap<>();
        Map<String, String> nodeLabels = new LinkedHashMap<>();
        Map<String, GoalTaskStatus> goalTaskMarks = new LinkedHashMap<>();
        Map<String, QualityStatus> qualityMarks = new LinkedHashMap<>();

        for (Actor actor : source.getActors()) {
            for (String obj : instancesByType.getOrDefault(actor.name(), List.of())) {
                List<IntentionalElement> elements = new ArrayList<>();
                for (IntentionalElement elem : actor.elements()) {
                    for (String occId : expand(source, elem.id(), obj, resolution, instancesByType)) {
                        elements.add(copyElement(elem, occId));
                        String ownerSuffix = occId.substring(occId.lastIndexOf("__") + 2);
                        nodeLabels.put(occId, elem.id() + " [" + ownerSuffix + "]");
                        recordStatus(elem, occId, ownerSuffix, checkpoint, elem.id(), goalTaskMarks, qualityMarks);
                    }
                }

                List<Refinement> refinements = new ArrayList<>();
                for (Refinement r : actor.refinements()) {
                    refinements.addAll(copyRefinement(source, r, obj, resolution, instancesByType));
                }
                List<Contribution> contributions = new ArrayList<>();
                for (Contribution c : actor.contributions()) {
                    for (String elemOcc : expand(source, c.element(), obj, resolution, instancesByType)) {
                        contributions.add(new Contribution(elemOcc, c.type(), occ(c.quality(), obj)));
                    }
                }

                String actorId = occ(actor.name(), obj);
                Actor copy = (actor instanceof Role)
                        ? new Role(actorId, elements, refinements, contributions,
                                List.of(), List.of(), List.of(), List.of(), List.of())
                        : new Agent(actorId, elements, refinements, contributions,
                                List.of(), List.of(), List.of(), List.of(), List.of());
                out.addActor(copy);
                actorLabels.put(actorId, actor.name() + " [" + obj + "]");
            }
        }

        for (Dependency d : source.getDependencies()) {
            for (Dependency copy : expandDependency(source, d, resolution, instancesByType)) {
                out.addDependency(copy);
            }
        }

        IStarMarking marking = IStarMarking.initial(out);
        for (var e : goalTaskMarks.entrySet()) marking = marking.withGoalTask(e.getKey(), e.getValue());
        for (var e : qualityMarks.entrySet()) marking = marking.withQuality(e.getKey(), e.getValue());
        marking = IStarPropagation.closePending(out, marking);

        return new Instantiation(out, marking, actorLabels, nodeLabels);
    }

    /**
     * A dependency's three named elements (dependerElmt, dependum, dependeeElmt) do not each
     * get their own independent instance loop -- when two of them resolve to the <em>same</em>
     * multi-instance ActorType (as MeetingAttendedByParticipant and Participate both resolve to
     * Participant, one forall-quantified and the other simply owned by it), they must share the
     * same instance per generated edge (Initiator.MeetingAttendedByParticipant[pa] -> task
     * Participate[pa] -> Participant[pa], not a cartesian product of every Initiator instance
     * against every Participant instance). Only genuinely distinct varying ActorTypes are
     * cartesian-producted; a role with no variance at all (dependerElmt null, or its ActorType
     * is a singleton) just uses that singleton's one instance.
     */
    private static List<Dependency> expandDependency(GoalModel source, Dependency d, ContextResolution resolution,
                                                      Map<String, List<String>> instancesByType) {
        List<String> ids = new ArrayList<>();
        if (d.dependerElmt() != null) ids.add(d.dependerElmt());
        if (d.dependeeElmt() != null) ids.add(d.dependeeElmt());
        if (d.dependum() != null) ids.add(d.dependum());

        List<Dependency> out = new ArrayList<>();
        for (Map<String, String> binding : bindingsFor(source, resolution, instancesByType, ids)) {
            String dependerActor = binding.getOrDefault(d.depender(), firstInstance(instancesByType, d.depender()));
            String dependeeActor = binding.getOrDefault(d.dependee(), firstInstance(instancesByType, d.dependee()));
            String dependerElmtInst = d.dependerElmt() == null ? dependerActor
                    : binding.getOrDefault(resolution.actorTypeOf(source, d.dependerElmt()), dependerActor);
            String dependeeElmtInst = d.dependeeElmt() == null ? dependeeActor
                    : binding.getOrDefault(resolution.actorTypeOf(source, d.dependeeElmt()), dependeeActor);
            String dependumInst = binding.getOrDefault(resolution.actorTypeOf(source, d.dependum()), dependeeElmtInst);

            out.add(new Dependency(
                    occ(d.depender(), dependerActor),
                    d.dependerElmt() == null ? null : occ(d.dependerElmt(), dependerElmtInst),
                    d.dependumKind(),
                    occ(d.dependum(), dependumInst),
                    occ(d.dependee(), dependeeActor),
                    d.dependeeElmt() == null ? null : occ(d.dependeeElmt(), dependeeElmtInst)));
        }
        return out;
    }

    private static String firstInstance(Map<String, List<String>> instancesByType, String actorType) {
        List<String> instances = instancesByType.getOrDefault(actorType, List.of());
        return instances.isEmpty() ? actorType : instances.get(0);
    }

    /**
     * The distinct multi-instance ActorTypes governing {@code ids} (via
     * {@link ContextResolution#actorTypeOf}), paired up as one binding per combination of their
     * instances -- so elements sharing a governing type end up synchronized to the same
     * instance within one binding, instead of every id looping independently. Empty/singleton
     * inputs yield exactly one (empty) binding.
     */
    private static List<Map<String, String>> bindingsFor(GoalModel source, ContextResolution resolution,
                                                          Map<String, List<String>> instancesByType,
                                                          List<String> ids) {
        List<String> drivingTypes = new ArrayList<>();
        for (String id : ids) {
            String t = resolution.actorTypeOf(source, id);
            if (instancesByType.getOrDefault(t, List.of()).size() > 1 && !drivingTypes.contains(t)) {
                drivingTypes.add(t);
            }
        }
        List<Map<String, String>> bindings = new ArrayList<>();
        for (List<String> combo : combinations(drivingTypes, instancesByType)) {
            Map<String, String> binding = new LinkedHashMap<>();
            for (int i = 0; i < drivingTypes.size(); i++) binding.put(drivingTypes.get(i), combo.get(i));
            bindings.add(binding);
        }
        return bindings;
    }

    /** cardinality of {@code id}'s own occurrence within one binding: bound instance if its
     *  governing type is one of the binding's driving types, else the surrounding box's own obj. */
    private static String occFor(GoalModel source, ContextResolution resolution, String id, String ownerObj,
                                 Map<String, String> binding) {
        String type = resolution.actorTypeOf(source, id);
        return occ(id, binding.getOrDefault(type, ownerObj));
    }

    /** Cartesian product of each driving type's declared instances, one combo per row, same
     *  column order as {@code types}. Empty {@code types} yields exactly one (empty) combo. */
    private static List<List<String>> combinations(List<String> types, Map<String, List<String>> instancesByType) {
        List<List<String>> out = new ArrayList<>();
        buildCombinations(types, 0, new ArrayList<>(), instancesByType, out);
        return out;
    }

    private static void buildCombinations(List<String> types, int index, List<String> current,
                                          Map<String, List<String>> instancesByType, List<List<String>> out) {
        if (index == types.size()) {
            out.add(List.copyOf(current));
            return;
        }
        for (String instance : instancesByType.getOrDefault(types.get(index), List.of())) {
            current.add(instance);
            buildCombinations(types, index + 1, current, instancesByType, out);
            current.remove(current.size() - 1);
        }
    }

    /** occ ids for elementId as it should appear on its declaring actor's box: one id (suffixed
     *  by {@code ownerObj}) when elementId's own governing ActorType is that actor's own type,
     *  or one id per instance of the governing ActorType otherwise (whether elementId is itself
     *  a forall/pick child, or a plain AND/OR descendant of one -- see
     *  {@link ContextResolution#actorTypeOf}). */
    private static List<String> expand(GoalModel source, String elementId, String ownerObj,
                                       ContextResolution resolution, Map<String, List<String>> instancesByType) {
        String governingType = resolution.actorTypeOf(source, elementId);
        String ownerType = source.ownerOf(elementId).orElse(null);
        if (governingType.equals(ownerType)) {
            return List.of(occ(elementId, ownerObj));
        }
        List<String> instances = instancesByType.getOrDefault(governingType, List.of());
        if (instances.isEmpty()) return List.of(occ(elementId, ownerObj));
        return instances.stream().map(o2 -> occ(elementId, o2)).toList();
    }

    private static void recordStatus(IntentionalElement elem, String occId, String obj, Checkpoint checkpoint,
                                     String baseId,
                                     Map<String, GoalTaskStatus> goalTaskMarks, Map<String, QualityStatus> qualityMarks) {
        IStarMarking m = findMarking(checkpoint, obj);
        if (m == null) return;
        switch (elem) {
            case Goal g -> { GoalTaskStatus s = m.goalTaskStatus(baseId); if (s != GoalTaskStatus.UNKNOWN) goalTaskMarks.put(occId, s); }
            case Task t -> { GoalTaskStatus s = m.goalTaskStatus(baseId); if (s != GoalTaskStatus.UNKNOWN) goalTaskMarks.put(occId, s); }
            case Quality q -> { QualityStatus s = m.qualityStatus(baseId); if (s != QualityStatus.UNKNOWN) qualityMarks.put(occId, s); }
            case Resource r -> { }
            case Obstacle o -> { }
        }
    }

    private static IStarMarking findMarking(Checkpoint checkpoint, String objectName) {
        for (var e : checkpoint.markings().entrySet()) {
            if (e.getKey().objectName().equals(objectName)) return e.getValue();
        }
        return null;
    }

    /**
     * AND/OR: parent and children are synchronized to a shared instance whenever they share a
     * governing ActorType (bindingsFor), instead of the child fanning out under a single fixed
     * parent occurrence -- needed now that a plain AND/OR child of a forall/pick-quantified goal
     * (CollectFromCalendar, HavePPCalled under TimetableCollected) is itself governed by that
     * same quantified ActorType (see the class doc). FOR/PICK keep the earlier "expand the
     * child fully under one parent occurrence" shape: that IS the aggregation step, not a
     * same-instance replication.
     */
    private static List<Refinement> copyRefinement(GoalModel source, Refinement r, String obj,
                                                   ContextResolution resolution,
                                                   Map<String, List<String>> instancesByType) {
        return switch (r) {
            case AndRefinement and -> {
                List<String> ids = new ArrayList<>(and.children());
                ids.add(and.parent());
                List<Refinement> out = new ArrayList<>();
                for (Map<String, String> binding : bindingsFor(source, resolution, instancesByType, ids)) {
                    out.add(new AndRefinement(
                            occFor(source, resolution, and.parent(), obj, binding),
                            and.children().stream().map(c -> occFor(source, resolution, c, obj, binding)).toList()));
                }
                yield out;
            }
            case OrRefinement or -> {
                List<Refinement> out = new ArrayList<>();
                for (Map<String, String> binding : bindingsFor(source, resolution, instancesByType,
                        List.of(or.parent(), or.child()))) {
                    out.add(new OrRefinement(occFor(source, resolution, or.parent(), obj, binding),
                            occFor(source, resolution, or.child(), obj, binding)));
                }
                yield out;
            }
            case ForRefinement forR -> {
                List<Refinement> out = new ArrayList<>();
                for (Map<String, String> binding : bindingsFor(source, resolution, instancesByType, List.of(forR.parent()))) {
                    out.add(new AndRefinement(occFor(source, resolution, forR.parent(), obj, binding),
                            expand(source, forR.child(), obj, resolution, instancesByType)));
                }
                yield out;
            }
            case PickRefinement p -> {
                List<Refinement> out = new ArrayList<>();
                for (Map<String, String> binding : bindingsFor(source, resolution, instancesByType, List.of(p.parent()))) {
                    String parentOcc = occFor(source, resolution, p.parent(), obj, binding);
                    for (String childOcc : expand(source, p.child(), obj, resolution, instancesByType)) {
                        out.add(new OrRefinement(parentOcc, childOcc));
                    }
                }
                yield out;
            }
        };
    }

    private static IntentionalElement copyElement(IntentionalElement elem, String id) {
        return switch (elem) {
            case Goal g -> new Goal(id, g.goalType(), g.oclSource());
            case Task t -> new Task(id, t.oclSource());
            case Resource r -> new Resource(id);
            case Quality q -> new Quality(id);
            case Obstacle o -> new Obstacle(id, o.type());
        };
    }

    private static String occ(String id, String obj) {
        return id + "__" + obj;
    }
}
