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
package org.pathvisio.model.graphics;

import org.pathvisio.model.elements.Anchor;
import org.pathvisio.model.elements.Point;

/**
 * This class holds coordinates x and y.
 * 
 * Used in {@link InfoBox}, {@link RectProperty}, {@link GenericPoint},
 * {@link Point}, and {@link Anchor}.
 * 
 * @author finterly
 */
public class Coordinate {

	private double x;
	private double y;

	/**
	 * Instantiates a Coordinate with x and y coordinate values. Coordinate values
	 * cannot be negative.
	 * 
	 * @param x the coordinate value for x.
	 * @param y the coordinate value for y.
	 */
	public Coordinate(double x, double y) {
		if (x < 0)
			throw new IllegalArgumentException("x coordinate < 0");
		if (y < 0)
			throw new IllegalArgumentException("y coordinate < 0");
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets x coordinate value.
	 * 
	 * @param x the coordinate value for x.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets x coordinate to given value.
	 * 
	 * @param x the coordinate value for x.
	 */
	public void setX(double x) {
		if (x < 0)
			throw new IllegalArgumentException("x coordinate < 0");
		this.x = x;
	}

	/**
	 * Gets y coordinate value.
	 * 
	 * @param y the coordinate value for y.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets y coordinate to given value.
	 * 
	 * @param y the coordinate value for y.
	 */
	public void setY(double y) {
		if (y < 0)
			throw new IllegalArgumentException("y coordinate < 0");
		this.y = y;
	}

	/**
	 * Returns true is given object is equal to this Coordinate object.
	 * 
	 * @return true if objects equal.
	 */
	public boolean equals(final Object o) {
		return this.equals((Coordinate) o);
	}

	/**
	 * Returns true is given Coordinate object is equal to this Coordinate object.
	 * 
	 * @param c the given coordinate object
	 * @return true if coordinates equal.
	 */
	public boolean equals(final Coordinate c) {
		return this.x == c.x && this.y == c.y;
	}

	/**
	 * Returns Coordinate as string of x/y value,
	 */
	public String toString() {
		return String.format("%f/%f", this.x, this.y);
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

}