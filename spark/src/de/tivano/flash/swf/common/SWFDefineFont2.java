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
 * $Id: SWFDefineFont2.java,v 1.8 2001/07/02 08:07:22 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;
import java.io.UnsupportedEncodingException;

/**
 * This class represents the <em>DefineFont2</em> tag of the SWF file format.
 * <p>A <em>DefineFont2</em> tag has the following structure: </p>
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
 *   <td>hasLayout</td>
 *   <td>1</td>
 *   <td>Flag, determines wheter ascent, descent, leading height,
 *   advance table and kerning information is present.</td> 
 * </tr>
 * <tr>
 *   <td>isShiftJIS</td>
 *   <td>1</td>
 *   <td>Flag for a ShiftJIS encoded font</td>
 * </tr>
 * <tr>
 *   <td>isUnicode</td>
 *   <td>1</td>
 *   <td>Flag for a Unicode encoded font</td>
 * </tr>
 * <tr>
 *   <td>isAnsi</td>
 *   <td>1</td>
 *   <td>Flag for an ANSI encoded font</td>
 * </tr>
 * <tr>
 *   <td>hasWideOffsets</td>
 *   <td>1</td>
 *   <td>If set, this font uses 32 bits for each entry in <em>offsetTable</em>,
 *   else 16</td>
 * </tr>
 * <tr>
 *   <td>hasWideCodes</td>
 *   <td>1</td>
 *   <td>If set, the entries in <em>codeTable</em> are 16 bit wide,
 *   else 8.</td>
 * </tr>
 * <tr>
 *   <td>isItalic</td>
 *   <td>1</td>
 *   <td>If set, this font is italic</td>
 * </tr>
 * <tr>
 *   <td>isBold</td>
 *   <td>1</td>
 *   <td>If set, this font is bold</td>
 * </tr>
 * <tr>
 *   <td>reservedFlags</td>
 *   <td>8</td>
 *   <td>reserved space for future extension, currently unused</td>
 * </tr>
 * <tr>
 *   <td>nameLen</td>
 *   <td>8</td>
 *   <td>Length of the font name</td>
 * </tr>
 * <tr>
 *   <td>fontName</td>
 *   <td>8* <em>nameLen</em></td>
 *   <td>Font name (encoded in ASCII ??)</td>
 * </tr>
 * <tr>
 *   <td>glyphCount</td>
 *   <td>16</td>
 *   <td>Number of glyphs in this font. Unsigned integer in LSB format</td>
 * </tr>
 * <tr>
 *   <td>offsetTable</td>
 *   <td>(<em>glyphCount</em> + 1) * 16 or (<em>glyphCount</em> + 1) * 32</td>
 *   <td>Offsets into <em>codeTable</em> for each glyph, starting at
 *   the beginnig of <em>offsetTable</em>. Depending on
 *   <em>hasWideOffsets</em>, each offset entry is either 16 or 32 bits
 *   wide. Contrary to the official documentation, there seems to be
 *   one more offset entry than there are glyphs in the font. Offsets
 *   are in LSB format</td> 
 * </tr>
 * <tr>
 *   <td>shapeTable</td>
 *   <td>varying</td>
 *   <td>A {@link SWFShape} structure for every glyph in this
 *    font. The start of each shape structure is aligned to a byte
 *    boundary.</td> 
 * </tr>
 * <tr>
 *   <td>codeTable</td>
 *   <td><em>glyphCount</em> * 8 or <em>glypCount</em> * 16</td>
 *   <td>The character values (in the specified encoding) for every
 *   glyph in this font. Depending on <em>hasWideCodes</em>, each
 *   entry is either 8 or 16 bits wide. Entries are in LSB format.</td> 
 * </tr>
 * </table>
 * <p>If the <em>hasLayout</em> flag is set, the following additional
 * data is present in the <em>DefineFont2</em> structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>ascent</td>
 *   <td>16</td>
 *   <td>The font ascend as signed integer in LSB format.</td>
 * </tr>
 * <tr>
 *   <td>descent</td>
 *   <td>16</td>
 *   <td>The font descend as signed integer in LSB format.</td>
 * </tr>
 * <tr>
 *   <td>leading</td>
 *   <td>16</td>
 *   <td>The leading height as signed integer in LSB format.</td>
 * </tr>
 * <tr>
 *   <td>advanceTable</td>
 *   <td><em>glyphCount</em> * 16</td>
 *   <td>The X advance values for every glyph (as signed 16 bit integers)</td>
 * </tr>
 * <tr>
 *   <td>boundsTable</td>
 *   <td>varying</td>
 *   <td>The bounding boxes for every glyph. One {@link SWFRectangle}
 *   structure per glyph. The start of each bounding box is aligned to
 *   a byte boundary.</td>
 * </tr>
 * <tr>
 *   <td>kerningCount</td>
 *   <td>16</td>
 *   <td>The number of entries in <em>kerningTable</em>. Unsigned
 *   integer in LSB format.</td>
 * </tr>
 * <tr>
 *   <td>kerningTable</td>
 *   <td><em>kerningCount</em> * 32 or <em>kerningCount</em> * 48</td>
 *   <td>Kerning information for different glyph pairs</td>
 * </tr>
 * </table>
 * <p>Each <em>kerningTable</em> entry has the follwoing structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>char1</td>
 *   <td>8 or 16</td>
 *   <td>The character code for the first glyph of the kerning
 *    pair (in LSB format if 16 bit). Width depends on
 *    <em>hasWideCodes</em></td> 
 * </tr>
 * <tr>
 *   <td>char2</td>
 *   <td>8 or 16</td>
 *   <td>The character code for the second glyph of the kerning
 *    pair (in LSB format if 16 bit). Width depends on
 *    <em>hasWideCodes</em></td> 
 * </tr>
 * <tr>
 *   <td>kerningAdjustment</td>
 *   <td>16</td>
 *   <td>The kerning adjustment for this pair as a signed integer in
 *   LSB format.</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFDefineFont2 extends SWFDataTypeBase
                            implements SWFTopLevelDataType {
    /** The SWF tag type of this class */
    public static final int TAG_TYPE = SWFTypes.DEFINE_FONT2;
    
    /** Helper structure to hold the kerning information */
    public static class KerningRecord {
	/** Character code (in font encoding) for the first char */
	public final byte[] CHAR_1;
	/** Character code (in font encoding) for the second char */
	public final byte[] CHAR_2;
	/** Kerning adjustment */
	public final int KERNING;
	private KerningRecord(byte[] char1, byte[] char2, int kern) {
	    CHAR_1 = char1;
	    CHAR_2 = char2;
	    KERNING = kern;
	}
    }
    
    private SWFShape shapeTable[];
    private byte[] codeTable[];
    private int advanceTable[];
    private SWFRectangle boundsTable[];
    private KerningRecord[] kerningTable;
    private int layout = 0;
    private int encoding = SWFFont.UNKNOWN;
    private String name = null;
    private int ascent = 0;
    private int descent = 0;
    private int leadingHeight = 0;
    private int fontID;
    private boolean hasLayoutInfo;
    private boolean hasWideCodes;

    /**
     * Construct a <code>SWFDefineFont2</code> object from a bit input stream.
     * @param input the input stream to read from
     * @exception SWFFormatException if the complete structure could
     * not be read from the stream.
     */
    public SWFDefineFont2(BitInputStream input) throws IOException {
	try {
	    fontID = input.readUW16LSB();
	    hasLayoutInfo = input.readBit();
	    
	    // Read the encoding flags and set encoding accordingly
	    int encodingRaw = (int)input.readUBits(3);
	    switch (encodingRaw) {
	    case 1: // ANSI
		encoding = SWFFont.ANSI;
		break;
	    case 2: // UNICODE
		encoding = SWFFont.UNICODE;
		break;
	    case 4: // SHIFT_JIS
		encoding = SWFFont.SHIFT_JIS;
		break;
	    default:
		throw new SWFFormatException(
		   "Illegal combination of font encoding flags encountered.");
	    }
	    int offsetWidth = (input.readBit()?32:16);
	    hasWideCodes = input.readBit();

	    if (input.readBit()) layout |= SWFFont.ITALIC;
	    if (input.readBit()) layout |= SWFFont.BOLD;

	    // Skip the reserved flags
	    input.skipBits(8);

	    byte[] nameRaw = new byte[input.readUByte()];
	    input.read(nameRaw);

	    // XXX: I'm only guessing that the font name is
	    // encoded in the same encoding as the font itself.
	    // It's not documented anywhere in the SWF specs, but some
	    // Japanese (Shift-JIS encoded) SWF files I've tested seem
	    // to indicate this...
	    name = new String(nameRaw,
			      SWFFont.getCanonicalEncodingName(encoding));

	    int glyphCount = input.readUW16LSB();
	    shapeTable     = new SWFShape[glyphCount];
	    codeTable      = new byte[glyphCount][(hasWideCodes?2:1)];
	    advanceTable   = new int[glyphCount];
	    boundsTable   =  new SWFRectangle[glyphCount];

	    // Skip the font offset table. We're only interested in
	    // reading all glyphs sequentially and we don't need the
	    // offset table for this.
	    // Note: Contrary to the official docs, the offset table
	    // seems to have one entry more than the number of glyphs
	    // in the font.
	    input.skipBits((glyphCount+1) * offsetWidth);
	    for (int i=0; i<shapeTable.length; i++) {
		shapeTable[i] = new SWFShape(input);
		// Make sure we continue reading at a byte boundary
		input.skipToByteBoundary();
	    }
	    for (int i=0; i<codeTable.length; i++) {
		if (hasWideCodes) codeTable[i][1] = (byte)input.read();
		codeTable[i][0] = (byte)input.read();
	    }
	    // ShiftJIS is a variable length encoding -> need to fix
	    // the encoding for some characters.
	    if (encoding == SWFFont.SHIFT_JIS) {
		for (int i=0; i<codeTable.length; i++) {
		    if (codeTable[i][0] == 0) {
			byte[] newCode = new byte[] { codeTable[i][1] };
			codeTable[i] = newCode;
		    }
		}
	    }

	    if (hasGlyphLayout()) {
		ascent  = input.readSW16LSB();
		descent = input.readSW16LSB();
		leadingHeight =  input.readSW16LSB();
		for (int i=0; i<advanceTable.length; i++) {
		    advanceTable[i] = input.readSW16LSB();
		}
		for (int i=0; i<boundsTable.length; i++) {
		    boundsTable[i] = new SWFRectangle(input);
		    // Bounds rectangles are aligned to byte boundaries...
		    input.skipToByteBoundary();
		}
		
		kerningTable = new KerningRecord[input.readUW16LSB()];
		byte[] char1;
		byte[] char2;
		for (int i=0; i<kerningTable.length; i++) {
		    if (hasWideCodes) {
			char1 =
			    new byte[] {input.readSByte(), input.readSByte()};
			char2 =
			    new byte[] {input.readSByte(), input.readSByte()};
		    } else {
			char1 =
			    new byte[] {input.readSByte()};
			char2 =
			    new byte[] {input.readSByte()};
		    }
		    kerningTable[i] =
			new KerningRecord(char1, char2,
					  input.readSW16LSB());
		}
	    } else {
		// we need an empty kerning table for
		// getKerningRecord() to work properly
		kerningTable = new KerningRecord[0];
	    }
	    
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a DefineFont2 tag");
	}
    }

    /**
     * Construct a <code>SWFDefineFont2</code> object from a
     * <code>SWFFont</code> object. The object will only include those
     * characters from <code>font</code> that are marked as used.
     * @param font the font data.
     * @exception UnsupportedEncodingException if the java platform
     * can't handle the encoding used for this font.
     */
    public SWFDefineFont2(SWFFont font)
	   throws UnsupportedEncodingException {
	int glyphCount = font.glyphCount();
	fontID    = font.getFontID();
	name      = font.getFontName();
	encoding  = font.getEncoding();
	layout    = font.getLayout();
	hasLayoutInfo = font.hasMetrics();

	shapeTable   = new SWFShape[glyphCount];
	codeTable    = new byte[glyphCount][];
	advanceTable = new int[glyphCount];
	boundsTable  = new SWFRectangle[glyphCount];
	
	for (int i=0; i<shapeTable.length; i++) {
	    shapeTable[i] = font.getShape(i);
	}
	
	// Calculate the hasWideCodes flag based only on the actual
	// character codes used. For fixed length encodings such as
	// ANSI or UNICODE (SWF doesn't use UTF8), this is overkill,
	// but for a variable length encoding such as SHIFT_JIS this
	// ensures that wide codes are only used if there is at least
	// one character *having* a wide code.
	// Note: As usual, the SWF spec is silent on this issue. I
	// hope this strategy works as expected...
	hasWideCodes = false;
	for (int i=0; i<codeTable.length; i++) {
	    codeTable[i] = font.encode(font.getCharCode(i));
	    hasWideCodes |= codeTable[i].length > 1;
	}

	if (hasLayoutInfo) {
	    for (int i=0; i<glyphCount; i++) {
		advanceTable[i] = font.getAdvance(i);
		boundsTable[i] = font.getBounds(i);
	    }
	    ascent = font.getAscent();
	    descent = font.getDescent();
	    leadingHeight = font.getLeading();
	}
	
	// FIXME: Handle kerning!
	kerningTable = new KerningRecord[0];
    }
    
    /**
     * Get the font ascent
     * @return the font ascent. Always 0 if {@link #hasGlyphLayout} returns
     * <code>false</code>. 
     */
    public int getAscent() { return ascent; }

    /**
     * Get the font descent
     * @return the font descent. Always 0 if {@link #hasGlyphLayout} returns
     * <code>false</code>. 
     */
    public int getDescent() { return descent; }

    /**
     * Get the leading height
     * @return the leading height. Always 0 if {@link #hasGlyphLayout} returns
     * <code>false</code>. 
     */
    public int getLeadingHeight() { return leadingHeight; }

    /**
     * Check if this font includes layout information.
     * If this method returns <code>false</code>, the information
     * returned by {@link #getAscent},
     * {@link #getDescent}, {@link #getLeadingHeight} and
     * {@link #getAdvance} is meaningless.
     */
    public boolean hasGlyphLayout() { return hasLayoutInfo; }
    
    /**
     * Get the font encoding.
     * This is one of the font encoding constants defined in {@link SWFFont}
     */
    public int getEncoding() { return encoding; }
    
    /**
     * Get the font layout flags.
     * This is a combination of the font layout constants defined in
     * {@link SWFFont}
     */
    public int getLayout() { return layout; }

    /** Get the font name */
    public String getName() { return name; }

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
    
    /**
     * Get the character code of glyph number <code>idx</code>.
     * The returned array contains either a single byte or two bytes,
     * representing the glyph's character code in this fonts encoding.
     * @param idx the index of the glyph
     * @exception IndexOutOfBoundException if <code>idx</code> is
     * outside the range of <code>0..getGlyphCount()-1</code>
     */
    public byte[] getCode(int idx) { return codeTable[idx]; }

    /**
     * Get X advance value for glyph number <code>idx</code>.
     * If there is no layout information present in the SWF data, all
     * entries are 0.
     * @param idx the index of the glyph
     * @exception IndexOutOfBoundException if <code>idx</code> is
     * outside the range of <code>0..getGlyphCount()-1</code>
     */
    public int getAdvance(int idx) { return advanceTable[idx]; }

    /**
     * Get the bounding box of glyph <code>idx</code>.
     * Note: This is the bounding box given in the SWF file. It may
     * differ from the actual bounding box of the glyphs
     * {@link SWFShape} definition.
     * @param idx the index of the glyph
     * @return the bounding box or <code>null</code> if no font layout
     * information was specified in the SWF data.
     * @exception IndexOutOfBoundException if <code>idx</code> is
     * outside the range of <code>0..getGlyphCount()-1</code>
     */
    public SWFRectangle getBounds(int idx) { return boundsTable[idx]; }

    /** Get the number of kerning pairs in this font */
    public int getKerningCount() { return kerningTable.length; }

    /**
     * Get the shape of glyph number <code>idx</code>.
     * @param idx the index of the kerning record
     * @exception IndexOutOfBoundException if <code>idx</code> is
     * outside the range of <code>0..getKerningCount()-1</code>
     */
    public KerningRecord getKerningRecord(int idx) {
	return kerningTable[idx];
    }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	// Calculate the length of the individual shapes. While we're
	// at it, calculate the word width for the offset table as
	// well.
	long length = 0;
	int glyphCount = getGlyphCount();
	int charWidth =  getCharWidth();
	int offsetWidth = 16;
	for (int i=0; i<glyphCount; i++) {
	    long shapeLen = paddedLength(getShape(i).length());
	    length += shapeLen;
	    if (shapeLen > 0xffff) offsetWidth = 32;
	}
	// Calculate the rest of the length.
	length += 56 + 8*getName().length() +
	    (glyphCount + 1) * offsetWidth +
	    glyphCount * charWidth;

	if (hasGlyphLayout()) {
	    // Calculate the lenght of the bounds table
	    for (int i=0; i<glyphCount; i++) {
		length += paddedLength(getBounds(i).length());
	    }
	    // Calculate the length of the remaining layout stuff
	    length += 64 + glyphCount * 16 +
		getKerningCount() * (2*charWidth + 16);
	}
	return length;
    }

    /** Get the number of bits used for the font characters */
    private int getCharWidth() {
	return (hasWideCodes?16:8);
    }

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	int glyphCount = getGlyphCount();
	boolean hasWideOffsets = false;
	// Calculate the word width for the offset table.
	for (int i=0; i<glyphCount; i++) {
	    long shapeLen = paddedLength(getShape(i).length());
	    if (shapeLen > 0xffff) hasWideOffsets = true;
	}
	// Font ID first...
	out.writeW16LSB(getID());
	// ...then the flags...
	out.writeBit(hasGlyphLayout());
	switch (getEncoding()) {
	case SWFFont.ANSI:      out.writeBits(1,3); break;
	case SWFFont.UNICODE:   out.writeBits(2,3); break;
	case SWFFont.SHIFT_JIS: out.writeBits(4,3); break;
	default:
	    // Paranoia code.
	    throw new IllegalStateException(
	     "Illegal encoding value " + getEncoding() +
	     " found. This should never happen. Please debug.");
	}
	out.writeBit(hasWideOffsets);
	out.writeBit(getCharWidth() > 8);
	out.writeBit((getLayout() & SWFFont.ITALIC) != 0);
	out.writeBit((getLayout() & SWFFont.BOLD) != 0);
	out.writeBits(0, 8); // reserved flags, zeroed out.

	// ...the font name...
	// XXX: I'm only guessing that the font name is
	// encoded in the same encoding as the font itself.
	// It's not documented anywhere in the SWF specs, but some
	// Japanese (Shift-JIS encoded) SWF files I've tested seem
	// to indicate this...
	byte[] nameRaw = getName().getBytes(
			SWFFont.getCanonicalEncodingName(getEncoding()));
	if (nameRaw.length > 255) {
	    throw new IllegalStateException(
	     "Font name longer than 255 bytes found. " +
	     "This should never happen. Please debug.");
	}
	out.write(nameRaw.length);
	out.write(nameRaw);

	// ... now we're starting with font data...
	out.writeW16LSB(glyphCount);
	// ... the offset table. This table holds offsets to the start
	// of every glyph shape, plus the offset to the code table
	// (i.e, the next position after the last glyph
	// shape). Offsets are in bytes relative to the start of the
	// offset table.
	int offset = (hasWideOffsets?4:2) * (glyphCount + 1);
	if (hasWideOffsets) out.writeW32LSB(offset);
	else out.writeW16LSB(offset);
	for (int i=0; i<glyphCount; i++) {
	    offset += paddedLength(getShape(i).length()) / 8;
	    if (hasWideOffsets) out.writeW32LSB(offset);
	    else out.writeW16LSB(offset);
	}
	// ... and now the actual shapes ...
	for (int i=0; i<glyphCount; i++) {
	    getShape(i).write(out);
	    out.padToByteBoundary();
	}
	// ... and finally, the code table ...
	for (int i=0; i<glyphCount; i++) {
	    // Include the necessary padding byte for 1 byte ShiftJIS
	    // characters...
	    byte[] code = getCode(i);
	    if (hasWideCodes && code.length == 1) out.write(0);
	    out.write(code);
	}

	// Now, write the layout info if required.
	if (hasGlyphLayout()) {
	    // ... general layout info ...
	    out.writeW16LSB(getAscent());
	    out.writeW16LSB(getDescent());
	    out.writeW16LSB(getLeadingHeight());
	    // ... advance value for every glyph ...
	    for (int i=0; i<glyphCount; i++) {
		out.writeW16LSB(getAdvance(i));
	    }
	    // ... bounding box for every glyph ...
	    for (int i=0; i<glyphCount; i++) {
		getBounds(i).write(out);
		// bounding boxes are byte aligned...
		out.padToByteBoundary();
	    }
	    // ... and finally the kerning table ...
	    int kerningCount = getKerningCount();
	    out.writeW16LSB(kerningCount);
	    for (int i=0; i<kerningCount; i++) {
		KerningRecord entry = getKerningRecord(i);
		out.write(entry.CHAR_1);
		out.write(entry.CHAR_2);
		out.writeW16LSB(entry.KERNING);
	    }
	}
    }

    /** Get the tag type of this object */
    public int getTagType() { return TAG_TYPE; }
}
