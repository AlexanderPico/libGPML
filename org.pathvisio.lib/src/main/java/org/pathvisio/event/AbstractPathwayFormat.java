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
package org.pathvisio.event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of PathwayImporter and PathwayExporter warnings
 * mechanism.
 */
public abstract class AbstractPathwayFormat implements PathwayModelImporter, PathwayModelExporter {
	private List<String> warnings = new ArrayList<String>();

	protected void clearWarnings() {
		warnings.clear();
	}

	/**
	 * Can be used by overriding classes to add to the list of warnings. Don't
	 * forget to call {@link clearWarnings} at the start of conversion.
	 * 
	 * @param warning
	 */
	protected void emitWarning(String warning) {
		warnings.add(warning);
	}

//	@Override TODO 
	public boolean isCorrectType(File f) {
		return true;
	}

//	@Override TODO
	public List<String> getWarnings() {
		return warnings;
	}

}