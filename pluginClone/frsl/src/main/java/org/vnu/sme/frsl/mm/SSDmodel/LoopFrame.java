package org.vnu.sme.frsl.mm.SSDmodel;

import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class LoopFrame extends FrameUC {

    public LoopFrame(SequenceUC sequenceUC, UCProperties properties, String name) {
        super(sequenceUC, properties, name, "Loop");
        // setColorTag(Color.pink);
        Flow flow = new Flow();
        listFlow.add(flow);
        
    }

    public void addFlow(MessageUC mess) {
        
        listFlow.getFirst().addLastMessage(mess);
    }
    
}
