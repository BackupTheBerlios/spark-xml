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
 * $Id: SWFEmptyTagWriter.java,v 1.1 2001/06/11 18:36:26 kunze Exp $
 */

package de.tivano.flash.swf.publisher;

import java.io.IOException;
import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitOutputStream;

/**
 * Represents "empty" SWF tags. An empty SWF tag does not contain any
 * data besides its tag header. 
 * @author Richard Kunze
 */
public class SWFEmptyTagWriter extends SWFTagWriter {
    /**
     * Construct a new tag writer for the given tag type.
     * @param typeID the type ID for the SWF data structure
     * @exception IllegalArgumentException if <code>tagID</code> is
     * not a legal SWF data type ID
     */
    public SWFEmptyTagWriter(int typeID) { super(typeID); }

    /**
     * Get the length (excluding the header) in bytes.
     * @returns 0
     */
    protected long getDataLength() { return 0; }

    /**
     * Write the actual SWF data (excluding the header)
     * to <code>out</code>. Does nothing, as there is no data to write.
     */
    public void writeData(BitOutputStream out) throws IOException {}
}
