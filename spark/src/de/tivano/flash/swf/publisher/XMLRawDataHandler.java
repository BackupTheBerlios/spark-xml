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
 * $Id: XMLRawDataHandler.java,v 1.3 2001/06/28 17:15:14 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.xml.sax.Attributes;
import java.io.IOException;
import de.tivano.flash.swf.common.BitOutputStream;

/**
 * Handler for the <em>&lt;RawData&gt;</em> XML tag.
 * The <em>&lt;RawData&gt;</em> tag is a catch-all construct that
 * simply holds the base64-encoded data of the corresponding SWF
 * toplevel structure.
 * @author Richard Kunze
 * @see de.tivano.flash.swf.parser.SWFAnyTagReader
 */
public class XMLRawDataHandler extends SWFTagHandlerBase {

    /** The SWF writer object */
    private SWFTagWriter writer = null;

    /** The base64 decoder */
    Base64Decoder decoder = null;

    /**
     * The SWF writer class for raw data.
     */
    private static class RawDataWriter extends SWFTagWriter {
	private ByteArrayOutputStream data = new ByteArrayOutputStream();
	public RawDataWriter(int typeID) {
	    super(typeID);
	}

	protected long getDataLength() { return data.size(); }
	public void writeData(BitOutputStream out) throws IOException {
	    data.writeTo(out);
	}
	/** Get an output stream for writing data */
	public OutputStream getDataOutputStream() { return data; }
    }

    /**
     * Create the SWF writer object for actually writing out raw data.
     */
    protected SWFTagWriter createDataObject() {
	return writer;
    }

    /**
     * Start processing a &lt;RawData&gt; element.
     */
    protected void startElement(String name, Attributes attrib)
	      throws SWFWriterException {
	RawDataWriter dataWriter = 
	    new RawDataWriter(Integer.parseInt(attrib.getValue("","type")));
	writer = dataWriter;
	decoder = new Base64Decoder(dataWriter.getDataOutputStream());
    }
	
    /**
     * Finish processing a &lt;RawData&gt; element.
     */
    protected void endElement() throws SWFWriterException {
	try {
	    decoder.close();
	    super.endElement();
	} catch (IOException e) {
	    fatalError("Error flushing the decoder stream", e);
	}
    }
	
    /**
     * Handle text. The text is assumed to be base64 encoded, raw SWF
     * data.
     * @param data the data buffer to read from
     * @param start index of the first relevant character
     * @param length the number of characters to read
     */
    protected void handleText(char[] data, int start, int length)
              throws SWFWriterException {
	try {
	    decoder.write(data, start, length);
	} catch (IOException e) {
	    fatalError("Cannot decode the data", e);
	}
    }
}
