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
 * $Id: SWFColorRGB.java,v 1.1 2001/05/30 16:23:16 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

import java.util.Arrays;

/**
 * This class represents a SWF RGB color structure.
 * <p>A RGB color structure is 24 bits long.
 * It has the following structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>red</td>
 *   <td>8</td>
 *   <td>Red component as unsigned int</td>
 * </tr>
 * <tr>
 *   <td>green</td>
 *   <td>8</td>
 *   <td>Green component as unsigned int</td>
 * </tr>
 * <tr>
 *   <td>blue</td>
 *   <td>8</td>
 *   <td>Blue component as unsigned int</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFColorRGB extends SWFDataTypeBase {
    /** The red value */
    private final int RED;

    /** The green value */
    private final int GREEN;

    /** The blue value */
    private final int BLUE;
    
    /**
     * Construct a <code>SWFColorRGB</code> from a bit input stream.
     * @exception SWFFormatException if the complete rectangle could
     * not be read from the stream.
     * @param input the bit stream to read from
     */
    public SWFColorRGB(BitInputStream input) throws IOException {
	try {
	    RED   = input.readUByte();
	    GREEN = input.readUByte();
	    BLUE  = input.readUByte();
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a color structure");
	}
    }

    /** Get the red value */
    public int getRed() { return RED; }

    /** Get the green value */
    public int getGreen() { return GREEN; }

    /** Get the blue value */
    public int getBlue() { return BLUE; }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() { return 24; }

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	out.write(getRed());
	out.write(getGreen());
	out.write(getBlue());
    }

    /**
     * Get a string representation of this color.
     * The individual components are converted to hexadecimal values
     * and concatenated in <em>red</em>, <em>green</em>, <em>blue</em>
     * order.
     */
    public String toHexString() {
	return toHex(getRed()) + toHex(getGreen()) + toHex(getBlue());
    }

    /**
     * Helper method to convert the color value to hex, padded on the
     * left with "0"
     */
    protected String toHex(int value) {
	String str = Integer.toHexString(value);
	return (str.length() > 1 ? str : "0" + str);
    }
}
