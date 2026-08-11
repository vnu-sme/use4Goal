package org.vnu.sme.frsl.view.FRSL2UCD;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.util.Collection;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ModelBrowser;
import org.tzi.use.gui.views.PrintableView;
import org.tzi.use.gui.views.View;
import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.Extend;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.view.Browser.Browser;
import org.vnu.sme.frsl.view.Browser.TreeUsecase;

public class UseCaseDiagramView extends JPanel implements View, PrintableView  {

    protected final MainWindow fMainWindow;

    private final FrslModel systemFRSL;

    private UseCaseDiagram fUseCaseDiagram;

    private UseCaseDiagramOptions opt;

    private JSplitPane sp;

	private Browser fModelBrowser; 

    private TreeUsecase treeUsecase;
        
    public UseCaseDiagramView (MainWindow mainWindow, FrslModel system) {
        this.fMainWindow = mainWindow;
        this.systemFRSL = system;
        this.setFocusable(true);
        setLayout(new BorderLayout());
        opt = new UseCaseDiagramOptions();
        treeUsecase = new TreeUsecase(system);
        initDiagram ();
    }

    public void initDiagram () {
        fUseCaseDiagram = new UseCaseDiagram(this, fMainWindow.logWriter(), opt);
        fUseCaseDiagram.setStatusBar(fMainWindow.statusBar());
        initState();


        fModelBrowser = new Browser(treeUsecase );
        
        this.removeAll();
        Component view = new JScrollPane(fUseCaseDiagram);

        sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sp.add(view);
        add(sp, BorderLayout.CENTER);

        
        // if (loadDefaultLayout) {
        // fUseCaseDiagram.loadDefaultLayout();
        // }
    }

    /*
	 * ----------------------------------------
	 * 
	 * change layout JPanel when select show browser.
	 * 
	 * ----------------------------------------
	 */
    public void changeLayout() {

        if (opt.isShowBrowser()) {
            sp.add(fModelBrowser);
            sp.setDividerLocation(400);
        } else {
            
            sp.remove(fModelBrowser);
        }
        
    }

    public FrslModel system () {
        return systemFRSL;
    }

    public ModelBrowser getModelBrowser() {
        return fMainWindow.getModelBrowser();
    }

    public boolean isSelectedView() {
        if (fMainWindow.getSelectedView() !=null) {
            return fMainWindow.getSelectedView().equals(this);
        }
        return false;
    }

    private void initState() {

        fUseCaseDiagram.addSystemBounds();

        // add usecase
        Collection<Usecase> allUseCase = systemFRSL.usecases();
        for (Usecase us : allUseCase) {
            fUseCaseDiagram.addUsecase(us);
        }

        // add actor
        Collection<Actor> allActor = systemFRSL.actors();
        for (Actor ac : allActor) {
            fUseCaseDiagram.addActor(ac);
        }

        // add association usecase to actor
        for (Usecase us : allUseCase) {
            fUseCaseDiagram.addAssociation(us, us.getPrimaryActor(), false);

            Collection<Actor> allSeconActor = us.getSecondaryActors();
            for (Actor ac : allSeconActor) {
                fUseCaseDiagram.addAssociation(us, ac, true);
            }

            //add include
            Map<String, Usecase> allInclude = us.getInclude();
            for (Usecase inlUsecase : allInclude.values()) {
                fUseCaseDiagram.addInclude(us, inlUsecase);
            }
        }
        // add extend
        Collection<Extend> allExtend = systemFRSL.extend();
        for (Extend ext : allExtend) {
            fUseCaseDiagram.addExtend(ext.getfExtendstion(), ext.getfExtendedUC());
        }
        
        fUseCaseDiagram.initialize();
    }

    
    @Override
	public float getContentHeight() {
		return fUseCaseDiagram.getPreferredSize().height;
	}
	
	@Override
	public float getContentWidth() {
		return fUseCaseDiagram.getPreferredSize().width;
	}
    @Override
	public void detachModel () {}
    @Override
	public void printView( PageFormat pf ) {
        fUseCaseDiagram.printDiagram( pf, "Use case diagram" );
    }

    @Override
	public void export( Graphics2D g ) {
    	boolean oldDb = fUseCaseDiagram.isDoubleBuffered();
    	fUseCaseDiagram.setDoubleBuffered(false);
    	fUseCaseDiagram.paint(g);
    	fUseCaseDiagram.setDoubleBuffered(oldDb);
    }

}
