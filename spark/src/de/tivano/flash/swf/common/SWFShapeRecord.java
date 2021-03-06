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
 * $Id: SWFShapeRecord.java,v 1.6 2002/05/22 17:11:17 richard Exp $
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
public abstract class SWFShapeRecord extends SWFDataTypeBase {
    /**
     * Create a new shape record. The type is determined from the
     * input.
     * @param input the input stream to read from
     * @param fillBits the number of index bits in a fill style state
     * change record
     * @param lineBits the number of index bits in a line style state
     * change record
     * @param useRGBA Flag, inidcates whether fill- and linestyle
     * definitions use RGB or RGBA values.
     * @return a new shape record matching the data on <code>input</code>.
     */
    public static SWFShapeRecord parse(BitInputStream input,
				       int fillBits, int lineBits,
				       boolean useRGBA)
	          throws IOException {
	if (input.readBit()) {
	    // Edge record. Now test if curve or straight line
	    if (input.readBit()) return new SWFStraightEdge(input);
	    else return new SWFCurvedEdge(input);
	} else {
	    return new SWFStateChange(input, fillBits, lineBits, useRGBA);
	}
    }
}
