package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.tzi.use.gui.views.diagrams.behavior.DrawingUtil;

public class ActorBox {

		private static final int TOP_MARGIN = 2;

		private int xPosOfBoxCenter;

		/**
		 * The x value where the object box starts.
		 */
		private int xPosOfBoxStart;

		/**
		 * The x-value where the objct box ends.
		 */
		private int xPosOfBoxEnd;

		/**
		 * The y-value where the object box starts.
		 */
		private int yPosOfBoxStart;

		/**
		 * The name of the object which belongs to the object box.
		 */
		private String fName;

        private int heightActor = 0;

		/**
		 * Constructs a new object box.
		 * 
		 * @param xValue the center x-value of the object box
		 * @param yValue the y-value on which the object box starts
		 * @param lifeline the corresponding lifeline
		 * @param name the name of the corresponding object
		 */
		ActorBox(int xValue, int yValue, String name) {
			xPosOfBoxCenter = xValue;
			yPosOfBoxStart = yValue;
			xPosOfBoxEnd = 0;
			fName = name;
		}


		/**
		 * Calculates the height of the box for the given font.
		 * 
		 */
		int getHeight() {
            return heightActor;
		}

		/**
		 * Calculates the width of the box for the given FontMetrics.
		 * 
		 */

		int getWidth() {
		return 100;

		}

		void setxPosOfBoxEnd(int xPosOfBoxEnd) {
			this.xPosOfBoxEnd = xPosOfBoxEnd;
		}

		public int getxPosOfBoxEnd() {
			return xPosOfBoxEnd;
		}
		
		/**
		 * Draws the box in the diagram.
		 * 
		 * @param graphic the graphic where the object box should be drawn in.
		 * @param fm the FontMetrics of the sequence diagram
		 * @param y the y-Value where the box begins.
		 */
		void drawBox(Graphics2D graphic, FontMetrics fm, int y, boolean background) {
			String name = " :" + fName;
			int labelHeight = (fm.getFont()).getSize();
			// the y-value where the label should be drawn
			int yValue = y + labelHeight + TOP_MARGIN;
			setxPosOfBoxEnd(xPosOfBoxCenter + fm.stringWidth(name) / 2 );
			
            DrawingUtil.drawBigActor(xPosOfBoxCenter, yValue, graphic);
            // draw object name
            heightActor = yValue + DrawingUtil.TOTAL_HEIGHT_BIG + fm.getAscent() + 2 * 5;
            
            graphic.drawString(name, (xPosOfBoxCenter - fm.stringWidth(name) / 2), heightActor);
            
            heightActor += labelHeight;   
				
		}
}
