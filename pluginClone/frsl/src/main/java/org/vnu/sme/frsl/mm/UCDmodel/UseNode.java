package org.vnu.sme.frsl.mm.UCDmodel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.tzi.use.gui.views.diagrams.DiagramOptionChangedListener;
import org.tzi.use.gui.views.diagrams.DiagramOptions;
import org.tzi.use.gui.views.diagrams.elements.CompartmentNode;
import org.tzi.use.gui.views.diagrams.util.Util;
import org.tzi.use.util.MathUtil;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;


public abstract class UseNode  extends CompartmentNode implements DiagramOptionChangedListener {
    protected DiagramOptions fOpt;
    protected Usecase fUsecase;
    protected String fLabel;

    protected final Integer SAPCE_HOZIRON = 25;
    protected final Integer SPACE_VERTICAL = 10;

    protected SystemBounds systemBounds;

    protected String[] title;
    protected Color[] colors;

    protected Rectangle2D.Double nameRect = new Rectangle2D.Double();
    protected Rectangle2D.Double extendPoint = new Rectangle2D.Double();
    

    public UseNode(Usecase us, DiagramOptions opt) {
        this.fOpt = opt;
        this.fUsecase = us;
        this.fLabel = us.getName().toString();
        this.fOpt.addOptionChangedListener(this);   
        

    }

    public void setSystemBounds(SystemBounds sys) {
        this.systemBounds = sys;
    }

    public SystemBounds getSystemBounds() {
        return systemBounds;
    }

    public Usecase getUsecase() {
        return fUsecase;
    }
    private double lineHeight;

    protected void calculateBounds() {
        double width = nameRect.width;
		double height = nameRect.height;
		
		double requiredHeight = nameRect.height;

        if (fOpt.isShowAttributes()) {
			width = Math.max(width, extendPoint.width);
			height += extendPoint.height;
			if (hasExtend())
				requiredHeight += lineHeight + VERTICAL_INDENT;
			else
				requiredHeight += 2 * VERTICAL_INDENT;
		}
		width += 10;
		
		setRequiredHeight("CLASSIFIERNODE", requiredHeight);
        height = MathUtil.max(height, getMinHeight(), getRequiredHeight());
        width = MathUtil.max(width, getMinWidth(), getRequiredWidth());

        width = width + 2 * SPACE_VERTICAL;
        setCalculatedBounds(width, width /5 * 3);
    }


    @Override
    public final void doCalculateSize( Graphics2D g ) {
        calculateNameRectSize(g, nameRect);
        calculateExtendPoint(g, extendPoint);
        systemBounds.doSomething();
        this.lineHeight = Util.getLineHeight(g.getFontMetrics());
        calculateBounds();
    }
	@Override
	public void optionChanged(String optionname) {
		
		// calculateBounds();
	}

    @Override
    protected String getStoreType() {
    	return "Usecase";
    }
    @Override
	public void dispose() {
		super.dispose();
		fOpt.removeOptionChangedListener(this);
	}

    @Override
	public String name() {
		return this.fUsecase.toString();
	}

    @Override
    public String getId() {
    	return name(); 
    }
    protected abstract void calculateNameRectSize(Graphics2D g, Rectangle2D.Double rect);
    protected abstract void calculateExtendPoint(Graphics2D g, Rectangle2D.Double rect);

    protected abstract boolean hasExtend();

}
