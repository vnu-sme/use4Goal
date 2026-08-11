package org.vnu.sme.frsl.view.selection;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;

import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class SequenceSelection {
    private final SequenceUC diagram;
    private final UCProperties properties;
    
    public SequenceSelection(SequenceUC diagram, UCProperties properties) {
        this.diagram = diagram;
        this.properties = properties;
    }


    public JCheckBoxMenuItem getSubMenuHideModelBrower() {
        JCheckBoxMenuItem showModelBrower = new JCheckBoxMenuItem("Show model browser");
        showModelBrower.setState(properties.getShowBrowser());
        showModelBrower.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // TODO Auto-generated method stub
                properties.setShowBrowser(e.getStateChange() == ItemEvent.SELECTED);
                diagram.hideOrShow(e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        return showModelBrower;
    }
}
