package org.vnu.sme.frsl.mm.SSDmodel;

import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class AltFrame extends FrameUC{

    private int numAlt; 
    
    public AltFrame(SequenceUC sequenceUC, UCProperties properties, String name) {
        super(sequenceUC, properties, name, "Alt");
        numAlt = 1;
    }

    public void setNumAlt(int numAlt) {
        this.numAlt += numAlt;
    }

    public boolean checkLength() {
        return listFlow.size() == numAlt;
    }

    public void addMess(MessageUC mess) {
        listFlow.getLast().addListMessage(mess);
    }


    public void addAltFlow() {
        Flow newFlow = new Flow();
        
        listFlow.add(newFlow);
    }

    public void createBaseFlow(MessageUC mess) {
        Flow flow = new Flow();
        flow.addListMessage(mess);

        listFlow.clear();
        listFlow.add(flow);

    }
   
}
