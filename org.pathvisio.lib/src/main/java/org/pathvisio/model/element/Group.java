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

import java.util.ArrayList;
import java.util.List;

import org.bridgedb.Xref;
import org.pathvisio.model.graphics.FontProperty;
import org.pathvisio.model.graphics.RectProperty;
import org.pathvisio.model.graphics.ShapeStyleProperty;
import org.pathvisio.model.type.GroupType;

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

	/**
	 * Instantiates a Group given all possible parameters.
	 * 
	 * @param rectProperty       the centering (position) and dimension properties.
	 * @param fontProperty       the font properties, e.g. textColor, fontName...
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor.
	 * @param groupRef           the parent group in which the pathway element
	 *                           belongs.
	 * @param type               the type of the group.
	 * @param textLabel          the text of the group.
	 * @param xref               the group Xref.
	 */
	public Group(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			Group groupRef, GroupType type, String textLabel, Xref xref) {
		super(rectProperty, fontProperty, shapeStyleProperty, groupRef);
		this.type = type;
		this.textLabel = textLabel;
		this.xref = xref;
		this.pathwayElements = new ArrayList<Groupable>();
	}

	/**
	 * Instantiates a Group given all possible parameters except textLabel.
	 */
	public Group(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			Group groupRef, GroupType type, Xref xref) {
		this(rectProperty, fontProperty, shapeStyleProperty, groupRef, type, null, xref);
	}

	/**
	 * Instantiates a Group given all possible parameters except xref.
	 */
	public Group(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			Group groupRef, GroupType type, String textLabel) {
		this(rectProperty, fontProperty, shapeStyleProperty, groupRef, type, textLabel, null);
	}

	/**
	 * Instantiates a Group given all possible parameters except textLabel and xref.
	 */
	public Group(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			Group groupRef, GroupType type) {
		this(rectProperty, fontProperty, shapeStyleProperty, groupRef, type, null, null);
	}

	/**
	 * Instantiates a Group given all possible parameters except groupRef, because
	 * the group does not belong in another group.
	 */
	public Group(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			GroupType type, String textLabel, Xref xref) {
		this(rectProperty, fontProperty, shapeStyleProperty, null, type, textLabel, xref);
	}

	/**
	 * Instantiates a Group given all possible parameters except groupRef and xref,
	 * because the group neither belongs in another group nor has xref.
	 */
	public Group(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			GroupType type, String textLabel) {
		this(rectProperty, fontProperty, shapeStyleProperty, null, type, textLabel, null);
	}

	/**
	 * Instantiates a Group given all possible parameters except groupRef and
	 * textLabel, because the group neither belongs in another group nor has
	 * textLabel.
	 */
	public Group(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			GroupType type, Xref xref) {
		this(rectProperty, fontProperty, shapeStyleProperty, null, type, null, xref);
	}

	/**
	 * Instantiates a Group given all possible parameters except groupRef,
	 * textLabel, and xref, because the group neither belongs in another group, nor
	 * has a textLabel, nor has xref.
	 */
	public Group(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			GroupType type) {
		this(rectProperty, fontProperty, shapeStyleProperty, null, type, null, null);
	}

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
	 * Adds the given pathway element to pathwayElements list of the group.
	 * 
	 * @param pathwayElement the given pathwayElement to add.
	 */
	public void addPathwayElement(Groupable pathwayElement) {
		assert (pathwayElement != null);
		Group groupRef = pathwayElement.getGroupRef();
		// set groupRef for pathway element if applicable
		if (groupRef == null)
			pathwayElement.setGroupRefTo(this);
		// add pathway element to this group
		if (groupRef == this && !hasPathwayElement(pathwayElement))
			pathwayElements.add(pathwayElement);
		// otherwise cannot add pathway element
		if (groupRef != this && groupRef != null)
			throw new IllegalStateException("Pathway element belongs to another group.");
		if (hasPathwayElement(pathwayElement))
			throw new IllegalStateException("Pathway element already belongs to this group.");

	}

	/**
	 * Removes the given pathway element from pathwayElements list of the group.
	 * 
	 * @param pathwayElement the given pathwayElement to remove.
	 */
	public void removePathwayElement(Groupable pathwayElement) {
		assert (pathwayElement != null);
		Group groupRef = pathwayElement.getGroupRef();
		// unset groupRef for pathway element if applicable
		if (groupRef == this)
			pathwayElement.unsetGroupRef();
		if (groupRef == null && hasPathwayElement(pathwayElement))
			pathwayElements.remove(pathwayElement);
		// otherwise cannot remove pathway element
		if ((groupRef != this && groupRef != null) || !hasPathwayElement(pathwayElement))
			throw new IllegalStateException("Pathway element does not belong to this group.");
	}

	/**
	 * Removes all pathway elements from the pathwayElements list.
	 */
	public void removePathwayElement() {
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
	 * @param type the type of group, e.g. complex.
	 */
	public void setType(GroupType type) {
		this.type = type;
	}

	/**
	 * Returns the text of of the group.
	 * 
	 * @return textLabel the text of of the group.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the group.
	 * 
	 * @param textLabel the text of of the group.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Returns the Xref for the group.
	 * 
	 * @return xref the xref of the group
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the group.
	 * 
	 * @param xref the xref of the group.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

}
