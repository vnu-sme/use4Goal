package org.vnu.sme.frsl.mm.SSDmodel;

import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class MessageSyncNon extends MessageSync{
    
    public MessageSyncNon( Step step,SequenceUC sequenceUC, UCProperties properties,
        LifeLineUC start, LifeLineUC goal) {
        super( step, sequenceUC, properties, start, goal);
        
    }
}
