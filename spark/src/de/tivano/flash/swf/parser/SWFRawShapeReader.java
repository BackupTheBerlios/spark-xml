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
 * $Id: SWFRawShapeReader.java,v 1.1 2001/05/28 17:51:28 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import de.tivano.flash.swf.common.SWFShape;
import de.tivano.flash.swf.common.SWFShapeRecord;
import de.tivano.flash.swf.common.SWFMoveTo;
import de.tivano.flash.swf.common.SWFStateChange;
import de.tivano.flash.swf.common.SWFStraightEdge;
import de.tivano.flash.swf.common.SWFCurvedEdge;
import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitInputStream;
import de.tivano.flash.swf.common.BitOutputStream;

import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.Iterator;

/**
 * Converts an SWF shape structure to XML. 
 * <p>As opposed to {@link SWFShapeReader}, this class does not emit XML
 * for the individual lines and curves comprising the shape. Instead,
 * the raw shape data is emitted as text child of the shape record
 * (either in base64 encoding or directly, depending on setting in the
 * the associated {@link SWFReader} ). This class can be used to
 * drastically reduce the size of shape data when the internal
 * structure of the shape is not needed.</p>
 * <p>This class emits the following XML snippet:</p>
 * <pre>
 * &lt;ShapeRaw&gt;
 *   &lt;!-- Raw or base64-encoded shape data --&gt;
 * &lt;/Shape&gt;
 * </pre>
 * @author Richard Kunze
 */
public class SWFRawShapeReader extends SWFShapeReader {

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
    public void toXML(SWFShape shape) throws IOException, SAXException {
	startElement("ShapeRaw", null);
	BitOutputStream out = new BitOutputStream(getRawDataOutputStream());
	try {
	    shape.write(out);
	    out.close();
	} catch (SAXIOException e) {
	    // Re-throw the wrapped exception
	    throw e.getCause();
	}
	endElement("ShapeRaw");
    }
}
