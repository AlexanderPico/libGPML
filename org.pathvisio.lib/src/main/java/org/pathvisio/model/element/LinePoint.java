/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2021 BiGCaT Bioinformatics, WikiPathways
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.pathvisio.model.element;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.graphics.Coordinate;
import org.pathvisio.model.type.ArrowHeadType;

/**
 * This class stores information for a Point pathway element. This class is
 * named LinePoint to avoid name conflict with awt.Point in downstream
 * applications.
 * 
 * @author finterly
 */
public class LinePoint extends PathwayElement {

	private LineElement lineElement;
	private ArrowHeadType arrowHead;
	private Coordinate xy;
	private PathwayElement elementRef; // optional, the pathway element to which the point refers.
	private double relX; // optional
	private double relY; // optional

	/**
	 * Instantiates a Point pathway element, with reference to another pathway
	 * element.
	 * 
	 * @param pathwayModel the parent pathway model.
	 * @param elementId    the unique pathway element identifier.
	 * @param lineElement  the parent line element to which the point belongs.
	 * @param arrowHead    the glyph at the ends of lines, intermediate points have
	 *                     arrowhead type "line" by default.
	 * @param xy           the xy coordinate position of the point.
	 * @param elementRef   the pathway element to which the point refers.
	 * @param relX         the relative x coordinate.
	 * @param relY         the relative x coordinate.
	 */
	public LinePoint(PathwayModel pathwayModel, String elementId, LineElement lineElement, ArrowHeadType arrowHead,
			Coordinate xy, PathwayElement elementRef, double relX, double relY) {
		super(pathwayModel, elementId);
		this.lineElement = lineElement;
		this.arrowHead = arrowHead;
		this.xy = xy;
		this.elementRef = elementRef;
		this.relX = relX;
		this.relY = relY;
	}

	/**
	 * Instantiates a Point pathway element, with no reference to another pathway
	 * element.
	 * 
	 * @param pathwayModel the parent pathway model.
	 * @param elementId    the unique pathway element identifier.
	 * @param lineElement  the parent line pathway element.
	 * @param arrowHead    the arrowhead property of the point (line by default).
	 * @param xy           the xy coordinate position of the point.
	 */
	public LinePoint(PathwayModel pathwayModel, String elementId, LineElement lineElement, ArrowHeadType arrowHead,
			Coordinate xy) {
		super(pathwayModel, elementId);
		this.lineElement = lineElement;
		this.arrowHead = arrowHead;
		this.xy = xy;
	}

	/**
	 * Returns the parent interaction or graphicalLine for this point.
	 * 
	 * @return lineElement the parent line element of this point.
	 */
	public LineElement getLineElement() {
		return lineElement;
	}

	/**
	 * Checks whether this point has a parent line element.
	 *
	 * @return true if and only if the line element of this point is effective.
	 */
	public boolean hasLineElement() {
		return getLineElement() != null;
	}

	/**
	 * Sets the parent interaction or graphicalLine for this point.
	 * 
	 * @param lineElement the line element to set.
	 */
	public void setLineElementTo(LineElement lineElement) {
		if (lineElement == null)
			throw new IllegalArgumentException("Invalid line pathway element.");
		if (hasLineElement())
			throw new IllegalStateException("Point already belongs to a line element.");
		setLineElement(lineElement);
		lineElement.addPoint(this);
	}

	/**
	 * Sets the parent interaction or graphicalLine for this point.
	 * 
	 * @param lineElement the line element to set.
	 */
	private void setLineElement(LineElement lineElement) {
		assert (lineElement != null);
		this.lineElement = lineElement;
	}

	/**
	 * Unsets the line element, if any, from this point.
	 */
	public void unsetLineElement() {
		if (hasLineElement()) {
			LineElement formerLineElement = this.getLineElement();
			setLineElement(null);
			formerLineElement.removePoint(this);
		}
	}

	/**
	 * Returns the arrowHead property of the point. Arrowhead specifies the glyph at
	 * the ends of graphical lines and interactions. Intermediate points have
	 * arrowhead type LINE (the absence of an arrowhead).
	 * 
	 * @return arrowhead the arrowhead property of the point.
	 */
	public ArrowHeadType getArrowHead() {
		if (arrowHead == null) {
			return ArrowHeadType.UNDIRECTED;
		} else {
			return arrowHead;
		}
	}

	/**
	 * Sets the arrowHead property of the point. Arrowhead specifies the glyph at
	 * the ends of graphical lines and interactions. Intermediate points have
	 * arrowhead type LINE (the absence of an arrowhead).
	 * 
	 * @param arrowHead the arrowhead property of the point.
	 */
	public void setArrowHead(ArrowHeadType arrowHead) {
		this.arrowHead = arrowHead;
	}

	/**
	 * Returns the xy coordinate position of the point.
	 * 
	 * @return xy the xy coordinate position of the point.
	 */
	public Coordinate getXY() {
		return xy;
	}

	/**
	 * Sets the xy coordinate position of the point.
	 * 
	 * @param xy the xy coordinate position of the point.
	 */
	public void setXY(Coordinate xy) {
		this.xy = xy;
	}

	/**
	 * Returns the pathway element to which the point refers to. In GPML, this is
	 * elementRef which refers to the elementId of a pathway element.
	 * 
	 * @return elementRef the pathway element to which the point refers.
	 */
	public PathwayElement getElementRef() {
		return elementRef;
	}

	/**
	 * Sets the pathway element to which the point refers to. In GPML, this is
	 * elementRef which refers to the elementId of a pathway element.
	 * 
	 * @param elementRef the pathway element to which the point refers.
	 */
	public void setElementRef(PathwayElement elementRef) {
		this.elementRef = elementRef;
	}

	/**
	 * Returns the relative x coordinate. When the given point is linked to a
	 * pathway element, relX and relY are the relative coordinates on the element,
	 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
	 * of the object.
	 * 
	 * @return relX the relative x coordinate.
	 */
	public double getRelX() {
		return relX;
	}

	/**
	 * Sets the relative x coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relX the relative x coordinate.
	 * @throws IllegalArgumentException if relX is not between -1.0 and 1.0. t
	 */
	public void setRelX(double relX) {
		if (Math.abs(relX) > 1.0) {
			Logger.log.trace("Warning: relX absolute value of " + String.valueOf(relX) + " greater than 1");
		}
		this.relX = relX;
	}

	/**
	 * Returns the relative y coordinate. When the given point is linked to a
	 * pathway element, relX and relY are the relative coordinates on the element,
	 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
	 * of the object.
	 * 
	 * @return relY the relative y coordinate.
	 */
	public double getRelY() {
		return relY;
	}

	/**
	 * Sets the relative y coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relY the relative y coordinate.
	 */
	public void setRelY(double relY) {
		if (Math.abs(relY) > 1.0) {
			Logger.log.trace("Warning: relY absolute value of " + String.valueOf(relY) + " greater than 1");
		}
		this.relY = relY;
	}
	
	/**
	 * Terminates this point. The pathway model and line element, if any, are unset
	 * from this point.
	 */
	@Override
	public void terminate() {
		unsetPathwayModel();
		unsetLineElement();
	}

}
