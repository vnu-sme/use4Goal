package org.vnu.sme.goal.dcr.view;

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
import org.vnu.sme.goal.dcr.mm.DcrModel;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public final class DcrDiagram extends DiagramView {
    private final Map<String, DcrEventNode> eventNodes = new LinkedHashMap<>();
    private final DiagramInputHandling inputHandling;
    private DcrModel model;
    private Path sourceFile;
    private String switchActionLabel;
    private Runnable switchAction;

    public DcrDiagram(PrintWriter log) {
        super(new DcrDiagramOptions(), log);
        inputHandling = new DiagramInputHandling(fNodeSelection, fEdgeSelection, this);
        addMouseListener(inputHandling);
        addKeyListener(inputHandling);
        setFocusable(true);
        fActionSaveLayout = new ActionSaveLayout("USE DCR graph layout", "dlt", this);
        fActionLoadLayout = new ActionLoadLayout("USE DCR graph layout", "dlt", this);
        getOptions().setDoAntiAliasing(true);
    }

    @Override
    public DcrDiagramOptions getOptions() {
        return (DcrDiagramOptions) fOpt;
    }

    public void setSwitchAction(String label, Runnable action) {
        this.switchActionLabel = label;
        this.switchAction = action;
    }

    public void setSourceFile(Path sourceFile) {
        this.sourceFile = sourceFile;
        if (sourceFile != null) {
            fOpt = new DcrDiagramOptions(sourceFile);
            fActionSaveLayout = new ActionSaveLayout("USE DCR graph layout", "dlt", this);
            fActionLoadLayout = new ActionLoadLayout("USE DCR graph layout", "dlt", this);
            getOptions().setDoAntiAliasing(true);
        }
    }

    public void setModel(DcrModel model) {
        this.model = model;
        this.eventNodes.clear();
        this.fGraph = new DiagramGraph();
        if (model == null) {
            repaint();
            return;
        }

        DcrLayout layout = DcrLayoutBuilder.build(model);
        Font nodeFont = Font.getFont("use.gui.view.objectdiagram", getFont());
        for (DcrNode item : layout.nodes.values()) {
            DcrEventNode node = new DcrEventNode(item.event, getOptions(), nodeFont);
            node.setPosition(item.x, item.y);
            node.setExactBounds(item.w, item.h);
            eventNodes.put(item.id, node);
            fGraph.add(node);
        }

        for (var relation : model.relations()) {
            DcrEventNode source = eventNodes.get(relation.source());
            DcrEventNode target = eventNodes.get(relation.target());
            if (source != null && target != null) {
                fGraph.addEdge(new DcrRelationEdge(relation, source, target, getOptions()));
            }
        }
        initialize();
        invalidateContent(true);
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
        for (DcrEventNode node : eventNodes.values()) {
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
                DcrEventNode node = eventNodes.get(name);
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
        throw new UnsupportedOperationException("DCR diagram does not support hiding all nodes.");
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
        String name = sourceFile == null ? (model == null ? "dcr" : model.name()) : sourceFile.getFileName().toString();
        return "_" + name + "_default.dlt";
    }
}
