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
 * $Id: SWFStateChange.java,v 1.4 2002/05/21 08:39:59 kunze Exp $
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
 * <p>A state change record where all of the flags are 0 means "end of
 * shape".</p>
 * <p>The actual data consists of the following structures (in that
 * order):</p>
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
 *   below for details</td> 
 * </tr>
 * </table>
 * <p>Each of these structures is only present if the corresponding
 * flag is set.</p>
 * 
 * <p>Finally, a new style definition has the following structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>fillCount</td>
 *   <td>8</td>
 *   <td>Number of entries in <em>fillStyles</em> as unsigned byte. The
 *   value 0xff is used as a flag.</td>
 * </tr>
 * <tr>
 *   <td>fillCountExt</td>
 *   <td>16</td>
 *   <td>Only present if <em>fillCount</em> is 0xff. Number of entries
 *   in <em>fillStyles</em>.
 * </tr>
 * <tr>
 *   <td>fillStyles</td>
 *   <td>varying</td>
 *   <td>Array of {@link SWFFillStyle} structures</td>
 * </tr>
 * <tr>
 *   <td>lineCount</td>
 *   <td>8</td>
 *   <td>Number of entries in <em>lineStyles</em> as unsigned byte. The
 *   value 0xff is used as a flag.</td>
 * </tr>
 * <tr>
 *   <td>lineCountExt</td>
 *   <td>16</td>
 *   <td>Only present if <em>lineCount</em> is 0xff. Number of entries
 *   in <em>lineStyles</em>.
 * </tr>
 * <tr>
 *   <td>lineStyles</td>
 *   <td>varying</td>
 *   <td>Array of {@link SWFLineStyle} structures</td>
 * </tr>
 * <tr>
 *   <td>numFillBits</td>
 *   <td>4</td>
 *   <td>Number of index bits to use for fill style changes</td>
 * </tr>
 * <tr>
 *   <td>numLineBits</td>
 *   <td>4</td>
 *   <td>Number of index bits to use for line style changes</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFStateChange extends SWFShapeRecord {
    
    protected static final int TYPE_MOVE_TO      = 1;
    protected static final int TYPE_FILL_STYLE_0 = 2;
    protected static final int TYPE_FILL_STYLE_1 = 4;
    protected static final int TYPE_LINE_STYLE   = 8;
    protected static final int TYPE_DEFINE_STYLE = 16;

    private static final SWFFillStyle[] EMPTY_FILL_STYLES
	= new SWFFillStyle[0];
    private static final SWFLineStyle[] EMPTY_LINE_STYLES
	= new SWFLineStyle[0];

    private int type;
    private int fillBits = 0;
    private int lineBits = 0;
    private int newStyleFillBits = 0;
    private int newStyleLineBits = 0;
    private SWFMoveTo moveTo = null;
    private int fillStyle0 = 0;
    private int fillStyle1 = 0;
    private int lineStyle  = 0;    
    private SWFFillStyle[] fillStyles = EMPTY_FILL_STYLES;
    private SWFLineStyle[] lineStyles = EMPTY_LINE_STYLES;

    /**
     * Construct a <code>SWFStateChange</code> shape record from a bit
     * input stream.
     *
     * <p>Note that the <code>fillBits</code> and
     * <code>lineBits</code> parameters are <em>not</em> the values
     * returned by {@link #getFillBits} and {@link #getLineBits}. The
     * constructor parameters are used to parse <em>this</em>
     * instance, while the values returned by the getter methods are
     * either 0 (if this instance does not include a new style
     * definition) or denote the values used for parsing state change
     * records that occur <em>later</em> in the file.</p>
     * @exception SWFFormatException if the complete record could
     * not be read from the stream.
     * @param input the bit stream to read from
     * @param fillBits the number of bits used as index in a fill
     * style change.
     * @param lineBits the number of bits used for the index in a line
     * style change.
     * @param useRGBA Flag, inidcates whether fill- and linestyle
     * definitions use RGB or RGBA values.
     */
    public SWFStateChange(BitInputStream input, int fillBits, int lineBits,
			  boolean useRGBA)
           throws IOException {
	try {
            this.fillBits = fillBits;
            this.lineBits = lineBits;
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
		int count = input.readUByte();
		if (count == 0xFF) count = input.readUW16LSB();
		fillStyles = new SWFFillStyle[count];
		for (int i=0; i<count; i++) {
		    fillStyles[i] = SWFFillStyle.parse(input, useRGBA);
		}
		count = input.readUByte();
		if (count == 0xFF) count = input.readUW16LSB();
		lineStyles = new SWFLineStyle[count];
		for (int i=0; i<count; i++) {
		    lineStyles[i] = SWFLineStyle.parse(input, useRGBA);
		}
		newStyleFillBits = (int)input.readUBits(4);
		newStyleLineBits = (int)input.readUBits(4);
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

    /** Check if this state change record defines new fill and line styles */
    public boolean hasNewStyles() { return (type & TYPE_DEFINE_STYLE) != 0; }

    /** Get the "move to" operation */
    public SWFMoveTo getMoveTo() { return moveTo; }

    /** Get the fill style for slot 0 */
    public int getFillStyle0() { return fillStyle0; }

    /** Get the fill style for slot 1 */
    public int getFillStyle1() { return fillStyle1; }

    /** Get the line style */
    public int getLineStyle() { return lineStyle; }

    /** Get the number of new fill styles */
    public int getFillStyleCount() { return fillStyles.length; }

    /**
     * Get the fill style definition number <code>idx</code>
     * @param idx the index of the fill style
     * @exception IndexOutOfBoundsException if <code>idx</code> is
     * outside the range of <code>0..{@link
     * #getFillStyleCount}-1</code>
     */
    public SWFFillStyle getNewFillStyle(int idx) { return fillStyles[idx]; }

    /** Get the number of fill style index bits to use for parsing this
     * state change record */
    public int getFillBits() { return fillBits; }
	
    /** Get the number of fill style index bits to use for parsing the next
     * state change record */
    public int getNewStyleFillBits() { return newStyleFillBits; }
    
    /** Get the number of new line styles */
    public int getLineStyleCount() { return lineStyles.length; }

    /**
     * Get the line style definition number <code>idx</code>
     * @param idx the index of the line style
     * @exception IndexOutOfBoundsException if <code>idx</code> is
     * outside the range of <code>0..{@link
     * #getLineStyleCount}-1</code>
     */
    public SWFLineStyle getNewLineStyle(int idx) { return lineStyles[idx]; }

    /** Get the number of line style index bits to use for parsing this
     * state change record */
    public int getLineBits() { return lineBits; }
	
    /** Get the number of line style index bits to use for parsing the next
     * state change record */
    public int getNewStyleLineBits() { return newStyleLineBits; }
	
    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	// The length includes the edge/state record flag...
	int length = 6;
	if (hasMoveTo()) length += getMoveTo().length();
	if (hasFillStyle0()) length += fillBits;
	if (hasFillStyle1()) length += fillBits;
	if (hasLineStyle()) length += lineBits;
	if (hasNewStyles()) {
	    length += 16;
	    if (getFillStyleCount() >= 0xFF) length += 16;
	    if (getLineStyleCount() >= 0xFF) length += 16;
	    for (int i=0; i<getFillStyleCount(); i++) {
		length += getNewFillStyle(i).length();
	    }
	    for (int i=0; i<getLineStyleCount(); i++) {
		length += getNewLineStyle(i).length();
	    }
	}
	return length;
    }

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	// Write the edge record flag first...
	out.writeBits(0,1);	
	out.writeBit(hasNewStyles());
	out.writeBit(hasLineStyle());
	out.writeBit(hasFillStyle1());
	out.writeBit(hasFillStyle0());
	out.writeBit(hasMoveTo());
	if (hasMoveTo()) getMoveTo().write(out);
	if (hasFillStyle0()) out.writeBits(getFillStyle0(), fillBits);
	if (hasFillStyle1()) out.writeBits(getFillStyle1(), fillBits);
	if (hasLineStyle()) out.writeBits(getLineStyle(), lineBits);
	if (hasNewStyles()) {
	    if (getFillStyleCount() >= 0xFF) {
		out.writeBits(0xff, 8);
		out.writeW16LSB(getFillStyleCount());
	    } else {
		out.writeBits(getFillStyleCount(), 8);
	    }
	    for (int i=0; i<getFillStyleCount(); i++) {
		getNewFillStyle(i).write(out);
	    }
	    if (getLineStyleCount() >= 0xFF) {
		out.writeBits(0xff, 8);
		out.writeW16LSB(getLineStyleCount());
	    } else {
		out.writeBits(getLineStyleCount(), 8);
	    }
	    for (int i=0; i<getLineStyleCount(); i++) {
		getNewLineStyle(i).write(out);
	    }
	    out.writeBits(getFillBits(), 4);
	    out.writeBits(getLineBits(), 4);
	}
    }
}
