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
 * $Id: SWFColorRGBA.java,v 1.2 2001/07/02 08:07:22 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

import java.util.Arrays;

/**
 * This class represents a SWF RGBA color structure.
 * <p>A RGB color structure is 32 bits long.
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
 * <tr>
 *   <td>alpha</td>
 *   <td>8</td>
 *   <td>Alpha component as unsigned int</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFColorRGBA extends SWFColorRGB {
    /** The alpha value */
    private final int ALPHA;

    /**
     * Construct a <code>SWFColorRGBA</code> from a string representation.
     * Currently, the only representation recognized is the one
     * produced by {@link #toHexString}.
     * @param str the string representation.
     * @exception IllegalArgumentException if <code>str</code> is not
     * a valid string representation of a color.
     */
    public SWFColorRGBA(String str) {
	super(str);
	try {
	    if (str.length() < 8) ALPHA = 0xFF;
	    else ALPHA  = Integer.parseInt(str.substring(6,8), 16);
	    if (ALPHA < 0) {
		throw new IllegalArgumentException(
		       "Not a legal color representation: " + str);
	    }
	} catch (NumberFormatException e) {
	    throw new IllegalArgumentException(
		       "Not a legal color representation: " + str);
	}
    }
    
    /**
     * Construct a <code>SWFColorRGBA</code> from a {@link
     * SWFColorRGB} and an alpha value.
     * @param rgb the RGB values
     * @param alpha the alpha value. 
     * @exception IllegalArgumentException if <code>alpha</code> is not
     * in the range of 0..255
     */
    public SWFColorRGBA(SWFColorRGB rgb, int alpha) {
	super(rgb);
	if (alpha < 0 || alpha > 255) {
	    throw new IllegalArgumentException("Not a legal alpha value: "
					       + alpha);
	}
	ALPHA = alpha;
    }
    
    /**
     * Construct a <code>SWFColorRGBA</code> from a bit input stream.
     * @exception SWFFormatException if the complete rectangle could
     * not be read from the stream.
     * @param input the bit stream to read from
     */
    public SWFColorRGBA(BitInputStream input) throws IOException {
	super(input);
	try {
	    ALPHA  = input.readUByte();
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a color structure");
	}
    }

    /** Get the alpha value */
    public int getAlpha() { return ALPHA; }

    /** Test if the alpha value differs from 0xFF */
    public boolean hasAlpha() { return ALPHA != 0xFF; }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() { return 32; }

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	super.write(out);
	out.write(getAlpha());
    }
    
    /**
     * Get a string representation of this color.
     * The individual components are converted to hexadecimal values
     * and concatenated in <em>red</em>, <em>green</em>,
     * <em>blue</em>, <em>alpha</em> (if included) order.
     * @param withAlpha if <code>true</code>, include the alpha value.
     */
    public String toHexString(boolean withAlpha) {
	if (withAlpha) return toHexString() + toHex(getAlpha());
	else return toHexString();
    }
    
    /** @see Object.equals */
    public boolean equals(Object obj) {
	if (obj instanceof SWFColorRGBA) {
	    SWFColorRGBA other = (SWFColorRGBA)obj;
	    return super.equals(obj) && other.getAlpha() == getAlpha();
	} else return false;
    }

}
