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
 * $Id: SWFTransparentGradientRecord.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents one entry in a gradient definition including transparency.
 *
 * It is 40 bits long and not necessarily aligned to a byte
 * boundary. A solid color gradient record has the following
 * structure:
 *
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>ratio</td>
 *   <td>8</td>
 *   <td>The ratio value as unsigned integer
 *   </ul>
 *   </td>
 * </tr>
 * <tr>
 *   <td>color</td>
 *   <td>32</td>
 *   <td>{@link SWFColorRGBA} structure defining the gradient color.</td>
 * </tr>
 *</table>
 */
public class SWFTransparentGradientRecord extends SWFGradientRecord {

    /** The color. */
    private final SWFColorRGBA COLOR;
    
    /** Get the color. */
    public SWFColorRGBA getColor() { return COLOR; }

    /**
     * Check if this gradient records include alpha values.
     * @returns <code>true</code> */
    public boolean hasAlpha() { return true; }

    /**
     * Construct a new gradient record from <code>input</code>.
     * @param input the input stream to read from.
     */
    SWFTransparentGradientRecord(BitInputStream input)
	throws IOException {
	super(input);
	COLOR = new SWFColorRGBA(input);
    }
    
    /**
     * Construct a new gradient record from a ratio value and a RGBA color.
     * @param ratio the ratio value
     * @param color the color value.
     */
    SWFTransparentGradientRecord(int ratio, SWFColorRGBA color) {
	super(ratio);
	COLOR = color;
    }
    
    /**
     * Construct a new gradient record from a ratio value and a RGB color.
     * The alpha value is set to 0xFF.
     * @param ratio the ratio value
     * @param color the color value.
     */
    SWFTransparentGradientRecord(int ratio, SWFColorRGB color) {
	this(ratio, new SWFColorRGBA(color, 0xff));
    }
    
    /**
     * Write the SWF representation of this object to
     * <code>out</code>.
     * @param out the ouput stream to write to
     */
    public void write(BitOutputStream out) throws IOException {
	super.write(out);
	COLOR.write(out);
    }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     * @return 40;
     */
    public long length() { return 40; }
}
