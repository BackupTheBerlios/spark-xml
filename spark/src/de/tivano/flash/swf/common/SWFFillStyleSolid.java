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
 * $Id: SWFFillStyleSolid.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a fill style definition for a solid fill. A
 * solid fill style definition is either 32 or 40 bits long and not
 * necessarily aligned to a byte boundary. It has the following structure:
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>type</td>
 *   <td>8</td>
 *   <td>Fill style type. Always 0x00 for solid fill.
 *   </ul>
 *   </td>
 * </tr>
 * <tr>
 *   <td>color</td>
 *   <td>24 or 32</td>
 *   <td>{@link SWFColorRGB} or {@link SWFColorRGBA} structure definig
 *   the fill color.</td>
 * </tr>
 *</table>
 */
public class SWFFillStyleSolid extends SWFFillStyle {

    /** The fill color */
    private SWFColorRGBA color;
    
    /** Get the fill color. */
    public SWFColorRGBA getColor() { return color; }

    /**
     * Construct a new solid fill style from <code>input</code>.
     * Do not use this constructor directly. Use the factory method
     * {@link SWFFillStyle#parse} instead to read fill styles from an
     * SWF stream. This constructor assumes that the fill style type has
     * already been read from <code>input</code>.
     * @param input the input stream to read from.
     * @param useRGBA flag, determines whether the fill color includes an
     * alpha value or not.
     */
    SWFFillStyleSolid(BitInputStream input, boolean useRGBA)
	throws IOException {
	setHasRGBAColors(useRGBA);
	setType(TYPE_SOLID_FILL);
	if (useRGBA) color = new SWFColorRGBA(input);
	else color = new SWFColorRGBA(input, 0xFF);
	
    }
    
    /**
     * Construct a new solid fill style from a RGBA color.
     * @param color the color value.
     * @param useRGBA if <code>false</code>, ignore the alpha value of
     * <code>color</code>.
     */
    SWFFillStyleSolid(SWFColorRGBA color, boolean useRGBA) {
	setHasRGBAColors(useRGBA);
	setType(TYPE_SOLID_FILL);
	this.color = color;
    }
    
    /**
     * Construct a new solid fill style from a RGB color. The alpha
     * value is set to 0xFF (i.e. solid)
     * @param color the color value.
     * @param useRGBA if <code>true</code>, include the alpha value in
     * the SWF structure on writing.
     */
    SWFFillStyleSolid(SWFColorRGB color, boolean useRGBA) {
	this (new SWFColorRGBA(color, 0xFF), useRGBA);
    }
    
    /**
     * Write the SWF representation of this object to
     * <code>out</code>. The implementation of this method only writes
     * the fill style type. Derived classes <em>must</em> overide
     * this method and <em>should</em> call this method first in their
     * own implementation.
     */
    public void write(BitOutputStream out) throws IOException {
	super.write(out);
	color.write(out, hasRGBAColors());
    }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	return (hasRGBAColors()?40:32);
    }

}
