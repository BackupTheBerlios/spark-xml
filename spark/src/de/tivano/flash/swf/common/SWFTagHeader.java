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
 * $Id: SWFTagHeader.java,v 1.9 2001/06/11 18:34:05 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents a SWF tag header.
 *
 * <p>The header of an SWF tag consists of two fields: A tag ID and a
 * length field. There are two types of SWF tag headers, distinguished by
 * the maximum value of the length field: <em>Short headers</em> for
 * records up to 62 bytes in length, and <em>long headers</em> for
 * records longer than 62 bytes. A short header has a total length of
 * 16 bits, a long header of 48.</p>
 * 
 * <p>In any case, the tag ID is encoded in the 10 high order bits of
 * the first 16 bit word. The 6 low order bits hold the record length
 * for short headers and the value <code>0x3f</code> for long
 * headers. The record size of a long header is encoded in a 32 bit
 * unsigned integer following the first 16 bit word. All values are
 * stored in LSB (least significant byte first) order.</p>
 *
 * <p><code>SWFTagHeader</code> transparently handles reading and
 * writing both header types.</p>
 * @author Richard Kunze
 */
public class SWFTagHeader {
    /** The maximum length (in bytes) of an SWF tag header */
    public static final int MAX_LENGTH = 6;
    
    /** The tag ID */
    private Integer id;
    
    /** The record length */
    private long length;

    /** Flag for a long tag header */
    private final int LONG_HEADER_FLAG = 0x3f;

    /** Mask for the length/flag portion of the first header word */
    private final int LENGTH_FIELD_MASK = 0x3f;

    /** Mask for the ID portion of the first header word */
    private final int ID_FIELD_MASK = 0xffc0;

    /**
     * Construct a tag header from an input stream.
     * @param input the input stream
     * @exception SWFFormatException if the tag header cannot be read
     * correctly.
     * @expetion IOException if some other IO error occurs
     */
    public SWFTagHeader(BitInputStream input)
	   throws IOException {
	long lengthTmp;

	// The tag header is always byte-aligned...
	if (!input.isAtByteBoundary())
	    throw new SWFFormatException("Tag headers must be byte-aligned");
	
	int tmp = input.readUW16LSB();
	id = new Integer(tmp >>> 6);
	lengthTmp = tmp & 0x3f;
	
	if (lengthTmp == LONG_HEADER_FLAG) {
	    try {
		lengthTmp = input.readUW32LSB();
	    } catch (EOFException e) {
		throw new SWFFormatException(
			    "Could not read 32bit record length");
	    }
	}
	length = lengthTmp;
    }

    /**
     * Construct a tag header with a given tag ID and length.
     * @param id the tag ID
     * @param length the record length in bytes
     * @exception IllegalArgumentException if <code>id</code> is not
     * in the range 0 to 1023.
     * @exception IllegalArgumentException if <code>length</code> is
     * not in the range 0 to 4294967295 (2<super>32</super>-1)
     */
    public SWFTagHeader(int id, long length) {
	this(id);
	setRecordLength(length);
    }

   /**
     * Construct a tag header with a given tag ID.
     * @param id the tag ID
     * @exception IllegalArgumentException if <code>id</code> is not
     * in the range 0 to 1023.
     */
    public SWFTagHeader(int id) {
	setID(id);
    }

    /** Construct a tag header. */
    public SWFTagHeader() {}

    /** Get the tag ID */
    public int getID() {
	return id.intValue();
    }

    /** Get the tag ID as an <code>Integer</code> object. */
    public Integer getIDAsInteger() {
	return id;
    }

    /** Get the length of the following record in bytes. */
    public long getRecordLength() {
	return length;
    }
    
    /**
     * Set the tag ID
     * @param id the new ID
     * @exception IllegalArgumentException if <code>id</code> is not
     * in the range 0 to 1023.
     */
    public void setID(int id) {
	if (id < 0 || id >= 1 << 10) throw new IllegalArgumentException
	    ("ID " + id + " is outside the allowed range of 0..1023");
	this.id = new Integer(id);
    }

    /**
     * Set the length of the following record in bytes.
     * @param length the new length
     * @exception IllegalArgumentException if <code>length</code> is
     * not in the range 0 to 4294967295 (2<super>32</super>-1)
     */
    public void setRecordLength(long length) {
	if (length < 0 || length >= 1L << 32) throw new IllegalArgumentException
	    ("length " + length + " is outside the allowed range of 0..2^32");
	this.length = length;
    }
    
    /**
     * Get the length of this tag header in bits.
     */
    public long length() {
	if (getRecordLength() < 63) return 16;
	else return 48;
    }

    /**
     * Write the SWF representation of this tag header to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	int tmp = getID() << 6;
	if (getRecordLength() < 63) {
	    tmp |= getRecordLength();
	    out.writeW16LSB(tmp);
	} else {
	    tmp |= 0x3f;
	    out.writeW16LSB(tmp);
	    out.writeW32LSB((int)getRecordLength());
	}
    }
}
