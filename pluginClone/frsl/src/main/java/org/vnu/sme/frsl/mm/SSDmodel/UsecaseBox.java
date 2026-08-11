package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;


public class UsecaseBox {
    /**
		 * Padding between object box's label and underline
		 */
		private static final int LABEL_UNDERLINE_PADDING = 2;

		/**
		 * Margin from label to the top line of object box
		 */
		private static final int TOP_MARGIN = 2;

		/**
		 * Sum of left margin and right margin from label
		 */
		private static final int LEFT_RIGHT_MARGIN = 10;

		/**
		 * Sum of top margin and bottom margin from label
		 */
		private static final int TOP_BOTTOM_MARGIN = 8;

		/**
		 * The x-value of the corresponding lifeline -> the center of the object
		 * box.
		 */
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

		/**
		 * Constructs a new object box.
		 * 
		 * @param xValue the center x-value of the object box
		 * @param yValue the y-value on which the object box starts
		 * @param lifeline the corresponding lifeline
		 * @param name the name of the corresponding object
		 */
		UsecaseBox(int xValue, int yValue, String name) {
			xPosOfBoxCenter = xValue;
			yPosOfBoxStart = yValue;
			xPosOfBoxStart = 0;
			xPosOfBoxEnd = 0;
			fName = name;
		}

		/**
		 * Returns the start x-value of the box.
		 * 
		 * @return the start x-value
		 */
		int getStart() {
			return xPosOfBoxStart;
		}

		/**
		 * Returns the end x-value of the box.
		 * 
		 * @return the end x-value
		 */
		int getEnd() {
			return xPosOfBoxEnd;
		}

		/**
		 * Returns the start y-value of the box.
		 * 
		 * @return the start y-value
		 */
		int getYPosOfBoxStart() {
			return yPosOfBoxStart;
		}

		/**
		 * Calculates the height of the box for the given font.
		 * 
		 */
		int getHeight() {
            return 30;
		}

		/**
		 * Calculates the width of the box for the given FontMetrics.
		 * 
		 */


		/**
		 * Sets the center x-value of the box.
		 * 
		 * @param x the new center x-value
		 */
		void setX(int x) {
			xPosOfBoxCenter = x;
		}

		/**
		 * Sets the start y-value of the box.
		 * 
		 * @param y the new start y-value
		 */
		void setY(int y) {
			yPosOfBoxStart = y;
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
			String name = "System: " + fName;
			// calculate width of the box label
			int labelWidth = fm.stringWidth(name);
			// calculate width of the object box
			int boxWidth = labelWidth + 2 * LEFT_RIGHT_MARGIN;
			setxPosOfBoxEnd(xPosOfBoxCenter + boxWidth);
			// calculate height of the box label
			int labelHeight = (fm.getFont()).getSize();
			// the y-value where the label should be drawn
			int yValue = y + labelHeight + TOP_MARGIN;
			// calculate height of box
			int boxHeight = getHeight();

			if (background) {
				graphic.setColor(Color.orange);
				graphic.fillRect(xPosOfBoxCenter - boxWidth / 2, y, boxWidth, boxHeight);
				graphic.setColor(Color.black);
			}


            // draw object name
            graphic.drawString(name, xPosOfBoxCenter - labelWidth /2 , yValue);
				
			// }
			// draw box
			graphic.drawRect(xPosOfBoxCenter - boxWidth / 2, y, boxWidth, boxHeight);

			
		}
	}
