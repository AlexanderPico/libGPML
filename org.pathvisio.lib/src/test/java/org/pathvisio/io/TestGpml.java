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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.bridgedb.Xref;
import org.pathvisio.io.*;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.type.*;

import junit.framework.TestCase;

public class TestGpml extends TestCase {

	private static final File PATHVISIO_BASEDIR = new File("../..");

	public static void testReadWrite() throws IOException, ConverterException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("readtest.xml");
		File file = new File(url.getPath());
		assertTrue (file.exists());
	
		PathwayModel pathwayModel = new PathwayModel();
		pathwayModel.readFromXml(file, true);
		
//		
		File tmp = File.createTempFile("testwrite", "gpml");
		GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp, true);
		System.out.println(tmp);
	}
}