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
 * $Id: SWFAnyTagReader.java,v 1.3 2001/03/16 16:51:08 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import org.xml.sax.SAXException;

import java.io.IOException;

import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitInputStream;

/**
 * Tag reader for arbitrary SWF tags.
 * This class can read arbitrary SWF tags. It will emit the following XML snippet:
 * <pre>
 * &lt;UnknownTag type="<em>TagType</em>"&gt;
 *   <em>Content of the unkown tag as base64-encoded data</em>
 * &lt;/UnknownTag&gt;
 * </pre>
 * <em>TagType</em> is the ID of the SWF tag.
 * @author Richard Kunze
 */
public class SWFAnyTagReader extends SWFTagReaderBase {
    /** Encoding table for Base64 according to RFC 1341 */
    private final char[] BASE64_TABLE = {
	'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
	'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
	'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    /** Maximum length for a line of Base64-encoded data according to
     * RFC 1341
     */
    private final int LINE_LENGTH = 76;

    /** Buffer for building the Base64-encoded strings */
    char[] buffer = new char[LINE_LENGTH + 1];
    
    /**
     * Read the tag content.
     * This class simply reads <code>header.getLength()</code> bytes
     * from <code>input</code>, encodes it in base64 and outputs it as
     * text child of an &lt;UnknownTag&gt;.
     * @param input the SWF data stream
     * @param header the record header for this record
     */
    public void parse(BitInputStream input, SWFTagHeader header)
                throws SAXException, IOException {
	SWFAttributes attrib = createAttributes();
	attrib.addAttribute("type", SWFAttributes.TYPE_CDATA,
			    Integer.toString(header.getID()));
	attrib.addAttribute("lenght", SWFAttributes.TYPE_CDATA,
			    Long.toString(header.getLength()));
	startElement("UnknownTag", attrib);
	long length = header.getLength();
	long groups = length / 3;
	int  pos    = 0;
	for (long i=0; i<groups; i++) {
	    buffer[pos++] = BASE64_TABLE[(int)input.readUBits(6)];
	    buffer[pos++] = BASE64_TABLE[(int)input.readUBits(6)];
	    buffer[pos++] = BASE64_TABLE[(int)input.readUBits(6)];
	    buffer[pos++] = BASE64_TABLE[(int)input.readUBits(6)];
	    if (pos == LINE_LENGTH) {
		buffer[pos] = '\n';
		characters(buffer, 0, buffer.length);
		pos = 0;
	    }
	}
	switch ((int)length%3) {
	case 2:
	    buffer[pos++] = BASE64_TABLE[(int)input.readUBits(6)];
	    buffer[pos++] = BASE64_TABLE[(int)input.readUBits(6)];
	    buffer[pos++] = BASE64_TABLE[(int)input.readUBits(4)<<2];
	    buffer[pos++] = '=';
	    break;
	case 1:
	    buffer[pos++] = BASE64_TABLE[(int)input.readUBits(6)];
	    buffer[pos++] = BASE64_TABLE[(int)input.readUBits(4)<<4];
	    buffer[pos++] = '=';
	    buffer[pos++] = '=';
	}
	buffer[pos] = '\n';
	characters(buffer, 0, pos+1);
	endElement("UnknownTag");
    }
}
