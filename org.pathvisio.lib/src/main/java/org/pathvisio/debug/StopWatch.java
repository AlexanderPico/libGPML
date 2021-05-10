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
package org.pathvisio.debug;

/**
 * A simple helper class for speed optimizations
 * 
 * @author unknown
 */
public class StopWatch {
	boolean running;
	long start;
	long last;

	/**
	 * 
	 */
	public void start() {
		start = System.currentTimeMillis();
		running = true;
	}

	/**
	 * @return
	 */
	public long stop() {
		last = System.currentTimeMillis() - start;
		running = false;
		return last;
	}

	/**
	 * @return
	 */
	public long look() {
		if (running)
			return System.currentTimeMillis() - start;
		return last;
	}

	/**
	 * @param msg
	 */
	public void stopToLog(String msg) {
		Logger.log.trace(msg + "\t" + stop());
	}
}
