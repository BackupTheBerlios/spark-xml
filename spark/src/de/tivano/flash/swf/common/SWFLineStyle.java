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
 * $Id: SWFLineStyle.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a line style definition.
 *
 * <p>A line style definition is either 40 or 48 bits long, depending
 * on whether the color includes transparency or not. It is not
 * necessarily aligned to a byte boundary. A line style defintion has
 * the following structure:</p>
 *
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>width</td>
 *   <td>16</td>
 *   <td>Width of the line (in TWIPS) as unsigned integer</td>
 * </tr>
 * <tr>
 *   <td>color</td>
 *   <td>24 or 32</td>
 *   <td>{@link SWFColorRGB} or {@link SWFColorRGBA} structure
 *   defining the color.</td>
 * </tr>
 *</table>
 * <p>This class acts as a factory for the different subclasses
 * implementing the individual line styles.</p>
 */
public abstract class SWFLineStyle extends SWFDataTypeBase {
    /** The line width */
    private final int WIDTH;

    /** 
     * Constructor. This implementation only reads the line width from
     * the input stream. It <em>must</em> be called by derived classes
     * in their corresponding constructor 
     */
    SWFLineStyle(BitInputStream input) throws IOException {
	WIDTH = input.readUW16LSB();
    }


    /**
     * Create a new line style record.
     * @param input the input stream to read from
     * @param useRGBA flag, indicates whether color values are RGB or
     * RGBA.
     * @exception SWFFormatException if no line style could be read.
     */
    public static SWFLineStyle parse(BitInputStream input, boolean useRGBA)
	   throws IOException {
	try {
	    if (useRGBA) return new SWFSolidLineStyle(input);
	    else return new SWFTransparentLineStyle(input);
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a line style structure");
	}
    }

    /** Get the line style width */
    public int getWidth() { return WIDTH; }

    /**
     * Get the line color. IF {@link #getAlpha} returns
     * <code>false</code>, the color must have an alpha value of 0xFF
     */
    public abstract SWFColorRGBA getColor();

    /** Flag, check if this line style includes transparency */
    public abstract boolean getAlpha();

    /**
     * Write the SWF representation of this object to
     * <code>out</code>.
     * This implementation only writes the line width. Derived classes
     * <em>must</em> override this method and <em>should</em> call it
     * first in their implementation.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	out.writeW16LSB(WIDTH);
    }
}
