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
 * $Id: SWFShapeRecord.java,v 1.1 2001/05/14 14:17:50 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

import java.util.Arrays;

/**
 * This class represents a SWF shape record structure.
 * <p>A shape record structure is either an edge record, a state
 * change record or the end-of-shape marker. It has the
 * following structure:
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>edgeFlag</td>
 *   <td>1</td>
 *   <td>1 for edge records, 0 for state changes</td>
 * </tr>
 * <tr>
 *   <td>data</td>
 *   <td>&gt;= 5</td>
 *   <td>the shape data, determined by the actual shape record type</td>
 * </tr>
 * </table>
 * The different types of shape records are modelled by subclasses of
 * this class. This class can act as a factory for the different shape
 * record types.
 * @author Richard Kunze
 */
public class SWFShapeRecord {
    
    protected static final int TYPE_MOVE_TO      = 1;
    protected static final int TYPE_FILL_STYLE_0 = 2;
    protected static final int TYPE_FILL_STYLE_1 = 4;
    protected static final int TYPE_LINE_STYLE   = 8;
    protected static final int TYPE_DEFINE_STYLE = 16;
	
    /**
     * Create a new shape record. The type is determined from the
     * input.
     * @param input the input stream to read from
     * @param fillBits the number of index bits in a fill style state
     * change record
     * @param lineBits the number of index bits in a line style state
     * change record
     * @return a new shape record matching the data on <code>input</code>.
     */
    public static SWFShapeRecord parse(BitInputStream input,
				       int fillBits, int lineBits)
	          throws IOException {
	if (input.readBit()) {
	    // Edge record. Now test if curve or straight line
	    if (input.readBit()) return new SWFStraightEdge(input);
	    else return new SWFCurvedEdge(input);
	} else {
	    // None-Edge record. Determine what type it is.
	    int type = (int)input.readUBits(5);
	    switch (type) {
	    case 0:
		return new SWFEndOfShape();
	    case TYPE_MOVE_TO:
		return new SWFMoveTo(input);
	    case TYPE_FILL_STYLE_0:
	    case TYPE_FILL_STYLE_1:
	    case TYPE_LINE_STYLE:
	    case TYPE_DEFINE_STYLE:
	    default:
		// A record that makes more than one state change at
		// the same time.
		throw new SWFFormatException(
		   "FIXME: State change records of type " + type +
		   "not yet implemented.");
	    }
	}
    }
}
