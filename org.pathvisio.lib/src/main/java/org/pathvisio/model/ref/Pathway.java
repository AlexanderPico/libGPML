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
package org.pathvisio.model.ref;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.Xref;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.events.PathwayElementListener;
import org.pathvisio.util.Utils;

/**
 * This class stores metadata for a Pathway. Pathway is treated as a
 * {@link PathwayElement} for simplicity of listeners and implementation.
 * 
 * Because of multiple optional parameters, a builder pattern is implemented for
 * Pathway. Example of how a Pathway object can be created:
 * 
 * Pathway pathway = new Pathway.PathwayBuilder("Title", 100, 100,
 * Color.decode("#ffffff")).setOrganism("Homo Sapiens")
 * .setSource("WikiPathways").setVersion("r1").setLicense("CC0").setXref(xref).build();
 * 
 * @author finterly
 */
public class Pathway extends PathwayElement {

	private String title;
	private double boardWidth;
	private double boardHeight;
	private Color backgroundColor;
	private List<Author> authors;

	// TODO check if builder and stuff works correctly...

	private String description;
	private String organism;
	private String source;
	private String version;
	private String license;
	private Xref xref;

	/**
	 * This builder class builds an Pathway object step-by-step.
	 * 
	 * @author finterly
	 */
	public static class PathwayBuilder {

		private String title = "Click to add title"; // TODO
		private double boardWidth = 0;
		private double boardHeight = 0;
		private Color backgroundColor = Color.decode("#ffffff");
		private List<Author> authors;
		private String description; // optional
		private String organism; // optional
		private String source; // optional
		private String version; // optional
		private String license; // optional
		private Xref xref; // optional

		/**
		 * Public constructor with required attribute name as parameter. //TODO actually
		 * required?
		 * 
		 * @param title           the title of this pathway.
		 * @param boardWidth      together with...
		 * @param boardHeight     define the drawing size.
		 * @param backgroundColor the background color of the drawing, default #ffffff
		 *                        (white)
		 */
		public PathwayBuilder(String title, double boardWidth, double boardHeight, Color backgroundColor) {
			this.title = title;
			this.boardWidth = boardWidth;
			this.boardHeight = boardHeight;
			this.backgroundColor = backgroundColor;
			this.authors = new ArrayList<Author>();
		}

		/**
		 * Sets description and returns this builder object. Description is the textual
		 * description for this pathway.
		 * 
		 * @param v the description of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setDescription(String v) {
			description = v;
			return this;
		}

		/**
		 * Sets organism and returns this builder object. Organism is the scientific
		 * name (e.g., Homo sapiens) of the species being described by this pathway.
		 * 
		 * @param v the organism of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setOrganism(String v) {
			organism = v;
			return this;
		}

		/**
		 * Sets source and returns this builder object. The source of this pathway, e.g.
		 * WikiPathways, KEGG, Cytoscape.
		 * 
		 * @param v the source of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setSource(String v) {
			source = v;
			return this;
		}

		/**
		 * Sets version and returns this builder object.
		 * 
		 * @param v the version of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setVersion(String v) {
			version = v;
			return this;
		}

		/**
		 * Sets license and returns this builder object.
		 * 
		 * @param v the license of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setLicense(String v) {
			license = v;
			return this;
		}

		/**
		 * Sets xref and returns this builder object.
		 * 
		 * @param v the xref of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setXref(Xref v) {
			xref = v;
			return this;
		}

		/**
		 * Calls the private constructor in this pathway class and passes builder object
		 * itself as the parameter to this private constructor.
		 * 
		 * @return the created Pathway object.
		 */
		public Pathway build() {
			return new Pathway(this);
		}
	}

	/**
	 * Private constructor for Pathway which takes PathwayBuilder object as its
	 * argument.
	 * 
	 * @param builder this pathwayBuilder object.
	 */
	private Pathway(PathwayBuilder builder) {
		this.title = builder.title;
		this.boardWidth = builder.boardWidth;
		this.boardHeight = builder.boardHeight;
		this.backgroundColor = builder.backgroundColor;
		this.authors = builder.authors;
		this.description = builder.description;
		this.organism = builder.organism;
		this.source = builder.source;
		this.version = builder.version;
		this.license = builder.license;
		this.xref = builder.xref;
	}

	/**
	 * Returns the title or name of this pathway.
	 * 
	 * @return title the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title or name of this pathway.
	 * 
	 * @param v the title to set.
	 */
	public void setTitle(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (title != v) {
			title = v;
			// fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this,
			// StaticProperty.TITLE));
		}
	}

	/**
	 * Returns the board width. Board width together with board height define
	 * drawing size.
	 * 
	 * @return boardWidth the board width
	 */
	public double getBoardWidth() {
		return boardWidth;
	}

	/**
	 * Sets the board width.
	 * 
	 * @param v the board width to set.
	 */
	public void setBoardWidth(double v) {
		if (v < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		} else {
			boardWidth = v;
		}
	}

	/**
	 * Returns the board height. Board width together with board height define
	 * drawing size.
	 * 
	 * @return boardHeight the board height
	 */
	public double getBoardHeight() {
		return boardHeight;
	}

	/**
	 * Sets the board height.
	 * 
	 * @param v the board height to set.
	 */
	public void setBoardHeight(double v) {
		if (v < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		} else {
			boardHeight = v;
		}
	}

	/**
	 * Returns the background color of this pathway.
	 * 
	 * @return backgroundColor the background color.
	 */
	public Color getBackgroundColor() {
		if (backgroundColor == null) {
			this.backgroundColor = Color.decode("#ffffff");
		}
		return backgroundColor;
	}

	/**
	 * Sets the background color of this pathway.
	 * 
	 * @param v the background color to set.
	 */
	public void setBackgroundColor(Color v) {
		backgroundColor = v;
	}

	/**
	 * Returns the list of authors for this pathway model.
	 * 
	 * @return authors the list of authors.
	 */
	public List<Author> getAuthors() {
		return authors;
	}

	/**
	 * Adds the given author to authors list.
	 * 
	 * @param author the author to add.
	 */
	public void addAuthor(Author author) {
		authors.add(author);
	}

	/**
	 * Removes the given author from authors list.
	 * 
	 * @param author the author to remove.
	 */
	public void removeAuthor(Author author) {
		authors.remove(author);
	}

	/**
	 * Returns the description of this pathway.
	 * 
	 * @return description the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this pathway.
	 * 
	 * @param v the description to set.
	 */
	public void setDescription(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		} else
			description = v;
	}

	/**
	 * Returns the organism of this pathway. Organism is the scientific name (e.g.,
	 * Homo sapiens) of the species being described by this pathway.
	 * 
	 * @return organism the organism.
	 */
	public String getOrganism() {
		return organism;
	}

	/**
	 * Sets the organism of this pathway. Organism is the scientific name (e.g.,
	 * Homo sapiens) of the species being described by this pathway.
	 * 
	 * @param v the organism to set.
	 */
	public void setOrganism(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (!Utils.stringEquals(organism, v)) {
			organism = v;
			// fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this,
			// StaticProperty.ORGANISM));
		}
	}

	/**
	 * Returns the source of this pathway, e.g. WikiPathways, KEGG, Cytoscape.
	 * 
	 * @return source the source.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source of this pathway, e.g. WikiPathways, KEGG, Cytoscape.
	 * 
	 * @param v the source to set.
	 */
	public void setSource(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		} else
			source = v;
	}

	/**
	 * Returns the version of this pathway.
	 * 
	 * @return version the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version of this pathway.
	 * 
	 * @param v the version to set.
	 */
	public void setVersion(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (version != v) {
			version = v;
			// fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this,
			// StaticProperty.VERSION));
		}
	}

	/**
	 * Returns the license of this pathway.
	 * 
	 * @return license the license.
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * Sets the license of this pathway.
	 * 
	 * @param v the license to set.
	 */
	public void setLicense(String v) {
		license = v;
	}

	/**
	 * Returns the Xref for this pathway.
	 * 
	 * @return xref the xref of this pathway.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this pathway.
	 * 
	 * @param v the xref to set for this pathway.
	 */
	public void setXref(Xref v) {
		xref = v;
		// fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this,
		// StaticProperty.IDENTIFIER));
	}

	// TODO....

	/**
	 * Fire and Listener methods below TODO
	 */
	int noFire = 0;

	public void dontFireEvents(int times) {
		noFire = times;
	}

	private Set<PathwayElementListener> listeners = new HashSet<PathwayElementListener>();

	public void addListener(PathwayElementListener v) {
		if (!listeners.contains(v))
			listeners.add(v);
	}

	public void removeListener(PathwayElementListener v) {
		listeners.remove(v);
	}

	public void fireObjectModifiedEvent(PathwayElementEvent e) {
		if (noFire > 0) {
			noFire -= 1;
			return;
		}
//		if (pathwayModel != null)
//			pathwayModel.childModified(e);
		for (PathwayElementListener g : listeners) {
			g.gmmlObjectModified(e);
		}
	}

	/**
	 * Terminates this pathway element. The pathway model, if any, is unset from
	 * this pathway element. Links to all annotationRefs, citationRefs, and
	 * evidenceRefs are removed from this data node.
	 */
	@Override
	public void terminate() {
		// Is pathway allowed to be terminated?
	}

}
