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
 * $Id: SWFTransparentLineStyle.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a transparent line style definition.
 *
 * <p>A transparent line style definition is 48 bits long and not
 * necessarily aligned to a byte boundary. A transparent line style
 * defintion has the following structure:</p>
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
 *   <td>32</td>
 *   <td>{@link SWFColorRGBA} structure
 *   defining the color.</td>
 * </tr>
 *</table>
 */
public class SWFTransparentLineStyle extends SWFLineStyle {
    /** The line color */
    private final SWFColorRGBA COLOR;

    /** 
     * Constructor. Don't call this constructur directly, use the
     * {@link SWFLineStyle#parse} factory method instead.
     */
    SWFTransparentLineStyle(BitInputStream input) throws IOException {
	super(input);
	COLOR = new SWFColorRGBA(input);
    }

    /** Get the line color. */
    public SWFColorRGBA getColor() { return COLOR; }

    /**
     * Flag, check if this line style includes transparency
     * @return <code>true</code>
     */
    public boolean getAlpha() { return true; }

    /**
     * Write the SWF representation of this object to
     * <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	super.write(out);
	COLOR.write(out);
    }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     * @return 48
     */
    public long length() { return 48; }
 
}
