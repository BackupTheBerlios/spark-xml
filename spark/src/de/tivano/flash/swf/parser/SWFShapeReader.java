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
 * $Id: SWFShapeReader.java,v 1.1 2001/05/15 18:16:08 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import de.tivano.flash.swf.common.SWFShape;
import de.tivano.flash.swf.common.SWFShapeRecord;
import de.tivano.flash.swf.common.SWFEndOfShape;
import de.tivano.flash.swf.common.SWFMoveTo;
import de.tivano.flash.swf.common.SWFStraightEdge;
import de.tivano.flash.swf.common.SWFCurvedEdge;
import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitInputStream;

import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.Iterator;

/**
 * Converts an SWF shape structure to XML.
 * This class emits the following XML snippet:
 * <pre>
 * &lt;Shape&gt;
 *   &lt;!-- There may be any number of path elements in a shape --&gt;
 *   &lt;Path&gt;
 *     &lt;Start  x="<em>X</em>" y="<em>Y</em>" /gt;
 *     &lt;!-- There may be any number of Line and Bezier elements in
 *             a Path, in any order --&gt;
 *     &lt;Line   x="<em>X</em>" y="<em>Y</em>" /gt;
 *     &lt;Bezier x="<em>X</em>" y="<em>Y</em>" cx="<em>CX</em> cy="<em>xy</em>"/gt;
 *   &lt;/Path&gt;
 * &lt;/Shape&gt;
 * </pre>
 * Currently, <code>SWFShapeHelper</code> does not support line or
 * fill styles. This will change in later versions.
 * @author Richard Kunze
 */
public class SWFShapeReader extends SWFTagReaderBase {

    /** Read the content.
     * @param input the SWF data stream
     * @param header the record header for this record. This parameter
     * is ignored and may be <code>null</code>
     */
    public void parse(BitInputStream input, SWFTagHeader header)
                throws SAXException, IOException {
	parse(input);
    }
    
    /**
     * Read the content.
     * @param input the SWF data stream
     */
    public void parse(BitInputStream input)
                throws SAXException, IOException {
	toXML(new SWFShape(input));
    }

    /**
     * Convert a {@link SWFShape} to XML.
     * @param the shape structure
     */
    public void toXML(SWFShape shape) throws SAXException {
	SWFAttributes attrib = createAttributes();
	startElement("Shape", null);
	Iterator records = shape.getShapeRecords();
	boolean insidePath = false;
	while (records.hasNext()) {
	    Object entry = records.next();
	    if (entry instanceof SWFCurvedEdge) {
		// Paranoia code. Should never happen.
		if (!insidePath) throw new SAXException(
                  "SWF edge record found without preceding MoveTo");
		attrib.clear();
		SWFCurvedEdge c = (SWFCurvedEdge)entry;
		attrib.addAttribute("x", c.getAnchorX());
		attrib.addAttribute("y", c.getAnchorY());
		attrib.addAttribute("cx", c.getControlX());
		attrib.addAttribute("cy", c.getControlY());
		emptyElement("Bezier", attrib);
	    } else if (entry instanceof SWFStraightEdge) {
		if (!insidePath) throw new SAXException(
                  "SWF edge record found without preceding MoveTo");
		SWFStraightEdge s = (SWFStraightEdge)entry;
		attrib.addAttribute("x", s.getX());
		attrib.addAttribute("y", s.getY());
		emptyElement("Line", attrib);
	    } else {
		if (insidePath) {
		    endElement("Path");
		    insidePath = false;
		}
		if (entry instanceof SWFEndOfShape) {
		    // Do nothing....
		} else if (entry instanceof SWFMoveTo) {
		    startElement("Path", null);
		    insidePath = true;
		    attrib.clear();
		    SWFMoveTo m = (SWFMoveTo)entry;
		    attrib.addAttribute("x", m.getX());
		    attrib.addAttribute("y", m.getY());
		    emptyElement("Start", attrib);
		} else {
		    throw new SAXException(
		      "FIXME: " + entry.getClass().getName() +
		      " not yet supported.");
		}
	    }
	}
	endElement("Shape");
    }
}
