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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.Xref;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.type.GroupType;
import org.pathvisio.props.StaticProperty;
import org.pathvisio.util.Utils;

/**
 * This class stores all information relevant to a Group pathway element.
 * 
 * @author finterly
 */
public class Group extends ShapedElement {

	private GroupType type = GroupType.GROUP;
	private String textLabel; // optional
	private Xref xref; // optional
	/* list of pathway elements which belong to the group. */
	private List<Groupable> pathwayElements; // should have at least one pathway element

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates a Group given all possible parameters.
	 * 
	 * @param type      the type of the group.
	 * @param textLabel the text of the group.
	 * @param xref      the group Xref.
	 */
	public Group(GroupType type, String textLabel, Xref xref) {
		super();
		this.type = type;
		this.textLabel = textLabel;
		this.xref = xref;
		this.pathwayElements = new ArrayList<Groupable>();
	}

	/**
	 * Instantiates a Group given all possible parameters except textLabel.
	 */
	public Group(GroupType type, Xref xref) {
		this(type, null, xref);
	}

	/**
	 * Instantiates a Group given all possible parameters except xref.
	 */
	public Group(GroupType type, String textLabel) {
		this(type, textLabel, null);
	}

	/**
	 * Instantiates a Group given all possible parameters except textLabel and xref.
	 */
	public Group(GroupType type) {
		this(type, null, null);
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the list of pathway element members of the group.
	 * 
	 * @return pathwayElements the list of pathway elements belonging to the group.
	 */
	public List<Groupable> getPathwayElements() {
		return pathwayElements;
	}

	/**
	 * Checks whether pathwayElements has the given pathwayElement.
	 * 
	 * @param pathwayElement the pathway element to look for.
	 * @return true if has pathwayElement, false otherwise.
	 */
	public boolean hasPathwayElement(Groupable pathwayElement) {
		return pathwayElements.contains(pathwayElement);
	}

	/**
	 * Adds the given pathway element to pathwayElements list of this group. Checks
	 * if pathway element is valid. Sets groupRef of pathway element to this group
	 * if necessary.
	 * 
	 * @param pathwayElement the given pathwayElement to add.
	 */
	public void addPathwayElement(Groupable pathwayElement) {
		assert (pathwayElement != null);
		// set groupRef for pathway element if necessary
		if (pathwayElement.getGroupRef() == null || pathwayElement.getGroupRef() != this)
			pathwayElement.setGroupRefTo(this);
		// add pathway element to this group
		if (pathwayElement.getGroupRef() == this && !hasPathwayElement(pathwayElement))
			pathwayElements.add(pathwayElement);
		// TODO recalculate size
	}

	/**
	 * Removes the given pathway element from pathwayElements list of the group.
	 * Checks if pathway element is valid. Unsets groupRef of pathway element from
	 * this group if necessary.
	 * 
	 * @param pathwayElement the given pathwayElement to remove.
	 */
	public void removePathwayElement(Groupable pathwayElement) {
		assert (pathwayElement != null);
		// unset groupRef for pathway element if necessary
		if (pathwayElement.getGroupRef() == this)
			pathwayElement.unsetGroupRef();
		// remove pathway element from this group
		if (pathwayElement.getGroupRef() == null && hasPathwayElement(pathwayElement))
			pathwayElements.remove(pathwayElement);
		// TODO recalculate size
	}

	/**
	 * Removes all pathway elements from the pathwayElements list.
	 */
	public void removePathwayElements() {
		for (int i = 0; i < pathwayElements.size(); i++) {
			removePathwayElement(pathwayElements.get(i));
		}
	}

	/**
	 * Returns GroupType. GroupType is GROUP by default.
	 * 
	 * @return type the type of group, e.g. complex.
	 */
	public GroupType getType() {
		return type;
	}

	/**
	 * Sets GroupType to the given groupType.
	 * 
	 * @param v the type to set for this group, e.g. complex.
	 */
	public void setType(GroupType v) {
		if (type != v && v != null) {
			type = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPTYPE));
		}
	}

	/**
	 * Returns the text of of this group.
	 * 
	 * @return textLabel the text of of this group.
	 * 
	 */
	@Override
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of this shaped pathway element.
	 * 
	 * @param v the text to set.
	 */
	@Override
	public void setTextLabel(String v) {
		String value = (v == null) ? "" : v;
		if (!Utils.stringEquals(textLabel, value)) {
			textLabel = value;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TEXTLABEL));
		}
	}

	/**
	 * Returns the Xref for the group.
	 * 
	 * @return xref the xref of this group
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this group.
	 * 
	 * @param v the xref to set for this group.
	 */
	public void setXref(Xref v) {
		if (v != null) {
			xref = v;
			// TODO
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
		}
	}

	/**
	 * Terminates this group. The pathway model and pathway elements, if any, are
	 * unset or removed from this group. Links to all annotationRefs, citationRefs,
	 * and evidenceRefs are removed from this group.
	 */
	@Override
	public void terminate() {
		removePathwayElements();
		super.terminate();
	}

	// ================================================================================
	// Bounds Methods
	// ================================================================================
	/**
	 * Default margins for group bounding-box in GPML2013a. Makes the bounds
	 * slightly larger than the summed bounds of the containing elements.
	 */
	public static final double DEFAULT_M_MARGIN = 8;
	public static final double COMPLEX_M_MARGIN = 12;

	/**
	 * Returns margin for group bounding-box around contained elements depending on
	 * group type, as specified in GPML2013a.
	 * 
	 * @return the margin for group.
	 */
	public double getMargin() {
		if (type == GroupType.COMPLEX) {
			return COMPLEX_M_MARGIN;
		} else {
			return DEFAULT_M_MARGIN;
		}
	}

	/**
	 * Center x of the group bounds
	 */
	@Override
	public double getCenterX() {
		return getBounds().getCenterX();
	}

	/**
	 * Center y of the group bounds
	 */
	@Override
	public double getCenterY() {
		return getBounds().getCenterY();
	}

	/**
	 * Width of the group bounds
	 */
	@Override
	public double getWidth() {
		return getBounds().getWidth();
	}

	/**
	 * Height of the group bounds
	 */
	@Override
	public double getHeight() {
		return getBounds().getHeight();
	}

	/**
	 * Left of the group bounds
	 */
	@Override
	public double getLeft() {
		return getBounds().getX();
	}

	/**
	 * Top of the group bounds
	 */
	@Override
	public double getTop() {
		return getBounds().getY();
	}

	@Override
	public void setCenterX(double v) {
		double d = v - getBounds().getCenterX();
		for (Groupable e : pathwayElements) {
			e.setCenterX(e.getCenterX() + d);
		}
	}

	@Override
	public void setCenterY(double v) {
		double d = v - getBounds().getCenterY();
		for (Groupable e : pathwayElements) {
			e.setCenterY(e.getCenterY() + d);
		}
	}

	@Override
	public void setWidth(double v) {
		double d = v - getBounds().getWidth();
		for (Groupable e : pathwayElements) {
			if (e instanceof ShapedElement) {
				((ShapedElement) e).setWidth(e.getWidth() + d);
			}
		}
	}

	@Override
	public void setHeight(double v) {
		double d = v - getBounds().getHeight();
		for (Groupable e : pathwayElements) {
			if (e instanceof ShapedElement) {
			((ShapedElement) e).setHeight(e.getHeight() + d);}
		}
	}

	@Override
	public void setLeft(double v) {
		double d = v - getBounds().getX();
		for (Groupable e : pathwayElements) {
			e.setLeft(e.getLeft() + d);
		}
	}

	@Override
	public void setTop(double v) {
		double d = v - getBounds().getY();
		for (Groupable e : pathwayElements) {
			e.setTop(e.getTop() + d);
		}
	}

	/**
	 * Iterates over all group elements to find the total rectangular bounds, taking
	 * into account rotation of the nested elements
	 * 
	 * @return the rectangular bounds for this group with rotation taken into
	 *         account.
	 */
	@Override
	public Rectangle2D getRotatedBounds() {
		Rectangle2D bounds = null;
		for (Groupable e : pathwayElements) {
			if (e == this)
				continue; // To prevent recursion error
			if (bounds == null)
				bounds = e.getRotatedBounds();
			else
				bounds.add(e.getRotatedBounds());
		}
		if (bounds != null) {
			double margin = getMargin();
			return new Rectangle2D.Double(bounds.getX() - margin, bounds.getY() - margin,
					bounds.getWidth() + 2 * margin, bounds.getHeight() + 2 * margin);
		} else {
			return new Rectangle2D.Double();
		}
	}

	/**
	 * Iterates over all group elements to find the total rectangular bounds. Note:
	 * doesn't include rotation of the nested elements. If you want to include
	 * rotation, use {@link #getRotatedBounds()} instead.
	 * 
	 * @return the rectangular bounds for this group.
	 */
	@Override
	public Rectangle2D getBounds() {
		Rectangle2D bounds = null;
		for (Groupable e : pathwayElements) {
			if (e == this)
				continue; // To prevent recursion error
			if (bounds == null)
				bounds = e.getBounds();
			else
				bounds.add(e.getBounds());
		}
		if (bounds != null) {
			double margin = getMargin();
			return new Rectangle2D.Double(bounds.getX() - margin, bounds.getY() - margin,
					bounds.getWidth() + 2 * margin, bounds.getHeight() + 2 * margin);
		} else {
			return new Rectangle2D.Double();
		}
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Note: doesn't change parent, only fields
	 *
	 * Used by UndoAction.
	 *
	 * @param src
	 */
	public void copyValuesFrom(Group src) { // TODO
		super.copyValuesFrom(src);
		textLabel = src.textLabel;
		type = src.type;
		xref = src.xref;
		fireObjectModifiedEvent(PathwayElementEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public Group copy() {
		Group result = new Group(type); // TODO
		result.copyValuesFrom(this);
		return result;
	}

}
