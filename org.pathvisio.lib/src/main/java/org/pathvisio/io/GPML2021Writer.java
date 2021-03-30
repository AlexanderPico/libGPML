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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.io.GpmlFormat2013a.BiopaxAttributeComparator;
import org.pathvisio.io.GpmlFormatAbstract.AttributeInfo;
import org.pathvisio.io.GpmlFormatAbstract.ByElementName;
import org.pathvisio.model.Anchor;
import org.pathvisio.model.AnnotationRef;
import org.pathvisio.model.Author;
import org.pathvisio.model.Comment;
import org.pathvisio.model.DataNode;
import org.pathvisio.model.DynamicProperty;
import org.pathvisio.model.GraphicalLine;
import org.pathvisio.model.Group;
import org.pathvisio.model.IElementIdContainer;
import org.pathvisio.model.Interaction;
import org.pathvisio.model.Label;
import org.pathvisio.model.Legend;
import org.pathvisio.model.LineElement;
import org.pathvisio.model.LineStyleType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.Point;
import org.pathvisio.model.Shape;
import org.pathvisio.model.ShapedElement;
import org.pathvisio.model.State;

public class GPML2021Writer implements GPMLWriter {

	// TODO
	protected void writeElementId(IElementIdContainer o, Element e) {
		String id = o.getElementId();
		// id has to be unique!
		if (id != null && !id.equals("")) {
			e.setAttribute("GraphId", o.getElementId());
		}
	}

	protected void writeGroupRef(PathwayElement o, Element e) {
		String id = o.getGroupRef();
		if (id != null && !id.equals("")) {
			e.setAttribute("GroupRef", o.getGroupRef());
		}
	}

	/**
	 * Updates pathway information.
	 *
	 * @param root the xml element
	 * @param o    the pathway element
	 * @throws ConverterException
	 */
	protected abstract void writeMappInfoVariable(Element root, Pathway p) throws ConverterException;

	/**
	 * Updates pathway information.
	 * 
	 * @param root
	 * @param o
	 * @throws ConverterException
	 */
	protected void writeMappInfo(Element root, Pathway p) throws ConverterException {
		setAttribute("Pathway", "Name", root, p.getTitle());
		setAttribute("Pathway", "Organism", root, p.getOrganism());
		setAttribute("Pathway", "Data-Source", root, p.getSource());
		setAttribute("Pathway", "Version", root, p.getVersion());

		// TODO License?

		// TODO Handle
		setAttribute("Pathway", "Author", root, p.getAuthor.getName());
		setAttribute("Pathway", "Email", root, p.getEmail());
		setAttribute("Pathway", "Maintainer", root, p.getMaintainer());
		setAttribute("Pathway", "Last-Modified", root, p.getLastModified());
		writeComments(p, root);
		writeBiopaxRef(p, root);
		writeAttributes(p, root);

		Element graphics = new Element("Graphics", nsGPML);
		root.addContent(graphics);
		setAttribute("Pathway.Graphics", "BoardWidth", graphics, String.valueOf(p.getBoardWidth()));
		setAttribute("Pathway.Graphics", "BoardHeight", graphics, String.valueOf(p.getBoardHeight()));

		writeMappInfoVariable(root, p);
	}

	/**
	 * Attribute in gpml
	 * 
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeDynamicProperty(PathwayElement o, Element e) throws ConverterException {
		if (e != null) {
			for (String key : o.getDynamicPropertyKeys()) {
				Element a = new Element("Attribute", e.getNamespace());
				setAttribute("Attribute", "Key", a, key);
				setAttribute("Attribute", "Value", a, o.getDynamicProperty(key));
				e.addContent(a);
			}
		}
	}

	/**
	 * @param o
	 * @param e
	 */
	protected void writeLegend(Legend o, Element e) {
		String centerX = o.getCenterXY().
		String centerY = o.getCenterXY().
		if (e != null) {
			e.setAttribute("CenterX", centerX);
			e.setAttribute("CenterY", centerY);
		}
	}

	@Override
	protected void writeMappInfoVariable(Element root, Pathway p) throws ConverterException {
		root.setAttribute("Pathway", "License", root, p.getLicense());
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	private void writePathwayElement(PathwayElement o, Element e) throws ConverterException {
		readElementId(o, e); // GraphId
		writeComment(o, e); // Comment
		writeBiopaxRef(o, e); // AnnotationRef, CitationRef, Evidence
		writeDynamicProperty(o, e);
	}

	// NOT PATHWAYELEMENTS
//	case LEGEND:
//	mapSimpleCenter(o, e);
//	break;
//	case INFOBOX:
//
//	readInfoBox (o, e);
//	break;
//case MAPPINFO:
//	mapCommon(o, e);
//	mapMappInfoData(o, e);
//	break;

	/**
	 * 
	 * Create a single PathwayElement based on a piece of Jdom tree. Used also by
	 * Patch utility. Pathway p may be null
	 */
	public PathwayElement mapElement(Element e, Pathway p) throws ConverterException {
		// Creates a pathway element
		// Adds pathway element to given Pathway.
		// reads(pathway element o, element e)... In the process it sets the pathway
		// element?
		// Finally this pathway element is returned.
	}

	// TODO WRITERECTPROPERTY?
// TODO 		setAttribute(base + ".Graphics", "ZOrder", graphics, "" + o.getZOrder());

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeRectProperty(ShapedElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		graphics.setAttribute("centerX", Double.toString(o.getRectProperty().getCenterXY().getX()));
		graphics.setAttribute("centerY", Double.toString(o.getRectProperty().getCenterXY().getY()));
		graphics.setAttribute("width", Double.toString(o.getRectProperty().getWidth()));
		graphics.setAttribute("height", Double.toString(o.getRectProperty().getHeight()));
	}

	protected void writeComments(PathwayElement o, Element e) throws ConverterException {
		if (e != null) {
			for (PathwayElement.Comment c : o.getComments()) {
				Element f = new Element("Comment", e.getNamespace());
				f.setText(c.getComment());
				setAttribute("Comment", "Source", f, c.getSource());
				e.addContent(f);
			}
		}
	}

	/**
	 * Gets FontProperty values from model and writes to gpml FontAttributes for
	 * Graphics element of the given pathway element.
	 * 
	 * @param o the given pathway element.
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeFontProperty(ShapedElement o, Element e) throws ConverterException {
		String textColor = ColorUtils.colorToHex(o.getFontProperty().getTextColor());
		String fontName = o.getFontProperty().getFontName() == null ? "" : o.getFontProperty().getFontName();
		String fontWeight = o.getFontProperty().getFontWeight() ? "Bold" : "Normal";
		String fontStyle = o.getFontProperty().getFontStyle() ? "Italic" : "Normal";
		String fontDecoration = o.getFontProperty().getFontDecoration() ? "Underline" : "Normal";
		String fontStrikethru = o.getFontProperty().getFontStrikethru() ? "Strikethru" : "Normal";
		String fontSize = Integer.toString((int) o.getFontProperty().getFontSize());
		String hAlignType = o.getFontProperty().getVAlign().getName();
		String vAlignType = o.getFontProperty().getHAlign().getName();
//		if (e != null) {
//			if (graphics != null) {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		graphics.setAttribute("textColor", textColor);
		graphics.setAttribute("fontName", fontName);
		graphics.setAttribute("fontWeight", fontWeight);
		graphics.setAttribute("fontStyle", fontStyle);
		graphics.setAttribute("fontDecoration", fontDecoration);
		graphics.setAttribute("fontStrikethru", fontStrikethru);
		graphics.setAttribute("fontSize", fontSize);
		graphics.setAttribute("hAlign", hAlignType);
		graphics.setAttribute("vAlign", vAlignType);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeShapeStyleProperty(ShapedElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());

		String borderColor = ColorUtils.colorToHex(o.getShapeStyleProperty().getBorderColor());
		String borderStyle = o.getShapeStyleProperty().getBorderStyle() != LineStyleType.DASHED ? "Solid" : "Broken";
		String borderWidth = String.valueOf(o.getShapeStyleProperty().getBorderWidth());
		// TODO transparent
		String fillColor = o.getShapeStyleProperty().getFillColor() == Color.decode("#00000000") ? "Transparent"
				: ColorUtils.colorToHex(o.getShapeStyleProperty().getFillColor());
		// TODO shapeType
		String zOrder = String.valueOf(o.getShapeStyleProperty().getZOrder());
		graphics.setAttribute("borderColor", borderColor);
		graphics.setAttribute("borderStyle", borderStyle);
		graphics.setAttribute("borderWidth", borderWidth);
		// TODO ConnectorType enum
		// TODO shapeType
		graphics.setAttribute("fillColor", fillColor);
		graphics.setAttribute("zOrder", zOrder);

	}

	/**
	 * DataNode, Label, Shape, Group
	 * 
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	private void writeShapedElement(ShapedElement o, Element e) throws ConverterException {
		writePathwayElement(o, e); // TODO: ElementId, CommentGroup
		String base = e.getName();
		String groupRef = o.getGroupRef().getElementId();
		setAttribute(base, "GroupRef", e, groupRef);
		writeRectProperty(o, e);
		writeFontProperty(o, e);
		writeShapeStyleProperty(o, e);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeLineStyleProperty(LineElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String lineColor = ColorUtils.colorToHex(o.getLineStyleProperty().getLineColor());
		String lineStyle = o.getLineStyleProperty().getLineStyle() != LineStyleType.DASHED ? "Solid" : "Broken";
		String lineWidth = String.valueOf(o.getLineStyleProperty().getLineWidth());
		String connectorType = o.getLineStyleProperty().getConnectorType().getName();
		String zOrder = String.valueOf(o.getLineStyleProperty().getZOrder());

		// TODO COLOR
		graphics.setAttribute("lineColor", lineColor);

		graphics.setAttribute("lineStyle", lineStyle);
		graphics.setAttribute("lineWidth", lineWidth);
		// TODO ConnectorType enum
		graphics.setAttribute("connectorType", connectorType);
		graphics.setAttribute("zOrder", zOrder);

	}

	/**
	 * @param o
	 * @param e
	 */
	protected void writeInfoBox(Pathway p, Element e) {
		String centerX = Double.toString(p.getInfoBox().getX());
		String centerY = Double.toString(p.getInfoBox().getY());
		if (e != null) {
			e.setAttribute("CenterX", centerX);
			e.setAttribute("CenterY", centerY);
		}
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeDataNode(DataNode o, Element e) throws ConverterException {

		// TODO STATE
		// TODO elementRef
		String textLabel = o.getTextLabel();
		String type = o.getType().getName();
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = o.getXref().getId();
		String dataSource = o.getXref().getDataSource().getFullName();
		e.setAttribute("textLabel", textLabel);
		e.setAttribute("type", type);
		xref.setAttribute("dataSource", dataSource == null ? "" : dataSource);
		xref.setAttribute("identifier",  identifier);
//		writeCommon(o, e); //Comment, Attribute, Biopax 
		writeShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeState(State o, Element e) throws ConverterException {
		writePathwayElement(o, e); // TODO: // ElemenId, CommentGroup
		// TODO elementRef
		e.setAttribute("State", "GraphRef", e, o.getElementRef()); // TODO Check if null
		e.setAttribute("State", "TextLabel", e, o.getTextLabel());
		e.setAttribute("State", "StateType", e, o.getType().getName());
		
		Element graphics = e.getChild("Graphics", e.getNamespace());
//		graphics.setAttribute("State.Graphics", "RelX", graphics, "" + o.getRelX());
//		graphics.setAttribute("State.Graphics", "RelY", graphics, "" + o.getRelY());
//		
//		graphics.setAttribute("State.Graphics", "Width", graphics, "" + o.getWidth());
//		graphics.setAttribute("State.Graphics", "Height", graphics, "" + o.getHeight());
//		/** FontProperty */
//		graphics.setAttribute("State.Graphics", "FontName", graphics,
//				o.getFontProperty().getFontName() == null ? "" : o.getFontProperty().getFontName());
//		graphics.setAttribute("State.Graphics", "FontWeight", graphics, o.getFontProperty().getFontWeight() ? "Bold" : "Normal");
//		graphics.setAttribute("State.Graphics", "FontStyle", graphics, o.getFontProperty().getFontStyle() ? "Italic" : "Normal");
//		graphics.setAttribute("State.Graphics", "FontDecoration", graphics,
//				o.getFontProperty().getFontDecoration() ? "Underline" : "Normal");
//		graphics.setAttribute("State.Graphics", "FontStrikethru", graphics,
//				o.getFontProperty().getFontStrikethru() ? "Strikethru" : "Normal");
//		graphics.setAttribute("State.Graphics", "FontSize", graphics, Integer.toString((int) o.getFontProperty().getFontSize()));
//		graphics.setAttribute("State.Graphics", "Align", graphics, o.getFontProperty().getVAlign().getName());
//		graphics.setAttribute("State.Graphics", "Valign", graphics, o.getFontProperty().getHAlign().getName());
//		/** TODO ShapeStyleProperty */
		/** Xref */
		Element xref = e.getChild("Xref", e.getNamespace());
		String dataSource = o.getXref().getDataSource().getFullName();
		xref.setAttribute("dataSource", dataSource == null ? "" : dataSource);
		xref.setAttribute("identifier", o.getXref().getId());
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeRotation(Shape o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());
		setAttribute("Shape.Graphics", "Rotation", graphics, Double.toString(o.getRotation()));
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeShapeType(PathwayElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String shapeName = o.getShapeType().getName();
		setAttribute(base + ".Graphics", "ShapeType", graphics, shapeName);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeInteraction(Interaction o, Element e) throws ConverterException {
		writeLineElement(o, e);
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = o.getXref().getId();
		String dataSource = o.getXref().getDataSource().getFullName();
		setAttribute("Interaction.Xref", "Database", xref, dataSource == null ? "" : dataSource);
		setAttribute("Interaction.Xref", "ID", xref, identifier);

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeLineElement(LineElement o, Element e) throws ConverterException {
		writePathwayElement(o, e); /** TODO elementId, CommentGroup */

		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String groupRef = o.getGroupRef().getElementId(); // TODO GroupRef
		setAttribute(base, "GroupRef", e, groupRef);
		writeLineStyleProperty(o, e);

		// Writes the entire list
		/** TODO GPML2021 Points not in Graphics */
		List<Point> pts = o.getPoints();
		for (int i = 0; i < pts.size(); i++) {
			Point pt = pts.get(i);
			Element point = new Element("Point", e.getNamespace());
			graphics.addContent(point);
			writeElementId(pt, point); // TODO
			setAttribute(base + ".Graphics.Point", "X", point, Double.toString(pt.getXY().getX()));
			setAttribute(base + ".Graphics.Point", "Y", point, Double.toString(pt.getXY().getY()));
			if (pt.getElementRef() != null && !pt.getElementRef().equals("")) {
				setAttribute(base + ".Graphics.Point", "GraphRef", point, pt.getElementRef());
				setAttribute(base + ".Graphics.Point", "RelX", point, Double.toString(pt.getRelX()));
				setAttribute(base + ".Graphics.Point", "RelY", point, Double.toString(pt.getRelY()));
			}
			if (pt.getArrowHead() != null) {
				setAttribute(base + ".Graphics.Point", "ArrowHead", point, pt.getArrowHead().getName());
			}
		}

		// Writes the entire list
		/** TODO GPML2021 Anchors not in Graphics */
		for (Anchor a : o.getAnchors()) {
			Element anchor = new Element("Anchor", e.getNamespace());
			writeElementId(a, anchor); // TODO
			setAttribute(base + ".Graphics.Anchor", "Position", anchor, Double.toString(a.getPosition()));
			setAttribute(base + ".Graphics.Anchor", "Shape", anchor, a.getShapeType().getName());
			graphics.addContent(anchor);
		}
	}

//
//	List<Label> labels = p.getLabels();
//	Collections.sort(labels); // TODO necessary?
//	for (Label o : labels) {
//		Element e = new Element("Label", getGpmlNamespace());
//		e.addContent(new Element("Graphics", getGpmlNamespace()));
//		writeLabel(o, e);
//		if (e != null) {
//			elementList.add(e);
//		}
//	}
//	
//	protected Label readLabels(List<Element> e) throws ConverterException {
//
//	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeLabel(Label o, Element e) throws ConverterException {
		String textLabel = o.getTextLabel();
		String href = o.getHref();
		setAttribute("Label", "TextLabel", e, textLabel);
		setAttribute("Label", "Href", e, href);
		writeShapeStyleProperty(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
										// ShapeStyleProperty
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeShape(Shape o, Element e) throws ConverterException {

//		writeCommon(o, e); //CommentGroup, elementId
		String textLabel = o.getTextLabel();
		String type = o.getType().getName(); // TODO Shape type enum
		setAttribute("Shape", "TextLabel", e, textLabel);
		setAttribute("Shape", "Type", e, type);
		writeShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty
		writeRotation(o, e); // TODO rotation
		break;
	}

	protected void writeGroup(Group o, Element e) throws ConverterException {

		// TODO 2013a group does not have graphics
		writeShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty
		/** TODO GroupID */
		String id = o.createGroupId();
		if (id != null && !id.equals("")) {
			e.setAttribute("GroupId", o.createGroupId());
		}

		String textLabel = o.getTextLabel();
		String type = o.getType().getName(); // TODO Group type
		setAttribute("Group", "TextLabel", e, textLabel);
		setAttribute("Group", "Style", e, type);

		/** Xref added in GPML2021 */
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = o.getXref().getId();
		String dataSource = o.getXref().getDataSource().getFullName();
		setAttribute("State.Xref", "Database", xref, dataSource == null ? "" : dataSource);
		setAttribute("State.Xref", "ID", xref, identifier);

	}

	/**
	 *
	 */
	public Document createJdom(Pathway p) throws ConverterException {
		Document doc = new Document();

		Element root = new Element("Pathway", getGpmlNamespace());
		doc.setRootElement(root);

		List<Element> elementList = new ArrayList<Element>();

		// MappInfo
		Xref xref = p.getXref();
		List<Author> authors = p.getAuthors(); // length 0 to unbounded

//		List<Annotation> annotations  // --> Manager Pathway.getCitationManager.getCitations()
//		private List<Citation> citations; // --> Manager
//		private List<Evidence> evidences; // --> Manager

		List<Comment> comments = p.getComments(); // length 0 to unbounded
		List<DynamicProperty> dynamicProperties = p.getDynamicProperties(); // length 0 to unbounded
		List<AnnotationRef> annotationRefs = p.getAnnotationRefs(); // length 0 to unbounded
//		List<Citation> citationRefs; // length 0 to unbounded
//		List<Evidence> evidenceRef; // length 0 to unbounded
		double boardWidth = p.getBoardWidth();
		double boardHeight = p.getBoardHeight();

		InfoBox i = p.getInfoBox();
		Element ie = new Element("InfoBox", getGpmlNamespace());
		writeInfoBox(i, ie);

		e = new Element("Legend", getGpmlNamespace()); // TODO handle Legend

		List<DataNode> dataNodes = p.getDataNodes(); // TODO States
		Collections.sort(dataNodes); // TODO necessary?
		for (DataNode o : dataNodes) {
			Element e = new Element("DataNode", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			e.addContent(new Element("Xref", getGpmlNamespace()));
			writeDataNode(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		e = new Element("State", getGpmlNamespace());
		e.addContent(new Element("Graphics", getGpmlNamespace()));
		e.addContent(new Element("Xref", getGpmlNamespace()));

		List<Interaction> interactions = p.getInteractions();
		Collections.sort(interactions); // TODO necessary?
		for (Interaction o : interactions) {
			Element e = new Element("Interaction", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			e.addContent(new Element("Xref", getGpmlNamespace()));
			writeInteraction(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		List<GraphicalLine> graphicalLines = p.getGraphicalLines();
		Collections.sort(graphicalLines); // TODO necessary?
		for (GraphicalLine o : graphicalLines) {
			Element e = new Element("GraphicalLine", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			writeLineElement(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		List<Shape> shapes = p.getShapes();
		Collections.sort(shapes); // TODO necessary?
		for (Shape o : shapes) {
			Element e = new Element("Shape", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			writeShape(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		List<Group> groups = p.getGroups();
		Collections.sort(groups); // TODO necessary?
		for (Group o : groups) {
			Element e = new Element("Group", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			writeGroup(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		// now sort the generated elements in the order defined by the xsd
		Collections.sort(elementList, new ByElementName());
		for (Element e : elementList) {
			// make sure biopax references are sorted alphabetically by rdf-id
			if (e.getName().equals("Biopax")) {
				for (Element e3 : e.getChildren()) {
					e3.removeChildren("AUTHORS", GpmlFormat.BIOPAX);
				}
				e.sortChildren(new BiopaxAttributeComparator());
			}
			root.addContent(e);
		}

		return doc;

//		Collections.sort(pathwayElements);
//		for (PathwayElement o : pathwayElements)
//		{
//			if (o.getObjectType() == ObjectType.MAPPINFO)
//			{
//				updateMappInfo(root, o);
//			}
//			else
//			{
//				Element e = createJdomElement(o);
//				if (e != null)
//					elementList.add(e);
//			}
//		}

//					case BIOPAX:
//						e = new Element("Biopax", getGpmlNamespace());
//						writeBiopax(o, e);
//						break;
//					}if(e==null)
//
//	{
//		throw new ConverterException("Error creating jdom element with objectType " + o.getObjectType());
//	}return e;}

	}

	/**
	 * Writes the JDOM document to the outputstream specified
	 * 
	 * @param out      the outputstream to which the JDOM document should be writed
	 * @param validate if true, validate the dom structure before writing. If there
	 *                 is a validation error, or the xsd is not in the classpath, an
	 *                 exception will be thrown.
	 * @throws ConverterException
	 */
	public void writeToXml(Pathway pwy, OutputStream out, boolean validate) throws ConverterException {
		Document doc = createJdom(pwy);

		// Validate the JDOM document
		if (validate)
			validateDocument(doc);
		// Get the XML code
		XMLOutputter xmlcode = new XMLOutputter(Format.getPrettyFormat());
		Format f = xmlcode.getFormat();
		f.setEncoding("UTF-8");
		f.setTextMode(Format.TextMode.NORMALIZE);
		xmlcode.setFormat(f);

		try {
			// Send XML code to the outputstream
			xmlcode.output(doc, out);
		} catch (IOException ie) {
			throw new ConverterException(ie);
		}
	}

	/**
	 * Writes the JDOM document to the file specified
	 * 
	 * @param file     the file to which the JDOM document should be saved
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 */
	public void writeToXml(Pathway pwy, File file, boolean validate) throws ConverterException {
		OutputStream out;
		try {
			out = new FileOutputStream(file);
		} catch (IOException ex) {
			throw new ConverterException(ex);
		}
		writeToXml(pwy, out, validate);
	}
}
