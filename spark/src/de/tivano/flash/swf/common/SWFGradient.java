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
 * $Id: SWFGradient.java,v 1.1 2002/01/25 13:50:09 kunze Exp $
 */

package de.tivano.flash.swf.common;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a gradient definition.
 *
 * It is of varying length and not necessarily aligned to a byte
 * boundary. A gradient has the follwoing structure:
 *
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>count</td>
 *   <td>8</td>
 *   <td>The number of gradient records
 *   </ul>
 *   </td>
 * </tr>
 * <tr>
 *   <td>gradientRecords</td>
 *   <td><em>count</em> * size of a gradient record</td>
 *   <td>Array of {@link SWFGradientRecord} structures.</td>
 * </tr>
 *</table>
 */
public class SWFGradient extends SWFDataTypeBase {

    /** The gradients */
    private final SWFGradientRecord[] GRADIENT_RECORDS;
    
    /** Get the number of gradient records that make up this gradient definition. */
    public int getRecordCount() { return GRADIENT_RECORDS.length; }

    /** Tell whether this gradient definition includes transparency or not. */
    public boolean hasAlpha() {
	return GRADIENT_RECORDS[0].hasAlpha();
    }

    /**
     * Get the gradient record number <code>idx</code>.
     * @param idx the index.
     * @return the gradient record
     * @exception IndexOutOfBoundException if <code>idx</code> is
     * outside the range of <code>0..{@link #getRecordCount}-1</code>
     */
    public SWFGradientRecord getRecord(int idx) { return GRADIENT_RECORDS[idx]; }

    /**
     * Construct a new gradient record from <code>input</code>.
     * @param input the input stream to read from.
     * @param useRGBA flag, determines whether the gradient records
     * use RGBA colors or not.
     * @exception SWFFormatException if the gradient structure could not be read.
     */
    public SWFGradient(BitInputStream input, boolean useRGBA)
	throws IOException {
	try {
	    int count = input.readUByte();
	    if (count < 1) {
		throw new SWFFormatException(
		  "Found a gradient record count of " + count +
		  ". Expected at least one gradient record.");
	    }
		
	    GRADIENT_RECORDS = new SWFGradientRecord[count];
	    for (int i=0; i<count; i++) {
		GRADIENT_RECORDS[i] = SWFGradientRecord.parse(input, useRGBA);
	    }
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a gradient structure");
	}
    }
    
    /**
     * Construct a new gradient record from an array of solid gradient records.
     * @param records the gradient records
     * @exception IllegalArgumentException if <code>record</code> has
     * length 0.
     * @exception NullPointerException if <code>record</code> is
     * <code>null</code> or if there is a <code>null</code> entry in
     * <code>records</code>.
     */
    public SWFGradient(SWFSolidGradientRecord[] records) {
	if (records.length == 0) {
	    throw new IllegalArgumentException(
			   "Expected at least one gradient record");
	}
	GRADIENT_RECORDS = new SWFGradientRecord[records.length];
	for (int i=0; i<records.length; i++) {
	    if (records[i] == null) {
		throw new NullPointerException(
			   "records[" + i +"] may not be null");
	    }
	    GRADIENT_RECORDS[i] = records[i];
	}
    }
    
    /**
     * Construct a new gradient record from an array of transparent gradient records.
     * @param records the gradient records
     * @exception IllegalArgumentException if <code>record</code> has
     * length 0.
     * @exception NullPointerException if <code>record</code> is
     * <code>null</code> or if there is a <code>null</code> entry in
     * <code>records</code>.
     */
    public SWFGradient(SWFTransparentGradientRecord[] records) {
	if (records.length == 0) {
	    throw new IllegalArgumentException(
			   "Expected at least one gradient record");
	}
	GRADIENT_RECORDS = new SWFGradientRecord[records.length];
	for (int i=0; i<records.length; i++) {
	    if (records[i] == null) {
		throw new NullPointerException(
			   "records[" + i +"] may not be null");
	    }
	    GRADIENT_RECORDS[i] = records[i];
	}
    }
    
    /**
     * Write the SWF representation of this object to
     * <code>out</code>.
     */
    public void write(BitOutputStream out) throws IOException {
	out.writeBits(GRADIENT_RECORDS.length, 8);
	for (int i=0; i<GRADIENT_RECORDS.length; i++) {
	    GRADIENT_RECORDS[i].write(out);
	}
    }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	long length = 8;
	// This works because all the gradient records have the same length
	if (GRADIENT_RECORDS.length > 0)
	    length += GRADIENT_RECORDS.length * GRADIENT_RECORDS[0].length();
	return length;
    }

}
