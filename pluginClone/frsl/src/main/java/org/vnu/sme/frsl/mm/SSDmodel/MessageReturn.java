package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.vnu.sme.frsl.mm.FRSLmodel.Action;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class MessageReturn extends MessageAsync {
    
    public MessageReturn( Step step,SequenceUC sequenceUC, UCProperties properties,
        LifeLineUC start, LifeLineUC goal, Action action) {
        super( step, sequenceUC, properties, start, goal, action);
    }


    @Override
    protected void drawMessage(Graphics2D g, FontMetrics fm) {
        int width = fm.stringWidth(getMessageInfo());
        yPos = yPosVir + properties.yPosStart();
        
        g.drawString(getMessageInfo(), (startXPos + goalXPos - width) /2, yPos - 5);

        Stroke oleStroke = g.getStroke();
        g.setStroke(properties.getDASHEDSTROKE());

        g.drawLine(startXPos, yPos, goalXPos, yPos);

        g.setStroke(oleStroke);
        
        drawArrow(g, goalXPos);
    }
}
