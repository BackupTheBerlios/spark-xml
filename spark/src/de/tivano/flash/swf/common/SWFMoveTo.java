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
 * $Id: SWFMoveTo.java,v 1.4 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a "move to" operation in a shape.
 * <p>"Move to" means that the current point position is changed
 * without drawing a line or curve to the previous point. A move to
 * operation has the following structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>NBits</td>
 *   <td>5</td>
 *   <td>The number of bits for the x and y values</td>
 * </tr>
 * <tr>
 *   <td>X</td>
 *   <td><em>NBits</em></td>
 *   <td>X value relative to the shape origin as a signed integer</td>
 * </tr>
 * <tr>
 *   <td>Y</td>
 *   <td><em>NBits</em></td>
 *   <td>Y value relative to the shape origin as a signed integer</td>
 * </tr>
 * </table>
 * <p>The "move to" structure is part of a {@link SWFStateChange}
 * shape record.</p>
 * @author Richard Kunze
 */
public class SWFMoveTo extends SWFDataTypeBase {

    private final int X;
    private final int Y;

    /**
     * Construct a <code>SWFMoveTo</code> shape record from a bit input stream.
     * @exception SWFFormatException if the complete record could
     * not be read from the stream.
     * @param input the bit stream to read from
     */
    public SWFMoveTo(BitInputStream input) throws IOException {
	try {
	    int nbits = (int)input.readUBits(5);
	    X = (int)input.readSBits(nbits);
	    Y = (int)input.readSBits(nbits);
	} catch (EOFException e) {
	    throw new SWFFormatException(
		  "Could not read a complete 'move to' state change record.");
	}
    }

    /** Get the X value */
    public int getX() { return X; }

    /** Get the Y value */
    public int getY() { return Y; }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() { return 5 + 2*getEntryLength(); }
    
     /**
     * Get the number of bits needed to represent the individual data
     * entries in this object.
     */
    private int getEntryLength() {
	return Math.max(minBitsS(getX()), minBitsS(getY()));
    }

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	int entryLength = getEntryLength();
	out.writeBits(entryLength, 5);
	out.writeBits(getX(), entryLength);
	out.writeBits(getY(), entryLength);
    }
}
