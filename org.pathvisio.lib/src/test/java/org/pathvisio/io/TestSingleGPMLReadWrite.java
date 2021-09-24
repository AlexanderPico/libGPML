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
package org.pathvisio.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.pathvisio.model.PathwayModel;

import junit.framework.TestCase;

/**
 * Test for reading and writing of a single GPML2013a or GPML2021, for
 * troubleshooting and resolving specific issues.
 * 
 * @author finterly
 */
public class TestSingleGPMLReadWrite extends TestCase {

	/**
	 * Tests reading and writing of a GPML2013a/GPML2021 file. For debugging and
	 * looking in detail at one particular example.
	 * 
	 * @throws ConverterException
	 * @throws IOException
	 */
	public static void testReadWriteGPML() throws IOException, ConverterException {

		// file to be read
//		URL url = Thread.currentThread().getContextClassLoader().getResource("Hs_Differentiation_Pathway_WP2848_107975.gpml");
		URL url = Thread.currentThread().getContextClassLoader().getResource("WP1140.gpml");

		File file = new File(url.getPath());
		assertTrue(file.exists());

		PathwayModel pathwayModel = new PathwayModel();
		pathwayModel.readFromXml(file, true);

		// writes to temp
		File tmp = File.createTempFile("testwrite", ".gpml");

		// choose here whether to write in GPML2013a or GPML2021 format
		GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp, false);
//		GPML2013aWriter.GPML2013aWRITER.writeToXml(pathwayModel, tmp, true);
		System.out.println(tmp);

	}
}