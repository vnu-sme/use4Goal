package org.vnu.sme.frsl.mm.SSDmodel;

import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class MessageSyncRef extends MessageSync{

    public MessageSyncRef( Step step,SequenceUC sequenceUC, UCProperties properties,
        LifeLineUC start, RefFrame frame) {
        super( step, sequenceUC, properties, start, frame);
    }

    @Override
    public int getRightPos(FrameUC frameUC) {
        return goalXPos + frame.getWidthSmallest() + getLevel(frameUC) * FRAME_VER;
    }

    @Override
    protected String getMessageInfo() {
        return getmName();
    }

}
