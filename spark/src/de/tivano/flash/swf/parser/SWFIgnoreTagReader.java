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
 * $Id: SWFIgnoreTagReader.java,v 1.1 2001/06/11 18:36:26 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;

import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitInputStream;

/**
 * SWF Tag handler that does not produce any XML. Use this handler for
 * skipping SWF tags such as "End" that do not need to be replicated
 * in XML.
 * @author Richard Kunze
 * @see org.xml.sax.ContentHandler
 */
public class SWFIgnoreTagReader extends SWFTagReaderBase {
    /**
     * Read the tag content. This method simply skips
     * <code>header.getRecordLength()</code> bytes.
     * @param input the SWF data stream
     * @param header the SWF tag header for this data record
     */
    public void parse(BitInputStream input, SWFTagHeader header)
	throws SAXException, IOException {
	input.skip(header.getRecordLength());
    }
}

