package org.vnu.sme.goal.acl.view;


/**
 * =============================================================================
 * MODULE: ACL diagram rendering: AclDiagram
 * =============================================================================
 * 1. PURPOSE:
 *    Adapts the ACL semantic model for diagram presentation. Semantic nodes/links and display options are input; positioned graph elements, rendered figures, or specification text are output.
 *
 * 2. CORE MAPPING / LOGIC RULES:
 *    - Keep semantic identity separate from mutable screen geometry.
 *    - Derive edge/node kinds from model relationships, not visual state.
 *    - Apply deterministic layout ordering so repeated renders remain stable.
 *    - Main operations exposed by this file: getOptions(), setSwitchAction(), setSourceFile(), setModel(), rebuild(), unionOfPopUpMenu(), actionPerformed().
 *
 * 3. PIPELINE / WORKFLOW:
 *      1. AclView receives model
 *      2. AclLayoutBuilder builds graph/layout
 *      3. AclDiagram renders nodes and edges
 *      4. selection/options trigger repaint
 * =============================================================================
 */
import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import org.tzi.use.gui.util.PersistHelper;
import org.tzi.use.gui.views.diagrams.DiagramGraph;
import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.event.ActionLoadLayout;
import org.tzi.use.gui.views.diagrams.event.ActionSaveLayout;
import org.tzi.use.gui.views.diagrams.event.DiagramInputHandling;
import org.vnu.sme.goal.acl.mm.AclModel;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public final class AclDiagram extends DiagramView {
    private final Map<String, PlaceableNode> nodeMap = new LinkedHashMap<>();
    private final DiagramInputHandling inputHandling;
    private AclModel model;
    private Path sourceFile;
    private String switchActionLabel;
    private Runnable switchAction;

    public AclDiagram(PrintWriter log) {
        super(new AclDiagramOptions(), log);
        inputHandling = new DiagramInputHandling(fNodeSelection, fEdgeSelection, this);
        addMouseListener(inputHandling);
        addKeyListener(inputHandling);
        setFocusable(true);
        fActionSaveLayout = new ActionSaveLayout("USE ACL layout", "dlt", this);
        fActionLoadLayout = new ActionLoadLayout("USE ACL layout", "dlt", this);
        getOptions().setDoAntiAliasing(true);
    }

    @Override
    public AclDiagramOptions getOptions() {
        return (AclDiagramOptions) fOpt;
    }

    public void setSwitchAction(String label, Runnable action) {
        this.switchActionLabel = label;
        this.switchAction = action;
    }

    public void setSourceFile(Path sourceFile) {
        this.sourceFile = sourceFile;
        if (sourceFile != null) {
            fOpt = new AclDiagramOptions(sourceFile);
            fActionSaveLayout = new ActionSaveLayout("USE ACL layout", "dlt", this);
            fActionLoadLayout = new ActionLoadLayout("USE ACL layout", "dlt", this);
            getOptions().setDoAntiAliasing(true);
        }
    }

    public void setModel(AclModel model) {
        this.model = model;
        rebuild();
    }/**
 * WHY: Diagram geometry is mutually dependent; rebuild computes the connected
 * positions together to avoid overlaps and unstable results from piecemeal updates.
 */


    private void rebuild() {
        nodeMap.clear();
        fGraph = new DiagramGraph();
        if (model == null) {
            repaint();
            return;
        }
        AclLayout layout = AclLayoutBuilder.build(model);
        setPreferredSize(new Dimension(layout.width, layout.height));
        Font font = Font.getFont("use.gui.view.objectdiagram", getFont());
        for (AclNode item : layout.nodes.values()) {
            PlaceableNode node;
            if (item.kind == AclNodeKind.ENTITY) {
                var entity = model.findEntity(item.label).orElseThrow();
                node = new AclEntityNode(entity, getOptions());
            } else {
                node = new AclDiagramNode(item, getOptions(), font);
            }
            node.setPosition(item.x, item.y);
            node.setExactBounds(item.w, item.h);
            nodeMap.put(item.id, node);
            fGraph.add(node);
        }
        for (AclEdge item : layout.edges) {
            PlaceableNode source = nodeMap.get(item.fromId());
            PlaceableNode target = nodeMap.get(item.toId());
            if (source != null && target != null) {
                fGraph.addEdge(new AclDiagramEdge(item, source, target, getOptions()));
            }
        }
        initialize();
        invalidateContent(true);
        revalidate();
        repaint();
    }

    @Override
    protected PopupMenuInfo unionOfPopUpMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuInfo popupInfo = new PopupMenuInfo(popupMenu);
        popupMenu.add(getMenuItemCommentNode(popupInfo));
        popupMenu.addSeparator();
        popupMenu.add(getMenuAlign());
        popupMenu.add(getMenuItemAntiAliasing());
        popupMenu.add(getMenuItemShowGrid());
        popupMenu.add(getMenuItemGrayscale());
        if (switchAction != null && switchActionLabel != null) {
            popupMenu.addSeparator();
            popupMenu.add(new AbstractAction(switchActionLabel) {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    switchAction.run();
                }
            });
        }
        addLayoutMenuItems(popupMenu);
        return popupInfo;
    }

    @Override
    public Set<? extends PlaceableNode> getHiddenNodes() {
        return Collections.emptySet();
    }

    @Override
    public void storePlacementInfos(PersistHelper helper, Element rootElement) {
        for (PlaceableNode node : nodeMap.values()) {
            node.storePlacementInfo(helper, rootElement, false);
        }
        for (EdgeBase edge : fGraph.getEdges()) {
            edge.storePlacementInfo(helper, rootElement, false);
        }
    }

    @Override
    public void restorePlacementInfos(PersistHelper helper, int version) {
        helper.getNav().push();
        AutoPilot ap = new AutoPilot(helper.getNav());
        try {
            ap.selectXPath("./node");
            while (ap.evalXPath() != -1) {
                String name = helper.getElementStringValue("name");
                PlaceableNode node = nodeMap.get(name);
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

    @Override
    public void showAll() {}

    @Override
    public void hideAll() {
        throw new UnsupportedOperationException("ACL diagram does not support hiding all nodes.");
    }

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

    @Override
    public void resetLayout() {
        setModel(model);
    }

    @Override
    protected String getDefaultLayoutFileSuffix() {
        String name = sourceFile == null ? (model == null ? "acl" : model.name()) : sourceFile.getFileName().toString();
        return "_" + name + "_default.dlt";
    }
}
