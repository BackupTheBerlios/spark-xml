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
 * $Id: SWFDefineFont.java,v 1.6 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents the <em>DefineFont</em> tag of the SWF file format.
 * <p>A <em>DefineFont</em> tag has the following structure: </p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>FontID</td>
 *   <td>16</td>
 *   <td>Font ID. Unique within an SWF file</td>
 * </tr>
 * <tr>
 *   <td>offsetTable</td>
 *   <td><em>glyphCount</em> * 16</td>
 *   <td>Offsets into <em>codeTable</em> for each glyph, starting at
 *   the beginnig of <em>offsetTable</em>, as 16 bit integers in LSB format.
 *   <em>glyphCount</em> is the number of glyphs in the font and can be
 *   inferred by dividing the first entry in the offset table by 2.</td>
 * </tr>
 * <tr>
 *   <td>shapeTable</td>
 *   <td>varying</td>
 *   <td>A {@link SWFShape} structure for every glyph in this
 *    font. The start of each shape structure is aligned to a byte
 *    boundary.</td> 
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFDefineFont extends SWFDataTypeBase
             implements SWFTopLevelDataType {
    /** The SWF tag type of this class */
    public static final int TAG_TYPE = SWFTypes.DEFINE_FONT;
    
    private SWFShape shapeTable[];
    private int fontID;
    
    /**
     * Construct a <code>SWFDefineFont</code> object from a bit input stream.
     * @param input the input stream to read from
     * @exception SWFFormatException if the complete structure could
     * not be read from the stream.
     */
    public SWFDefineFont(BitInputStream input) throws IOException {
	try {
	    this.fontID = input.readUW16LSB();
	    // Calculate the number of glyphs in this font from the
	    // first entry in the offset table... 
	    int glyphCount = input.readUW16LSB() / 2;
	    shapeTable     = new SWFShape[glyphCount];

	    // Skip the font offset table. We're only interested in
	    // reading all glyphs sequentially and we don't need the
	    // offset table for this.
	    input.skipBits((glyphCount-1) * 16);
	    for (int i=0; i<shapeTable.length; i++) {
		shapeTable[i] = new SWFShape(input, false);
		// Make sure we continue reading at a byte boundary
		input.skipToByteBoundary();
	    }
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a DefineFont tag");
	}
    }

    /**
     * Construct a <code>SWFDefineFont</code> object from a
     * <code>SWFFont</code> object. The object will only include those
     * characters from <code>font</code> that are marked as used.
     * @param font the font data.
     */
    public SWFDefineFont(SWFFont font) {
	fontID = font.getFontID();
	shapeTable = new SWFShape[font.glyphCount()];
	for (int i=0; i<shapeTable.length; i++) {
	    shapeTable[i] = font.getShape(i);
	}
    }

    /** Get the font id */
    public int getID() { return fontID; }

    /** Get the number of glyphs in this font */
    public int getGlyphCount() { return shapeTable.length; }    

    /**
     * Get the shape of glyph number <code>idx</code>.
     * @param idx the index of the glyph
     * @exception IndexOutOfBoundException if <code>idx</code> is
     * outside the range of <code>0..getGlyphCount()-1</code>
     */
    public SWFShape getShape(int idx) { return shapeTable[idx]; }
    
    public long length() {

	int glyphCount = getGlyphCount();
	// Calculate the length of the individual shapes.
	long shapeLength = 0;
	for (int i=0; i<glyphCount; i++) {
	    shapeLength += paddedLength(getShape(i).length());
	}
	
	// the length is 16 bit for the font info plus 16 bit for
	// each glyph in the offset table plus the actual glyph data.
	return 16 + glyphCount * 16 + shapeLength;
    }


    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	int glyphCount = getGlyphCount();

	// Font ID first...
	out.writeW16LSB(getID());
	
	// ... the offset table. This table holds offsets to the start
	// of every glyph shape. Offsets are in bytes relative to the
	// start of the offset table.
	int offset = 2 * glyphCount;
	out.writeW16LSB(offset);
	for (int i=0; i<glyphCount-1; i++) {
	    offset += paddedLength(getShape(i).length()) / 8;
	    out.writeW16LSB(offset);
	}
	// ... and now the actual shapes ...
	for (int i=0; i<glyphCount; i++) {
	    getShape(i).write(out);
	    out.padToByteBoundary();
	}
    }

    /** Get the tag type of this object */
    public int getTagType() { return TAG_TYPE; }
}
