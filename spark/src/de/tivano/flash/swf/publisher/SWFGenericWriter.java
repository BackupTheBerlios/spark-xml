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
 * $Id: SWFGenericWriter.java,v 1.2 2001/07/04 08:37:05 kunze Exp $
 */

package de.tivano.flash.swf.publisher;

import java.io.IOException;
import de.tivano.flash.swf.common.SWFTopLevelDataType;
import de.tivano.flash.swf.common.BitOutputStream;

/**
 * Generic writer for every {@link
 * de.tivano.flash.swf.common.SWFDataType} that represents a SWF top
 * level structure.
 * @author Richard Kunze
 */
public class SWFGenericWriter extends SWFTagWriter {
    /** The data to write */
    private SWFTopLevelDataType data;
    
    /**
     * Construct a new tag writer for the given tag type.
     * @param typeID the type ID for the SWF data structure
     * @param data the data to write
     * @exception IllegalArgumentException if <code>tagID</code> is
     * not a legal SWF data type ID
     */
    public SWFGenericWriter(SWFTopLevelDataType data) {
	super(data.getTagType());
	this.data = data;
    }

    /**
     * Get the length (excluding the header) in bytes.
     */
    protected long getDataLength() { return data.length() / 8; }

    /**
     * Write the actual SWF data (excluding the header)
     * to <code>out</code>. Does nothing, as there is no data to write.
     */
    public void writeData(BitOutputStream out) throws IOException {
	data.write(out);
    }
}
