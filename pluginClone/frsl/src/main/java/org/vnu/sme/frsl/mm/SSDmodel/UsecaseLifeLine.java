package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public class UsecaseLifeLine extends LifeLineUC {

    private Usecase fUsecase;

    private UsecaseBox fUsecaseBox;

    public UsecaseLifeLine(SequenceUC sequenceUC, UCProperties properties, Usecase usecase, int col) {
        super(sequenceUC, properties);
        fUsecase = usecase;

        xPos = properties.getLeftMargin() + col * properties.llStep();
        lifeName = usecase.getName();

        fUsecaseBox = new UsecaseBox(xPos, -1, usecase.getName());
    }

    @Override
    protected void drawLifeline(Graphics2D g, FontMetrics fm) {
        int y = properties.yScroll() - 20;
        int y_start = 0, y_end = 40;

        boolean isInitStateShown = true;

        fUsecaseBox.drawBox(g, fm, y_end, isInitStateShown);

    }

    @Override
    public int getMaxXLifeLine() {
        return fUsecaseBox.getxPosOfBoxEnd();
    }
    
    
}
