package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Map;
import java.util.TreeMap;

import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;


public abstract class MessageUC {

    protected enum Direct {
        LEFT ,
        RIGHT,
        SELF,
        INCLINED,
    }

    protected int FRAME_VER = 25, FRAME_HOR = 18;
    protected Direct mDirect;
    protected SequenceUC sequenceUC;

    protected UCProperties properties;

    protected int yPos;  // vi tri duoi cung cua mess 
    protected int yPosVir;
    
    protected int startXPos, goalXPos ;

    protected LifeLineUC start;
    protected LifeLineUC goal;
    protected RefFrame frame;

    protected int marginTop = 0, marginBottom = 0;

    protected Step step;

    protected int numOrder;
    protected LevelFrame levelFrame;

    public MessageUC ( Step step,SequenceUC sequenceUC, UCProperties properties,
    LifeLineUC start, LifeLineUC goal) {
        this.start = start;
        this.goal = goal;
        this.step = step;
        this.sequenceUC = sequenceUC;
        this.properties = properties;
        levelFrame = new LevelFrame();
        initDirect();
        initActivation();
    }

    public MessageUC ( Step step,SequenceUC sequenceUC, UCProperties properties,
    LifeLineUC start, RefFrame frame) {
        this.start = start;
        this.frame = frame;
        this.step = step;
        this.sequenceUC = sequenceUC;
        this.properties = properties;
        levelFrame = new LevelFrame();
        initDirectRef();
        initActivationRef();
    }

    public void updateYPos() {
        yPosVir = properties.yPosState() + marginTop + properties.getFyMessageMargin();
        properties.setfYPosState(yPosVir + marginBottom);
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom += marginBottom;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop += marginTop;
    }

    public LifeLineUC getGoal() {
        return goal;
    }

    public void setGoal(LifeLineUC goal) {
        this.goal = goal;
    }

    public LifeLineUC getStart() {
        return start;
    }

    public String getmName() {
        return step.getName() ;
    }
    public int getNumOrder() {
        return numOrder;
    }

    public int getLeftPos(FrameUC frameUC) {
        if (mDirect == Direct.RIGHT) {
            return startXPos - getLevel(frameUC) * FRAME_VER;
        } else {
            return goalXPos + getLevel(frameUC) * FRAME_VER;
        }
    }

    public int getRightPos(FrameUC frameUC) {
        if (mDirect == Direct.LEFT) {
            return startXPos - getLevel(frameUC) * FRAME_VER;
        } else {
            return goalXPos + getLevel(frameUC) * FRAME_VER ;
        }
    }

    public int getTopYPos(FrameUC frameUC) {
        return getTopYPos() - getLevel(frameUC) * FRAME_HOR;
    }

    public int getBotYPos(FrameUC frameUC) {
        return getBotYPos() + getLevel(frameUC) * FRAME_HOR;
    }

    public int getTopYPos() {
        return yPos;
    }

    public int getBotYPos() {
        return yPos;
    }

    protected int getLevel(FrameUC frameUC) {
        return levelFrame.getLevel(frameUC);
    }
    
    public void initDirectRef() {
        mDirect = Direct.RIGHT;
        startXPos = start.xPos + properties.getfActivationWidth()/2;
        goalXPos = frame.getPosXLeft();
    }

    public void initDirect() {
        startXPos = start.xPos;
        goalXPos = goal.xPos;
        if(startXPos <= goalXPos) {
            mDirect = Direct.RIGHT;
            goalXPos -= properties.getfActivationWidth()/2;
            startXPos += properties.getfActivationWidth()/2;
        } else {
            goalXPos += properties.getfActivationWidth()/2;
            startXPos -= properties.getfActivationWidth()/2;
            mDirect = Direct.LEFT;
        }
    }

    protected void initActivation() {
        if (mDirect == Direct.RIGHT) {
            start.insertActivation(this);
            goal.createActivation(this);
        } else {
            start.insertActivation(this);
            goal.insertActivation(this);
        }
    }

    protected void initActivationRef() {
        if (mDirect == Direct.RIGHT) {
            start.createActivation(this);
        } else {
            start.createActivation(this);
        }
    }

    public void draw (Graphics2D g) {
        FontMetrics fm = sequenceUC.getFontMetrics(properties.getFont());
        g.setColor(Color.black);

        drawMessage(g, fm);

        properties.updatefyLifeLineHeight(yPos);

    }


    protected  void drawMessage(Graphics2D g, FontMetrics fm) {
        int width = fm.stringWidth(getmName());

        yPos = yPosVir + properties.yPosStart();

        g.drawString(getMessageInfo(), (startXPos + goalXPos - width) /2 , yPos - 5);

        g.drawLine(startXPos, yPos, goalXPos, yPos);
        
        drawArrow(g, goalXPos);
    }

    protected abstract String getMessageInfo();
    protected abstract void drawArrow(Graphics2D g,int goalXPos ) ;

    // public void show() {
    //     levelFrame.show();
    // }
    public void setLevel(FrameUC frameUC) {
        levelFrame.addLevel(frameUC);
    }

    public class LevelFrame {
        private int level;
        private Map <String, Integer> mapLevel;
        
        public LevelFrame() {
            mapLevel = new TreeMap<>();
            level = 0;
        }

        public void addLevel(FrameUC frameUC) {
            if (mapLevel.containsKey(frameUC.getFrameName())) return;
            mapLevel.put(frameUC.getFrameName(), level + 1);
            level++;
        }

        public int getLevel(FrameUC frameUC) {
            return mapLevel.get(frameUC.getFrameName());
        }

        // public void show() {
        //     System.out.println(mapLevel);
        // }
    }
}
