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
 * $Id: SWFTagWriter.java,v 1.1 2001/06/01 17:25:53 kunze Exp $
 */

package de.tivano.flash.swf.publisher;

import java.io.IOException;
import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitOutputStream;

/**
 * Base class for all classes that represent SWF toplevel data structures.
 * @author Richard Kunze
 */
public abstract class SWFTagWriter {
    /** The associated SWF tag header */
    protected final SWFTagHeader HEADER = new SWFTagHeader();

    /** Construct a new tag writer. */
    protected SWFTagWriter() {}

    /**
     * Construct a new tag writer for the given tag type.
     * Derived classes <em>must</em> call this constructor.
     * @param typeID the type ID for the SWF data structure
     * @exception IllegalArgumentException if <code>tagID</code> is
     * not a legal SWF data type ID
     */
    protected SWFTagWriter(int typeID) {
	HEADER.setID(typeID);
    }

    /** Get the total length (including the header) in bytes. */
    public long getTotalLength() {
	// The length of the header varies with the length of the
	// follwoing record, so we have to set it here to get correct
	// results...
	long dataLength = getDataLength();
	HEADER.setRecordLength(dataLength);
	// SWFHeader.length() returns the length in bits, but for an SWF
	// header, this is always a multiple of 8
	return dataLength + HEADER.length() / 8;
    }

    /**
     * Write the complete SWF data (including the header)
     * to <code>out</code>
     */
    public void write(BitOutputStream out) throws IOException {
	HEADER.setRecordLength(getDataLength());
	HEADER.write(out);
	writeData(out);
    }

    /**
     * Get the length (excluding the header) in bytes.
     * Subclasses must implement this method.
     */
    protected abstract long getDataLength();

    /**
     * Write the actual SWF data (excluding the header)
     * to <code>out</code>.
     */
    public abstract void writeData(BitOutputStream out) throws IOException;
}
