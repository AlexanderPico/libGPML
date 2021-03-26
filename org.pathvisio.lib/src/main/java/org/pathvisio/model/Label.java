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

import org.pathvisio.util.Utils;

/**
 * This class stores all information relevant to a Label pathway element.
 * 
 * @author finterly
 */
public class Label extends ShapedElement {

	private String elementId;
	private String textLabel = "";
	private String groupRef;
	private String href = ""; // hyperlink


	
//	/*
//	 * The group to which the shaped element belongs. In GPML, this is groupRef
//	 * which refers to elementId (formerly groupId) of a gpml:Group.
//	 */
//	private Group groupRef; //optional
//
//	
//	 * @param groupRef           the group to which the shaped element belongs. In
//	 *                           GPML, this is groupRef which refers to elementId
//	 *                           (formerly groupId) of a gpml:Group.
	 
//	/**
//	 * Returns the group to which the pathway element belongs. A groupRef indicates an object is
//	 * part of a gpml:Group with a elementId.
//	 * 
//	 * @return groupRef the groupRef of the pathway element.
//	 */
//	public Group getGroupRef() {
//		return groupRef;
//	}
//
//	/**
//	 * Sets the group to which the pathway element belongs. A groupRef indicates an object is
//	 * part of a gpml:Group with a elementId.
//	 * 
//	 * @param groupRef the groupRef of the pathway element.
//	 */
//	public void setGroupRef(Group groupRef) {
//		this.groupRef = groupRef;
//	}
//	 

	/**
	 * Gets the hyperlink for a Label.
	 * 
	 * @return href the hyperlink reference to a url.
	 */
	public String getHref() {
		return href;
	}

	/**
	 * Sets the hyperlink for a Label.
	 * 
	 * @param input the given string link. If input is null, href is set to
	 *              ""(empty).
	 */
	public void setHref(String input) {
		String link = (input == null) ? "" : input;
		if (!Utils.stringEquals(href, link)) {
			href = link;
			if (PreferenceManager.getCurrent() == null)
				PreferenceManager.init();
			setColor(PreferenceManager.getCurrent().getColor(GlobalPreference.COLOR_LINK));
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.HREF));
		}
	}

	/**
	 * Gets the elementId of the label.
	 * 
	 * @return elementId the unique id of the label.
	 * 
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the label.
	 * 
	 * @param elementId the unique id of the label.
	 * 
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the groupRef of the label. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the label.
	 * 
	 */
	public Object getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the label. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the label.
	 * 
	 */
	public void getGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * Gets the text of of the label.
	 * 
	 * @return textLabel the text of of the label.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the label.
	 * 
	 * @param textLabel the text of of the label.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	
	/**
	 * Sets the text label of this object to the given string or ""(empty) if input
	 * is null.
	 * 
	 * @param input the given string text. If input is null, textLabel is set to
	 *              ""(empty).
	 */
	public void setTextLabel(String input) {
		String text = (input == null) ? "" : input;
		if (!Utils.stringEquals(textLabel, text)) {
			textLabel = text;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TEXTLABEL));
		}
	}
	
	
	

}