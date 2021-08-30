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

import java.util.Set;

//import java.beans.PropertyChangeEvent;

import org.bridgedb.Xref;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.GraphLink.LinkableFrom;
import org.pathvisio.model.GraphLink.LinkableTo;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.ref.ElementInfo;
import org.pathvisio.model.type.StateType;
import org.pathvisio.props.StaticProperty;
import org.pathvisio.util.Utils;

/**
 * This class stores all information relevant to a State pathway element.
 * 
 * @author finterly
 */
public class State extends ShapedElement implements LinkableTo {

	private DataNode dataNode; // parent dataNode (NB: elementRef was formerly elementId of parent data node)
	private String textLabel;
	private StateType type;
	private double relX;
	private double relY;
	private Xref xref; // optional

	/**
	 * Instantiates a State pathway element given all possible parameters.
	 * 
	 * @param textLabel the text label of the state.
	 * @param type      the type of the state, e.g. protein modification.
	 * @param relX      the relative x coordinates on the parent object, where 0,0
	 *                  is at the center of the object and 1,1 at the bottom-right
	 *                  corner of the object.
	 * @param relY      the relative y coordinates on the parent object, where 0,0
	 *                  is at the center of the object and 1,1 at the bottom-right
	 *                  corner of the object.
	 * @param xref      the state xref.
	 */
	public State(String textLabel, StateType type, double relX, double relY, Xref xref) {
		super();
		this.textLabel = textLabel;
		this.type = type;
		this.relX = relX;
		this.relY = relY;
		this.xref = xref;
	}

	/**
	 * Instantiates a State pathway element given all possible parameters except
	 * xref.
	 */
	public State(String textLabel, StateType type, double relX, double relY) {
		this(textLabel, type, relX, relY, null);
	}

	/**
	 * Returns the parent data node to which the state belongs.
	 * 
	 * NB: prior to GPML2021, elementRef was used to refer to the elementId of
	 * parent data node.
	 * 
	 * @return dataNode the parent data node of the state.
	 */
	public DataNode getDataNode() {
		return dataNode;
	}

	/**
	 * Checks whether this state has a parent datanode.
	 *
	 * @return true if and only if the datanode of this state is effective.
	 */
	public boolean hasDataNode() {
		return getDataNode() != null;
	}

	/**
	 * Sets the parent data node to which the state belongs. NB: Only set when a
	 * data nodes adds this state. This method is not used directly.
	 * 
	 * NB: prior to GPML2021, elementRef was used to refer to the elementId of
	 * parent data node.
	 * 
	 * @param dataNode the parent data node of the state.
	 */
	protected void setDataNodeTo(DataNode dataNode) { // TODO Make LinkTooo?
		if (dataNode == null)
			throw new IllegalArgumentException("Invalid datanode.");
		if (hasDataNode())
			throw new IllegalStateException("State already belongs to a data node.");
		setDataNode(dataNode);
	}

	/**
	 * Sets the parent data node for this state. NB: This method is not used
	 * directly.
	 * 
	 * @param dataNode the given dataNode to set.
	 */
	private void setDataNode(DataNode dataNode) {
		this.dataNode = dataNode;
	}

	/**
	 * Unsets the data node, if any, from this state. NB: This method is not used
	 * directly.
	 */
	protected void unsetDataNode() {
		if (hasDataNode())
			setDataNode(null);
	}

	/**
	 * Returns the text of of the state.
	 * 
	 * @return textLabel the text of of the state.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the shaped pathway element.
	 * 
	 * @param textLabel the text of of the shaped pathway element.
	 */
	public void setTextLabel(String input) {
		String textLabel = (input == null) ? "" : input;
		if (!Utils.stringEquals(this.textLabel, textLabel)) {
			this.textLabel = textLabel;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TEXTLABEL));
		}
	}

	/**
	 * Returns the type of the state.
	 * 
	 * @return type the type of state, e.g. complex.
	 */
	public StateType getType() {
		return type;
	}

	/**
	 * Sets the type of the state.
	 * 
	 * @param type the type of state, e.g. complex.
	 */
	public void setType(StateType type) {
		if (this.type != type && type != null) {
			this.type = type;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.STATETYPE));
		}
	}

	/**
	 * Returns the relative x coordinate. When the given state is linked to a data
	 * node, relX and relY are the relative coordinates on the data node, where 0,0
	 * is at the center of the data node and 1,1 at the bottom right corner of the
	 * data node.
	 * 
	 * @return relX the relative x coordinate.
	 */
	public double getRelX() {
		return relX;
	}

	/**
	 * Sets the relative x coordinate. When the given state is linked to a data
	 * node, relX and relY are the relative coordinates on the data node, where 0,0
	 * is at the center of the data node and 1,1 at the bottom right corner of the
	 * data node.
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
	 * Returns the relative y coordinate. When the given state is linked to a data
	 * node, relX and relY are the relative coordinates on the data node, where 0,0
	 * is at the center of the data node and 1,1 at the bottom right corner of the
	 * data node.
	 * 
	 * @return relY the relative y coordinate.
	 */
	public double getRelY() {
		return relY;
	}

	/**
	 * Sets the relative y coordinate. When the given state is linked to a data
	 * node, relX and relY are the relative coordinates on the data node, where 0,0
	 * is at the center of the data node and 1,1 at the bottom right corner of the
	 * data node.
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

	/**
	 * Returns the Xref for the state.
	 * 
	 * @return xref the xref of the state.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the state.
	 * 
	 * @param xref the xref of the state.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
		// TODO
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
	}

	/**
	 * Returns {@link LinkableFrom} pathway elements, at this time that only goes
	 * for {@link LinePoint}, for this {@link LinkableTo} pathway element.
	 */
	@Override
	public Set<LinkableFrom> getLinkableFroms() {
		return GraphLink.getReferences(this, getPathwayModel());
	}

	/**
	 * Terminates this state. The pathway model and data node, if any, are unset
	 * from this state. Links to all annotationRefs, citationRefs, and evidenceRefs
	 * are removed from this state.
	 */
	@Override
	public void terminate() {
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();
		unsetDataNode();
		unsetPathwayModel();
	}

//	@Override
//	public void propertyChange(PropertyChangeEvent evt) {
//		float speed = model.getSpeed();
//		view.setSpeed(speed);
//	}

}
