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
 * $Id: SWFDefineTextField.java,v 1.1 2001/05/30 16:23:16 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;
import java.io.ByteArrayOutputStream;

/**
 * This class represents the <em>DefineTextField</em> tag of the SWF
 * file format. 
 * <p>A <em>DefineTextField</em> tag has the following structure: </p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>textID</td>
 *   <td>16</td>
 *   <td>Text field ID. Unique within an SWF file. Stored in LSB format</td>
 * </tr>
 * <tr>
 *   <td>bounds</td>
 *   <td>varying, 16-136</td>
 *   <td>Bounding box. Stored as {@link SWFRectangle}, padded to the
 *   next byte boundary.</td>
 * </tr>
 * <tr>
 *   <td>hasText</td>
 *   <td>1</td>
 *   <td>If set, initial text is present</td>
 * </tr>
 * <tr>
 *   <td>useWordWrap</td>
 *   <td>1</td>
 *   <td>If set, the client should wrap words at the text boundary
 *   when editing</td>
 * </tr>
 * <tr>
 *   <td>isMultiline</td>
 *   <td>1</td>
 *   <td>If set, this text contains more than one line</td>
 * </tr>
 * <tr>
 *   <td>isPassword</td>
 *   <td>1</td>
 *   <td>If set, this is a password input field</td>
 * </tr>
 * <tr>
 *   <td>isReadonly</td>
 *   <td>1</td>
 *   <td>If set, this field cannot be edited in the client</td>
 * </tr>
 * <tr>
 *   <td>hasTextColor</td>
 *   <td>1</td>
 *   <td>If set, there is a color specification present</td>
 * </tr>
 * <tr>
 *   <td>hasMaxLength</td>
 *   <td>1</td>
 *   <td>If set, a maximum text length limit is specified</td>
 * </tr>
 * <tr>
 *   <td>hasFont</td>
 *   <td>1</td>
 *   <td>If set, a font specification is present</td>
 * </tr>
 * <tr>
 *   <td>reserved</td>
 *   <td>2</td>
 *   <td>Reserved area for flags. Always 0</td>
 * </tr>
 * <tr>
 *   <td>hasLayout</td>
 *   <td>1</td>
 *   <td>If set, layout information is present.</td>
 * </tr>
 * <tr>
 *   <td>noSelect</td>
 *   <td>1</td>
 *   <td>Indicates that the contents of this text field cannot be
 *   selected for copy-and-paste</td>
 * </tr>
 * <tr>
 *   <td>hasBorder</td>
 *   <td>1</td>
 *   <td>If set, draw a border around the text</td>
 * </tr>
 * <tr>
 *   <td>unknown1</td>
 *   <td>1</td>
 *   <td>I'm not quite sure what this flag does.</td>
 * </tr>
 * <tr>
 *   <td>isHTML</td>
 *   <td>1</td>
 *   <td>If set, the text contains a limited number of HTML tags for
 *   layout control (&lt;P&gt;, &lt;FONT&gt; &lt;B&gt; and &lt;I&gt;,
 *   maybe others)</td>
 * </tr>
 * <tr>
 *   <td>unknown2</td>
 *   <td>1</td>
 *   <td>I'm not quite sure what this flag does.</td>
 * </tr>
 * <tr>
 *   <td>fontID</td>
 *   <td>16</td>
 *   <td>ID of the font to use. Stored in LSB format. This field is
 *   only present if <em>hasFont</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>fontHeight</td>
 *   <td>16</td>
 *   <td>Height of the font in TWIPS. Stored in LSB format. This field is
 *   only present if <em>hasFont</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>textColor</td>
 *   <td>32</td>
 *   <td>Text color as {@link SWFColorRGBA} structure. Only present if
 *   <em>hasTextColor</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>maxLength</td>
 *   <td>16</td>
 *   <td>Maximum text length as unsigned integer in LSB format.
 *   Only present if <em>hasMaxLength</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>textAlign</td>
 *   <td>8</td>
 *   <td>Text alignment. Values are 0 for "left", 1 for "right",
 *   2 for "center" and 3 for "justify".
 *   Only present if <em>hasLayout</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>leftMargin</td>
 *   <td>16</td>
 *   <td>Left margin in TWIPS, stored as unsigned integer in LSB format.
 *   Only present if <em>hasLayout</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>rightMargin</td>
 *   <td>16</td>
 *   <td>Right margin in TWIPS, stored as unsigned integer in LSB format.
 *   Only present if <em>hasLayout</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>indent</td>
 *   <td>16</td>
 *   <td>Indentation of the first line in TWIPS, stored as unsigned
 *   integer in LSB format.
 *   Only present if <em>hasLayout</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>lineSpacing</td>
 *   <td>16</td>
 *   <td>Vertical spacing between adjacent lines in TWIPS, stored as unsigned
 *   integer in LSB format.
 *   Only present if <em>hasLayout</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>varName</td>
 *   <td>varying</td>
 *   <td>Variable name for this text field as 0-terminated String.</td>
 * </tr>
 * <tr>
 *   <td>text</td>
 *   <td>varying</td>
 *   <td>Content of this text field as 0-terminated String.</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFDefineTextField extends SWFDataTypeBase {
    /** The SWF tag type of this class */
    public static final int TAG_TYPE = 37;

    /** Text alignment constants */
    public static final int ALIGN_LEFT    = 0;
    public static final int ALIGN_RIGHT   = 1;
    public static final int ALIGN_CENTER  = 2;
    public static final int ALIGN_JUSTIFY = 3;

    private int textID;
    private SWFRectangle bounds;
    private boolean hasLayout;
    private boolean hasFont;
    private boolean isSelectable;
    private boolean isHTML;
    private boolean hasBorder;
    private boolean useWordWrap;
    private boolean isMultiline;
    private boolean isPassword;
    private boolean isReadonly;
    private int maxLength = -1;
    private byte[] text = null;
    private String varName = null;
    private int fontID = 0;
    private int fontHeight = 0;
    private SWFColorRGBA textColor = null;
    private int textAlign;
    private int leftMargin;
    private int rightMargin;
    private int textIndent;
    private int lineSpacing;

    /**
     * Construct a <code>SWFDefineTextField</code> object from a bit
     * input stream.
     * @param input the input stream to read from
     * @exception SWFFormatException if the complete structure could
     * not be read from the stream.
     */
    public SWFDefineTextField(BitInputStream input) throws IOException {
	try {
	    textID = input.readUW16LSB();
	    bounds = new SWFRectangle(input);
	    input.skipToByteBoundary();

	    boolean hasText = input.readBit();
	    useWordWrap  = input.readBit();
	    isMultiline  = input.readBit();
	    isPassword   = input.readBit();
	    isReadonly   = input.readBit();
	    boolean hasColor = input.readBit();
	    boolean hasMaxLen = input.readBit();
	    hasFont = input.readBit();
	    input.skipBits(2); // reserved flags
	    hasLayout    = input.readBit();
	    isSelectable = !input.readBit();
	    hasBorder    = input.readBit();
	    input.skipBits(1); // Unknown flag, seems to be always 0
	    isHTML       = input.readBit();
	    input.skipBits(1); // Unknown flag, seems to be always 0

	    if (hasFont) {
		fontID = input.readUW16LSB();
		fontHeight = input.readUW16LSB();
	    }
	    if (hasColor) textColor = new SWFColorRGBA(input);
	    if (hasMaxLen) maxLength = input.readUW16LSB();
	    if (hasLayout) {
		textAlign   = input.readUByte();
		leftMargin  = input.readUW16LSB();
		rightMargin = input.readUW16LSB();
		textIndent  = input.readUW16LSB();
		lineSpacing = input.readUW16LSB();
	    }
	    
	    // Rant: SWF *always* includes length information before
	    // the structure itself, making a "streaming" writer
	    // impossible. Only here do they *omit* the length info
	    // and force me to actually scan a 0-terminated string
	    // byte by byte. Did anyone at Macromedia ever *think*
	    // about what they were doing when they designed the
	    // damn format?
	    ByteArrayOutputStream buf = new ByteArrayOutputStream();
	    int tmp;
	    while ((tmp = input.read()) != 0) buf.write(tmp);
	    // XXX: I'm only assuming that variable names are always
	    // encoded in ANSI. The specs don't say anything here,
	    // as usual...
	    varName = buf.toString(
		    SWFFont.getCanonicalEncodingName(SWFFont.ANSI));
	    if (varName.length() == 0) varName = null;
	    if (hasText) {
		buf.reset();
		while ((tmp = input.read()) != 0) buf.write(tmp);
		// Store he text as raw data because it's encoded according
		// to the associated font and we can't access that
		// here for decoding...
		text = buf.toByteArray();
	    }
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a DefineTextField tag");
	}
    }

    /** Get the text id */
    public int getID() { return textID; }

    /** Get the bounding box */
    public SWFRectangle getBounds() { return bounds; }

    /** Check if this text has layout info */
    public boolean hasLayout() { return hasLayout; }

    /** Check if this text has an associated font */
    public boolean hasFont() { return hasFont; }

    /** Check if this text is selectable */
    public boolean isSelectable() { return isSelectable; }

    /** Check if this text contains HTML markup */
    public boolean isHTML() { return isHTML; }

    /** Check if a border should be drawn around the text */
    public boolean hasBorder() { return hasBorder; }

    /** Check if lines should be word-wrapped on input */
    public boolean useWordWrap() { return useWordWrap; }

    /** Check if this text field contains more than one line of text */
    public boolean isMultiline() { return isMultiline; }

    /** Check if this text field contains a password */
    public boolean isPassword() { return isPassword; }

    /** Check if this text field is editable */
    public boolean isReadonly() { return isReadonly; }

    /**
     * Get the maximum length for text in this input field.
     * A value of -1 means unlimited.
     */
    public int getMaxLength() { return maxLength; }

    /**
     * Get the text. The text is returned as a byte array encoded
     * in the encoding of the associated font (see {@link #getFontID}).
     * If no text is set, <code>null</code> is returned.
     */
    public byte[] getText() { return text; }

    /**
     * Get the variable name for this field. This is <code>null</code>
     * if no variable name is set.
     */
    public String getVarName() { return varName; }

    /**
     * Get the font ID of the associated font.
     * This is meaningless if {@link #hasFont} returns <code>false</code>.
     */
    public int getFontID() { return fontID; }

    /**
     * Get the height (in TWIPS) of the associated font.
     * This is meaningless if {@link #hasFont} returns <code>false</code>.
     */
    public int getFontHeight() { return fontHeight; }

    /** Get the text color for this text field. This is
     * <code>null</code> if no text color is set
     */
    public SWFColorRGBA getTextColor() { return textColor; }

    /**
     * Get the alignment for this text field.
     * This is only valid if {@link #hasLayout} returns <code>true</code>.
     * @return One of the alignment constants defined in this class.
     */
    public int getTextAlign() { return textAlign; }
    
    /**
     * Get the left margin for this text field.
     * This is only valid if {@link #hasLayout} returns <code>true</code>.
     */
    public int getLeftMargin() { return leftMargin; }
    
    /**
     * Get the right margin for this text field.
     * This is only valid if {@link #hasLayout} returns <code>true</code>.
     */
    public int getRightMargin() { return rightMargin; }
    
    /**
     * Get the indentation of the first line of this text field.
     * This is only valid if {@link #hasLayout} returns <code>true</code>.
     */
    public int getTextIndent() { return textIndent; }
    
    /**
     * Get the vertical spacing for adjacent lines.
     * This is only valid if {@link #hasLayout} returns <code>true</code>.
     */
    public int getLineSpacing() { return lineSpacing; }
    
    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	throw new UnsupportedOperationException("FIXME: Not yet implemented");
    }

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	throw new UnsupportedOperationException("FIXME: Not yet implemented");
    }
}
