package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.tzi.use.gui.main.MainWindow;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;
import org.vnu.sme.frsl.view.selection.event.ActionShowSequenceView;


public class RefFrame extends FrameUC {
    private FontMetrics fm;
    private int height, width;

    private int xPosCenter;
    private int widthSmallest = 80;
    private Usecase frameUsecase;
    
    public RefFrame (SequenceUC sequenceUC, UCProperties properties, int col, Usecase usecase) {
        super(sequenceUC, properties, usecase.getName(), "ref");
        this.frameUsecase = usecase;
        xPosCenter = properties.getLeftMargin() + col * properties.llStep();

        setColorBody(Color.pink);
        initPos();

        Flow flow = new Flow();
        listFlow.add(flow);
    }

    public void initPos() {
        fm = sequenceUC.getFontMetrics(properties.getFont());
        width = fm.stringWidth(frameName);
        height = fm.getFont().getSize();
        if (width > widthSmallest) {
            setWidthSmallest(width + 25);
        }
        frameBounds.x = xPosCenter  ;
        frameBounds.width = widthSmallest  ;

    }

    public void addOnlyOne(MessageUC mess) {
        listFlow.getFirst().clear();
        listFlow.getFirst().addListMessage(mess);
    }

    public int getWidthSmallest() {
        return widthSmallest;
    }

    public int getPosXLeft() {
        return xPosCenter;
    }

    private void setWidthSmallest(int widthSmallest) {
        this.widthSmallest = widthSmallest;
    }

    @Override
    protected void drawFrame(Graphics2D g, FontMetrics fm) {
        
        super.drawFrame(g, fm);
        g.drawString(frameName, xPosCenter + 2 ,(int) listFlow.getFirst().getCenterY() + height /2 );
    }
    
    @Override
    protected void caculateFrameBounds() {
        listFlow.getFirst().caculateFlowBounds();
        frameBounds.y = listFlow.getFirst().getY() ;
        frameBounds.height = listFlow.getLast().getMaxY() - listFlow.getFirst().getY() ;
    }

    public void open(MainWindow mainWindow, FrslModel model) {
        new ActionShowSequenceView(frameName, frameUsecase, mainWindow, model).showFromAnother();;
    }

}
