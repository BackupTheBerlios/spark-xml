/**
 * The contents of this file are subject to the Flash2XML Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the
 * License on the Flash2XML web site
 * (http://www.tivano.de/opensource/flash). 
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License. 
 *
 * The Initial Developer of Flash2XML is Tivano Software GmbH. The
 * original Flash2XML and portions created by Tivano Software GmbH are
 * Copyright Tivano Software GmbH. All Rights Reserved. 
 *
 * Contributor(s):
 *      Richard Kunze, Tivano Software GmbH.
 *
 * $Id: XMLGlyphHandler.java,v 1.1 2001/06/28 17:15:46 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import de.tivano.flash.swf.common.SWFTypes;
import de.tivano.flash.swf.common.SWFFont;
import de.tivano.flash.swf.common.SWFRectangle;
import de.tivano.flash.swf.common.SWFShape;
import org.xml.sax.Attributes;

/**
 * Handler for the <em>&lt;Font&gt;</em> XML tag.
 * The <em>&lt;Font&gt;</em> tag defines a font which is expressed in
 * SWF as either a DefineFont2 structure or a
 * DefineFont/DefineFontInfo pair.
 * @see de.tivano.flash.swf.parse.SWFDefineFont2Reader
 * @see de.tivano.flash.swf.parse.SWFDefineFontReader
 * @see de.tivano.flash.swf.parse.SWFDefineFontInfoReader
 */
public class XMLGlyphHandler extends XMLHandlerBase {
    private Character ch;
    private int advance = 0;
    private SWFRectangle bounds = null;
    private SWFShape shape;
    
    public XMLGlyphHandler() {
	registerElementHandler("ShapeRaw", new XMLShapeRawHandler());
	registerElementHandler("Shape", new XMLShapeHandler());
    }

    /** Get the character code for this glyph. */
    public Character getChar() { return ch; }

    /**
     * Get the X advance value of this glyph.
     * @return the advance value or 0 if none is specified in the XML
     * element.
     */
    public int getAdvance() { return advance; }

    /**
     * Get the bounding box for this glyph.
     * @return the bounding box, or <code>null</code> if none is
     * specified in the XML element.
     */
    public SWFRectangle getBounds() { return bounds; }

    /** Get the shape of this glyph */
    public SWFShape getShape() { return shape; }

    /**
     * Collect the information of the child <em>&lt;Shape&gt;</em> or
     * <em>&lt;ShapeRaw&gt;</em> XML element.
     */
    protected void notify(java.lang.String element, XMLHandlerBase handler)
	throws SWFWriterException {
	shape = ((XMLShapeHandler)handler).getShape();
    }

    /** Collect information from the XML attributes */
    protected void startElement(java.lang.String name, Attributes attrib)
	      throws SWFWriterException {
	// Paranoia code.
	if (!name.equals("Glyph")) {
	    fatalError("Illegal element for this handler: " + name);
	}
	String tmp;
	tmp = attrib.getValue("", "char");
	ch = new Character(tmp.charAt(0));
	tmp = attrib.getValue("", "advance");
	if (tmp!=null) advance = Integer.parseInt(tmp);
	String xmin, xmax, ymin, ymax;
	xmin = attrib.getValue("", "xmin");
	xmax = attrib.getValue("", "xmax");
	ymin = attrib.getValue("", "ymin");
	ymax = attrib.getValue("", "ymax");
	if (!(xmin == null && xmax == null && ymin == null && ymax == null)) {
	  try {
	    bounds = new SWFRectangle(Integer.parseInt(xmin),
				      Integer.parseInt(xmax),
				      Integer.parseInt(ymin),
				      Integer.parseInt(ymax));
	  } catch (Exception e) {
	    fatalError(e);
	  }
	}
    }
}
