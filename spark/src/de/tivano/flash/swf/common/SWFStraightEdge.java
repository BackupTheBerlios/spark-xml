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
 * $Id: SWFStraightEdge.java,v 1.1 2001/05/14 14:17:50 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a straight edge in a shape.
 * <p>In the SWF file, only the difference to the previous end point
 * is stored. A straight edge record has the following structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>NBits</td>
 *   <td>4</td>
 *   <td>The number of bits following values. Note: the actual number
 *   of bits is <em>NBits</em> + 2</td>
 * </tr>
 * <tr>
 *   <td>GeneralLineFlag</td>
 *   <td>1</td>
 *   <td>Tells if this is a general line (1) or a vertical or
 *   horizontal one (0).</td>
 * </tr>
 * <tr>
 *   <td>data</td>
 *   <td><em>NBits</em> + 3 or 2 * (<em>NBits</em> + 2)</td>
 *   <td>The actual line data. See below.</td>
 * </tr>
 * </table>
 * Depending on the value of the <em>GeneralLineFlag</em>, the
 * <em>data</em> portion is different. A general line has the
 * following structure:
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>DeltaX</td>
 *   <td>NBits + 2</td>
 *   <td>The X delta from the previous end point.</td>
 * </tr>
 * <tr>
 *   <td>DeltaY</td>
 *   <td>NBits + 2</td>
 *   <td>The Y delta from the previous end point.</td>
 * </tr>
 * </table>
 * <p>A vertical or horizontal line has the following structure</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>VerticalFlag</td>
 *   <td>1</td>
 *   <td>1 for vertical lines, 0 for horizontal ones.</td>
 * </tr>
 * <tr>
 *   <td>Delta</td>
 *   <td><em>NBits</em> + 2</td>
 *   <td>The X or Y (depending on the flag) delta</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFStraightEdge extends SWFShapeRecord {

    private final int DELTA_X;
    private final int DELTA_Y;

    /**
     * Construct a <code>SWFStraightEdge</code> shape record from a bit
     * input stream. 
     * @exception SWFFormatException if the complete record could
     * not be read from the stream.
     * @param input the bit stream to read from
     */
    public SWFStraightEdge(BitInputStream input) throws IOException {
	try {
	    int nbits = (int)input.readUBits(4) + 2;
	    if (input.readBit()) {
		// General line
		DELTA_X = (int)input.readSBits(nbits);
		DELTA_Y = (int)input.readSBits(nbits);
	    } else {
		// Horizontal or vertical line
		if (input.readBit()) {
		    DELTA_X = 0;
		    DELTA_Y = (int)input.readSBits(nbits);
		} else {
		    DELTA_X = (int)input.readSBits(nbits);
		    DELTA_Y = 0;
		}
	    }
	} catch (EOFException e) {
	    throw new SWFFormatException(
		  "Could not read a complete straight edge record.");
	}
    }
    
    /** Get the X value of the control point */
    public int getX() { return DELTA_X; }

    /** Get the Y value of the control point */
    public int getY() { return DELTA_Y; }
}