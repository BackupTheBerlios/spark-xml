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
 * $Id: SWFAnyTagReader.java,v 1.2 2001/03/14 12:27:11 kunze Exp $
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
	startElement("UnknownTag", attrib);
	endElement("UnknownTag");
    }
}
