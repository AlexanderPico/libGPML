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
package oldclasses.model;

import org.pathvisio.model.PathwayElement;

/**
 * This class stores information for a dynamic property. Dynamic property is
 * used to store arbitrary data. Is CommentGroup.Property in GPML.
 * 
 * NB: previously named CommentGroup.Attribute in GPML.
 * 
 * @author unknown, finterly
 */
public class DynamicProperty {

	private String key;
	private String value;
	private PathwayElement parent; //TODO

	/**
	 * Instantiates a Property, key value pair information.
	 * 
	 * @param key   the key of a key value pair.
	 * @param value the value of a key value pair.
	 */
	public DynamicProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Gets the key of a key value pair.
	 * 
	 * @return key the key of a key value pair.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of a key value pair.
	 * 
	 * @param key the key of a key value pair.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the value of a key value pair.
	 * 
	 * @return value the value of a key value pair.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of a key value pair.
	 * 
	 * @param value the value of a key value pair.
	 */
	public void setValue(String value) {
		this.value = value;
	}

}

/* ------------------------------- AS MAP OLD ------------------------------- */

