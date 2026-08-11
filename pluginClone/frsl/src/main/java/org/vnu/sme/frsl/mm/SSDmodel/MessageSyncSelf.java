package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.vnu.sme.frsl.mm.FRSLmodel.ActStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Action;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class MessageSyncSelf extends MessageSync {

    private int xPosRight;
    private final int nameMarginLeft = 10;
    
    public MessageSyncSelf( Step step,SequenceUC sequenceUC, UCProperties properties,
        LifeLineUC start) {
        super( step, sequenceUC, properties, start, start);

        xPosRight = startXPos + properties.getSelfMessXPos();
        
    }

    @Override
    public int getTopYPos() {
        return yPos - properties.getSelfMessYPos();
    }

    @Override
    public int getBotYPos() {
        return yPos;
    }

    @Override
    protected void initActivation() {
        start.insertActivation(this);

    }

    @Override
    public void updateYPos() {
        yPosVir = properties.yPosState() + marginTop + properties.getFyMessageMargin();
        properties.setfYPosState(yPosVir + marginBottom + properties.getSelfMessYPos());
    }

    @Override
    public int getLeftPos(FrameUC frameUC) {
        return start.xPos - getLevel(frameUC) * FRAME_VER;
    }

    @Override
    public int getRightPos(FrameUC frameUC) {
        return start.xPos + properties.getSelfMessXPos() + getLevel(frameUC) * FRAME_VER;
    }

    @Override
    public void initDirect() {
        
        mDirect = Direct.SELF;
        goalXPos =start.xPos + properties.getfActivationWidth()/2;
        startXPos = start.xPos + properties.getfActivationWidth()/2;
    }

    @Override
    protected void drawMessage(Graphics2D g, FontMetrics fm) {
        String name = getMessageInfo();

        yPos = yPosVir + properties.yPosStart();

        int yPosUnder = yPos + properties.getSelfMessYPos();
        
        g.drawLine(startXPos, yPos, xPosRight, yPos);

        g.drawString(name, xPosRight + nameMarginLeft, (yPosUnder + yPos ) /2 + 5);

        g.drawLine(xPosRight, yPos, xPosRight, yPosUnder);

        g.drawLine(xPosRight, yPosUnder, startXPos, yPosUnder);
        
        yPos = yPosUnder;

        drawArrow(g, startXPos);

    }

    @Override
    protected String getMessageInfo() {
        // TODO Auto-generated method stub
        ActStep actStep = (ActStep) step;
        if (actStep.getActions().size() == 0) return getmName();
        String messInfo = getmName() + ": {";
        for (Action ac : actStep.getActions()) {
            for (ObjVar obj : ac.getObjVars()) {
                messInfo += obj.getName() + ", ";
            }
        }
        messInfo = messInfo.substring(0, messInfo.length() - 2);
        messInfo += "}";
        return messInfo;
    }
}
