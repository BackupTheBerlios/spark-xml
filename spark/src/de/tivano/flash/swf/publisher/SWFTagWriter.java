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
 * $Id: SWFTagWriter.java,v 1.7 2001/07/04 08:37:05 kunze Exp $
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
     * @param typeID the type ID for the SWF data structure
     * @exception IllegalArgumentException if <code>tagID</code> is
     * not a legal SWF data type ID
     */
    protected SWFTagWriter(int typeID) {
	HEADER.setID(typeID);
    }

    /** Get the total length (including the header) in bytes. */
    public long getTotalLength() throws IOException {
	doInitDataLength();
	// The length of the header varies with the length of the
	// following record, so we have to set it here to get correct
	// results...
	long dataLength = getDataLength();
	HEADER.setRecordLength(dataLength);
	// SWFHeader.length() returns the length in bits, but for an SWF
	// header, this is always a multiple of 8
	return dataLength + HEADER.length() / 8;
    }

    /**
     * Write the complete SWF data (including the header)
     * to <code>out</code>.
     */
    public void write(BitOutputStream out) throws IOException {
	doInitWriteData();
	HEADER.setRecordLength(getDataLength());
	HEADER.write(out);
	writeData(out);
    }

    /** Flag, tells if {@link #initWriteData} has been called already */
    private boolean dataInitialized = false;

    /** Calls {@link initWriteData} if it has not been called before */
    protected void doInitWriteData() throws IOException {
	if (!dataInitialized) {
	    dataInitialized = true;
	    initWriteData();
	}
    }

    /**
     * Prepare the data immediately prior to writing.
     * Subclasses may use this method to make last-minute decisions
     * about the data they want to write.
     * This method is called exactly once before the first call to
     * {@link #writeData} and after all XML data has been processed.
     * The default implementation does nothing.
     */
    protected void initWriteData() throws IOException {}

    /** Flag, tells if {@link #initDataLength} has been called already */
    private boolean lengthInitialized = false;

    /** Calls {@link #initDataLength} if it has not been called before */
    protected void doInitDataLength() throws IOException {
	if (!lengthInitialized) {
	    lengthInitialized = true;
	    initDataLength();
	}
    }

     /**
     * Prepare the data for calculating the length.
     * Subclasses may use this method to make last-minute decisions
     * about the data.
     * This method is called exactly once before the first call to
     * {@link #getDataLength} and after all XML data has been processed.
     * The default implementation calls {@link #initWriteData} if it
     * has not been called before.
     */
    protected void initDataLength() throws IOException {
	doInitWriteData();
    }

   /** Get the SWF type ID for this structure */
    public int getTypeID() { return HEADER.getID(); }

    /**
     * Get the length (excluding the header) in bytes.
     * It is guaranteed that {@link #initDataLength} has been called
     * before the first call to this method.
     * Subclasses must implement this method.
     */
    protected abstract long getDataLength();

    /**
     * Write the actual SWF data (excluding the header).
     * It is guaranteed that {@link #initWriteData} has been called before
     * the first call to this method.
     * to <code>out</code>.
     */
    public abstract void writeData(BitOutputStream out) throws IOException;
}
