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
 * $Id: SWFRectangle.java,v 1.6 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

import java.util.Arrays;

/**
 * This class represents a SWF rectangle structure.
 * <p>A rectangle structure is between 9 and 133 bits long and not
 * necessarily aligned to a byte boundary in the SWF data stream.
 * It has the following structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>Nbits</td>
 *   <td>5</td>
 *   <td>number of bits in each value field</td>
 * </tr>
 * <tr>
 *   <td>Xmin</td>
 *   <td><em>Nbits</em></td>
 *   <td>X minimum postion, signed</td>
 * </tr>
 * <tr>
 *   <td>Xmax</td>
 *   <td><em>Nbits</em></td>
 *   <td>X maximum postion, signed</td>
 * </tr>
 * <tr>
 *   <td>Ymin</td>
 *   <td><em>Nbits</em></td>
 *   <td>Y minimum postion, signed</td>
 * </tr>
 * <tr>
 *   <td>Ymax</td>
 *   <td><em>Nbits</em></td>
 *   <td>Y maximum postion, signed</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFRectangle extends SWFDataTypeBase {
    /** Minimum X coordinate */
    private final int X_MIN;

    /** Minimum Y coordinate */
    private final int Y_MIN;

    /** Maximum X coordinate */
    private final int X_MAX;

    /** Maximum Y coordinate */
    private final int Y_MAX;


    /**
     * Construct a <code>SWFRectangle</code> from a bit input stream.
     * @exception SWFFormatException if the complete rectangle could
     * not be read from the stream.
     * @param input the bit stream to read from
     */
    public SWFRectangle(BitInputStream input) throws IOException {
	try {
	    int fieldLen = (int)input.readUBits(5);
	    X_MIN = (int)input.readSBits(fieldLen);
	    X_MAX = (int)input.readSBits(fieldLen);
	    Y_MIN = (int)input.readSBits(fieldLen);
	    Y_MAX = (int)input.readSBits(fieldLen);
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a rectangle");
	}
    }

    /**
     * Construct a <code>SWFRectangle</code> from the given data.
     * @param xmin the minimum X coordinate
     * @param xmax the maximum X coordinate
     * @param ymin the minimum Y coordinate
     * @param ymax the maximum Y coordinate
     * @exception IllegalArgumentException if <code>xmin &gt;
     * xmax</code> or <code>ymin &gt; ymax</code>.
     * @exception IllegalArgumentException if any of the values does
     * not fit into a 31 (sic!) bit field.
     */
    public SWFRectangle(int xmin, int xmax, int ymin, int ymax) {
	if (xmin > xmax) {
	    throw new IllegalArgumentException("xmin > xmax");
	}
	if (ymin > ymax) {
	    throw new IllegalArgumentException("ymin > ymax");
	}
	if (minBitsS(xmin) > 31 || minBitsS(xmax) > 31 ||
	    minBitsS(ymin) > 31 || minBitsS(ymax) > 31) {
	    throw new IllegalArgumentException(
			   "Value(s) too big for a 31 bit field");
	}
	X_MIN = xmin;
	X_MAX = xmax;
	Y_MIN = ymin;
	Y_MAX = ymax;
    }

    /** Get the minimum X coordinate */
    public int getXMin() { return X_MIN; }

    /** Get the minimum Y coordinate */
    public int getYMin() { return Y_MIN; }

    /** Get the maximum X coordinate */
    public int getXMax() { return X_MAX; }

    /** Get the maximum Y coordinate */
    public int getYMax() { return Y_MAX; }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	return 5 + 4*getEntryLength();
    }

    /**
     * Get the number of bits needed to represent the individual data
     * entries in this object.
     */
    private int getEntryLength() {
	return Math.max(Math.max(minBitsS(Y_MIN), minBitsS(Y_MAX)),
			Math.max(minBitsS(X_MIN), minBitsS(X_MAX)));
    }

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	int entryLength = getEntryLength();
	out.writeBits(entryLength, 5);
	out.writeBits(X_MIN, entryLength);
	out.writeBits(X_MAX, entryLength);
	out.writeBits(Y_MIN, entryLength);
	out.writeBits(Y_MAX, entryLength);
    }
}
