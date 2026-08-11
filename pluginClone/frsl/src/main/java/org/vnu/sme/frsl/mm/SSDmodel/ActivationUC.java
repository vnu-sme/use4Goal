package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.Color;
import java.awt.Graphics2D;

import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class ActivationUC {
    private int xPosCenter;
    private int height;
    private int yStart;

    private MessageUC messStart;
    private MessageUC messEnd;

    private UCProperties properties;

    public ActivationUC(int xPosCenter, UCProperties properties, MessageUC messStart) {
        this.xPosCenter = xPosCenter;
        this.properties = properties;

        createPos(messStart);
    }

    public void draw(Graphics2D g) {
        Color old = g.getColor();
        g.setColor(Color.blue);
        updateLocate();
        g.fillRect(xPosCenter - properties.getfActivationWidth() /2 , yStart, properties.getfActivationWidth(), height);
        g.setColor(old);
    }

    private void updateLocate() {
        height = messEnd.getBotYPos() - messStart.getTopYPos();
        if(height ==0) {
            height = properties.getfActivationHeight();
        }
        yStart = messStart.getTopYPos() - properties.getfActiMarginTop();
        height += properties.getfActiMarginBottom() + properties.getfActiMarginTop();
    }

    
    private void createPos(MessageUC messStart) {
        this.messStart = messStart;
        this.messEnd = messStart;
    }

    public MessageUC getMessEnd() {
        return messEnd;
    }
    public MessageUC getMessStart() {
        return messStart;
    }
    public void setMessEnd(MessageUC messEnd) {
        this.messEnd = messEnd;
    }

    // public void setMessStart(MessageUC messStart) {
    //     this.messStart = messStart;
    // }

}
