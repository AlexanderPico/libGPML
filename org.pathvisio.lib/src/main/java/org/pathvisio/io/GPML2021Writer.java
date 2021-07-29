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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.ref.Annotation;
import org.pathvisio.model.ref.AnnotationRef;
import org.pathvisio.model.ref.Author;
import org.pathvisio.model.ref.Citation;
import org.pathvisio.model.ref.CitationRef;
import org.pathvisio.model.ref.Comment;
import org.pathvisio.model.ref.ElementInfo;
import org.pathvisio.model.ref.Evidence;
import org.pathvisio.model.ref.EvidenceRef;
import org.pathvisio.model.ref.Pathway;
import org.pathvisio.model.ref.UrlRef;
import org.pathvisio.util.ColorUtils;
import org.pathvisio.util.XrefUtils;

/**
 * This class writes a PathwayModel to an output (GPML 2021).
 * 
 * @author finterly
 */
public class GPML2021Writer extends GPML2021FormatAbstract implements GpmlFormatWriter {

	public static final GPML2021Writer GPML2021WRITER = new GPML2021Writer("GPML2021.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2021"));

	/**
	 * Constructor for GPML writer.
	 * 
	 * @param xsdFile the schema file.
	 * @param nsGPML  the GPML namespace.
	 */
	protected GPML2021Writer(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

	/**
	 * Writes the JDOM {@link Document} document to the outputstream specified.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param output       the outputstream to which the JDOM document should be
	 *                     written
	 * @param validate     if true, validate the dom structure before writing. If
	 *                     there is a validation error, or the xsd is not in the
	 *                     classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	public void writeToXml(PathwayModel pathwayModel, OutputStream output, boolean validate) throws ConverterException {

		Document doc = createJdom(pathwayModel);

		if (validate)
			validateDocument(doc);
		// Get the XML code
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		Format xmlformat = xmlOutput.getFormat();
		xmlformat.setEncoding("UTF-8");
//		xmlformat.setTextMode(Format.TextMode.NORMALIZE); TODO Default to preserve spaces? 
		xmlOutput.setFormat(xmlformat);

		try {
			// Send XML code to the outputstream
			xmlOutput.output(doc, output); // new FileOutputStream(new File("fileName.gpml")
			// Create a new file and write XML to it
			System.out.println("Wrote pathway model successfully to gpml file");
		} catch (IOException e) {
			throw new ConverterException(e);
		}

	}

	/**
	 * Writes the JDOM document to the file specified.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param file         the file to which the JDOM document should be saved.
	 * @param validate     if true, validate the dom structure before writing to
	 *                     file.
	 * @throws ConverterException
	 */
	public void writeToXml(PathwayModel pathwayModel, File file, boolean validate) throws ConverterException {
		OutputStream out;
		try {
			out = new FileOutputStream(file);
		} catch (IOException ex) {
			throw new ConverterException(ex);
		}
		writeToXml(pathwayModel, out, true);
	}

	/**
	 * Creates and returns the JDOM document {@link Document} written from given
	 * pathwayModel {@link PathwayModel} data.
	 * 
	 * @param pathwayModel the pathway model to be written.
	 * @throws ConverterException
	 */
	public Document createJdom(PathwayModel pathwayModel) throws ConverterException {
		// removes empty groups
		removeEmptyGroups(pathwayModel);
		// checks if interactions/graphicaLines have at least 2 points
		validateLineElements(pathwayModel);

		Document doc = new Document();
		Element root = new Element("Pathway", getGpmlNamespace());
		doc.setRootElement(root);

		if (root != null) {
			writePathwayInfo(pathwayModel, root);

			writeDataNodes(pathwayModel.getDataNodes(), root);
			writeInteractions(pathwayModel.getInteractions(), root);
			writeGraphicalLines(pathwayModel.getGraphicalLines(), root);
			writeLabels(pathwayModel.getLabels(), root);
			writeShapes(pathwayModel.getShapes(), root);
			writeGroups(pathwayModel.getGroups(), root);

			writeAnnotations(pathwayModel.getAnnotations(), root);
			writeCitations(pathwayModel.getCitations(), root);
			writeEvidences(pathwayModel.getEvidences(), root);
		}
		return doc;
	}

	/**
	 * Checks whether interactions and graphicaLines have at least two points.
	 * 
	 * @param pathwayModel the pathway model.
	 * @throws ConverterException
	 */
	protected void validateLineElements(PathwayModel pathwayModel) throws ConverterException {
		for (Interaction interaction : pathwayModel.getInteractions()) {
			if (interaction.getLinePoints().size() < 2) {
				throw new ConverterException("Interaction " + interaction.getElementId() + " has "
						+ interaction.getLinePoints().size() + " point(s),  must have at least 2.");
			}
		}
		for (GraphicalLine graphicalLine : pathwayModel.getGraphicalLines()) {
			if (graphicalLine.getLinePoints().size() < 2) {
				throw new ConverterException("GraphicalLine " + graphicalLine.getElementId() + " has "
						+ graphicalLine.getLinePoints().size() + " point(s),  must have at least 2.");
			}
		}
	}

	/**
	 * Writes pathway object {@link Pathway} information and authors list to root
	 * element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void writePathwayInfo(PathwayModel pathwayModel, Element root) throws ConverterException {
		Pathway pathway = pathwayModel.getPathway();
		root.setAttribute("title", pathway.getTitle());
		if (pathway.getOrganism() != null)
			root.setAttribute("organism", pathway.getOrganism());
		if (pathway.getSource() != null)
			root.setAttribute("source", pathway.getSource());
		if (pathway.getVersion() != null)
			root.setAttribute("version", pathway.getVersion());
		if (pathway.getLicense() != null)
			root.setAttribute("license", pathway.getLicense());
		if (pathway.getXref() != null)
			writeXref(pathway.getXref(), root, false);
		String description = pathway.getDescription();
		if (description != null) {
			Element desc = new Element("Description", root.getNamespace());
			desc.setText(description);
			root.addContent(desc);
		}
		writeAuthors(pathway.getAuthors(), root);
		writeComments(pathway.getComments(), root);
		writeDynamicProperties(pathway.getDynamicProperties(), root);
		writeAnnotationRefs(pathway.getAnnotationRefs(), root);
		writeCitationRefs(pathway.getCitationRefs(), root);
		writeEvidenceRefs(pathway.getEvidenceRefs(), root);

		Element gfx = new Element("Graphics", root.getNamespace());
		root.addContent(gfx);
		gfx.setAttribute("boardWidth", String.valueOf(pathway.getBoardWidth()));
		gfx.setAttribute("boardHeight", String.valueOf(pathway.getBoardHeight()));
		writeInfoBox(pathway.getInfoBox(), root);
	}

	/**
	 * Writes xref {@link Xref} information to new element. Xref is required for
	 * Evidences. Xref is optional for the Pathway, DataNodes, States, Interactions,
	 * Groups, and Annotations. For Citations, either Xref and/or Url are required.
	 * 
	 * @param xref     the xref of the pathway or pathway element.
	 * @param e        the parent element.
	 * @param required if true, xref is a required property.
	 */
	protected void writeXref(Xref xref, Element e, boolean required) {
		if (xref == null && required) {
			Element xrf = new Element("Xref", e.getNamespace());
			xrf.setAttribute("identifier", "");
			xrf.setAttribute("dataSource", "");
			e.addContent(xrf);
		}
		if (xref != null) {
			String identifier = xref.getId();
			DataSource dataSource = xref.getDataSource();
			String dataSourceStr = XrefUtils.getXrefDataSourceStr(dataSource);
			if (dataSourceStr != null && !dataSourceStr.equals("")) {
				Element xrf = new Element("Xref", e.getNamespace());
				xrf.setAttribute("identifier", identifier == null ? "" : identifier);
				xrf.setAttribute("dataSource", dataSourceStr);
				e.addContent(xrf);
			}
		}
	}

	/**
	 * Writes url {@link UrlRef} information to new element. Url is optional for
	 * Annotations and Evidences. For Citations, either Xref and/or Url are
	 * required.
	 * 
	 * @param xref     the xref of the pathway or pathway element.
	 * @param e        the parent element.
	 * @param required if true, xref is a required property.
	 */
	protected void writeUrl(UrlRef url, Element e) {
		if (url != null) {
			String link = url.getLink();
			String description = url.getDescription();
			if (link != null && !link.equals("")) {
				Element u = new Element("Url", e.getNamespace());
				u.setAttribute("link", link);
				if (description != null && !description.equals(""))
					u.setAttribute("description", description);
				e.addContent(u);
			}
		}
	}

	/**
	 * Writes author {@link Author} information.
	 * 
	 * @param authors the list of authors.
	 * @param root    the root element.
	 * @throws ConverterException
	 */
	protected void writeAuthors(List<Author> authors, Element root) throws ConverterException {
		if (!authors.isEmpty()) {
			Element aus = new Element("Authors", root.getNamespace());
			List<Element> auList = new ArrayList<Element>();
			for (Author author : authors) {
				if (author == null)
					continue;
				Element au = new Element("Author", root.getNamespace());
				au.setAttribute("name", author.getName());
				// sets optional properties
				String username = author.getUsername();
				int order = author.getOrder();
				writeXref(author.getXref(), au, false);
				if (username != null)
					au.setAttribute("username", username);
				if (order != 0)
					au.setAttribute("order", String.valueOf(order));
				if (au != null) {
					auList.add(au);
				}
			}
			if (auList != null && auList.isEmpty() == false) {
				aus.addContent(auList);
				root.addContent(aus);
			}
		}
	}

	/**
	 * Writes comments {@link Comment} information for pathway or pathway element.
	 * 
	 * @param comments the list of comments of pathway or pathway element.
	 * @param e        the parent element.
	 * @throws ConverterException
	 */
	protected void writeComments(List<Comment> comments, Element e) throws ConverterException {
		for (Comment comment : comments) {
			if (comment != null) {
				// write comment only if comment has text
				String commentText = comment.getCommentText();
				if (commentText != null && !commentText.equals("")) {
					Element cmt = new Element("Comment", e.getNamespace());
					cmt.setText(commentText);
					String source = comment.getSource();
					if (source != null && !source.equals(""))
						cmt.setAttribute("source", source);
					if (cmt != null)
						e.addContent(cmt);
				}
			}
		}
	}

	/**
	 * Writes dynamic property information for pathway or pathway element.
	 * {@link Pathway#getDynamicProperty() , ElementInfo#getDynamicProperty()}
	 * 
	 * @param dynamicProperties the list of dynamic properties.
	 * @param e                 the parent element.
	 * @throws ConverterException
	 */
	protected void writeDynamicProperties(Map<String, String> dynamicProperties, Element e) throws ConverterException {
		for (String key : dynamicProperties.keySet()) {
			String value = dynamicProperties.get(key);
			// warnings for conversion GPML2021 to GPML2013a
			if (GPML2013aFormatAbstract.GPML2013A_KEY_SET.contains(key)) {
				Logger.log.trace("Warning: Conversion GPML2013a to GPML2021: " + e.getName() + " dynamic property \""
						+ key + "\" (key) and \"" + value + "\" (value) info lost.");
				continue;
			}
			Element dp = new Element("Property", e.getNamespace());
			dp.setAttribute("key", key);
			dp.setAttribute("value", value);
			if (dp != null)
				e.addContent(dp);
		}
	}

	/**
	 * Writes annotation reference information for pathway or pathway element.
	 * {@link Pathway#getAnnotationRefs , ElementInfo#getAnnotationRefs}. In
	 * GPML2021, annotationRef can have citationRefs and/or evidenceRefs nested
	 * inside.
	 * 
	 * @param annotationRefs the list of annotation references.
	 * @param e              the parent element.
	 * @throws ConverterException
	 */
	protected void writeAnnotationRefs(List<AnnotationRef> annotationRefs, Element e) throws ConverterException {
		for (AnnotationRef annotationRef : annotationRefs) {
			Element anntRef = new Element("AnnotationRef", e.getNamespace());
			anntRef.setAttribute("elementRef", annotationRef.getAnnotation().getElementId());
			writeCitationRefs(annotationRef.getCitationRefs(), anntRef);
			writeEvidenceRefs(annotationRef.getEvidenceRefs(), anntRef);
			if (anntRef != null)
				e.addContent(anntRef);
		}
	}

	/**
	 * Writes citation reference information for pathway or pathway element.
	 * {@link Pathway#getCitationRefs , ElementInfo#getCitationRefs}.
	 * 
	 * @param citationRefs the list of citation references.
	 * @param e            the parent element.
	 * @throws ConverterException
	 */
	protected void writeCitationRefs(List<CitationRef> citationRefs, Element e) throws ConverterException {
		if (e != null) {
			for (CitationRef citationRef : citationRefs) {
				Element citRef = new Element("CitationRef", e.getNamespace());
				citRef.setAttribute("elementRef", citationRef.getCitation().getElementId());
				writeAnnotationRefs(citationRef.getAnnotationRefs(), citRef);
				if (citRef != null)
					e.addContent(citRef);
			}
		}
	}

	/**
	 * Writes evidence reference information for pathway or pathway element
	 * {@link Pathway#getEvidenceRefs , ElementInfo#getEvidenceRefs}.
	 * 
	 * 
	 * @param evidenceRefs the list of evidence references.
	 * @param e            the parent element.
	 * @throws ConverterException
	 */
	protected void writeEvidenceRefs(List<EvidenceRef> evidenceRefs, Element e) throws ConverterException {
		if (e != null) {
			for (EvidenceRef evidenceRef : evidenceRefs) {
				Element evidRef = new Element("EvidenceRef", e.getNamespace());
				evidRef.setAttribute("elementRef", evidenceRef.getEvidence().getElementId());
				if (evidRef != null)
					e.addContent(evidRef);
			}
		}
	}

	/**
	 * Writes the infobox x and y coordinate {@link Pathway#getInfoBox} n
	 * information.
	 * 
	 * @param infoBox the infobox xy coordinates.
	 * @param root    the root element.
	 */
	protected void writeInfoBox(Coordinate infoBox, Element root) {
		Element ifb = new Element("InfoBox", root.getNamespace());
		ifb.setAttribute("centerX", Double.toString(infoBox.getX()));
		ifb.setAttribute("centerY", Double.toString(infoBox.getY()));
		root.addContent(ifb);
	}

	/**
	 * Writes datanode {@link DataNode} information.
	 * 
	 * @param dataNodes the list of datanodes.
	 * @param root      the root element.
	 * @throws ConverterException
	 */
	protected void writeDataNodes(List<DataNode> dataNodes, Element root) throws ConverterException {
		if (!dataNodes.isEmpty()) {
			Element dns = new Element("DataNodes", root.getNamespace());
			List<Element> dnList = new ArrayList<Element>();
			for (DataNode dataNode : dataNodes) {
				if (dataNode == null)
					continue;
				Element dn = new Element("DataNode", root.getNamespace());
				writeXref(dataNode.getXref(), dn, false);
				writeStates(dataNode.getStates(), dn);
				writeShapedElement(dataNode, dn);
				Element gfx = dn.getChild("Graphics", dn.getNamespace());
				double rotation = dataNode.getRotation();
				if (rotation != 0)
					gfx.setAttribute("rotation", Double.toString(rotation));
				dn.setAttribute("textLabel", dataNode.getTextLabel());
				dn.setAttribute("type", dataNode.getType().getName());
				writeGroupRef(dataNode.getGroupRef(), dn);
				writeElementRef(dataNode.getAliasRef(), dn);
				if (dn != null) {
					dnList.add(dn);
				}
			}
			if (dnList != null && dnList.isEmpty() == false) {
				dns.addContent(dnList);
				root.addContent(dns);
			}
		}
	}

	/**
	 * Writes state {@link State} information.
	 * 
	 * @param states the list of states.
	 * @param dn     the parent data node element.
	 * @throws ConverterException
	 */
	protected void writeStates(List<State> states, Element dn) throws ConverterException {
		if (!states.isEmpty()) {
			Element sts = new Element("States", dn.getNamespace());
			List<Element> stList = new ArrayList<Element>();
			for (State state : states) {
				if (state == null)
					continue;
				Element st = new Element("State", dn.getNamespace());
				writeXref(state.getXref(), st, false);

				Element gfx = new Element("Graphics", st.getNamespace());
				st.addContent(gfx);
				gfx.setAttribute("relX", Double.toString(state.getRelX()));
				gfx.setAttribute("relY", Double.toString(state.getRelY()));
				gfx.setAttribute("width", Double.toString(state.getWidth()));
				gfx.setAttribute("height", Double.toString(state.getHeight()));
				writeFontProperty(state.getFontProp(), gfx);
				// writes all shape style properties except zOrder
				writeShapeStyleProperty(state.getShapeStyleProp(), gfx, false);
				double rotation = state.getRotation();
				if (rotation != 0)
					gfx.setAttribute("rotation", Double.toString(rotation));
				writeElementInfo(state, st);
				st.setAttribute("textLabel", state.getTextLabel() == null ? "" : state.getTextLabel());
				st.setAttribute("type", state.getType().getName());

				if (st != null) {
					stList.add(st);
				}
			}
			if (stList != null && stList.isEmpty() == false) {
				sts.addContent(stList);
				dn.addContent(sts);
			}
		}
	}

	/**
	 * Writes interaction {@link Interaction} information.
	 * 
	 * @param interactions the list of interactions.
	 * @param root         the root element;
	 * @throws ConverterException
	 */
	protected void writeInteractions(List<Interaction> interactions, Element root) throws ConverterException {
		if (!interactions.isEmpty()) {
			Element ias = new Element("Interactions", root.getNamespace());
			List<Element> iaList = new ArrayList<Element>();
			for (Interaction interaction : interactions) {
				if (interaction == null)
					continue;
				Element ia = new Element("Interaction", root.getNamespace());
				writeXref(interaction.getXref(), ia, false);
				writeLineElement(interaction, ia);
				if (ia != null) {
					iaList.add(ia);
				}
			}
			if (iaList != null && iaList.isEmpty() == false) {
				ias.addContent(iaList);
				root.addContent(ias);
			}
		}
	}

	/**
	 * Writes graphical line {@link GraphicalLine} information.
	 * 
	 * @param graphicalLines the list of graphical lines.
	 * @param root           the root element.
	 * @throws ConverterException
	 */
	protected void writeGraphicalLines(List<GraphicalLine> graphicalLines, Element root) throws ConverterException {
		if (!graphicalLines.isEmpty()) {
			Element glns = new Element("GraphicalLines", root.getNamespace());
			List<Element> glnList = new ArrayList<Element>();
			for (GraphicalLine graphicalLine : graphicalLines) {
				if (graphicalLine == null)
					continue;
				Element gln = new Element("GraphicalLine", root.getNamespace());
				writeLineElement(graphicalLine, gln);
				if (gln != null) {
					glnList.add(gln);
				}
			}
			if (glnList != null && glnList.isEmpty() == false) {
				glns.addContent(glnList);
				root.addContent(glns);
			}
		}
	}

	/**
	 * Writes line element {@link LineElement} information for interactions or
	 * graphicalLines.
	 * 
	 * @param lineElement the interaction or graphicalLine.
	 * @param ln          the line element.
	 * @throws ConverterException
	 */
	protected void writeLineElement(LineElement lineElement, Element ln) throws ConverterException {
		Element wyps = new Element("Waypoints", ln.getNamespace());
		ln.addContent(wyps);
		writePoints(lineElement.getLinePoints(), wyps);
		writeAnchors(lineElement.getAnchors(), wyps);
		Element gfx = new Element("Graphics", ln.getNamespace());
		ln.addContent(gfx);
		writeLineStyleProperty(lineElement.getLineStyleProp(), gfx);
		writeElementInfo(lineElement, ln);
		writeGroupRef(lineElement.getGroupRef(), ln);
	}

	/**
	 * Writes point {@link LinePoint} information.
	 * 
	 * @param points the list of points.
	 * @param wyps   the parent element.
	 * @throws ConverterException
	 */
	protected void writePoints(List<LinePoint> points, Element wyps) throws ConverterException {
		List<Element> ptList = new ArrayList<Element>();
		for (LinePoint point : points) {
			if (point == null)
				continue;
			Element pt = new Element("Point", wyps.getNamespace());
			writeElementId(point.getElementId(), pt);
			pt.setAttribute("arrowHead", point.getArrowHead().getName());
			pt.setAttribute("x", Double.toString(point.getXY().getX()));
			pt.setAttribute("y", Double.toString(point.getXY().getY()));
			if (writeElementRef(point.getElementRef(), pt)) {
				pt.setAttribute("relX", Double.toString(point.getRelX()));
				pt.setAttribute("relY", Double.toString(point.getRelY()));
			}
			if (pt != null)
				ptList.add(pt);
		}
		if (ptList != null && ptList.isEmpty() == false)
			wyps.addContent(ptList);
	}

	/**
	 * Writes anchor {@link Anchor} information.
	 * 
	 * @param anchors the list of anchors.
	 * @param wyps    the parent element.
	 * @throws ConverterException
	 */
	protected void writeAnchors(List<Anchor> anchors, Element wyps) throws ConverterException {
		if (!anchors.isEmpty()) {
			List<Element> anList = new ArrayList<Element>();
			for (Anchor anchor : anchors) {
				if (anchor == null)
					continue;
				Element an = new Element("Anchor", wyps.getNamespace());
				writeElementId(anchor.getElementId(), an);
				an.setAttribute("position", Double.toString(anchor.getPosition()));
				an.setAttribute("shapeType", anchor.getShapeType().getName());
				if (an != null) {
					anList.add(an);
				}
			}
			if (anList != null && anList.isEmpty() == false) {
				wyps.addContent(anList);
			}
		}
	}

	/**
	 * Writes label {@link Label} information.
	 * 
	 * @param labels the list of labels.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeLabels(List<Label> labels, Element root) throws ConverterException {
		if (!labels.isEmpty()) {
			Element lbs = new Element("Labels", root.getNamespace());
			List<Element> lbList = new ArrayList<Element>();
			for (Label label : labels) {
				if (label == null)
					continue;
				Element lb = new Element("Label", root.getNamespace());
				writeShapedElement(label, lb);
				Element gfx = lb.getChild("Graphics", lb.getNamespace());
				double rotation = label.getRotation();
				if (rotation != 0)
					gfx.setAttribute("rotation", Double.toString(rotation));
				lb.setAttribute("textLabel", label.getTextLabel());
				if (label.getHref() != null)
					lb.setAttribute("href", label.getHref());
				writeGroupRef(label.getGroupRef(), lb);
				if (lb != null) {
					lbList.add(lb);
				}
			}
			if (lbList != null && lbList.isEmpty() == false) {
				lbs.addContent(lbList);
				root.addContent(lbs);
			}
		}
	}

	/**
	 * Writes shape {@link Shape} information.
	 * 
	 * @param shapes the list of shapes.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeShapes(List<Shape> shapes, Element root) throws ConverterException {
		if (!shapes.isEmpty()) {
			Element shps = new Element("Shapes", root.getNamespace());
			List<Element> shpList = new ArrayList<Element>();
			for (Shape shape : shapes) {
				if (shape == null)
					continue;
				Element shp = new Element("Shape", root.getNamespace());
				writeShapedElement(shape, shp);
				if (shape.getTextLabel() != null)
					shp.setAttribute("textLabel", shape.getTextLabel());
				writeGroupRef(shape.getGroupRef(), shp);
				Element gfx = shp.getChild("Graphics", shp.getNamespace());
				double rotation = shape.getRotation();
				if (rotation != 0)
					gfx.setAttribute("rotation", Double.toString(rotation));
				if (shp != null) {
					shpList.add(shp);
				}
			}
			if (shpList != null && shpList.isEmpty() == false) {
				shps.addContent(shpList);
				root.addContent(shps);
			}
		}
	}

	/**
	 * Writes group {@link Group} information.
	 * 
	 * @param groups the list of groups.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeGroups(List<Group> groups, Element root) throws ConverterException {
		if (!groups.isEmpty()) {
			Element grps = new Element("Groups", root.getNamespace());
			List<Element> grpList = new ArrayList<Element>();
			for (Group group : groups) {
				if (group == null)
					continue;
				Element grp = new Element("Group", root.getNamespace());
				writeXref(group.getXref(), grp, false);
				writeShapedElement(group, grp);
				if (group.getTextLabel() != null)
					grp.setAttribute("textLabel", group.getTextLabel());
				grp.setAttribute("type", group.getType().getName());
				writeGroupRef(group.getGroupRef(), grp);
				if (grp != null) {
					grpList.add(grp);
				}
			}
			if (grpList != null && grpList.isEmpty() == false) {
				grps.addContent(grpList);
				root.addContent(grps);
			}
		}
	}

	/**
	 * Writes annotation {@link Annotation} information.
	 * 
	 * @param annotations the list of annotations.
	 * @param root        the root element.
	 * @throws ConverterException
	 */
	protected void writeAnnotations(List<Annotation> annotations, Element root) throws ConverterException {
		if (!annotations.isEmpty()) {
			Element annts = new Element("Annotations", root.getNamespace());
			List<Element> anntList = new ArrayList<Element>();
			for (Annotation annotation : annotations) {
				if (annotation == null)
					continue;
				Element annt = new Element("Annotation", root.getNamespace());
				writeElementId(annotation.getElementId(), annt);
				annt.setAttribute("value", annotation.getValue());
				annt.setAttribute("type", annotation.getType().getName());
				writeXref(annotation.getXref(), annt, false);
				writeUrl(annotation.getUrl(), annt);
				if (annt != null)
					anntList.add(annt);
			}
			if (anntList != null && anntList.isEmpty() == false) {
				annts.addContent(anntList);
				root.addContent(annts);
			}
		}
	}

	/**
	 * Writes citation {@link Citation} information.
	 * 
	 * @param citations the list of citations.
	 * @param root      the root element.
	 * @throws ConverterException
	 */
	protected void writeCitations(List<Citation> citations, Element root) throws ConverterException {
		if (!citations.isEmpty()) {
			Element cits = new Element("Citations", root.getNamespace());
			List<Element> citList = new ArrayList<Element>();
			for (Citation citation : citations) {
				if (citation == null)
					continue;
				Element cit = new Element("Citation", root.getNamespace());
				writeElementId(citation.getElementId(), cit);
				writeXref(citation.getXref(), cit, false);
				writeUrl(citation.getUrl(), cit);
				if (cit != null)
					citList.add(cit);
			}
			if (citList != null && citList.isEmpty() == false) {
				cits.addContent(citList);
				root.addContent(cits);
			}
		}
	}

	/**
	 * Writes evidence {@link Evidence} information.
	 * 
	 * @param evidences the list of evidences.
	 * @param root      the root element.
	 * @throws ConverterException
	 */
	protected void writeEvidences(List<Evidence> evidences, Element root) throws ConverterException {
		if (!evidences.isEmpty()) {
			Element evids = new Element("Evidences", root.getNamespace());
			List<Element> evidList = new ArrayList<Element>();
			for (Evidence evidence : evidences) {
				if (evidence == null)
					continue;
				Element evid = new Element("Evidence", root.getNamespace());
				writeElementId(evidence.getElementId(), evid);
				writeXref(evidence.getXref(), evid, true);
				writeUrl(evidence.getUrl(), evid);
				if (evidence.getValue() != null)
					evid.setAttribute("value", evidence.getValue());
				if (evid != null)
					evidList.add(evid);
			}
			if (evidList != null && evidList.isEmpty() == false) {
				evids.addContent(evidList);
				root.addContent(evids);
			}
		}
	}

	/**
	 * Writes elementId {@link PathwayElement} property information.
	 * 
	 * @param elementId the elementId.
	 * @param e         the parent element.
	 */
	protected void writeElementId(String elementId, Element e) {
		if (elementId != null && !elementId.equals(""))
			e.setAttribute("elementId", elementId);
	}

	/**
	 * Writes elementRef property information. Returns boolean if elementRef is
	 * written. Used in {@link #writeDataNodes} and {@link #writePoints}.
	 * 
	 * @param elementRef the elementRef.
	 * @param e          the parent element.
	 * @return true if elementRef exists and is successfully written.
	 */
	protected boolean writeElementRef(PathwayElement elementRef, Element e) {
		if (elementRef != null) {
			String elementRefStr = elementRef.getElementId();
			if (elementRefStr != null && !elementRefStr.equals(""))
				e.setAttribute("elementRef", elementRefStr);
			return true;
		}
		return false;
	}

	/**
	 * Writes groupRef property information.
	 * 
	 * @param groupRef the groupRef.
	 * @param e        the parent element.
	 */
	protected void writeGroupRef(Group groupRef, Element e) {
		if (groupRef != null) {
			String groupRefStr = groupRef.getElementId();
			if (groupRefStr != null && !groupRefStr.equals(""))
				e.setAttribute("groupRef", groupRefStr);
		}
	}

	/**
	 * Writes shapedElement {@link ShapedElement} information for datanodes, labels,
	 * shapes, or groups.
	 * 
	 * @param shapedElement the datanode, label, shape, or group.
	 * @param e             the shape element.
	 * @throws ConverterException
	 */
	protected void writeShapedElement(ShapedElement shapedElement, Element se) throws ConverterException {
		Element gfx = new Element("Graphics", se.getNamespace());
		se.addContent(gfx);
		writeRectProperty(shapedElement.getRectProp(), gfx);
		writeFontProperty(shapedElement.getFontProp(), gfx);
		writeShapeStyleProperty(shapedElement.getShapeStyleProp(), gfx, true);
		writeElementInfo(shapedElement, se);
	}

	/**
	 * Writes elementId, comment group {comment, dynamic property, annotationRef,
	 * citationRef) and evidenceRef {@link ElementInfo} information for datanodes,
	 * interactions, graphicalLines, labels, shapes, and group.
	 * 
	 * @param elementInfo the pathway element.
	 * @param e           the parent element.
	 * @throws ConverterException
	 */
	protected void writeElementInfo(ElementInfo elementInfo, Element e) throws ConverterException {
		writeElementId(elementInfo.getElementId(), e);
		writeComments(elementInfo.getComments(), e);
		writeDynamicProperties(elementInfo.getDynamicProperties(), e);
		writeAnnotationRefs(elementInfo.getAnnotationRefs(), e);
		writeCitationRefs(elementInfo.getCitationRefs(), e);
		writeEvidenceRefs(elementInfo.getEvidenceRefs(), e);
	}

	/**
	 * Writes rect property {@link RectProperty} information.
	 * 
	 * @param rectProp the rectproperties.
	 * @param gfx      the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeRectProperty(RectProperty rectProp, Element gfx) throws ConverterException {
		gfx.setAttribute("centerX", Double.toString(rectProp.getCenterXY().getX()));
		gfx.setAttribute("centerY", Double.toString(rectProp.getCenterXY().getY()));
		gfx.setAttribute("width", Double.toString(rectProp.getWidth()));
		gfx.setAttribute("height", Double.toString(rectProp.getHeight()));
	}

	/**
	 * Writes font property {@link FontProperty} information.
	 * 
	 * @param fontProp the font properties.
	 * @param gfx      the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeFontProperty(FontProperty fontProp, Element gfx) throws ConverterException {
		gfx.setAttribute("textColor", ColorUtils.colorToHex(fontProp.getTextColor(), false));
		gfx.setAttribute("fontName", fontProp.getFontName() == null ? "Arial" : fontProp.getFontName());
		gfx.setAttribute("fontWeight", fontProp.getFontWeight() ? "Bold" : "Normal");
		gfx.setAttribute("fontStyle", fontProp.getFontStyle() ? "Italic" : "Normal");
		gfx.setAttribute("fontDecoration", fontProp.getFontDecoration() ? "Underline" : "Normal");
		gfx.setAttribute("fontStrikethru", fontProp.getFontStrikethru() ? "Strikethru" : "Normal");
		gfx.setAttribute("fontSize", Integer.toString((int) fontProp.getFontSize()));
		gfx.setAttribute("hAlign", fontProp.getHAlign().getName());
		gfx.setAttribute("vAlign", fontProp.getVAlign().getName());
	}

	/**
	 * Writes shape style property {@link ShapeStyleProperty} information.
	 * 
	 * @param shapeProp the shape style properties.
	 * @param gfx       the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeShapeStyleProperty(ShapeStyleProperty shapeProp, Element gfx, boolean writeZOrder)
			throws ConverterException {
		gfx.setAttribute("borderColor", ColorUtils.colorToHex(shapeProp.getBorderColor(), false));
		gfx.setAttribute("borderStyle", shapeProp.getBorderStyle().getName());
		gfx.setAttribute("borderWidth", String.valueOf(shapeProp.getBorderWidth()));
		gfx.setAttribute("fillColor", ColorUtils.colorToHex(shapeProp.getFillColor(), false));
		gfx.setAttribute("shapeType", shapeProp.getShapeType().getName());
		if (writeZOrder)
			gfx.setAttribute("zOrder", String.valueOf(shapeProp.getZOrder()));
	}

	/**
	 * Writes line style property {@link LineStyleProperty} information.
	 * 
	 * @param lineProp the line style properties.
	 * @param gfx      the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeLineStyleProperty(LineStyleProperty lineProp, Element gfx) throws ConverterException {
		gfx.setAttribute("lineColor", ColorUtils.colorToHex(lineProp.getLineColor(), false));
		gfx.setAttribute("lineStyle", lineProp.getLineStyle().getName());
		gfx.setAttribute("lineWidth", String.valueOf(lineProp.getLineWidth()));
		gfx.setAttribute("connectorType", lineProp.getConnectorType().getName());
		gfx.setAttribute("zOrder", String.valueOf(lineProp.getZOrder()));
	}
}
