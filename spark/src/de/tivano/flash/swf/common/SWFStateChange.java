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
 * $Id: SWFStateChange.java,v 1.1 2001/05/16 16:54:42 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a "state change" shape record.
 * A state change record can combine a number of operations:
 * <ul>
 * <li> Change the current position without drawing.
 * <li> Change one of the currently active fill styles. There are two fill
 * style slots per shape.
 * <li> Change the currently active line style.
 * <li> Define a new set of fill and line styles.
 * </ul>
 * <p>A state change record has the following structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>newStyles</td>
 *   <td>1</td>
 *   <td>If set, a fill and line style definition is present</td>
 * </tr>
 * <tr>
 *   <td>changeLineStyle</td>
 *   <td>1</td>
 *   <td>If set, the current line style ist changed</td>
 * </tr>
 * <tr>
 *   <td>changeFillStyle1</td>
 *   <td>1</td>
 *   <td>If set, the fill style for slot 1 is changed</td>
 * </tr>
 * <tr>
 *   <td>changeFillStyle0</td>
 *   <td>1</td>
 *   <td>If set, the fill style for slot 0 is changed</td>
 * </tr>
 * <tr>
 *   <td>moveTo</td>
 *   <td>1</td>
 *   <td>If set, the current position is changed</td>
 * </tr>
 * <tr>
 *   <td>data</td>
 *   <td>varying</td>
 *   <td>The actual data. See below</td>
 * </tr>
 * </table>
 * <p>The actual data consists of the following structures (in that
 * order):
 * <ol>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>moveTo</td>
 *   <td>varying</td>
 *   <td>"Move to" operation (see {@link SWFMoveTo})</td>
 * </tr>
 * <tr>
 *   <td>fillStyle0</td>
 *   <td><em>fillBits</em></td>
 *   <td>Fill style for slot 0 as unsigned int. <em>fillBits</em> is
 *   passed in to the constructor.</td>
 * </tr>
 * <tr>
 *   <td>fillStyle1</td>
 *   <td><em>fillBits</em></td>
 *   <td>Fill style for slot 1 as unsigned int. <em>fillBits</em> is
 *   passed in to the constructor.</td>
 * </tr>
 * <tr>
 *   <td>lineStyle</td>
 *   <td><em>lineBits</em></td>
 *   <td>Line style as unsigned int. <em>lineBits</em> is
 *   passed in to the constructor.</td>
 * </tr>
 * <tr>
 *   <td>newStyles</td>
 *   <td>varying</td>
 *   <td>Definition of new fill and line styles. See
 *   {@link SWFDefineStyle}</td> 
 * </tr>
 * </table>
 * <p>Each of these structures is only present if the corresponding
 * flag is set.</p>
 * <p>A state change record with none of the above structures set
 * means "end of shape".
 * @author Richard Kunze
 */
public class SWFStateChange extends SWFShapeRecord {
    
    protected static final int TYPE_MOVE_TO      = 1;
    protected static final int TYPE_FILL_STYLE_0 = 2;
    protected static final int TYPE_FILL_STYLE_1 = 4;
    protected static final int TYPE_LINE_STYLE   = 8;
    protected static final int TYPE_DEFINE_STYLE = 16;

    private int type;
    private SWFMoveTo moveTo = null;
    private int fillStyle0 = 0;
    private int fillStyle1 = 0;
    private int lineStyle  = 0;
    // FIXME: Implement the rest of the state changes.

    /**
     * Construct a <code>SWFMoveTo</code> shape record from a bit input stream.
     * @exception SWFFormatException if the complete record could
     * not be read from the stream.
     * @param input the bit stream to read from
     * @param fillBits the number of bits used as index in a fill
     * style change
     * @param lineBits the number of bits used for the index in a line
     * style change
     */
    public SWFStateChange(BitInputStream input, int fillBits, int lineBits)
           throws IOException {
	try {
	    type = (int)input.readUBits(5);
	    if ((type & TYPE_MOVE_TO) != 0) {
		moveTo =  new SWFMoveTo(input);
	    }
	    if ((type & TYPE_FILL_STYLE_0) != 0) {
		fillStyle0 = (int)input.readUBits(fillBits);
	    }
	    if ((type & TYPE_FILL_STYLE_1) != 0) {
		fillStyle1 = (int)input.readUBits(fillBits);
	    }
	    if ((type & TYPE_LINE_STYLE) != 0) {
		lineStyle = (int)input.readUBits(lineBits);
	    }
	    if ((type & TYPE_DEFINE_STYLE) != 0) {
		throw new SWFFormatException(
		   "FIXME: Define new fill/line styles not yet implemented");
	    }
	} catch (EOFException e) {
	    throw new SWFFormatException(
		  "Could not read a complete state change record.");
	}
    }

    /** Check if this state change is an "end of shape" record */
    public boolean isEndOfShape() { return type == 0; }

    /** Check if this state change record has a "move to" operation */
    public boolean hasMoveTo() { return (type & TYPE_MOVE_TO) != 0; }

    /** Check if this state change record changes the fill style for
     * slot 0 */
    public boolean hasFillStyle0() { return (type & TYPE_FILL_STYLE_0) != 0; }

    /** Check if this state change record changes the fill style for
     * slot 1 */
    public boolean hasFillStyle1() { return (type & TYPE_FILL_STYLE_1) != 0; }

    /** Check if this state change record changes the line style */
    public boolean hasLineStyle() { return (type & TYPE_LINE_STYLE) != 0; }

    /** Get the "move to" operation */
    public SWFMoveTo getMoveTo() { return moveTo; }

    /** Get the fill style for slot 0 */
    public int getFillStyle0() { return fillStyle0; }

    /** Get the fill style for slot 1 */
    public int getFillStyle1() { return fillStyle1; }

    /** Get the line style */
    public int getLineStyle() { return lineStyle; }
}
