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
 * $Id: SWFTagReader.java,v 1.4 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.parser;

import org.xml.sax.SAXException;

import java.io.IOException;
import de.tivano.flash.swf.common.BitInputStream;
import de.tivano.flash.swf.common.SWFTagHeader;

/**
 * Interface for reading SWF tags.
 * <p>Implementing classes read SWF records from an
 * <code>InputStream</code> and report corresponding SAX events by
 * means of an associated <code>ContentHandler</code></p>
 * @author Richard Kunze
 * @see SWFReader
 * @see org.xml.sax.ContentHandler
 */
public interface SWFTagReader {

    /**
     * Associate an <code>SWFReader</code> with this object.  Among
     * other things, the associated <code>SWFReader</code> allows
     * access to the <code>ContentHandler</code> and
     * <code>ErrorHandler</code> used to report SAX events or errors
     * to the SAX application.
     * 
     * @param driver the associated <code>SWFReader</code> for this
     * tag handler. 
     */
    public void setSAXDriver(SWFReader driver);

    /**
     * Read part of an SWF data stream.
     * <p>The current stream position is
     * just after the record header of this record. the record header
     * itself is provided in <code>header</code>.</p>
     * 
     * <p>Implementing classes must read the complete tag from
     * <code>input</code>, i.e. they must read exactly
     * <code>header.getLength()</code> bytes.</p>
     * @param input the SWF data stream
     * @param header the record header for this record
     */
    public void parse(BitInputStream input, SWFTagHeader header)
	throws SAXException, IOException;
}
