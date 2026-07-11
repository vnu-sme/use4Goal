package org.vnu.sme.goal.bpmn2.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import org.tzi.use.gui.util.PersistHelper;
import org.tzi.use.gui.views.diagrams.DiagramGraph;
import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.gui.views.diagrams.elements.EdgeProperty;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.event.ActionLoadLayout;
import org.tzi.use.gui.views.diagrams.event.ActionSaveLayout;
import org.tzi.use.gui.views.diagrams.event.DiagramInputHandling;
import org.tzi.use.gui.views.diagrams.util.Direction;
import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;
import org.vnu.sme.goal.bpmn2.mm.EventTrigger;
import org.vnu.sme.goal.bpmn2.mm.GatewayKind;
import org.vnu.sme.goal.bpmn2scenario.mm.Bpmn2ScenarioSnapshot;
import org.vnu.sme.goal.bpmn2scenario.mm.NodeOccurrence;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public final class Bpmn2Diagram extends DiagramView {
    private final Map<String, Bpmn2DiagramNode> nodeMap = new LinkedHashMap<>();
    private final Map<String, List<String>> poolLanes = new LinkedHashMap<>();
    private final Map<String, List<String>> poolNodes = new LinkedHashMap<>();
    private final Map<String, List<String>> laneNodes = new LinkedHashMap<>();
    private final DiagramInputHandling inputHandling;
    private Bpmn2Model model;
    private Bpmn2ScenarioSnapshot scenarioSnapshot;
    private Path sourceFile;
    private Path scenarioFile;
    private String scenarioSource;
    private Bpmn2ViewMode mode = Bpmn2ViewMode.COLLABORATION;
    private String processId;
    private String switchActionLabel;
    private Runnable switchAction;

    public Bpmn2Diagram(PrintWriter log) {
        super(new Bpmn2DiagramOptions(), log);
        inputHandling = new Bpmn2InputHandling();
        addMouseListener(inputHandling);
        addKeyListener(inputHandling);
        setFocusable(true);
        fActionSaveLayout = new ActionSaveLayout("USE BPMN layout", "dlt", this);
        fActionLoadLayout = new ActionLoadLayout("USE BPMN layout", "dlt", this);
        getOptions().setDoAntiAliasing(true);
    }

    @Override
    public Bpmn2DiagramOptions getOptions() {
        return (Bpmn2DiagramOptions) fOpt;
    }

    public void setSwitchAction(String label, Runnable action) {
        this.switchActionLabel = label;
        this.switchAction = action;
    }

    public void setSourceFile(Path sourceFile) {
        this.sourceFile = sourceFile;
        if (sourceFile != null) {
            fOpt = new Bpmn2DiagramOptions(sourceFile);
            fActionSaveLayout = new ActionSaveLayout("USE BPMN layout", "dlt", this);
            fActionLoadLayout = new ActionLoadLayout("USE BPMN layout", "dlt", this);
            getOptions().setDoAntiAliasing(true);
        }
    }

    public void setModel(Bpmn2Model model) {
        this.model = model;
        this.processId = firstProcessId(model);
        rebuild();
    }

    public void setScenarioSnapshot(Bpmn2ScenarioSnapshot snapshot) {
        this.scenarioSnapshot = snapshot;
        if (snapshot != null
                && mode != Bpmn2ViewMode.SCENARIO_EXECUTION
                && mode != Bpmn2ViewMode.SCENARIO_AGGREGATE) {
            mode = Bpmn2ViewMode.SCENARIO_EXECUTION;
        }
        rebuild();
    }

    public void setScenarioSource(Path scenarioFile, String scenarioSource) {
        this.scenarioFile = scenarioFile;
        this.scenarioSource = scenarioSource;
    }

    private void setMode(Bpmn2ViewMode mode) {
        this.mode = mode;
        rebuild();
    }

    private void setProcessId(String processId) {
        this.processId = processId;
        this.mode = Bpmn2ViewMode.PROCESS;
        rebuild();
    }

    private void rebuild() {
        nodeMap.clear();
        poolLanes.clear();
        poolNodes.clear();
        laneNodes.clear();
        fGraph = new DiagramGraph();
        if (model == null) {
            repaint();
            return;
        }
        Font font = Font.getFont("use.gui.view.objectdiagram", getFont());
        if (mode == Bpmn2ViewMode.CHOREOGRAPHY) {
            rebuildChoreography(font);
        } else {
            Bpmn2Layout layout = mode == Bpmn2ViewMode.SCENARIO_EXECUTION && scenarioSnapshot != null
                    ? Bpmn2LayoutBuilder.buildScenario(model, scenarioSnapshot)
                    : mode == Bpmn2ViewMode.SCENARIO_AGGREGATE && scenarioSnapshot != null
                    ? Bpmn2LayoutBuilder.buildScenarioAggregate(model, scenarioSnapshot, processId)
                    : mode == Bpmn2ViewMode.PROCESS
                    ? Bpmn2LayoutBuilder.buildProcess(model, processId)
                    : Bpmn2LayoutBuilder.buildCollaboration(model);
            rebuildFlowLayout(layout, font);
        }
        initialize();
        invalidateContent(true);
    }

    private void rebuildFlowLayout(Bpmn2Layout layout, Font font) {
        for (Bpmn2Pool pool : layout.pools) {
            String poolId = "pool:" + pool.id;
            List<String> laneIds = new ArrayList<>();
            List<String> nodeIds = new ArrayList<>();
            addContainer(poolId, pool.label, Bpmn2DiagramNode.ContainerKind.POOL, pool.x, pool.y, pool.w, pool.h, font);
            for (Bpmn2Lane lane : pool.lanes) {
                String laneId = "lane:" + lane.id;
                laneIds.add(laneId);
                List<String> laneNodeIds = lane.elements.stream().map(n -> n.id).toList();
                nodeIds.addAll(laneNodeIds);
                laneNodes.put(laneId, laneNodeIds);
                addContainer(laneId, lane.label, Bpmn2DiagramNode.ContainerKind.LANE, lane.x, lane.y, lane.w, lane.h, font);
            }
            nodeIds.addAll(pool.elements.stream().map(n -> n.id).toList());
            poolLanes.put(poolId, laneIds);
            poolNodes.put(poolId, nodeIds);
        }
        for (Bpmn2Node item : layout.nodes.values()) {
            Bpmn2DiagramNode node = new Bpmn2DiagramNode(item, getOptions(), font);
            if (item.scenarioState == Bpmn2DiagramNode.ScenarioState.NONE) {
                applyScenarioState(node, item.id);
            }
            node.setPosition(item.x, item.y);
            node.setExactBounds(item.w, item.h);
            nodeMap.put(item.id, node);
            fGraph.add(node);
        }
        for (Bpmn2Edge item : layout.edges) {
            PlaceableNode source = nodeMap.get(item.fromId());
            PlaceableNode target = nodeMap.get(item.toId());
            if (source != null && target != null) {
                fGraph.addEdge(new Bpmn2DiagramEdge(item, source, target, getOptions()));
            }
        }
    }

    private void rebuildChoreography(Font font) {
        Bpmn2ChoreoLayout layout = Bpmn2LayoutBuilder.buildChoreography(model);
        int top = 24;
        int bottom = Math.max(120, layout.height - 48);
        for (Bpmn2ChoreoParticipant p : layout.participants.values()) {
            addContainer("participant:" + p.id, p.label, Bpmn2DiagramNode.ContainerKind.PARTICIPANT,
                    p.x - 64, top, 128, bottom, font);
        }
        int i = 0;
        for (Bpmn2ChoreoMessage message : layout.messages) {
            Bpmn2ChoreoParticipant from = layout.participants.get(message.fromParticipantId());
            Bpmn2ChoreoParticipant to = layout.participants.get(message.toParticipantId());
            if (from == null || to == null) continue;
            int minX = Math.min(from.x, to.x);
            int maxX = Math.max(from.x, to.x);
            Bpmn2Node task = new Bpmn2Node("choreo:" + i, message.label(), Bpmn2NodeKind.CHOREOGRAPHY,
                    EventTrigger.NONE, true, GatewayKind.XOR);
            task.w = Math.max(120, Math.min(210, maxX - minX - 24));
            task.h = 52;
            task.x = (minX + maxX) / 2 - task.w / 2;
            task.y = message.y() - task.h / 2;
            Bpmn2DiagramNode taskNode = new Bpmn2DiagramNode(task, getOptions(), font);
            applyScenarioState(taskNode, task.id);
            taskNode.setPosition(task.x, task.y);
            taskNode.setExactBounds(task.w, task.h);
            nodeMap.put(task.id, taskNode);
            fGraph.add(taskNode);

            String sourceId = "participant:" + message.fromParticipantId();
            String targetId = "participant:" + message.toParticipantId();
            PlaceableNode source = nodeMap.get(sourceId);
            PlaceableNode target = nodeMap.get(targetId);
            if (source != null) {
                fGraph.addEdge(new Bpmn2DiagramEdge(new Bpmn2Edge(sourceId, task.id, Bpmn2EdgeKind.MESSAGE, null),
                        source, taskNode, getOptions()));
            }
            if (target != null) {
                fGraph.addEdge(new Bpmn2DiagramEdge(new Bpmn2Edge(task.id, targetId, Bpmn2EdgeKind.MESSAGE, null),
                        taskNode, target, getOptions()));
            }
            i++;
        }
    }

    private void applyScenarioState(Bpmn2DiagramNode node, String elementId) {
        if (scenarioSnapshot == null) return;
        List<String> active = occurrencesFor(scenarioSnapshot.active(), elementId);
        if (!active.isEmpty()) {
            node.setScenarioState(Bpmn2DiagramNode.ScenarioState.ACTIVE, active);
            return;
        }
        List<String> completed = occurrencesFor(scenarioSnapshot.completed(), elementId);
        if (!completed.isEmpty()) {
            node.setScenarioState(Bpmn2DiagramNode.ScenarioState.COMPLETED, completed);
        }
    }

    private static List<String> occurrencesFor(List<NodeOccurrence> occurrences, String elementId) {
        List<String> result = new ArrayList<>();
        for (NodeOccurrence occurrence : occurrences) {
            if (occurrence.elementId().equals(elementId)) {
                result.add(occurrence.display());
            }
        }
        return result;
    }

    private void addContainer(String id, String label, Bpmn2DiagramNode.ContainerKind kind,
                              int x, int y, int w, int h, Font font) {
        Bpmn2DiagramNode node = new Bpmn2DiagramNode(id, label, kind, w, h, getOptions(), font);
        node.setPosition(x, y);
        node.setExactBounds(w, h);
        nodeMap.put(id, node);
        fGraph.add(node);
    }

    @Override
    public void drawDiagram(Graphics g) {
        synchronized (fGraph) {
            Graphics2D g2d = (Graphics2D) g;
            if (fOpt.isDoAntiAliasing()) {
                Map<?, ?> hints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
                if (hints == null) {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                } else {
                    g2d.setRenderingHints(hints);
                }
            }

            Dimension d = getSize();
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, d.width, d.height);

            fGraph.initialize();
            for (EdgeBase e : fGraph.getInvalidatedEdges()) e.calculateSize(g2d);
            for (PlaceableNode n : fGraph.getInvalidatedNodes()) {
                int minHeight = 0;
                for (EdgeBase e : fGraph.allEdges(n)) {
                    int heightHint = 0;
                    if (e.source().equals(n)) heightHint += e.getSourceHeightHint();
                    if (!e.isReflexive() && e.target().equals(n)) heightHint += e.getTargetHeightHint();
                    if (heightHint > 0) minHeight += heightHint + 4;
                }
                n.setRequiredHeight("BY_EDGES", minHeight + 4);
                n.calculateSize(g2d);
            }
            for (EdgeBase e : fGraph.getInvalidatedEdges()) e.updatePosition();
            fGraph.clearInvalidated();

            double maxX = 0;
            double maxY = 0;

            for (Bpmn2DiagramNode node : nodeMap.values()) {
                if (node.containerKind() == Bpmn2DiagramNode.ContainerKind.POOL
                        || node.containerKind() == Bpmn2DiagramNode.ContainerKind.PARTICIPANT) {
                    node.draw(g2d);
                    maxX = Math.max(maxX, node.getX() + node.getWidth());
                    maxY = Math.max(maxY, node.getY() + node.getHeight());
                }
            }
            for (Bpmn2DiagramNode node : nodeMap.values()) {
                if (node.containerKind() == Bpmn2DiagramNode.ContainerKind.LANE) {
                    node.draw(g2d);
                    maxX = Math.max(maxX, node.getX() + node.getWidth());
                    maxY = Math.max(maxY, node.getY() + node.getHeight());
                }
            }

            Iterator<EdgeBase> edgeIterator = fGraph.edgeIterator();
            while (edgeIterator.hasNext()) {
                edgeIterator.next().draw(g2d);
            }

            for (Bpmn2DiagramNode node : nodeMap.values()) {
                if (!node.isContainer()) {
                    node.draw(g2d);
                    maxX = Math.max(maxX, node.getX() + node.getWidth());
                    maxY = Math.max(maxY, node.getY() + node.getHeight());
                }
            }

            edgeIterator = fGraph.edgeIterator();
            while (edgeIterator.hasNext()) {
                EdgeBase e = edgeIterator.next();
                PlaceableNode eastNode = e.getWayPointMostTo(Direction.EAST);
                maxX = Math.max(maxX, eastNode.getX() + eastNode.getWidth());
                PlaceableNode southNode = e.getWayPointMostTo(Direction.SOUTH);
                maxY = Math.max(maxY, southNode.getY() + southNode.getHeight());
                e.drawProperties(g2d);
                for (EdgeProperty ep : e.getProperties()) {
                    if (!ep.isVisible()) continue;
                    maxX = Math.max(maxX, ep.getX() + ep.getWidth());
                    maxY = Math.max(maxY, ep.getY() + ep.getHeight());
                }
            }

            Dimension newDimension = new Dimension((int) maxX + 5, (int) maxY + 5);
            if (!newDimension.equals(getPreferredSize())) {
                setPreferredSize(newDimension);
                revalidate();
            }
        }
    }

    private void moveOwnedNodes(String containerId, double dx, double dy, Set<PlaceableNode> selected) {
        Bpmn2DiagramNode container = nodeMap.get(containerId);
        if (container == null || !container.isContainer()) return;

        if (container.containerKind() == Bpmn2DiagramNode.ContainerKind.POOL) {
            for (String laneId : poolLanes.getOrDefault(containerId, List.of())) {
                moveNode(laneId, dx, dy, selected);
            }
            for (String nodeId : poolNodes.getOrDefault(containerId, List.of())) {
                moveNode(nodeId, dx, dy, selected);
            }
        } else if (container.containerKind() == Bpmn2DiagramNode.ContainerKind.LANE) {
            for (String nodeId : laneNodes.getOrDefault(containerId, List.of())) {
                moveNode(nodeId, dx, dy, selected);
            }
        }
    }

    private void moveNode(String id, double dx, double dy, Set<PlaceableNode> selected) {
        Bpmn2DiagramNode node = nodeMap.get(id);
        if (node == null || selected.contains(node)) return;
        node.setDraggedPosition(dx, dy);
        invalidateNode(node);
    }

    private void updateContainerBounds() {
        for (String laneId : laneNodes.keySet()) {
            expandContainerToFit(laneId, laneNodes.get(laneId), 52, 18);
        }
        for (String poolId : poolLanes.keySet()) {
            List<String> children = new ArrayList<>();
            children.addAll(poolLanes.getOrDefault(poolId, List.of()));
            children.addAll(poolNodes.getOrDefault(poolId, List.of()).stream()
                    .filter(id -> nodeMap.get(id) != null && !isInAnyLane(id))
                    .toList());
            expandContainerToFit(poolId, children, 26, 16);
        }
    }

    private boolean isInAnyLane(String nodeId) {
        for (List<String> ids : laneNodes.values()) {
            if (ids.contains(nodeId)) return true;
        }
        return false;
    }

    private void expandContainerToFit(String containerId, List<String> childIds, double headerWidth, double pad) {
        Bpmn2DiagramNode container = nodeMap.get(containerId);
        if (container == null || childIds == null || childIds.isEmpty()) return;

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (String childId : childIds) {
            Bpmn2DiagramNode child = nodeMap.get(childId);
            if (child == null) continue;
            minX = Math.min(minX, child.getX());
            minY = Math.min(minY, child.getY());
            maxX = Math.max(maxX, child.getX() + child.getWidth());
            maxY = Math.max(maxY, child.getY() + child.getHeight());
        }
        if (!Double.isFinite(minX)) return;

        double newX = Math.min(container.getX(), minX - headerWidth - pad);
        double newY = Math.min(container.getY(), minY - pad);
        double newW = Math.max(container.getWidth(), maxX + pad - newX);
        double newH = Math.max(container.getHeight(), maxY + pad - newY);
        container.setPosition(newX, newY);
        container.setExactBounds(newW, newH);
        invalidateNode(container);
    }

    private void reflowLaneStacks(Set<PlaceableNode> selected) {
        Set<String> touched = new LinkedHashSet<>();
        for (PlaceableNode node : selected) {
            if (node instanceof Bpmn2DiagramNode bpmnNode
                    && bpmnNode.containerKind() == Bpmn2DiagramNode.ContainerKind.LANE) {
                touched.add(bpmnNode.getId());
            }
        }
        if (touched.isEmpty()) return;
        for (String laneId : touched) {
            for (String poolId : poolLanes.keySet()) {
                if (poolLanes.getOrDefault(poolId, List.of()).contains(laneId)) {
                    reflowLaneStack(poolId, laneId);
                }
            }
        }
    }

    private void reflowLaneStack(String poolId, String anchorLaneId) {
        List<String> laneIds = poolLanes.getOrDefault(poolId, List.of());
        int anchorIndex = laneIds.indexOf(anchorLaneId);
        if (anchorIndex < 0) return;

        Bpmn2DiagramNode pool = nodeMap.get(poolId);
        Bpmn2DiagramNode anchor = nodeMap.get(anchorLaneId);
        if (pool == null || anchor == null) return;

        double topBound = pool.getY() + 14;
        if (anchor.getY() < topBound) {
            moveContainerWithOwned(anchorLaneId, 0, topBound - anchor.getY());
        }

        for (int i = anchorIndex - 1; i >= 0; i--) {
            Bpmn2DiagramNode current = nodeMap.get(laneIds.get(i));
            Bpmn2DiagramNode below = nodeMap.get(laneIds.get(i + 1));
            if (current == null || below == null) continue;
            double overlap = current.getY() + current.getHeight() - below.getY();
            if (overlap > 0) moveContainerWithOwned(current.getId(), 0, -overlap);
        }

        Bpmn2DiagramNode first = nodeMap.get(laneIds.get(0));
        if (first != null && first.getY() < topBound) {
            double dy = topBound - first.getY();
            for (int i = 0; i <= anchorIndex; i++) {
                moveContainerWithOwned(laneIds.get(i), 0, dy);
            }
        }

        for (int i = Math.max(1, anchorIndex + 1); i < laneIds.size(); i++) {
            Bpmn2DiagramNode previous = nodeMap.get(laneIds.get(i - 1));
            Bpmn2DiagramNode current = nodeMap.get(laneIds.get(i));
            if (previous == null || current == null) continue;
            double minY = previous.getY() + previous.getHeight();
            if (current.getY() < minY) moveContainerWithOwned(current.getId(), 0, minY - current.getY());
        }
    }

    private void moveContainerWithOwned(String containerId, double dx, double dy) {
        Bpmn2DiagramNode container = nodeMap.get(containerId);
        if (container == null) return;
        container.setDraggedPosition(dx, dy);
        invalidateNode(container);
        if (container.containerKind() == Bpmn2DiagramNode.ContainerKind.LANE) {
            for (String nodeId : laneNodes.getOrDefault(containerId, List.of())) {
                moveNode(nodeId, dx, dy, Set.of());
            }
        } else if (container.containerKind() == Bpmn2DiagramNode.ContainerKind.POOL) {
            for (String laneId : poolLanes.getOrDefault(containerId, List.of())) {
                moveContainerWithOwned(laneId, dx, dy);
            }
            for (String nodeId : poolNodes.getOrDefault(containerId, List.of())) {
                if (!isInAnyLane(nodeId)) moveNode(nodeId, dx, dy, Set.of());
            }
        }
    }

    private final class Bpmn2InputHandling extends DiagramInputHandling {
        Bpmn2InputHandling() {
            super(fNodeSelection, fEdgeSelection, Bpmn2Diagram.this);
        }

        @Override
        protected void moveSelectedObjects(int dx, int dy) {
            Set<PlaceableNode> selected = new LinkedHashSet<>();
            for (PlaceableNode node : fNodeSelection) selected.add(node);

            super.moveSelectedObjects(dx, dy);

            for (PlaceableNode node : selected) {
                if (node instanceof Bpmn2DiagramNode bpmnNode && bpmnNode.isContainer()) {
                    moveOwnedNodes(bpmnNode.getId(), dx, dy, selected);
                }
            }
            reflowLaneStacks(selected);
            updateContainerBounds();
            invalidateContent(true);
        }

        @Override
        protected void resizeSelectedObjects(java.awt.Point p) {
            Set<PlaceableNode> selected = new LinkedHashSet<>();
            for (PlaceableNode node : fNodeSelection) selected.add(node);
            super.resizeSelectedObjects(p);
            reflowLaneStacks(selected);
            updateContainerBounds();
            invalidateContent(true);
        }
    }

    @Override
    protected PopupMenuInfo unionOfPopUpMenu() {
        JPopupMenu menu = new JPopupMenu();
        PopupMenuInfo info = new PopupMenuInfo(menu);
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem process = new JRadioButtonMenuItem("Process", mode == Bpmn2ViewMode.PROCESS);
        process.addActionListener(e -> setMode(Bpmn2ViewMode.PROCESS));
        JRadioButtonMenuItem collaboration = new JRadioButtonMenuItem("Collaboration", mode == Bpmn2ViewMode.COLLABORATION);
        collaboration.addActionListener(e -> setMode(Bpmn2ViewMode.COLLABORATION));
        JRadioButtonMenuItem choreography = new JRadioButtonMenuItem("Choreography", mode == Bpmn2ViewMode.CHOREOGRAPHY);
        choreography.addActionListener(e -> setMode(Bpmn2ViewMode.CHOREOGRAPHY));
        group.add(process);
        group.add(collaboration);
        group.add(choreography);
        menu.add(process);
        menu.add(collaboration);
        menu.add(choreography);
        if (scenarioSnapshot != null) {
            JRadioButtonMenuItem scenarioExecution = new JRadioButtonMenuItem("Scenario execution",
                    mode == Bpmn2ViewMode.SCENARIO_EXECUTION);
            scenarioExecution.addActionListener(e -> setMode(Bpmn2ViewMode.SCENARIO_EXECUTION));
            JRadioButtonMenuItem scenarioAggregate = new JRadioButtonMenuItem("Scenario aggregate",
                    mode == Bpmn2ViewMode.SCENARIO_AGGREGATE);
            scenarioAggregate.addActionListener(e -> setMode(Bpmn2ViewMode.SCENARIO_AGGREGATE));
            group.add(scenarioExecution);
            group.add(scenarioAggregate);
            menu.add(scenarioExecution);
            menu.add(scenarioAggregate);
        }
        if (model != null && !model.processes().isEmpty()) {
            JMenu processes = new JMenu("Process id");
            for (org.vnu.sme.goal.bpmn2.mm.Process p : model.processes()) {
                JMenuItem item = new JMenuItem(p.name() == null ? p.id() : p.name());
                item.addActionListener(e -> setProcessId(p.id()));
                processes.add(item);
            }
            menu.add(processes);
        }
        menu.addSeparator();
        menu.add(getMenuItemCommentNode(info));
        menu.addSeparator();
        menu.add(getMenuAlign());
        menu.add(getMenuItemAntiAliasing());
        menu.add(getMenuItemShowGrid());
        menu.add(getMenuItemGrayscale());
        if (scenarioSource != null && !scenarioSource.isBlank()) {
            menu.addSeparator();
            menu.add(new AbstractAction("Show scenario source") {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    showScenarioSource();
                }
            });
        }
        if (switchAction != null && switchActionLabel != null) {
            menu.addSeparator();
            menu.add(new AbstractAction(switchActionLabel) {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    switchAction.run();
                }
            });
        }
        addLayoutMenuItems(menu);
        return info;
    }

    private void showScenarioSource() {
        JTextArea area = new JTextArea(scenarioSource, 28, 92);
        area.setEditable(false);
        area.setCaretPosition(0);
        String title = scenarioFile == null ? "BPMN scenario" : "BPMN scenario - " + scenarioFile.getFileName();
        JOptionPane.showMessageDialog(this, new JScrollPane(area), title, JOptionPane.PLAIN_MESSAGE);
    }

    @Override public Set<? extends PlaceableNode> getHiddenNodes() { return Collections.emptySet(); }

    @Override
    public void storePlacementInfos(PersistHelper helper, Element rootElement) {
        for (Bpmn2DiagramNode node : nodeMap.values()) {
            node.storePlacementInfo(helper, rootElement, false);
        }
        for (EdgeBase edge : fGraph.getEdges()) {
            edge.storePlacementInfo(helper, rootElement, false);
        }
    }

    @Override
    public void restorePlacementInfos(PersistHelper helper, int version) {
        restoreNodes(helper, version);
        restoreEdges(helper, version);
        invalidateContent(true);
    }

    private void restoreNodes(PersistHelper helper, int version) {
        helper.getNav().push();
        AutoPilot ap = new AutoPilot(helper.getNav());
        try {
            ap.selectXPath("./node");
            while (ap.evalXPath() != -1) {
                String name = helper.getElementStringValue("name");
                Bpmn2DiagramNode node = nodeMap.get(name);
                if (node != null) {
                    node.restorePlacementInfo(helper, version);
                    invalidateNode(node);
                }
            }
        } catch (XPathParseException | XPathEvalException | NavException ex) {
            fLog.append(ex.getMessage()).append('\n');
        } finally {
            ap.resetXPath();
            helper.getNav().pop();
        }
    }

    private void restoreEdges(PersistHelper helper, int version) {
        Map<String, EdgeBase> edges = new LinkedHashMap<>();
        for (EdgeBase edge : fGraph.getEdges()) {
            edges.put(edge.getId(), edge);
        }

        helper.getNav().push();
        AutoPilot ap = new AutoPilot(helper.getNav());
        try {
            ap.selectXPath("./edge");
            while (ap.evalXPath() != -1) {
                String source = helper.getElementStringValue("source");
                String target = helper.getElementStringValue("target");
                String name = helper.getElementStringValue("name");
                EdgeBase edge = edges.get("bpmn::" + name.toUpperCase(Locale.ROOT) + "::" + source + "::" + target);
                if (edge != null) {
                    edge.restorePlacementInfo(helper, version);
                }
            }
        } catch (XPathParseException | XPathEvalException | NavException ex) {
            fLog.append(ex.getMessage()).append('\n');
        } finally {
            ap.resetXPath();
            helper.getNav().pop();
        }
    }

    @Override public void showAll() {}

    @Override public void hideAll() {}

    @Override
    public DiagramData getVisibleData() {
        return new DiagramData() {
            @Override public Set<PlaceableNode> getNodes() { return new java.util.LinkedHashSet<>(fGraph.getNodes()); }
            @Override public Set<EdgeBase> getEdges() { return Collections.unmodifiableSet(fGraph.getEdges()); }
            @Override public boolean hasNodes() { return !fGraph.isEmpty(); }
        };
    }

    @Override
    public DiagramData getHiddenData() {
        return new DiagramData() {
            @Override public Set<PlaceableNode> getNodes() { return Collections.emptySet(); }
            @Override public Set<EdgeBase> getEdges() { return Collections.emptySet(); }
            @Override public boolean hasNodes() { return false; }
        };
    }

    @Override public void resetLayout() { rebuild(); }

    private static String firstProcessId(Bpmn2Model model) {
        return model == null || model.processes().isEmpty() ? null : model.processes().get(0).id();
    }

    @Override
    protected String getDefaultLayoutFileSuffix() {
        String name = sourceFile == null ? "bpmn" : sourceFile.getFileName().toString();
        return "_" + name + "_" + mode.name().toLowerCase() + "_default.dlt";
    }
}
