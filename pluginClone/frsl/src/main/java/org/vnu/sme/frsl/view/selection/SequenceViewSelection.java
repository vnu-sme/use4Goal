package org.vnu.sme.frsl.view.selection;

import javax.swing.JMenu;

import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.mm.UCDmodel.UcdModel;
import org.vnu.sme.frsl.view.FRSL2UCD.UseCaseDiagram;

public class SequenceViewSelection {

    private final UseCaseDiagram diagram;
    
    public SequenceViewSelection(UseCaseDiagram diagram) {
        this.diagram = diagram;
    }

    public JMenu showSequenceDiagramView() {
        JMenu showSequenceDiagramView = new JMenu("Show sequence");
        UcdModel data = diagram.getData();
        
        for (Usecase usecase : data.getUseNodeMap().keySet()) {
            String label = usecase.getName();
            showSequenceDiagramView.add(diagram.showSequenceDiagramView(label, usecase ));
        }

        return showSequenceDiagramView;
    }
}
