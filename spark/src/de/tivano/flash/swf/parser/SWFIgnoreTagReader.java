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
 * $Id: SWFIgnoreTagReader.java,v 1.2 2002/05/22 17:11:17 richard Exp $
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

