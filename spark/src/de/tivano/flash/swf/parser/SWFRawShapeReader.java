/**
 * The contents of this file are subject to the Spark Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the
 * License on the Spark web site
 * (http://www.tivano.de/opensource/flash). 
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License. 
 *
 * The Initial Developer of Spark is Tivano Software GmbH. The
 * original Spark and portions created by Tivano Software GmbH are
 * Copyright Tivano Software GmbH. All Rights Reserved. 
 *
 * Contributor(s):
 *      Richard Kunze, Tivano Software GmbH.
 *
 * $Id: SWFRawShapeReader.java,v 1.3 2002/05/22 17:11:17 richard Exp $
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

    /**
     * Construct a new <code>SWFShapeReader</code>
     * @param useRGBA flag, tells if this shape reader will assume
     * that colors in fill- or linestyle definitions include an alpha
     * value.
     */
    public SWFRawShapeReader(boolean useRGBA) { super(useRGBA); }

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
	    throw e.getSAXCause();
	}
	endElement("ShapeRaw");
    }
}
