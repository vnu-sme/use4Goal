package org.vnu.sme.frsl.view.selection.event;

import javax.swing.AbstractAction;


public abstract class ActionShowDiagram extends AbstractAction{
    // private DiagramView diagram;

    ActionShowDiagram(String label) {
        super(label);
        // this.diagram = diagram; 
    }

    protected abstract void showDiagram();


}
