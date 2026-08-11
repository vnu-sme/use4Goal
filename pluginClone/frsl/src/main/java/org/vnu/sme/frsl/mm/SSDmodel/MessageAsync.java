package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.Graphics2D;
import java.util.ArrayList;

import org.vnu.sme.frsl.mm.FRSLmodel.Action;
import org.vnu.sme.frsl.mm.FRSLmodel.ObjVar;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class MessageAsync extends MessageUC {

    private Action action;
    private ArrayList<Action> listAction;
    public MessageAsync( Step step,SequenceUC sequenceUC, UCProperties properties,
        LifeLineUC start, LifeLineUC goal, Action action) {
        super( step, sequenceUC, properties, start, goal);
        this.action = action;
        listAction = new ArrayList<>();
        listAction.add(action);
    }

    public void addActionMess(Action action) {
        listAction.add(action);
    }
   
    @Override
    protected void drawArrow(Graphics2D g, int goalXPos) {
        int xd = (mDirect == Direct.RIGHT) ? -10 : +10;
        int yz = 4;
        int[] xp = { goalXPos, goalXPos + xd, goalXPos + xd };
		int[] yp = { yPos, yPos - yz, yPos + yz };
        g.drawLine(xp[0], yp[0], xp[1], yp[1]);
        g.drawLine(xp[0], yp[0], xp[2], yp[2]);
    }

    @Override
    protected String getMessageInfo() {
        // TODO Auto-generated method stub
        if (action == null) return getmName();
        String messInfo = getmName() + ": {";
        for (Action ac : listAction) {
            for (ObjVar obj : ac.getObjVars()) {
                messInfo += obj.getName() + ", ";
            }
        }
        messInfo = messInfo.substring(0, messInfo.length() - 2);
        messInfo += "}";
        return messInfo;
    }
}