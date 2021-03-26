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
package org.pathvisio.model;


/**
 * This class stores information for a Point pathway element.
 * 
 * @author finterly
 */
public class Point extends PathwayElement {

	private ArrowHeadType arrowHead; // line by default
	private Coordinate xy;
	/*
	 * Below are optional parameters for when a point refers to a pathway element.
	 */
	private PathwayElement elementRef; // optional
	private double relX; // optional
	private double relY; // optional

	/**
	 * Instantiates a Point pathway element, with reference to another pathway
	 * element.
	 * 
	 * @param elementId  the unique pathway element identifier.
	 * @param elementRef the pathway element to which the point refers.
	 * @param arrowHead  the glyph at the ends of lines, intermediate points have
	 *                   arrowhead type "line" by default.
	 * @param x          the x coordinate position of the point.
	 * @param y          the y coordinate position of the point.
	 * @param relX       the relative x coordinate.
	 * @param relY       the relative x coordinate.
	 */
	public Point(String elementId, PathwayModel pathwayModel, ArrowHeadType arrowHead, Coordinate xy,
			PathwayElement elementRef, double relX, double relY) {
		super(elementId, pathwayModel);
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
	 * @param elementId the unique pathway element identifier.
	 * @param arrowHead the arrowhead property of the point (line by default).
	 * @param x         the x coordinate position of the point.
	 * @param y         the y coordinate position of the point.
	 */
	public Point(String elementId, PathwayModel pathwayModel, ArrowHeadType arrowHead, Coordinate xy) {
		super(elementId, pathwayModel);
		this.arrowHead = arrowHead;
		this.xy = xy;
	}

	/**
	 * Returns the arrowHead property of the point. Arrowhead specifies the glyph at
	 * the ends of graphical lines and interactions. Intermediate points have
	 * arrowhead type LINE (the absence of an arrowhead).
	 * 
	 * @return arrowhead the arrowhead property of the point.
	 * 
	 */
	public ArrowHeadType getArrowHead() {
		if (arrowHead == null) {
			return ArrowHeadType.LINE;
		} else {
			return arrowHead;
		}
	}

	/**
	 * Sets the arrowHead property of the point. Arrowhead specifies the glyph at
	 * the ends of graphical lines and interactions. Intermediate points have
	 * arrowhead type LINE (the absence of an arrowhead).
	 * 
	 * @param arrowhead the arrowhead property of the point.
	 * 
	 */
	public void setArrowHead(ArrowHeadType arrowHead) {
		this.arrowHead = arrowHead;
	}

	/**
	 * Returns the xy coordinate position of the point.
	 * 
	 * @param xy the xy coordinate position of the point.
	 */
	public Coordinate getXY() {
		return xy;
	}

	/**
	 * Sets the xy coordinate position of the point.
	 * 
	 * @return xy the xy coordinate position of the point.
	 */
	public void setXY(Coordinate xy) {
		this.xy = xy;
	}

	/**
	 * @return
	 */
	public PathwayElement getElementRef() {
		return elementRef;
	}

	/**
	 * @param elementRef
	 */
	public void setElementRef(PathwayElement elementRef) {
		this.elementRef = elementRef;
	}

	/**
	 * Gets the relative x coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relX the relative x coordinate.
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
		if (Math.abs(relX) <= 1.0) {
			this.relX = relX;
		} else {
			throw new IllegalArgumentException("relX " + relX + " should be between -1.0 and 1.0");
		}
	}

	/**
	 * Gets the relative y coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relY the relative y coordinate.
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
		if (Math.abs(relY) <= 1.0) {
			this.relY = relY;
		} else {
			throw new IllegalArgumentException("relY " + relY + " should be between -1.0 and 1.0");
		}
	}

}
