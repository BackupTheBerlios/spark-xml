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
 * $Id: SWFTextRecord.java,v 1.1 2001/06/01 08:40:07 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

import java.util.Arrays;

/**
 * This class represents a SWF text record structure.
 * <p>A shape record structure is either s string of characters with
 * advance values or a text state change record. It has the
 * following structure:
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>typeFlag</td>
 *   <td>1</td>
 *   <td>0 for character strings, 1 for state changes</td>
 * </tr>
 * <tr>
 *   <td>data</td>
 *   <td>&gt;= 5</td>
 *   <td>the actual data, determined by the record type</td>
 * </tr>
 * </table>
 * The different types of text records are modelled by subclasses of
 * this class. This class can act as a factory for the different text
 * record types.
 * @author Richard Kunze
 */
public abstract class SWFTextRecord extends SWFDataTypeBase {
    /**
     * Create a new text record. The type is determined from the
     * input.
     * @param input the input stream to read from
     * @param glyphBits the number of index bits for glyph lookup
     * in a text chars record
     * @param advanceBits the number of bits for the X advance values
     * in a text chars record
     * @return a new text record matching the data on <code>input</code>.
     */
    public static SWFTextRecord parse(BitInputStream input,
				      int glyphBits, int advanceBits)
	          throws IOException {
	if (input.readBit()) {
	    return new SWFTextChars(input, glyphBits, advanceBits);
	} else {
	    return new SWFTextChange(input);
	}
    }
}