package org.vnu.sme.frsl.view.FRSL2UCD;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JPopupMenu;

import org.tzi.use.gui.main.ModelBrowserSorting;
import org.tzi.use.gui.main.ModelBrowser.SelectionChangedListener;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeEvent;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeListener;
import org.tzi.use.gui.util.PersistHelper;
import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.event.ActionLoadLayout;
import org.tzi.use.gui.views.diagrams.event.ActionSaveLayout;
import org.tzi.use.gui.views.diagrams.event.DiagramInputHandling;
import org.tzi.use.gui.views.diagrams.event.HighlightChangeEvent;
import org.tzi.use.gui.views.diagrams.event.HighlightChangeListener;
import org.tzi.use.uml.mm.MModelElement;
import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.mm.UCDmodel.ActorNode;
import org.vnu.sme.frsl.mm.UCDmodel.AssociationEdge;
import org.vnu.sme.frsl.mm.UCDmodel.ExtendEdge;
import org.vnu.sme.frsl.mm.UCDmodel.IncludeEdge;
import org.vnu.sme.frsl.mm.UCDmodel.SystemBounds;
import org.vnu.sme.frsl.mm.UCDmodel.UcdModel;
import org.vnu.sme.frsl.mm.UCDmodel.UsecaseNode;
import org.vnu.sme.frsl.view.selection.SequenceViewSelection;
import org.vnu.sme.frsl.view.selection.UsecaseSelection;
import org.vnu.sme.frsl.view.selection.event.ActionShowSequenceView;
import org.w3c.dom.Element;


@SuppressWarnings("serial")
public class UseCaseDiagram extends DiagramView implements HighlightChangeListener , SelectionChangedListener, SortChangeListener{
	
    private final UseCaseDiagramView fParent;

    protected final UcdModel visibleData = new UcdModel();

	private final UsecaseSelection fSelection;

	private final SequenceViewSelection fView;

	private final DiagramInputHandling inputHandling;

    public UseCaseDiagram(UseCaseDiagramView parent, PrintWriter log, UseCaseDiagramOptions opt) {
        super(opt, log);
        this.fParent = parent;
		inputHandling = new DiagramInputHandling(fNodeSelection, fEdgeSelection, this);

		fSelection = new UsecaseSelection(this, opt);
		fView = new SequenceViewSelection(this);
		fActionLoadLayout = new ActionLoadLayout("USE Case diagram layout", "clt", this);

		fActionSaveLayout = new ActionSaveLayout("USE Case diagram layout", "clt", this);

		startLoadMouseListener();
		startLayoutThread();
    }

	private void startLoadMouseListener () {
		addMouseListener(inputHandling);
		fParent.addKeyListener(inputHandling);

		fParent.getModelBrowser().addHighlightChangeListener(this);
		fParent.getModelBrowser().addSelectionChangedListener(this);
		ModelBrowserSorting.getInstance().addSortChangeListener(this);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// need a new layouter to adapt to new window size
				fLayouter = null;
			}
		});
	}

	public void addSystemBounds () {
		Map<String, UsecaseNode> listUsecase = new TreeMap<String, UsecaseNode>();

		SystemBounds n = new SystemBounds(fOpt, listUsecase);

		fGraph.add(n);
		visibleData.getSystemBound().put("system", n);
		fLayouter = null;

	}

    public void addUsecase(Usecase us) {
        int [] position = randomPosition();

        UsecaseNode n = new UsecaseNode(us, fOpt);
        n.setPosition(position[0], position[1]);

        n.setMinWidth(minClassNodeHeight);
        n.setMinHeight(minClassNodeHeight);

        fGraph.add(n);
		visibleData.getUseNodeMap().put(us, n);
		SystemBounds sys = visibleData.getSystemBound().get("system");
		n.setSystemBounds(sys);
		sys.addListUsecase(n);

        fLayouter = null;
    }

	public void addActor(Actor ac) {
		int [] position = randomPosition();

		ActorNode n = new ActorNode(ac, fOpt);
		n.setPosition(position[0], position[1]);
		n.setMinHeight(minClassNodeHeight);
		n.setMinWidth(minClassNodeWidth);

		fGraph.add(n);
		visibleData.getActorNodeMap().put(ac, n);
		fLayouter = null;
	}

	public void addAssociation(Usecase us, Actor ac,boolean isDashed) {
		PlaceableNode source = visibleData.getActorNodeMap().get(ac);
		PlaceableNode targer = visibleData.getUseNodeMap().get(us);

		String name = ac.getName() + us.getName();

		AssociationEdge e = new AssociationEdge(source, targer, name, this, isDashed);
		
		fGraph.addEdge(e);
		visibleData.getAssociationMap().put(name, e);
		fLayouter = null; 
		
	}

	public void addExtend(Usecase extendUc ,Usecase uc ) {
		PlaceableNode source = visibleData.getUseNodeMap().get(uc);
		PlaceableNode target = visibleData.getUseNodeMap().get(extendUc);

		String name = extendUc.getName() + uc.getName();
		IncludeEdge e = new IncludeEdge(source, target, name, this, true);

		fGraph.addEdge(e);
		fLayouter = null;
	}

	public void addInclude(Usecase extend ,Usecase iclu ) {
		PlaceableNode source = visibleData.getUseNodeMap().get(iclu);
		PlaceableNode target = visibleData.getUseNodeMap().get(extend);

		String name = extend.getName() + iclu.getName();
		ExtendEdge e = new ExtendEdge(source, target, name, this, true );

		fGraph.addEdge(e);
		fLayouter = null;
	}


	@Override
	public void stateChanged(HighlightChangeEvent e) {

	}

	@Override
	public void stateChanged(SortChangeEvent e) {
		for (UsecaseNode n : this.visibleData.getUseNodeMap().values()) {
			n.stateChanged(e);
		}
	}
	@Override
	public void selectionChanged(MModelElement element) {
		fLog.println("selection changed");
	}
    @Override
	public DiagramData getHiddenData() {
        // no override
		return visibleData;
	}
    @Override
	public DiagramData getVisibleData() {
        // no override
		return visibleData;
	}

    @Override
	public void showAll() {
		//no override
	}
	
	/*
	 * ----------------------------------------
	 * 
	 * create popup menu.
	 * 
	 * ----------------------------------------
	 */
	@Override
	protected PopupMenuInfo unionOfPopUpMenu() {
		PopupMenuInfo popupMenuInfo = super.unionOfPopUpMenu();
		JPopupMenu popupMenu = popupMenuInfo.popupMenu;
		int pos = 0;

		
		popupMenu.insert(fSelection.getSubMenuHideUsecase(), pos++);
		popupMenu.insert(fView.showSequenceDiagramView(), pos++);
		popupMenu.insert(fSelection.getSubMenuHideModelBrowser(), pos);

		return popupMenuInfo;
	}

	public ActionShowSequenceView showSequenceDiagramView(String label, Usecase usecase) {
		return new ActionShowSequenceView(label, usecase, fParent.fMainWindow, fParent.system());
	}

	public UcdModel getData() {
		return visibleData;
	}

	public void hideOrShow(Boolean isShow) {
		fParent.changeLayout();
	}

    @Override
	public void hideAll() {
		// no override
	}
	@Override
	public Set<PlaceableNode> getHiddenNodes() {
        // no override
		return visibleData.getNodes();
	}
    @Override
	public void restorePlacementInfos(PersistHelper helper, int version) {
		// no override
	}
	@Override
	public void storePlacementInfos(PersistHelper helper, Element parent) {
		// no override
	}
	@Override
	protected String getDefaultLayoutFileSuffix() {
		return ".clt";
	}

    private int[] randomPosition() {
		// Find a random new position. getWidth and getHeight return 0
		// if we are called on a new diagram.
		int fNextNodeX = (int) (Math.random() * Math.max(100, fParent.getWidth() - 50));
		int fNextNodeY = (int) (Math.random() * Math.max(100, fParent.getHeight() - 50));

		return new int[] {fNextNodeX, fNextNodeY};
	}
}
