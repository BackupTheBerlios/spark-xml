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
 * $Id: XMLShapeRawHandler.java,v 1.2 2002/01/25 13:50:10 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import de.tivano.flash.swf.common.SWFShape;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Handler for the <em>&lt;ShapeRaw&gt;</em> XML tag.
 * <p>The <em>&lt;ShapeRaw&gt;</em> tag holds the undecoded, raw data
 * of an SWF shape structure in base64 encoding. It is mainly used for
 * the glyph shapes in font, where the actual shape is of little
 * interest to the application and using raw shapes instead of the
 * more verbose form considerably cuts down the size of the XML data.
 */
public class XMLShapeRawHandler extends XMLShapeHandler {

    ByteArrayOutputStream data = new ByteArrayOutputStream();
    Base64Decoder dataDecoder = new Base64Decoder(data);

    /** Collect the base64 encoded data */
    protected void handleText(char[] ch, int start, int length)
	      throws SWFWriterException {
	try {
	    dataDecoder.write(ch, start, length);
	} catch (IOException e) {
	    fatalError(e);
	}
    }

    /** Construct the SWF shape from the collected raw data */
    protected void endElement() throws SWFWriterException {
	try {
	    dataDecoder.flush();
	    // XXX: Make this handle RGBA shapes as well...
	    shape = new SWFShape(data.toByteArray(), false);
	    data.reset();
	} catch (IOException e) {
	    fatalError(e);
	}
	super.endElement();
    }

}
