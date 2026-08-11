package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class ActorLifeLine extends LifeLineUC{

    private Actor fActor;

    private ActorBox fActorBox;


    public ActorLifeLine(SequenceUC sequenceUC, UCProperties properties, Actor actor, int col) {
        super(sequenceUC, properties);
        fActor = actor;
        
        xPos = properties.getLeftMargin() + col * properties.llStep();
        
        lifeName = actor.getName();
        fActorBox = new ActorBox(xPos, -1, actor.getName());
    }

    @Override
    protected void drawLifeline(Graphics2D g, FontMetrics fm) {
        
        int y_start = 0;

        boolean isInitStateShown = true;

        fActorBox.drawBox(g, fm, y_start, isInitStateShown);
        properties.updateYStartDashLine(fActorBox.getHeight());

    }

    @Override
    public int getMaxXLifeLine() {
        return fActorBox.getxPosOfBoxEnd();
    }

   
    
}
