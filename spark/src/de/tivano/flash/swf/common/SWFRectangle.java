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
 * $Id: SWFRectangle.java,v 1.3 2001/03/15 10:57:40 kunze Exp $
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
public class SWFRectangle {
    /** Minimum X coordinate */
    private final long X_MIN;

    /** Minimum Y coordinate */
    private final long Y_MIN;

    /** Maximum X coordinate */
    private final long X_MAX;

    /** Maximum Y coordinate */
    private final long Y_MAX;


    /**
     * Construct a <code>SWFRectangle</code> from a bit input stream.
     * @exception SWFFormatException if the complete rectangle could
     * not be read from the stream.
     * @param input the bit stream to read from
     */
    public SWFRectangle(BitInputStream input) throws IOException {
	try {
	    int fieldLen = (int)input.readUBits(5);
	    X_MIN = input.readSBits(fieldLen);
	    X_MAX = input.readSBits(fieldLen);
	    Y_MIN = input.readSBits(fieldLen);
	    Y_MAX = input.readSBits(fieldLen);
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a rectangle");
	}
    }

    /** Get the minimum X coordinate */
    public long getXMin() { return X_MIN; }

    /** Get the minimum Y coordinate */
    public long getYMin() { return Y_MIN; }

    /** Get the maximum X coordinate */
    public long getXMax() { return X_MAX; }

    /** Get the maximum Y coordinate */
    public long getYMax() { return Y_MAX; }
}
