package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.Graphics2D;

import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;


public class MessageSync extends MessageUC {
    
    public MessageSync( Step step,SequenceUC sequenceUC, UCProperties properties,
        LifeLineUC start, LifeLineUC goal) {
        super( step, sequenceUC, properties, start, goal);
        
    }

    public MessageSync( Step step,SequenceUC sequenceUC, UCProperties properties,
        LifeLineUC start, RefFrame frame) {
        super( step, sequenceUC, properties, start, frame);
        
    }

    @Override
    protected void drawArrow(Graphics2D g, int goalXPos) {
        int xd = (mDirect == Direct.RIGHT) ? -10 : +10;
        int yz = 4;
        int[] xp = { goalXPos, goalXPos + xd, goalXPos + xd };
		int[] yp = { yPos, yPos - yz, yPos + yz };
        g.fillPolygon(xp, yp, xp.length);

    }

    @Override
    protected String getMessageInfo() {
        // TODO Auto-generated method stub
        return getmName();
    }

}
