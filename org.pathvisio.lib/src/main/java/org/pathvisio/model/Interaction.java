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

import org.bridgedb.Xref;
import org.pathvisio.event.PathwayObjectEvent;
import org.pathvisio.prop.StaticProperty;

/**
 * This class stores information for an Interaction pathway element.
 * 
 * @author finterly
 */
public class Interaction extends LineElement implements Xrefable {

	private Xref xref; // optional

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates an Interaction pathway element given all possible parameters.
	 * 
	 * @param xref the interaction Xref.
	 */
	public Interaction(Xref xref) {
		super();
		this.xref = xref;
	}

	/**
	 * Instantiates an Interaction pathway element given all required parameters
	 * except xref.
	 */
	public Interaction() {
		this(null);
	}
	
	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the Xref for this interaction.
	 * 
	 * @return xref the xref of interaction.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this interaction.
	 * 
	 * @param v the xref to set for this interaction.
	 */
	public void setXref(Xref v) {
		if (v != null) {
			xref = v;
			// TODO
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
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
	public void copyValuesFrom(Interaction src) { //TODO
		super.copyValuesFrom(src);
		xref = src.xref;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public Interaction copy() {
		Interaction result = new Interaction(); //TODO 
		result.copyValuesFrom(this);
		return result;
	}

}
