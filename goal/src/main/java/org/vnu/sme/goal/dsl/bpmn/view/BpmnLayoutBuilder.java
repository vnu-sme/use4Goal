package org.vnu.sme.goal.dsl.bpmn.view;

import java.util.*;

import org.vnu.sme.goal.dsl.bpmn.mm.*;
import org.vnu.sme.goal.dsl.bpmn.mm.Process; // disambiguate from java.lang.Process
import org.vnu.sme.goal.dsl.bpmnscenario.mm.BpmnScenarioSnapshot;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.NodeOccurrence;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.TokenMark;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.Value;

/**
 * MM (BpmnModel) -> BpmnLayout / BpmnChoreoLayout. Pure computation — no
 * Swing/AWT import. Three entry points, one per {@link BpmnViewMode}:
 * {@link #buildProcess}, {@link #buildCollaboration}, {@link #buildChoreography}.
 *
 * Note: SubProcess nodes are rendered as a single box — nested elements are
 * not laid out separately (matches the pre-refactor BpmnView behaviour).
 */
public final class BpmnLayoutBuilder {

    private static final int MARGIN    = 20;
    private static final int POOL_HDR  = 26;
    private static final int LANE_HDR  = 52;
    private static final int LANE_H    = 110;
    private static final int POOL_PAD  = 14;
    private static final int ELEM_PAD  = 24;
    private static final int EVT_D     = 34;
    private static final int TASK_W    = 110, TASK_H = 55;
    private static final int CALL_W    = 110, CALL_H = 55;
    private static final int GW_D      = 44;
    private static final int SUB_W     = 120, SUB_H  = 58;
    private static final int LANE_MIN_H = 50;

    private static final int CHOREO_COL_MARGIN = 90;
    private static final int CHOREO_COL_W      = 190;
    private static final int CHOREO_MSG_TOP    = 60;
    private static final int CHOREO_MSG_ROW_H  = 60;
    private static final int CHOREO_BOTTOM_PAD = 60;

    private BpmnLayoutBuilder() {}

    // ── Collaboration: all processes + message flows ────────────────────────

    public static BpmnLayout buildCollaboration(BpmnModel model) {
        return buildPools(model, model == null ? List.of() : model.processes(), true);
    }

    // ── Process: exactly one process, no message flows ──────────────────────

    public static BpmnLayout buildProcess(BpmnModel model, String processId) {
        if (model == null || processId == null) return emptyLayout();
        Optional<Process> p = model.findProcess(processId);
        if (p.isEmpty()) return emptyLayout();
        return buildPools(model, List.of(p.get()), false);
    }

    public static BpmnLayout buildScenarioAggregate(BpmnModel model, BpmnScenarioSnapshot snapshot, String processId) {
        if (model == null || snapshot == null) return emptyLayout();
        String id = processId != null ? processId : firstScenarioProcessId(snapshot);
        if (id == null) return emptyLayout();

        BpmnLayout layout = buildProcess(model, id);
        Map<String, List<String>> completed = scenarioDetailsByElement(snapshot.completed());
        Map<String, List<String>> active = scenarioDetailsByElement(snapshot.active());
        Map<String, List<String>> fired = scenarioDetailsByElement(snapshot.fired());

        for (BpmnNode node : layout.nodes.values()) {
            List<String> activeDetails = active.getOrDefault(node.id, List.of());
            List<String> completedDetails = completed.getOrDefault(node.id, List.of());
            List<String> firedDetails = fired.getOrDefault(node.id, List.of());
            int activeCount = activeDetails.size();
            int doneCount = completedDetails.size() + firedDetails.size();
            if (activeCount + doneCount == 0) continue;

            node.label = aggregateLabel(node.label, activeCount, doneCount);
            node.w = Math.max(node.w, activeCount > 0 && doneCount > 0 ? 170 : 145);
            node.scenarioState = activeCount > 0
                    ? BpmnDiagramNode.ScenarioState.ACTIVE
                    : BpmnDiagramNode.ScenarioState.COMPLETED;
            if (!activeDetails.isEmpty()) {
                node.scenarioDetails.add("active (" + activeCount + ")");
                node.scenarioDetails.addAll(activeDetails);
            }
            if (!completedDetails.isEmpty()) {
                node.scenarioDetails.add("completed (" + completedDetails.size() + ")");
                node.scenarioDetails.addAll(completedDetails);
            }
            if (!firedDetails.isEmpty()) {
                node.scenarioDetails.add("fired (" + firedDetails.size() + ")");
                node.scenarioDetails.addAll(firedDetails);
            }
        }
        return layout;
    }

    // ── Scenario/Object projection: one lane per declared actor object ─────

    public static BpmnLayout buildScenario(BpmnModel model, BpmnScenarioSnapshot snapshot) {
        if (model == null || snapshot == null || snapshot.processInstances().isEmpty()) return emptyLayout();

        String processId = snapshot.processInstances().values().iterator().next();
        Process process = model.findProcess(processId).orElse(null);
        if (process == null) return emptyLayout();

        Map<String, BpmnNode> nodeMap = new LinkedHashMap<>();
        List<BpmnEdge> edges = new ArrayList<>();

        BpmnPool pool = new BpmnPool(process.id(), label(process.name(), process.id()) + " scenario");
        pool.x = MARGIN;
        pool.y = MARGIN;

        int laneY = pool.y + POOL_PAD;
        int maxW = 0;
        Map<String, BpmnLane> objectLanes = new LinkedHashMap<>();
        for (Map.Entry<String, String> actor : snapshot.actors().entrySet()) {
            BpmnLane lane = new BpmnLane(actor.getKey(), actor.getKey() + " : " + actor.getValue());
            lane.x = MARGIN + POOL_HDR;
            lane.y = laneY;
            lane.h = scenarioLaneHeight(actor.getKey(), actor.getValue(), snapshot);
            lane.w = 1880;
            objectLanes.put(actor.getKey(), lane);
            pool.lanes.add(lane);
            laneY += lane.h;
        }

        BpmnNode start = sized(new BpmnNode("scenario:start", "start", BpmnNodeKind.START_EVT,
                EventTrigger.NONE, true, GatewayKind.XOR), EVT_D, EVT_D);
        start.x = 70;
        start.y = pool.y + 32;
        BpmnNode finish = sized(new BpmnNode("scenario:end", "end", BpmnNodeKind.END_EVT,
                EventTrigger.NONE, true, GatewayKind.XOR), EVT_D, EVT_D);
        finish.x = 1960;
        finish.y = pool.y + Math.max(120, laneY - pool.y - 320);
        pool.elements.add(start);
        pool.elements.add(finish);
        nodeMap.put(start.id, start);
        nodeMap.put(finish.id, finish);

        List<ScenarioOccurrence> occurrences = scenarioOccurrences(snapshot);
        Map<String, BpmnNode> byOccurrenceKey = new LinkedHashMap<>();
        Map<String, BpmnNode> byElementAndTarget = new LinkedHashMap<>();
        Map<String, Integer> participantRows = participantRows(snapshot);
        Map<String, Integer> laneAutoRows = new LinkedHashMap<>();

        for (int i = 0; i < occurrences.size(); i++) {
            ScenarioOccurrence occurrence = occurrences.get(i);
            String performer = performerOf(occurrence);
            BpmnLane lane = objectLanes.get(performer);
            FlowElement element = model.findFlowElement(occurrence.node().elementId()).orElse(null);
            if (lane == null || element == null) continue;

            BpmnNode node = toNode(element);
            node.id = "scenario:o" + (i + 1) + ":" + occurrence.node().elementId() + ":" + performer;
            node.label = occurrenceExecutionLabel(i + 1, node.label, occurrence);
            node.w = Math.max(node.w, 180);
            node.scenarioState = occurrence.state();
            node.scenarioDetails.add(occurrence.node().display());
            node.x = scenarioColumn(occurrence.node().elementId(), i);
            node.y = occurrenceY(lane, occurrence, participantRows, laneAutoRows);

            lane.elements.add(node);
            nodeMap.put(node.id, node);
            byOccurrenceKey.put(occurrenceKey(occurrence), node);
            if (occurrence.node().objectId() != null) {
                byElementAndTarget.put(occurrence.node().elementId() + "::" + occurrence.node().objectId(), node);
            } else {
                byElementAndTarget.put(occurrence.node().elementId() + "::" + performer, node);
            }
            maxW = Math.max(maxW, node.x + node.w);
        }

        if (!addMeetingExecutionEdges(edges, start, finish, byElementAndTarget, snapshot)) {
            addFallbackExecutionEdges(edges, start, finish, occurrences, byOccurrenceKey);
        }

        for (BpmnLane lane : objectLanes.values()) {
            if (lane.elements.isEmpty()) {
                BpmnNode state = objectStateNode(lane.id, snapshot);
                state.x = MARGIN + POOL_HDR + LANE_HDR + ELEM_PAD;
                state.y = lane.y + (lane.h - state.h) / 2;
                lane.elements.add(state);
                nodeMap.put(state.id, state);
            }
            int laneMaxX = lane.elements.stream().mapToInt(n -> n.x + n.w).max().orElse(lane.x + lane.w);
            lane.w = Math.max(lane.w, laneMaxX - lane.x + ELEM_PAD);
            maxW = Math.max(maxW, lane.x + lane.w);
        }

        pool.w = Math.max(maxW - pool.x + MARGIN, 2050);
        pool.h = laneY - pool.y + POOL_PAD;
        for (BpmnLane lane : pool.lanes) lane.w = pool.w - POOL_HDR;

        return new BpmnLayout(List.of(pool), nodeMap, edges,
                Math.max(pool.x + pool.w + MARGIN, 900), Math.max(pool.y + pool.h + MARGIN, 600));
    }

    private static BpmnLayout emptyLayout() {
        return new BpmnLayout(List.of(), Map.of(), List.of(), 900, 600);
    }

    private static String firstScenarioProcessId(BpmnScenarioSnapshot snapshot) {
        return snapshot.processInstances().isEmpty() ? null : snapshot.processInstances().values().iterator().next();
    }

    private static Map<String, List<String>> scenarioDetailsByElement(List<NodeOccurrence> occurrences) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (NodeOccurrence occurrence : occurrences) {
            result.computeIfAbsent(occurrence.elementId(), ignored -> new ArrayList<>())
                    .add(occurrence.display());
        }
        return result;
    }

    private static String aggregateLabel(String base, int activeCount, int doneCount) {
        if (activeCount > 0 && doneCount > 0) return base + " [active " + activeCount + "/done " + doneCount + "]";
        if (activeCount > 0) return base + " [active " + activeCount + "]";
        return base + " [" + doneCount + "]";
    }

    private record ScenarioOccurrence(NodeOccurrence node, BpmnDiagramNode.ScenarioState state) {}

    private static List<ScenarioOccurrence> scenarioOccurrences(BpmnScenarioSnapshot snapshot) {
        List<ScenarioOccurrence> result = new ArrayList<>();
        snapshot.completed().forEach(o -> result.add(new ScenarioOccurrence(o, BpmnDiagramNode.ScenarioState.COMPLETED)));
        snapshot.active().forEach(o -> result.add(new ScenarioOccurrence(o, BpmnDiagramNode.ScenarioState.ACTIVE)));
        snapshot.fired().forEach(o -> result.add(new ScenarioOccurrence(o, BpmnDiagramNode.ScenarioState.COMPLETED)));
        return result;
    }

    private static String performerOf(ScenarioOccurrence occurrence) {
        if (occurrence.node().actorId() != null) return occurrence.node().actorId();
        if (occurrence.node().objectId() != null) return occurrence.node().objectId();
        return occurrence.node().processInstanceId();
    }

    private static int scenarioLaneHeight(String actorId, String actorType, BpmnScenarioSnapshot snapshot) {
        if ("Organizer".equals(actorType)) return 300;
        if ("Secretary".equals(actorType)) return 170;
        if ("Initiator".equals(actorType)) return 100;
        if ("Participant".equals(actorType)) return 90;
        long occurrences = scenarioOccurrences(snapshot).stream()
                .filter(o -> actorId.equals(performerOf(o)))
                .count();
        return (int) Math.max(90, 35 + Math.max(1, occurrences) * 58);
    }

    private static Map<String, Integer> participantRows(BpmnScenarioSnapshot snapshot) {
        Map<String, Integer> rows = new LinkedHashMap<>();
        for (Value value : snapshot.values().values()) {
            if (value instanceof Value.ListValue list && list.items().stream().allMatch(snapshot.actors()::containsKey)) {
                for (String item : list.items()) {
                    if ("Participant".equals(snapshot.actors().get(item)) && !rows.containsKey(item)) {
                        rows.put(item, rows.size());
                    }
                }
                if (!rows.isEmpty()) return rows;
            }
        }
        for (Map.Entry<String, String> actor : snapshot.actors().entrySet()) {
            if ("Participant".equals(actor.getValue())) rows.put(actor.getKey(), rows.size());
        }
        return rows;
    }

    private static int scenarioColumn(String elementId, int index) {
        return switch (elementId) {
            case "decideMeetingDetails" -> 160;
            case "checkCalendar" -> 400;
            case "requestSecretaryCall" -> 620;
            case "collectConstraintsByPhone" -> 840;
            case "chooseTimeAndDate" -> 1120;
            case "announceMeeting" -> 1380;
            case "participate" -> 1660;
            default -> 180 + index * 220;
        };
    }

    private static int occurrenceY(BpmnLane lane, ScenarioOccurrence occurrence,
                                   Map<String, Integer> participantRows, Map<String, Integer> laneAutoRows) {
        String elementId = occurrence.node().elementId();
        String target = occurrence.node().objectId();
        if (target != null && participantRows.containsKey(target)
                && ("checkCalendar".equals(elementId)
                || "requestSecretaryCall".equals(elementId)
                || "announceMeeting".equals(elementId))) {
            return lane.y + 15 + participantRows.get(target) * 55;
        }
        if ("chooseTimeAndDate".equals(elementId)) return lane.y + Math.max(20, lane.h / 2 - 30);
        if ("collectConstraintsByPhone".equals(elementId) && target != null && participantRows.containsKey(target)) {
            int row = Math.max(0, participantRows.get(target) - 2);
            return lane.y + 15 + row * 55;
        }
        if ("participate".equals(elementId)) return lane.y + Math.max(15, (lane.h - TASK_H) / 2);
        String performer = performerOf(occurrence);
        int row = laneAutoRows.merge(performer, 1, Integer::sum) - 1;
        return lane.y + 15 + row * 55;
    }

    private static String occurrenceExecutionLabel(int number, String base, ScenarioOccurrence occurrence) {
        StringBuilder label = new StringBuilder("o").append(number).append(" ").append(base);
        if (occurrence.node().objectId() != null) label.append(" target=").append(occurrence.node().objectId());
        return label.toString();
    }

    private static String occurrenceKey(ScenarioOccurrence occurrence) {
        return occurrence.node().elementId() + "::" + performerOf(occurrence) + "::"
                + (occurrence.node().objectId() == null ? "" : occurrence.node().objectId());
    }

    private static boolean addMeetingExecutionEdges(List<BpmnEdge> edges, BpmnNode start, BpmnNode finish,
                                                    Map<String, BpmnNode> byElementAndTarget,
                                                    BpmnScenarioSnapshot snapshot) {
        BpmnNode decide = firstByElement(byElementAndTarget, "decideMeetingDetails");
        BpmnNode choose = firstByElement(byElementAndTarget, "chooseTimeAndDate");
        if (decide == null || choose == null) return false;

        edges.add(new BpmnEdge(start.id, decide.id, BpmnEdgeKind.SEQUENCE, null));
        List<String> participants = participantRows(snapshot).keySet().stream().toList();
        for (String participant : participants) {
            BpmnNode check = byElementAndTarget.get("checkCalendar::" + participant);
            BpmnNode request = byElementAndTarget.get("requestSecretaryCall::" + participant);
            BpmnNode collect = byElementAndTarget.get("collectConstraintsByPhone::" + participant);
            BpmnNode announce = byElementAndTarget.get("announceMeeting::" + participant);
            BpmnNode participate = byElementAndTarget.get("participate::" + participant);

            if (check != null) edges.add(new BpmnEdge(decide.id, check.id, BpmnEdgeKind.SEQUENCE, null));
            BpmnNode readyForChoose = check;
            if (check != null && request != null) {
                edges.add(new BpmnEdge(check.id, request.id, BpmnEdgeKind.SEQUENCE, null));
                readyForChoose = request;
            }
            if (request != null && collect != null) {
                edges.add(new BpmnEdge(request.id, collect.id, BpmnEdgeKind.SEQUENCE, "alex calls " + participant));
                readyForChoose = collect;
            }
            if (readyForChoose != null) edges.add(new BpmnEdge(readyForChoose.id, choose.id, BpmnEdgeKind.SEQUENCE, null));
            if (announce != null) edges.add(new BpmnEdge(choose.id, announce.id, BpmnEdgeKind.SEQUENCE, null));
            if (announce != null && participate != null) {
                edges.add(new BpmnEdge(announce.id, participate.id, BpmnEdgeKind.SEQUENCE, null));
            }
            if (participate != null) edges.add(new BpmnEdge(participate.id, finish.id, BpmnEdgeKind.SEQUENCE, null));
        }
        return true;
    }

    private static BpmnNode firstByElement(Map<String, BpmnNode> nodes, String elementId) {
        for (Map.Entry<String, BpmnNode> entry : nodes.entrySet()) {
            if (entry.getKey().startsWith(elementId + "::")) return entry.getValue();
        }
        return null;
    }

    private static void addFallbackExecutionEdges(List<BpmnEdge> edges, BpmnNode start, BpmnNode finish,
                                                  List<ScenarioOccurrence> occurrences,
                                                  Map<String, BpmnNode> byOccurrenceKey) {
        BpmnNode previous = start;
        for (ScenarioOccurrence occurrence : occurrences) {
            BpmnNode node = byOccurrenceKey.get(occurrenceKey(occurrence));
            if (node == null) continue;
            edges.add(new BpmnEdge(previous.id, node.id, BpmnEdgeKind.SEQUENCE, null));
            previous = node;
        }
        edges.add(new BpmnEdge(previous.id, finish.id, BpmnEdgeKind.SEQUENCE, null));
    }

    private static Map<String, String> elementLaneIndex(Process process) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Lane lane : process.lanes()) {
            for (FlowElement element : lane.flowElements()) {
                result.put(element.id(), lane.id());
            }
        }
        return result;
    }

    private static void placeOccurrences(List<NodeOccurrence> occurrences, String stateLabel, BpmnModel model,
            Map<String, String> elementLane, Map<String, BpmnLane> objectLanes, Map<String, Integer> ownerX,
            Map<String, BpmnNode> nodeMap, Map<String, BpmnNode> occurrenceByOwnerAndElement,
            BpmnDiagramNode.ScenarioState state) {
        int i = 0;
        for (NodeOccurrence occurrence : occurrences) {
            String owner = occurrence.objectId() != null ? occurrence.objectId() : occurrence.actorId();
            if (owner == null || !objectLanes.containsKey(owner)) continue;
            FlowElement element = model.findFlowElement(occurrence.elementId()).orElse(null);
            if (element == null) continue;

            BpmnLane lane = objectLanes.get(owner);
            BpmnNode node = toNode(element);
            node.id = "scenario:" + stateLabel + ":" + occurrence.elementId() + ":" + owner + ":" + i;
            node.label = occurrenceLabel(node.label, owner, elementLane.get(occurrence.elementId()), stateLabel);
            node.w = Math.max(node.w, 170);
            node.scenarioState = state;
            node.scenarioDetails.add(occurrence.display());
            node.x = ownerX.get(owner);
            node.y = lane.y + (lane.h - node.h) / 2;

            ownerX.put(owner, node.x + node.w + ELEM_PAD);
            lane.elements.add(node);
            nodeMap.put(node.id, node);
            occurrenceByOwnerAndElement.put(owner + "::" + occurrence.elementId(), node);
            i++;
        }
    }

    private static BpmnNode placeSyntheticNode(String owner, String elementId, BpmnModel model,
            Map<String, BpmnLane> objectLanes, Map<String, Integer> ownerX, Map<String, BpmnNode> nodeMap) {
        FlowElement element = model.findFlowElement(elementId).orElse(null);
        if (element == null) return null;
        BpmnLane lane = objectLanes.get(owner);
        if (lane == null) return null;

        BpmnNode node = toNode(element);
        node.id = "scenario:token:" + elementId + ":" + owner;
        node.label = node.label + " [" + owner + "]";
        node.w = Math.max(node.w, 150);
        node.x = ownerX.get(owner);
        node.y = lane.y + (lane.h - node.h) / 2;
        ownerX.put(owner, node.x + node.w + ELEM_PAD);
        lane.elements.add(node);
        nodeMap.put(node.id, node);
        return node;
    }

    private static BpmnNode objectStateNode(String actorId, BpmnScenarioSnapshot snapshot) {
        String stateText = snapshot.values().entrySet().stream()
                .filter(e -> e.getKey().startsWith(actorId + "."))
                .map(e -> e.getKey().substring(actorId.length() + 1) + "=" + valueText(e.getValue()))
                .findFirst()
                .orElse("object");
        return sized(new BpmnNode("scenario:object:" + actorId, stateText, BpmnNodeKind.TASK,
                EventTrigger.NONE, true, GatewayKind.XOR), TASK_W, TASK_H);
    }

    private static String valueText(Value value) {
        return switch (value) {
            case Value.Atom atom -> atom.text();
            case Value.ListValue list -> "[" + String.join(",", list.items()) + "]";
        };
    }

    private static String occurrenceLabel(String base, String owner, String role, String stateLabel) {
        String roleText = role == null ? "" : role + "/";
        return base + " [" + roleText + owner + ", " + stateLabel + "]";
    }

    private static BpmnLayout buildPools(BpmnModel model, List<Process> processes, boolean includeMessageFlows) {
        Map<String, BpmnNode> nodeMap = new LinkedHashMap<>();
        List<BpmnPool>        pools   = new ArrayList<>();
        if (model == null || processes.isEmpty()) return emptyLayout();

        int py = MARGIN;
        int maxW = 0;

        for (Process process : processes) {
            BpmnPool vp = new BpmnPool(process.id(), label(process.name(), process.id()));
            vp.x = MARGIN;
            vp.y = py;

            int contentX = MARGIN + POOL_HDR;
            int laneY    = py + POOL_PAD;
            Set<String> laneElementIds = new HashSet<>();

            for (Lane lane : process.lanes()) {
                BpmnLane vl = new BpmnLane(lane.id(), label(lane.name(), lane.id()));
                vl.x = contentX;
                vl.y = laneY;

                int ex = contentX + LANE_HDR + ELEM_PAD;
                int maxNodeH = 0;
                for (FlowElement fe : lane.flowElements()) {
                    laneElementIds.add(fe.id());
                    BpmnNode vn = toNode(fe);
                    vn.x = ex;
                    vl.elements.add(vn);
                    nodeMap.put(vn.id, vn);
                    ex += vn.w + ELEM_PAD;
                    if (vn.h > maxNodeH) maxNodeH = vn.h;
                }
                vl.h = maxNodeH > 0 ? maxNodeH + ELEM_PAD * 2 : LANE_MIN_H;
                for (BpmnNode vn : vl.elements) vn.y = laneY + (vl.h - vn.h) / 2;
                vl.w = Math.max(ex - contentX + ELEM_PAD, 400);
                maxW = Math.max(maxW, MARGIN + POOL_HDR + vl.w);
                vp.lanes.add(vl);
                laneY += vl.h;
            }

            // Process.flowElements() = lane elements ∪ top-level elements (Process owns both).
            // Only render the ones NOT already placed inside a lane.
            List<FlowElement> topLevel = process.flowElements().stream()
                    .filter(fe -> !laneElementIds.contains(fe.id()))
                    .toList();

            if (!topLevel.isEmpty()) {
                int ex = contentX + ELEM_PAD;
                int rowY = laneY + POOL_PAD;
                for (FlowElement fe : topLevel) {
                    BpmnNode vn = toNode(fe);
                    vn.x = ex; vn.y = rowY;
                    vp.elements.add(vn);
                    nodeMap.put(vn.id, vn);
                    ex += vn.w + ELEM_PAD;
                    maxW = Math.max(maxW, ex + MARGIN);
                }
                laneY += LANE_H + POOL_PAD;
            }

            vp.h = laneY - py + POOL_PAD;
            pools.add(vp);
            py += vp.h + MARGIN;
        }

        // Normalise all pools and lanes to the same width
        for (BpmnPool vp : pools) {
            vp.w = maxW - MARGIN;
            for (BpmnLane vl : vp.lanes) vl.w = vp.w - POOL_HDR;
        }

        List<BpmnEdge> edges = new ArrayList<>();
        for (Process process : processes)
            for (SequenceFlow sf : process.sequenceFlows())
                edges.add(new BpmnEdge(sf.source().id(), sf.target().id(), BpmnEdgeKind.SEQUENCE, sf.label()));

        if (includeMessageFlows)
            for (MessageFlow mf : model.messageFlows())
                edges.add(new BpmnEdge(mf.source().id(), mf.target().id(), BpmnEdgeKind.MESSAGE, messageLabel(mf)));

        int width  = Math.max(maxW + MARGIN, 900);
        int height = Math.max(py + MARGIN, 600);
        return new BpmnLayout(pools, nodeMap, edges, width, height);
    }

    private static BpmnNode toNode(FlowElement fe) {
        return switch (fe) {
            case StartEvent se -> sized(new BpmnNode(se.id(), se.id(), BpmnNodeKind.START_EVT,
                    se.trigger(), true, GatewayKind.XOR), EVT_D, EVT_D);
            case EndEvent ee -> sized(new BpmnNode(ee.id(), ee.id(), BpmnNodeKind.END_EVT,
                    ee.trigger(), true, GatewayKind.XOR), EVT_D, EVT_D);
            case IntermediateEvent ie -> sized(new BpmnNode(ie.id(), ie.id(), BpmnNodeKind.INT_EVT,
                    ie.trigger(), ie.direction() == EventDirection.CATCHING, GatewayKind.XOR), EVT_D, EVT_D);
            case Task t -> sized(new BpmnNode(t.id(), label(t.name(), t.id()), BpmnNodeKind.TASK,
                    EventTrigger.NONE, true, GatewayKind.XOR), TASK_W, TASK_H);
            case CallActivity ca -> sized(new BpmnNode(ca.id(), ca.id(), BpmnNodeKind.CALL_ACTIVITY,
                    EventTrigger.NONE, true, GatewayKind.XOR), CALL_W, CALL_H);
            case SubProcess sp -> sized(new BpmnNode(sp.id(), label(sp.name(), sp.id()), BpmnNodeKind.SUBPROCESS,
                    EventTrigger.NONE, true, GatewayKind.XOR), SUB_W, SUB_H);
            case Gateway gw -> sized(new BpmnNode(gw.id(), gw.id(), BpmnNodeKind.GATEWAY,
                    EventTrigger.NONE, true, gw.kind()), GW_D, GW_D);
        };
    }

    private static BpmnNode sized(BpmnNode n, int w, int h) {
        n.w = w; n.h = h;
        return n;
    }

    private static String label(String name, String id) { return name != null ? name : id; }

    private static String messageLabel(MessageFlow mf) {
        if (mf.message() == null) return null;
        return mf.message().name() != null ? mf.message().name() : mf.message().id();
    }

    // ── Choreography: message flows only, 1 column per Participant (Process) ─

    public static BpmnChoreoLayout buildChoreography(BpmnModel model) {
        if (model == null || model.messageFlows().isEmpty())
            return new BpmnChoreoLayout(Map.of(), List.of(), 700, 300);

        Map<String, BpmnChoreoParticipant> participants = new LinkedHashMap<>();
        List<BpmnChoreoMessage> messages = new ArrayList<>();

        int y = CHOREO_MSG_TOP;
        for (MessageFlow mf : model.messageFlows()) {
            String fromProc = model.ownerProcessId(mf.source().id()).orElse(mf.source().id());
            String toProc   = model.ownerProcessId(mf.target().id()).orElse(mf.target().id());

            BpmnChoreoParticipant fromP = participants.computeIfAbsent(fromProc, id -> newParticipant(model, id));
            BpmnChoreoParticipant toP   = participants.computeIfAbsent(toProc,   id -> newParticipant(model, id));

            messages.add(new BpmnChoreoMessage(fromP.id, toP.id, messageLabel(mf), y));
            y += CHOREO_MSG_ROW_H;
        }

        int i = 0;
        for (BpmnChoreoParticipant p : participants.values()) {
            p.x = CHOREO_COL_MARGIN + i * CHOREO_COL_W;
            i++;
        }

        int width  = Math.max(CHOREO_COL_MARGIN * 2 + participants.size() * CHOREO_COL_W, 500);
        int height = y + CHOREO_BOTTOM_PAD;
        return new BpmnChoreoLayout(participants, messages, width, height);
    }

    private static BpmnChoreoParticipant newParticipant(BpmnModel model, String processId) {
        String lbl = model.findProcess(processId)
                .map(p -> label(p.name(), p.id()))
                .orElse(processId);
        return new BpmnChoreoParticipant(processId, lbl, 0);
    }
}
