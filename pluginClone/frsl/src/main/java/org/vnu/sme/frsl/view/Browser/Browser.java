package org.vnu.sme.frsl.view.Browser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.tzi.use.config.Options;
// import org.tzi.use.gui.main.ModelBrowserSorting;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeEvent;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeListener;
import org.vnu.sme.frsl.mm.FRSLmodel.UseType;


public class Browser extends JPanel
    implements DragSourceListener, DragGestureListener, SortChangeListener {
    
    private TreeNode treeNode;
    private DragSource fDragSource = null;
    private JTree fTree;
    private JEditorPane fHtmlPane;
    private DefaultMutableTreeNode fTop;
    private DefaultTreeModel fTreeModel = null;
    // private ModelBrowserSorting fMbs;
    private BrowserHandling fMouseHandler;
    private EventListenerList fListenerList;



    public Browser(TreeNode treeNode) {
        fListenerList = new EventListenerList();
        setModel(treeNode);
        fDragSource = new DragSource();
        fDragSource.createDefaultDragGestureRecognizer(fTree, 
		DnDConstants.ACTION_MOVE, this);

        fTree.getSelectionModel().setSelectionMode(
                                                   TreeSelectionModel.SINGLE_TREE_SELECTION);
        fTree.putClientProperty("JTree.lineStyle", "Angled");
        fTree.setCellRenderer(new CellRenderer());

        ToolTipManager.sharedInstance().registerComponent(fTree);

        fTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) fTree
						.getLastSelectedPathComponent();

				if (treeNode == null || node == null)
					return;

				Object nodeInfo = node.getUserObject();
				if (node.isLeaf() && nodeInfo instanceof UseType) {
					UseType me = (UseType) nodeInfo;
					displayInfo(me);
					int selectedRow = 0;
					// which node is selected
					for (int i = 0; i < fTree.getRowCount(); i++) {
						if (fTree.isRowSelected(i)) {
							selectedRow = i;
						}
					}
					fMouseHandler.setSelectedNodeRectangle(fTree
							.getRowBounds(selectedRow));
					fMouseHandler.setActor(me);
					
					// fireSelectionChanged(me);
				} else {
					// fireSelectionChanged(null);
				}
			}
		});

        
        setLayout();
    }

    private void setLayout() {

        JScrollPane treeView = new JScrollPane(fTree);

        // Create the HTML viewing pane.
        fHtmlPane = new JEditorPane();
        fHtmlPane.setEditable(false);
        fHtmlPane.setContentType("text/html");
        JScrollPane htmlView = new JScrollPane(fHtmlPane);

        // Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                              treeView, htmlView);

        Dimension minimumSize = new Dimension(100, 50);
        htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setPreferredSize(new Dimension(500, 300));
        splitPane.setDividerLocation((int) (0.42 * Options.DEFAULT_HEIGHT));//FIXME 240);
        splitPane.setResizeWeight(.6d);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    public void setModel(TreeNode treeNode) {
        this.treeNode = treeNode;

        // Create the nodes.
        if (treeNode != null ) {
            fTop = new DefaultMutableTreeNode(treeNode.getTop());
            createNodes(fTop);
        } else {
            fTop = new DefaultMutableTreeNode("No model available");
        }

        // Create new tree or reinitialize existing tree
        if (fTree == null ) {
            fTreeModel = new DefaultTreeModel(fTop);
            fTree = new JTree(fTreeModel);
            // fMbs = ModelBrowserSorting.getInstance();
            // fMbs.addSortChangeListener( this );
            
            fMouseHandler = new BrowserHandling( this );
            fTree.addMouseListener( fMouseHandler );
        } else {
            fTreeModel.setRoot(fTop);
            fTreeModel = (DefaultTreeModel) fTree.getModel();
        }

        // reset HTML pane
        if (fHtmlPane != null )
            fHtmlPane.setText("");
    }

    private void displayInfo(UseType use) {
        StringWriter sw = new StringWriter();
        sw.write("<html><head>");
        sw.write("<style> <!-- body { font-family: sansserif; } --> </style>");
        sw.write("</head><body><font size=\"-1\">");
	
		
        PrintVisitor pw = new PrintVisitor(sw);
        use.visitPrint(pw);

        sw.write("</font></body></html>");
        String spec = sw.toString();
        fHtmlPane.setText(spec);

    }
    public void createNodes( final DefaultMutableTreeNode top ) {
        
        for (int i =0; i< treeNode.getLenght(); i++) {
            addChildNodes(top, treeNode.getName(i), treeNode.getValue(i));
        }
		
    }


    public void fireStateChanged(UseType useType, boolean highlight) {
        Object[] listeners = fListenerList.getListenerList();

        for (int i = listeners.length-2; i>= 0; i-= 2) {

        }
    }

    private void addChildNodes(DefaultMutableTreeNode top, String name, Collection<?> item) {
        DefaultMutableTreeNode modelCategory = new DefaultMutableTreeNode(name);
        top.add(modelCategory);
        Iterator<?> it = item.iterator();

        while(it.hasNext()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(it.next());
            modelCategory.add(child);
        }
    }


    // drag source listener
    public void dropActionChanged(DragSourceDragEvent dsde) {
        //Log.trace(this, "dropActionChanged");
    }
    public void dragExit(DragSourceEvent dse) {
        //Log.trace(this, "dragExit");
    }
    public void dragEnter(DragSourceDragEvent dsde) {
        //Log.trace(this, "dragEnter");
    }
    public void dragOver(DragSourceDragEvent dsde) {
        //Log.trace(this, "dragOver");
    }
    public void dragDropEnd(DragSourceDropEvent dsde) {
        //Log.trace(this, "dragDropEnd");
    }

    // drag gesture listener
    public void dragGestureRecognized(DragGestureEvent dge) {
        
    }

    // sort change listener
    public void stateChanged( SortChangeEvent e ) {
        // ArrayList<Integer> pathWereExpanded = new ArrayList<Integer>();
        // int selectedRow = -1;
        
        // // which nodes are expanded
        // for ( int i=0; i<fTree.getRowCount(); i++ ){
        //     if ( fTree.isExpanded( i ) ){
        //         pathWereExpanded.add( i );
        //     }
        //     // which node is selected
        //     if ( fTree.isRowSelected( i ) ) {
        //         selectedRow = i;
        //     }
        // }

        // fTop.removeAllChildren();
        // createNodes( fTop );
        // fTreeModel.reload();
        // fHtmlPane.setText( "" );

        // // expand all nodes that were expanded.
        // for ( int i=0; i<pathWereExpanded.size(); i++ ){
        //     fTree.expandRow( pathWereExpanded.get(i).intValue() );
        // }
        // // set selected node again.
        // if ( selectedRow >= 0 ) {
        //     fTree.setSelectionRow( selectedRow );    
        // }
    }



    class CellRenderer extends DefaultTreeCellRenderer {
	public Component getTreeCellRendererComponent(JTree tree, Object value,
		boolean sel, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {
	    super.getTreeCellRendererComponent(tree, value, sel, expanded,
		    leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            int level = node.getLevel();
            this.setToolTipText(null);
            // always display root and categories as non-leaf nodes
            if (level == 0 ) {
                if (node.isLeaf() )
                    setIcon(getClosedIcon()); // we don't have a model
                else 
                    setIcon(getOpenIcon());
            } else if (level == 1 ) {
                if (tree.isExpanded(new TreePath(node.getPath())) )
                    setIcon(getOpenIcon());
                else
                    setIcon(getClosedIcon());
            } 
            return this;
        }
    }
}

