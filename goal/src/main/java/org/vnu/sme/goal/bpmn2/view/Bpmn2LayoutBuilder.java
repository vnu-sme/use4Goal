package org.vnu.sme.goal.bpmn2.view;

import java.util.*;

import org.vnu.sme.goal.bpmn2.mm.*;
import org.vnu.sme.goal.bpmn2.mm.Process; // disambiguate from java.lang.Process
import org.vnu.sme.goal.bpmn2scenario.mm.Bpmn2ScenarioSnapshot;
import org.vnu.sme.goal.bpmn2scenario.mm.NodeOccurrence;
import org.vnu.sme.goal.bpmn2scenario.mm.TokenMark;
import org.vnu.sme.goal.bpmn2scenario.mm.Value;

/**
 * MM (Bpmn2Model) -> Bpmn2Layout / Bpmn2ChoreoLayout. Pure computation — no
 * Swing/AWT import. Three entry points, one per {@link Bpmn2ViewMode}:
 * {@link #buildProcess}, {@link #buildCollaboration}, {@link #buildChoreography}.
 *
 * Note: SubProcess nodes are rendered as a single box — nested elements are
 * not laid out separately (matches the pre-refactor Bpmn2View behaviour).
 */
public final class Bpmn2LayoutBuilder {

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

    private Bpmn2LayoutBuilder() {}

    // ── Collaboration: all processes + message flows ────────────────────────

    public static Bpmn2Layout buildCollaboration(Bpmn2Model model) {
        return buildPools(model, model == null ? List.of() : model.processes(), true);
    }

    // ── Process: exactly one process, no message flows ──────────────────────

    public static Bpmn2Layout buildProcess(Bpmn2Model model, String processId) {
        if (model == null || processId == null) return emptyLayout();
        Optional<Process> p = model.findProcess(processId);
        if (p.isEmpty()) return emptyLayout();
        return buildPools(model, List.of(p.get()), false);
    }

    public static Bpmn2Layout buildScenarioAggregate(Bpmn2Model model, Bpmn2ScenarioSnapshot snapshot, String processId) {
        if (model == null || snapshot == null) return emptyLayout();
        String id = processId != null ? processId : firstScenarioProcessId(snapshot);
        if (id == null) return emptyLayout();

        Bpmn2Layout layout = buildProcess(model, id);
        Map<String, List<String>> completed = scenarioDetailsByElement(snapshot.completed());
        Map<String, List<String>> active = scenarioDetailsByElement(snapshot.active());
        Map<String, List<String>> fired = scenarioDetailsByElement(snapshot.fired());

        for (Bpmn2Node node : layout.nodes.values()) {
            List<String> activeDetails = active.getOrDefault(node.id, List.of());
            List<String> completedDetails = completed.getOrDefault(node.id, List.of());
            List<String> firedDetails = fired.getOrDefault(node.id, List.of());
            int activeCount = activeDetails.size();
            int doneCount = completedDetails.size() + firedDetails.size();
            if (activeCount + doneCount == 0) continue;

            node.label = aggregateLabel(node.label, activeCount, doneCount);
            node.w = Math.max(node.w, activeCount > 0 && doneCount > 0 ? 170 : 145);
            node.scenarioState = activeCount > 0
                    ? Bpmn2DiagramNode.ScenarioState.ACTIVE
                    : Bpmn2DiagramNode.ScenarioState.COMPLETED;
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

    public static Bpmn2Layout buildScenario(Bpmn2Model model, Bpmn2ScenarioSnapshot snapshot) {
        if (model == null || snapshot == null || snapshot.processInstances().isEmpty()) return emptyLayout();

        String processId = snapshot.processInstances().values().iterator().next();
        Process process = model.findProcess(processId).orElse(null);
        if (process == null) return emptyLayout();

        Map<String, Bpmn2Node> nodeMap = new LinkedHashMap<>();
        List<Bpmn2Edge> edges = new ArrayList<>();

        Bpmn2Pool pool = new Bpmn2Pool(process.id(), label(process.name(), process.id()) + " scenario");
        pool.x = MARGIN;
        pool.y = MARGIN;

        int laneY = pool.y + POOL_PAD;
        int maxW = 0;
        Map<String, Bpmn2Lane> objectLanes = new LinkedHashMap<>();
        for (Map.Entry<String, String> actor : snapshot.actors().entrySet()) {
            Bpmn2Lane lane = new Bpmn2Lane(actor.getKey(), actor.getKey() + " : " + actor.getValue());
            lane.x = MARGIN + POOL_HDR;
            lane.y = laneY;
            lane.h = scenarioLaneHeight(actor.getKey(), actor.getValue(), snapshot);
            lane.w = 1880;
            objectLanes.put(actor.getKey(), lane);
            pool.lanes.add(lane);
            laneY += lane.h;
        }

        Bpmn2Node start = sized(new Bpmn2Node("scenario:start", "start", Bpmn2NodeKind.START_EVT,
                EventTrigger.NONE, true, GatewayKind.XOR), EVT_D, EVT_D);
        start.x = 70;
        start.y = pool.y + 32;
        Bpmn2Node finish = sized(new Bpmn2Node("scenario:end", "end", Bpmn2NodeKind.END_EVT,
                EventTrigger.NONE, true, GatewayKind.XOR), EVT_D, EVT_D);
        finish.x = 1960;
        finish.y = pool.y + Math.max(120, laneY - pool.y - 320);
        pool.elements.add(start);
        pool.elements.add(finish);
        nodeMap.put(start.id, start);
        nodeMap.put(finish.id, finish);

        List<ScenarioOccurrence> occurrences = scenarioOccurrences(snapshot);
        Map<String, Bpmn2Node> byOccurrenceKey = new LinkedHashMap<>();
        Map<String, Bpmn2Node> byElementAndTarget = new LinkedHashMap<>();
        Map<String, Integer> participantRows = participantRows(snapshot);
        Map<String, Integer> laneAutoRows = new LinkedHashMap<>();

        for (int i = 0; i < occurrences.size(); i++) {
            ScenarioOccurrence occurrence = occurrences.get(i);
            String performer = performerOf(occurrence);
            Bpmn2Lane lane = objectLanes.get(performer);
            FlowElement element = model.findFlowElement(occurrence.node().elementId()).orElse(null);
            if (lane == null || element == null) continue;

            Bpmn2Node node = toNode(element);
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

        for (Bpmn2Lane lane : objectLanes.values()) {
            if (lane.elements.isEmpty()) {
                Bpmn2Node state = objectStateNode(lane.id, snapshot);
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
        for (Bpmn2Lane lane : pool.lanes) lane.w = pool.w - POOL_HDR;

        return new Bpmn2Layout(List.of(pool), nodeMap, edges,
                Math.max(pool.x + pool.w + MARGIN, 900), Math.max(pool.y + pool.h + MARGIN, 600));
    }

    private static Bpmn2Layout emptyLayout() {
        return new Bpmn2Layout(List.of(), Map.of(), List.of(), 900, 600);
    }

    private static String firstScenarioProcessId(Bpmn2ScenarioSnapshot snapshot) {
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

    private record ScenarioOccurrence(NodeOccurrence node, Bpmn2DiagramNode.ScenarioState state) {}

    private static List<ScenarioOccurrence> scenarioOccurrences(Bpmn2ScenarioSnapshot snapshot) {
        List<ScenarioOccurrence> result = new ArrayList<>();
        snapshot.completed().forEach(o -> result.add(new ScenarioOccurrence(o, Bpmn2DiagramNode.ScenarioState.COMPLETED)));
        snapshot.active().forEach(o -> result.add(new ScenarioOccurrence(o, Bpmn2DiagramNode.ScenarioState.ACTIVE)));
        snapshot.fired().forEach(o -> result.add(new ScenarioOccurrence(o, Bpmn2DiagramNode.ScenarioState.COMPLETED)));
        return result;
    }

    private static String performerOf(ScenarioOccurrence occurrence) {
        if (occurrence.node().actorId() != null) return occurrence.node().actorId();
        if (occurrence.node().objectId() != null) return occurrence.node().objectId();
        return occurrence.node().processInstanceId();
    }

    private static int scenarioLaneHeight(String actorId, String actorType, Bpmn2ScenarioSnapshot snapshot) {
        if ("Organizer".equals(actorType)) return 300;
        if ("Secretary".equals(actorType)) return 170;
        if ("Initiator".equals(actorType)) return 100;
        if ("Participant".equals(actorType)) return 90;
        long occurrences = scenarioOccurrences(snapshot).stream()
                .filter(o -> actorId.equals(performerOf(o)))
                .count();
        return (int) Math.max(90, 35 + Math.max(1, occurrences) * 58);
    }

    private static Map<String, Integer> participantRows(Bpmn2ScenarioSnapshot snapshot) {
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

    private static int occurrenceY(Bpmn2Lane lane, ScenarioOccurrence occurrence,
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

    private static boolean addMeetingExecutionEdges(List<Bpmn2Edge> edges, Bpmn2Node start, Bpmn2Node finish,
                                                    Map<String, Bpmn2Node> byElementAndTarget,
                                                    Bpmn2ScenarioSnapshot snapshot) {
        Bpmn2Node decide = firstByElement(byElementAndTarget, "decideMeetingDetails");
        Bpmn2Node choose = firstByElement(byElementAndTarget, "chooseTimeAndDate");
        if (decide == null || choose == null) return false;

        edges.add(new Bpmn2Edge(start.id, decide.id, Bpmn2EdgeKind.SEQUENCE, null));
        List<String> participants = participantRows(snapshot).keySet().stream().toList();
        for (String participant : participants) {
            Bpmn2Node check = byElementAndTarget.get("checkCalendar::" + participant);
            Bpmn2Node request = byElementAndTarget.get("requestSecretaryCall::" + participant);
            Bpmn2Node collect = byElementAndTarget.get("collectConstraintsByPhone::" + participant);
            Bpmn2Node announce = byElementAndTarget.get("announceMeeting::" + participant);
            Bpmn2Node participate = byElementAndTarget.get("participate::" + participant);

            if (check != null) edges.add(new Bpmn2Edge(decide.id, check.id, Bpmn2EdgeKind.SEQUENCE, null));
            Bpmn2Node readyForChoose = check;
            if (check != null && request != null) {
                edges.add(new Bpmn2Edge(check.id, request.id, Bpmn2EdgeKind.SEQUENCE, null));
                readyForChoose = request;
            }
            if (request != null && collect != null) {
                edges.add(new Bpmn2Edge(request.id, collect.id, Bpmn2EdgeKind.SEQUENCE, "alex calls " + participant));
                readyForChoose = collect;
            }
            if (readyForChoose != null) edges.add(new Bpmn2Edge(readyForChoose.id, choose.id, Bpmn2EdgeKind.SEQUENCE, null));
            if (announce != null) edges.add(new Bpmn2Edge(choose.id, announce.id, Bpmn2EdgeKind.SEQUENCE, null));
            if (announce != null && participate != null) {
                edges.add(new Bpmn2Edge(announce.id, participate.id, Bpmn2EdgeKind.SEQUENCE, null));
            }
            if (participate != null) edges.add(new Bpmn2Edge(participate.id, finish.id, Bpmn2EdgeKind.SEQUENCE, null));
        }
        return true;
    }

    private static Bpmn2Node firstByElement(Map<String, Bpmn2Node> nodes, String elementId) {
        for (Map.Entry<String, Bpmn2Node> entry : nodes.entrySet()) {
            if (entry.getKey().startsWith(elementId + "::")) return entry.getValue();
        }
        return null;
    }

    private static void addFallbackExecutionEdges(List<Bpmn2Edge> edges, Bpmn2Node start, Bpmn2Node finish,
                                                  List<ScenarioOccurrence> occurrences,
                                                  Map<String, Bpmn2Node> byOccurrenceKey) {
        Bpmn2Node previous = start;
        for (ScenarioOccurrence occurrence : occurrences) {
            Bpmn2Node node = byOccurrenceKey.get(occurrenceKey(occurrence));
            if (node == null) continue;
            edges.add(new Bpmn2Edge(previous.id, node.id, Bpmn2EdgeKind.SEQUENCE, null));
            previous = node;
        }
        edges.add(new Bpmn2Edge(previous.id, finish.id, Bpmn2EdgeKind.SEQUENCE, null));
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

    private static void placeOccurrences(List<NodeOccurrence> occurrences, String stateLabel, Bpmn2Model model,
            Map<String, String> elementLane, Map<String, Bpmn2Lane> objectLanes, Map<String, Integer> ownerX,
            Map<String, Bpmn2Node> nodeMap, Map<String, Bpmn2Node> occurrenceByOwnerAndElement,
            Bpmn2DiagramNode.ScenarioState state) {
        int i = 0;
        for (NodeOccurrence occurrence : occurrences) {
            String owner = occurrence.objectId() != null ? occurrence.objectId() : occurrence.actorId();
            if (owner == null || !objectLanes.containsKey(owner)) continue;
            FlowElement element = model.findFlowElement(occurrence.elementId()).orElse(null);
            if (element == null) continue;

            Bpmn2Lane lane = objectLanes.get(owner);
            Bpmn2Node node = toNode(element);
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

    private static Bpmn2Node placeSyntheticNode(String owner, String elementId, Bpmn2Model model,
            Map<String, Bpmn2Lane> objectLanes, Map<String, Integer> ownerX, Map<String, Bpmn2Node> nodeMap) {
        FlowElement element = model.findFlowElement(elementId).orElse(null);
        if (element == null) return null;
        Bpmn2Lane lane = objectLanes.get(owner);
        if (lane == null) return null;

        Bpmn2Node node = toNode(element);
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

    private static Bpmn2Node objectStateNode(String actorId, Bpmn2ScenarioSnapshot snapshot) {
        String stateText = snapshot.values().entrySet().stream()
                .filter(e -> e.getKey().startsWith(actorId + "."))
                .map(e -> e.getKey().substring(actorId.length() + 1) + "=" + valueText(e.getValue()))
                .findFirst()
                .orElse("object");
        return sized(new Bpmn2Node("scenario:object:" + actorId, stateText, Bpmn2NodeKind.TASK,
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

    private static Bpmn2Layout buildPools(Bpmn2Model model, List<Process> processes, boolean includeMessageFlows) {
        Map<String, Bpmn2Node> nodeMap = new LinkedHashMap<>();
        List<Bpmn2Pool>        pools   = new ArrayList<>();
        if (model == null || processes.isEmpty()) return emptyLayout();

        int py = MARGIN;
        int maxW = 0;

        for (Process process : processes) {
            Bpmn2Pool vp = new Bpmn2Pool(process.id(), label(process.name(), process.id()));
            vp.x = MARGIN;
            vp.y = py;

            int contentX = MARGIN + POOL_HDR;
            int laneY    = py + POOL_PAD;
            Set<String> laneElementIds = new HashSet<>();

            for (Lane lane : process.lanes()) {
                Bpmn2Lane vl = new Bpmn2Lane(lane.id(), label(lane.name(), lane.id()));
                vl.x = contentX;
                vl.y = laneY;

                int ex = contentX + LANE_HDR + ELEM_PAD;
                int maxNodeH = 0;
                for (FlowElement fe : lane.flowElements()) {
                    laneElementIds.add(fe.id());
                    Bpmn2Node vn = toNode(fe);
                    vn.x = ex;
                    vl.elements.add(vn);
                    nodeMap.put(vn.id, vn);
                    ex += vn.w + ELEM_PAD;
                    if (vn.h > maxNodeH) maxNodeH = vn.h;
                }
                vl.h = maxNodeH > 0 ? maxNodeH + ELEM_PAD * 2 : LANE_MIN_H;
                for (Bpmn2Node vn : vl.elements) vn.y = laneY + (vl.h - vn.h) / 2;
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
                    Bpmn2Node vn = toNode(fe);
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
        for (Bpmn2Pool vp : pools) {
            vp.w = maxW - MARGIN;
            for (Bpmn2Lane vl : vp.lanes) vl.w = vp.w - POOL_HDR;
        }

        List<Bpmn2Edge> edges = new ArrayList<>();
        for (Process process : processes)
            for (SequenceFlow sf : process.sequenceFlows())
                edges.add(new Bpmn2Edge(sf.source().id(), sf.target().id(), Bpmn2EdgeKind.SEQUENCE, sf.label()));

        if (includeMessageFlows)
            for (MessageFlow mf : model.messageFlows())
                edges.add(new Bpmn2Edge(mf.source().id(), mf.target().id(), Bpmn2EdgeKind.MESSAGE, messageLabel(mf)));

        int width  = Math.max(maxW + MARGIN, 900);
        int height = Math.max(py + MARGIN, 600);
        return new Bpmn2Layout(pools, nodeMap, edges, width, height);
    }

    private static Bpmn2Node toNode(FlowElement fe) {
        return switch (fe) {
            case StartEvent se -> sized(new Bpmn2Node(se.id(), se.id(), Bpmn2NodeKind.START_EVT,
                    se.trigger(), true, GatewayKind.XOR), EVT_D, EVT_D);
            case EndEvent ee -> sized(new Bpmn2Node(ee.id(), ee.id(), Bpmn2NodeKind.END_EVT,
                    ee.trigger(), true, GatewayKind.XOR), EVT_D, EVT_D);
            case IntermediateEvent ie -> sized(new Bpmn2Node(ie.id(), ie.id(), Bpmn2NodeKind.INT_EVT,
                    ie.trigger(), ie.direction() == EventDirection.CATCHING, GatewayKind.XOR), EVT_D, EVT_D);
            case Task t -> sized(new Bpmn2Node(t.id(), label(t.name(), t.id()), Bpmn2NodeKind.TASK,
                    EventTrigger.NONE, true, GatewayKind.XOR), TASK_W, TASK_H);
            case CallActivity ca -> sized(new Bpmn2Node(ca.id(), ca.id(), Bpmn2NodeKind.CALL_ACTIVITY,
                    EventTrigger.NONE, true, GatewayKind.XOR), CALL_W, CALL_H);
            case SubProcess sp -> sized(new Bpmn2Node(sp.id(), label(sp.name(), sp.id()), Bpmn2NodeKind.SUBPROCESS,
                    EventTrigger.NONE, true, GatewayKind.XOR), SUB_W, SUB_H);
            case Gateway gw -> sized(new Bpmn2Node(gw.id(), gw.id(), Bpmn2NodeKind.GATEWAY,
                    EventTrigger.NONE, true, gw.kind()), GW_D, GW_D);
        };
    }

    private static Bpmn2Node sized(Bpmn2Node n, int w, int h) {
        n.w = w; n.h = h;
        return n;
    }

    private static String label(String name, String id) { return name != null ? name : id; }

    private static String messageLabel(MessageFlow mf) {
        if (mf.message() == null) return null;
        return mf.message().name() != null ? mf.message().name() : mf.message().id();
    }

    // ── Choreography: message flows only, 1 column per Participant (Process) ─

    public static Bpmn2ChoreoLayout buildChoreography(Bpmn2Model model) {
        if (model == null || model.messageFlows().isEmpty())
            return new Bpmn2ChoreoLayout(Map.of(), List.of(), 700, 300);

        Map<String, Bpmn2ChoreoParticipant> participants = new LinkedHashMap<>();
        List<Bpmn2ChoreoMessage> messages = new ArrayList<>();

        int y = CHOREO_MSG_TOP;
        for (MessageFlow mf : model.messageFlows()) {
            String fromProc = model.ownerProcessId(mf.source().id()).orElse(mf.source().id());
            String toProc   = model.ownerProcessId(mf.target().id()).orElse(mf.target().id());

            Bpmn2ChoreoParticipant fromP = participants.computeIfAbsent(fromProc, id -> newParticipant(model, id));
            Bpmn2ChoreoParticipant toP   = participants.computeIfAbsent(toProc,   id -> newParticipant(model, id));

            messages.add(new Bpmn2ChoreoMessage(fromP.id, toP.id, messageLabel(mf), y));
            y += CHOREO_MSG_ROW_H;
        }

        int i = 0;
        for (Bpmn2ChoreoParticipant p : participants.values()) {
            p.x = CHOREO_COL_MARGIN + i * CHOREO_COL_W;
            i++;
        }

        int width  = Math.max(CHOREO_COL_MARGIN * 2 + participants.size() * CHOREO_COL_W, 500);
        int height = y + CHOREO_BOTTOM_PAD;
        return new Bpmn2ChoreoLayout(participants, messages, width, height);
    }

    private static Bpmn2ChoreoParticipant newParticipant(Bpmn2Model model, String processId) {
        String lbl = model.findProcess(processId)
                .map(p -> label(p.name(), p.id()))
                .orElse(processId);
        return new Bpmn2ChoreoParticipant(processId, lbl, 0);
    }
}
