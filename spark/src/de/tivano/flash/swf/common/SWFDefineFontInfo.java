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
 * $Id: SWFDefineFontInfo.java,v 1.2 2001/05/30 16:23:16 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents the <em>DefineFontInfo</em> tag of the SWF
 * file format. 
 * <p>A <em>DefineFontInfo</em> tag has the following structure: </p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>FontID</td>
 *   <td>16</td>
 *   <td>Font ID. Same ID as the corresponding <em>DefineFont</em> tag.</td>
 * </tr>
 * <tr>
 *   <td>nameLen</td>
 *   <td>8</td>
 *   <td>Length of the font name</td>
 * </tr>
 * <tr>
 *   <td>fontName</td>
 *   <td>8* <em>nameLen</em></td>
 *   <td>Font name (encoded in font encoding)</td>
 * </tr>
 * <tr>
 *   <td>reserved</td>
 *   <td>2</td>
 *   <td>Reserved space for flags</td> 
 * </tr>
 * <tr>
 *   <td>isUnicode</td>
 *   <td>1</td>
 *   <td>Flag for a Unicode encoded font (?). Note: This is according
 *   to the specs and may be wrong. <a href="#fonts">See below for
 *   more information.</td>
 * </tr>
 * <tr>
 *   <td>isAnsi</td>
 *   <td>1</td>
 *   <td>Flag for an ANSI encoded font (?). Note: According to the specs,
 *   this flag indicates a ShiftJIS-encoded font but this seems to be
 *   false. <a href="#fonts">See below for more information.</a></td>
 * </tr>
 * <tr>
 *   <td>isShiftJIS</td>
 *   <td>1</td>
 *   <td>Flag for a ShiftJIS encoded font (?). Note: According to the specs,
 *   this flag indicates an ANSI-encoded font but this seems to be
 *   false. <a href="#fonts">See below for more information.</td>
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
 *   <td>hasWideCodes</td>
 *   <td>1</td>
 *   <td>If set, the entries in <em>codeTable</em> are 16 bit wide,
 *   else 8.</td>
 * </tr>
 * <tr>
 *   <td>codeTable</td>
 *   <td><em>glyphCount</em> * 8 or <em>glypCount</em> * 16</td>
 *   <td>The character values (in the specified encoding) for every
 *   glyph in this font. Depending on <em>hasWideCodes</em>, each
 *   entry is either 8 or 16 bits wide. Entries are in LSB format.</td> 
 * </tr>
 * </table>
 * <p>The value for <em>glyphCount</em> is not specified in the tag
 * itself, it must be obtained by querying the corresponding
 * <em>DefineFont</em> structure or inferred from the tag length.</p>
 * <p><a name="fonts"><em>Author's note:</em></a> The SWF specs seem to be
 * incorrect with regard to the font encoding flags. The flag for
 * ShiftJIS encoding (according to spec) is set on every European font
 * I've seen so far and is <em>not</em> set on the few Asian fonts
 * I've found that were encoded as DefineFont/DefineFontInfo pairs
 * instead of DefineFont2. In addition, I've seen some Asian fonts
 * that appear to be Unicode that have the <em>hasWideCodes</em> flag
 * but have none of the font encoding flags set. For now, I'm assuming
 * that the ShiftJIS and ANSI flags are swapped, and that the encoding
 * is either ANSI or Unicode (depending on the <em>hasWideCodes</em>
 * flag) when no encoding flags are present. If you have sample SWF
 * files containing Unicode or Shift-JIS encoded fonts as
 * DefineFont/DefineFontInfo pairs, I'd appreciate it if you could
 * send a copy to <a href="mailto:flash2xml@tivano.de">flash2xml@tivano.de</a>
 * for testing.</p>
 * @author Richard Kunze
 */
public class SWFDefineFontInfo extends SWFDataTypeBase {
    /** The SWF tag type of this class */
    public static final int TAG_TYPE = 13;
    
    private byte[] codeTable[];
    private int layout = 0;
    private int encoding = SWFFont.UNKNOWN;
    private String name = null;
    private int fontID;
    private boolean hasWideCodes = false;
    
    /**
     * Construct a <code>SWFFontInfo</code> object from a bit input stream.
     * @param input the input stream to read from
     * @param glyphCount the number of glyphs in the corresponding font.
     * @exception SWFFormatException if the complete structure could
     * not be read from the stream.
     */
    public SWFDefineFontInfo(BitInputStream input, int glyphCount)
	   throws IOException {
	this(input, glyphCount, input.readUW16LSB());
    }

    /**
     * Construct a <code>SWFDefineFontInfo</code> object from a bit
     * input stream. 
     * This constructor assumes that the font ID has already been read
     * from <code>input</code> and that input is positioned at the
     * start of the offset table.
     * @param input the input stream to read from
     * @param glyphCount the number of glyphs in the corresponding font.
     * @param fontID the ID for this input stream.
     * @exception SWFFormatException if the complete structure could
     * not be read from the stream.
     */
    public SWFDefineFontInfo(BitInputStream input, int glyphCount, int fontID)
	   throws IOException {
	try {
	    this.fontID = fontID;

	    // ... then the name ...
	    byte[] nameRaw = new byte[input.readUByte()];
	    input.read(nameRaw);

	    // ... skip the reserved flags ...
	    input.skipBits(2);
	    
	    // ... read the encoding flags ...
	    int encodingRaw = (int)input.readUBits(3);
	    
	    // ... read the layout flags ...
	    if (input.readBit()) layout |= SWFFont.ITALIC;
	    if (input.readBit()) layout |= SWFFont.BOLD;

	    // ... and the flag for wide codes ...
	    hasWideCodes = input.readBit();

	    // ... now that we have all info, set encoding and font name ...
	    switch (encodingRaw) {
	    case 0:
		// I've seen at least one Asian font with no font
		// flags that seemed to be Unicode, so I'm making a
		// wild guess here.
		encoding =
		    (hasWideCodes?SWFFont.UNICODE:SWFFont.ANSI);
		break;
	    case 1: // SHIFT_JIS
		encoding = SWFFont.SHIFT_JIS;
		break;
	    case 2: // ANSI
		encoding = SWFFont.ANSI;
		break;
	    case 4: // UNICODE
		encoding = SWFFont.UNICODE;
		break;
	    default:
		throw new SWFFormatException(
		   "Illegal combination of font encoding flags encountered: " + encodingRaw);
	    }

	    // XXX: I'm only guessing that the font name is
	    // encoded in the same encoding as the font itself.
	    // It's not documented anywhere in the SWF specs, but some
	    // Asian (Shift-JIS and Unicode encoded) SWF files I've
	    // tested seem to indicate this...
	    name = new String(nameRaw,
			      SWFFont.getCanonicalEncodingName(encoding));

	    // ... finally, read the font code table ...
	    codeTable = new byte[glyphCount][(hasWideCodes?2:1)];
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
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a DefineFont2 tag");
	}
    }

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
    public int getGlyphCount() { return codeTable.length; }    

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
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	return 32 + getCharWidth() * getGlyphCount();
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
	// Font ID first...
	out.writeW16LSB(getID());
	
	// ...the font name. I'm guessing here that the name is
	// encoded in ASCII...
	byte[] nameRaw = getName().getBytes("US-ASCII");
	if (nameRaw.length > 255) {
	    throw new IllegalStateException(
	     "Font name longer than 255 bytes found. " +
	     "This should never happen. Please debug.");
	}
	out.write(nameRaw.length);
	out.write(nameRaw);
	
	// ...then the flags...
	out.writeBits(0, 2); // reserved flags, zeroed out.
	switch (getEncoding()) {
	case SWFFont.SHIFT_JIS: out.writeBits(1,3); break;
	case SWFFont.ANSI:      out.writeBits(2,3); break;
	case SWFFont.UNICODE:   out.writeBits(4,3); break;
	default:
	    // Paranoia code.
	    throw new IllegalStateException(
	     "Illegal encoding value " + getEncoding() +
	     " found. This should never happen. Please debug.");
	}
	out.writeBit((getLayout() & SWFFont.ITALIC) != 0);
	out.writeBit((getLayout() & SWFFont.BOLD) != 0);
	out.writeBit(hasWideCodes);

	// ... and finally, the code table ...
	for (int i=0; i<glyphCount; i++) {
	    // Include the necessary padding byte for 1 byte ShiftJIS
	    // characters...
	    byte[] code = getCode(i);
	    if (hasWideCodes && code.length == 1) out.write(0);
	    out.write(code);
	}
    }
}
