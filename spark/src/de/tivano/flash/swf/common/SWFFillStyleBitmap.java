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
 * $Id: SWFFillStyleBitmap.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a fill style definition for a bitmap fill. A
 * bitmap fill style definition has varying length and is not
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
 *   <td>Fill style type. 0x40 for a tiled or 0x41 for a clipped bitmap fill.
 *   </ul>
 *   </td>
 * </tr>
 * <tr>
 *   <td>bitmapID</td>
 *   <td>16</td>
 *   <td>ID of the bitmap character to use</td>
 * </tr>
 * <tr>
 *   <td>matrix</td>
 *   <td>varying</td>
 *   <td>{@link SWFMatrix} structure defining the transformation
 *   matrix for this fill style.</td>
 * </tr>
 *</table>
 */
public class SWFFillStyleBitmap extends SWFFillStyle {

    /** The fill bitmap ID */
    private final int BITMAP_ID;
    
    /** Get the fill bitmap ID. */
    public int getBitmapID() { return BITMAP_ID; }

    /** The transformation matrix */
    private final SWFMatrix MATRIX;
    
    /** Get the transformation matrix. */
    public SWFMatrix getMatrix() { return MATRIX; }

    /**
     * Construct a new bitmap fill style from <code>input</code>.
     * Do not use this constructor directly. Use the factory method
     * {@link SWFFillStyle#parse} instead to read fill styles from an
     * SWF stream. This constructor assumes that the fill style type has
     * already been read from <code>input</code>.
     * @param input the input stream to read from.
     * @param type the fill type. Must be either {@link
     * SWFFillStyle#TYPE_TILED_BITMAP_FILL} or {@link
     * SWFFillStyle#TYPE_CLIPPED_BITMAP_FILL}.
     * @exception SWFFormatException if the fill style definition could not be
     * read
     * @exception IllegalArgumentException if <code>type</code> is not
     * a legal type value
     */
    SWFFillStyleBitmap(BitInputStream input, int type) throws IOException {
	switch (type) {
	case TYPE_TILED_BITMAP_FILL:
	case TYPE_CLIPPED_BITMAP_FILL:
	    setType(type);
	    break;
	default:
	    throw new IllegalArgumentException("Illegal fill style type: " +
					       type);
	}
	BITMAP_ID = input.readUW16LSB();
	MATRIX = new SWFMatrix(input);
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
	out.writeW16LSB(BITMAP_ID);
	MATRIX.write(out);
    }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	return 24 + MATRIX.length();
    }

}
