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
 * $Id: SWFFillStyle.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a fill style definition.
 *
 * <p>A fill style definition has varying length (at lest 32 bits) and
 * is not necessarily aligned to a byte boundary. It has the following
 * structure:</p>
 *
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>type</td>
 *   <td>8</td>
 *   <td>Fill style type. Possible values are
 *   <ul>
 *   <li>0x00: Solid fill
 *   <li>0x10: Linear gradient fill
 *   <li>0x12: Radial gradient fill
 *   <li>0x40: Tiled bitmap fill
 *   <li>0x41: Clipped bitmap fill
 *   </ul>
 *   </td>
 * </tr>
 * <tr>
 *   <td>styleDef</td>
 *   <td>varying</td>
 *   <td>Fill style definition. Depends on the fill style type.</td>
 * </tr>
 *</table>
 * <p>This class acts as a factory for the different subclasses
 * implementing the individual fill styles.</p>
 */
public abstract class SWFFillStyle extends SWFDataTypeBase {

    /** Fill style type constant */
    public static final int TYPE_SOLID_FILL = 0x00;

    /** Fill style type constant */
    public static final int TYPE_LINEAR_GRADIENT_FILL = 0x10;

    /** Fill style type constant */
    public static final int TYPE_RADIAL_GRADIENT_FILL = 0x12;

    /** Fill style type constant */
    public static final int TYPE_TILED_BITMAP_FILL = 0x40;

    /** Fill style type constant */
    public static final int TYPE_CLIPPED_BITMAP_FILL = 0x41;

    /** The fill style type */
    private int type;

    /** Flag, determines if colors are read/written as RGB or RGBA */
    private boolean useRGBA;

    /**
     * Create a new fill style record. The type is determined from the
     * input. 
     * @param input the input stream to read from
     * @param useRGBA flag, indicates whether color values are RGB or
     * RGBA.
     * @exception SWFFormatException if no fill style could be read.
     */
    public static SWFFillStyle parse(BitInputStream input, boolean useRGBA)
	   throws IOException {
	try {
	    int type = input.readUByte();
	    switch (type) {
	    case TYPE_SOLID_FILL:
		return new SWFFillStyleSolid(input, useRGBA);
	    case TYPE_LINEAR_GRADIENT_FILL:
	    case TYPE_RADIAL_GRADIENT_FILL:
		return new SWFFillStyleGradient(input, useRGBA, type);
	    case TYPE_TILED_BITMAP_FILL:
	    case TYPE_CLIPPED_BITMAP_FILL:
		return new SWFFillStyleBitmap(input, type);
	    default:
		throw new SWFFormatException("Unknown fill style type: " + type);
	    }
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a fill style structure");
	}
    }

    /**
     * Get the RGBA flag. If <code>true</code>, color values in the
     * SWF input or output stream are expected to include an alpha
     * value.
     */
    public boolean hasRGBAColors() { return useRGBA; }

    /**
     * Set the RGBA flag. If <code>true</code>, color values in the
     * SWF input or output stream are expected to include an alpha
     * value.
     * @param the new value.
     */
    protected void setHasRGBAColors(boolean value) { useRGBA = value; }

    /** Get the fill style type */
    public int getType() { return type; }

    /** Set the fill style type.
     * @param value the fill style type. Must be one ofthe fill style
     * constants defined in this class.
     */
    protected void setType(int value) { type = value; }

    /**
     * Write the SWF representation of this object to
     * <code>out</code>. The implementation of this method only writes
     * the fill style type. Derived classes <em>must</em> overide
     * this method and <em>should</em> call this method first in their
     * own implementation.
     */
    public void write(BitOutputStream out) throws IOException {
	out.writeBits(getType(), 8);
    }
}
