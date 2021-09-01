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

import java.util.ArrayList;
import java.util.List;

import org.bridgedb.Xref;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.ref.ElementInfo;
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
	private List<ElementInfo> pathwayElements; // should have at least one pathway element

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
		this.pathwayElements = new ArrayList<ElementInfo>();
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

	/**
	 * Returns the list of pathway element members of the group.
	 * 
	 * @return pathwayElements the list of pathway elements belonging to the group.
	 */
	public List<ElementInfo> getPathwayElements() {
		return pathwayElements;
	}

	/**
	 * Checks whether pathwayElements has the given pathwayElement.
	 * 
	 * @param pathwayElement the pathway element to look for.
	 * @return true if has pathwayElement, false otherwise.
	 */
	public boolean hasPathwayElement(ElementInfo pathwayElement) {
		return pathwayElements.contains(pathwayElement);
	}

	/**
	 * Adds the given pathway element to pathwayElements list of this group. Checks
	 * if pathway element is valid. Sets groupRef of pathway element to this group
	 * if necessary.
	 * 
	 * @param pathwayElement the given pathwayElement to add.
	 */
	public void addPathwayElement(ElementInfo pathwayElement) {
		assert (pathwayElement != null);
		// set groupRef for pathway element if necessary
		if (pathwayElement.getGroupRef() == null || pathwayElement.getGroupRef() != this)
			pathwayElement.setGroupRefTo(this);
		// add pathway element to this group
		if (pathwayElement.getGroupRef() == this && !hasPathwayElement(pathwayElement))
			pathwayElements.add(pathwayElement);

	}

	/**
	 * Removes the given pathway element from pathwayElements list of the group.
	 * Checks if pathway element is valid. Unsets groupRef of pathway element from
	 * this group if necessary.
	 * 
	 * @param pathwayElement the given pathwayElement to remove.
	 */
	public void removePathwayElement(ElementInfo pathwayElement) {
		assert (pathwayElement != null);
		// unset groupRef for pathway element if necessary
		if (pathwayElement.getGroupRef() == this)
			pathwayElement.unsetGroupRef();
		// remove pathway element from this group
		if (pathwayElement.getGroupRef() == null && hasPathwayElement(pathwayElement))
			pathwayElements.remove(pathwayElement);
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
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of this shaped pathway element.
	 * 
	 * @param v the text to set.
	 */
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
		xref = v;
		// TODO
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
	}

	/**
	 * Terminates this group. The pathway model and pathway elements, if any, are
	 * unset or removed from this group. Links to all annotationRefs, citationRefs,
	 * and evidenceRefs are removed from this group.
	 */
	@Override
	public void terminate() {
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();
		removePathwayElements();
		unsetPathwayModel();
	}

}
