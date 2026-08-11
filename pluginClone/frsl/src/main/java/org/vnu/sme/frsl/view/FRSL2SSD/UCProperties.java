package org.vnu.sme.frsl.view.FRSL2SSD;

import java.awt.BasicStroke;
import java.awt.Font;

public class UCProperties {

    private int fYStart;
    private int fYStep;
	private int fXStep;

	private int fYPosState = 0;
	private static int fYScroll;

	private int fyLifeLineHeight;

	// activation
	private int fActivationHeight;
	private int fActivationWidth;
	private int fActiMarginTop;
	private int fActiMarginBottom;

	private int fLeftMargin;
	private Font fFont;
	private int fFontSize;

	// frame
	private int frameTagLineIncline;
	private int frameMarginVertical;
	private int frameMarginHorizon;
	private int framePaddingTop;
	private int framePaddingBottom;
	private int frameTagHeight;


	// message 
	private int fyMessageTop;
	private int fyMessageMargin;
	private int fyMessageBottom;

	// message type
	private int selfMessXPos;
	private int selfMessYPos;


	// diagram
	private boolean showBrowser = false;

	private static final float DASH1[] = { 5.0f };
	private static final BasicStroke DASHEDSTROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, DASH1, 0.0f);

	

    public UCProperties() {

        fYStart = 30;
		fYStep = 25;
		fXStep = 140;
		fYScroll = 62;
		fLeftMargin = 140;

		fyMessageTop = 10;
		fyMessageMargin = 30;
		fyMessageBottom = 30;

		fyLifeLineHeight = 20;

		fActivationHeight = 10;
		fActivationWidth = 10;
		fActiMarginBottom= 3;
		fActiMarginTop = 3;
		fFont = Font.getFont("use.gui.view.sequencediagram", getFont());
		fFontSize = fFont.getSize();

		frameTagLineIncline = 5;
		frameMarginVertical = 60;
		frameMarginHorizon = 15;
		
		framePaddingTop = 10;
		framePaddingBottom = 10;
		frameTagHeight = 5;

		selfMessXPos = 30;
		selfMessYPos = 25;
    }

	public int getSelfMessXPos() {
		return selfMessXPos;
	}

	public int getSelfMessYPos() {
		return selfMessYPos;
	}

	public boolean getShowBrowser() {
		return showBrowser;
	}

	public void setShowBrowser(boolean show) {
		showBrowser = show;
	}

	public int getFramePaddingTop() {
		return framePaddingTop;
	}

	public int getFramePaddingBottom() {
		return framePaddingBottom;
	}

	public int getFrameTagHeight() {
		return frameTagHeight;
	}
	

	public int getFrameMarginVertical() {
		return frameMarginVertical;
	}

	public int getFrameMarginHorizon() {
		return frameMarginHorizon;
	}

	public int getFrameTagLineIncline() {
		return frameTagLineIncline;
	}

	public BasicStroke getDASHEDSTROKE() {
		return DASHEDSTROKE;
	}
    public Font getFont() {
		return fFont;
	}

    public int yScroll() {
		return fYScroll;
	}

    public int getLeftMargin() {
		return fLeftMargin;
	}

	public int yStart() {
		return fYStart;
	}

    public int llStep() {
		return fXStep;
	}

	public void updatefyLifeLineHeight(int fyPos) {
		if (fyPos < fyLifeLineHeight) return;
		fyLifeLineHeight = fyPos + fyMessageBottom;
	}

	public int getFyLifeLineHeight() {
		return fyLifeLineHeight;
	}

	public int yyStep() {
		return fYStep;
	}

	public void updateYStartDashLine(int y) {
		if(y > fYStart) {
			fYStart = y;
			fyLifeLineHeight += fYStart;
		}
	}

	public int yPosStart() {
		return fYStart + fyMessageTop - fyMessageMargin;
	}

	public int getFyMessageMargin() {
		return fyMessageMargin;
	}

	public int yPosState() {
		return fYPosState;
	}

	public void setfYPosState(int pos) {
		this.fYPosState = pos;
	}

	public int getfActivationWidth() {
		return fActivationWidth;
	}
	public int getfActivationHeight() {
		return fActivationHeight;
	}
	public int getfActiMarginTop() {
		return fActiMarginTop;
	}
	public int getfActiMarginBottom() {
		return fActiMarginBottom;
	}
}
