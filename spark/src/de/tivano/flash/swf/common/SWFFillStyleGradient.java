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
 * $Id: SWFFillStyleGradient.java,v 1.1 2002/01/25 13:50:09 kunze Exp $
 */

package de.tivano.flash.swf.common;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a fill style definition for a gradient fill. A
 * gradient fill style definition has varying length and is not
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
 *   <td>Fill style type. 0x10 for a linear or 0x12 for a radial gradient fill.
 *   </ul>
 *   </td>
 * </tr>
 * <tr>
 *   <td>matrix</td>
 *   <td>varying</td>
 *   <td>{@link SWFMatrix} structure defining the transformation
 *   matrix for this fill style.</td>
 * </tr>
 * <tr>
 *   <td>gradient</td>
 *   <td>varying</td>
 *   <td>{@link SWFGradient} structure defining the fill gradient.</td>
 * </tr>
 *</table>
 */
public class SWFFillStyleGradient extends SWFFillStyle {

    /** The fill gradient */
    private final SWFGradient GRADIENT;
    
    /** Get the fill gradient. */
    public SWFGradient getGradient() { return GRADIENT; }

    /** The transformation matrix */
    private final SWFMatrix MATRIX;
    
    /** Get the transformation matrix. */
    public SWFMatrix getMatrix() { return MATRIX; }

    /**
     * Construct a new solid fill style from <code>input</code>.
     * Do not use this constructor directly. Use the factory method
     * {@link SWFFillStyle#parse} instead to read fill styles from an
     * SWF stream. This constructor assumes that the fill style type has
     * already been read from <code>input</code>.
     * @param input the input stream to read from.
     * @param useRGBA flag, determines whether the fill color includes an
     * alpha value or not.
     * @param type the fill type. Must be either {@link
     * SWFFillStyle#TYPE_LINEAR_GRADIENT_FILL} or {@link
     * SWFFillStyle#TYPE_RADIAL_GRADIENT_FILL}.
     * @exception SWFFormatException if the fill style definition could not be
     * read
     * @exception IllegalArgumentException if <code>type</code> is not
     * a legal type value
     */
    SWFFillStyleGradient(BitInputStream input, boolean useRGBA, int type)
	throws IOException {
	setHasRGBAColors(useRGBA);
	switch (type) {
	case TYPE_LINEAR_GRADIENT_FILL:
	case TYPE_RADIAL_GRADIENT_FILL:
	    setType(type);
	    break;
	default:
	    throw new IllegalArgumentException("Illegal fill style type: " +
					       type);
	}
	MATRIX = new SWFMatrix(input);
	GRADIENT = new SWFGradient(input, useRGBA);
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
	MATRIX.write(out);
	GRADIENT.write(out);
    }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	return 8 + MATRIX.length() + GRADIENT.length();
    }

}
