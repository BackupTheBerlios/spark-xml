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
 * $Id: SWFCurvedEdge.java,v 1.1 2001/05/14 14:17:49 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a curved edge in a shape.
 * <p>Curved edges are quadratic bezier spline segments. In the SWF
 * file, only the difference to the previous end point and control
 * point is stored. A curved edge record has the following
 * structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>NBits</td>
 *   <td>4</td>
 *   <td>The number of bits for following values. Note: the actual number
 *   of bits is <em>NBits</em> + 2</td>
 * </tr>
 * <tr>
 *   <td>ControlX</td>
 *   <td><em>NBits</em> + 2</td>
 *   <td>X value of the control point, relative to the previous end
 *   point</td>
 * </tr>
 * <tr>
 *   <td>ControlY</td>
 *   <td><em>NBits</em> + 2</td>
 *   <td>Y value of the control point, relative to the previous end
 *   point</td>
 * </tr>
 * <tr>
 *   <td>AnchorX</td>
 *   <td><em>NBits</em> + 2</td>
 *   <td>X value of the anchor point, relative to the previous end
 *   point</td>
 * </tr>
 * <tr>
 *   <td>AnchorY</td>
 *   <td><em>NBits</em> + 2</td>
 *   <td>Y value of the anchor point, relative to the previous end
 *   point.</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFCurvedEdge extends SWFShapeRecord {

    private final int CONTROL_X;
    private final int CONTROL_Y;
    private final int ANCHOR_X;
    private final int ANCHOR_Y;


    /**
     * Construct a <code>SWFCurvedEdge</code> shape record from a bit
     * input stream. 
     * @exception SWFFormatException if the complete record could
     * not be read from the stream.
     * @param input the bit stream to read from
     */
    public SWFCurvedEdge(BitInputStream input) throws IOException {
	try {
	    int nbits = (int)input.readUBits(4) + 2;
	    CONTROL_X = (int)input.readSBits(nbits);
	    CONTROL_Y = (int)input.readSBits(nbits);
	    ANCHOR_X  = (int)input.readSBits(nbits);
	    ANCHOR_Y  = (int)input.readSBits(nbits);
	} catch (EOFException e) {
	    throw new SWFFormatException(
		  "Could not read a complete curved edge record.");
	}
    }
    
    /** Get the X value of the control point */
    public int getControlX() { return CONTROL_X; }

    /** Get the Y value of the control point */
    public int getControlY() { return CONTROL_Y; }

    /** Get the X value of the anchor point */
    public int getAnchorX() { return ANCHOR_X; }

    /** Get the Y value of the anchor point */
    public int getAnchorY() { return ANCHOR_Y; }
}