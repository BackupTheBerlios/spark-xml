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
 * $Id: SWFFont.java,v 1.1 2001/05/14 14:17:49 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the base class for all the various SWF font structures.
 * <p>an SWF font can hold the following information:</p>
 * <ul>
 * <li>the font encoding. This can be <em>ANSI</em>,
 * <em>SHIFT-JIS</em> or <em>UNICODE</em></li>
 * <li>the font name</li>
 * <li>layout attributes such as <em>bold</em> or <em>italic</em></li>
 * <li>general metrics information like ascender or descender.</li>
 * <li>the character code (in the specified encoding) for each glyph</li>
 * <li>the shape of each glyph</li>
 * <li>the bounding box of each glyph</li>
 * <li>the X advance value for each glyph</li>
 * <li>kerning information for glyph pairs</li>
 * </ul>
 * <p>This class provides methods to access all of this
 * information. In addition, it provides convenience methods to
 * convert the glyph indizes used to store characters in SWF files to
 * Java <code>char</code> and vice versa.
 * @author Richard Kunze
 */
public class SWFFont {

    /**  */

    /** A helper class to hold per-glyph information */
    protected class Glyph {
	/** The advance value for this glyph */
	private int advance = 0;
	/** The character code */
	private Character character = null;
	/** The glyph index */
	private final int INDEX;
	/** The shape of this glyph */
	private SWFShape shape = null;

	/** Create a new glyph and add it to the glyph table */
	private Glyph() {
	    INDEX = glyphTable.size();
	    glyphTable.add(this);
	}
	
	/** Get the glyph index. */
	public int getIndex() { return INDEX; }
	/** Get the character code. */
	public char getCharacter() { return character.charValue(); }
	/**
	 * Set the character code. This updates the encoding table as
	 * well.
	 * @exception IllegalArgumentException if <code>value</code>
	 * already encodes a different glyph.
	 */
	void setCharacter(char value) {
	    Character newChar = new Character(value);
	    Object entry = encodingTable.get(newChar);
	    if (entry != null && entry != this) {
		throw new IllegalArgumentException(
		 "Encoding for '" + value + "' already present.");
	    }
	    
	    // Delete the old encoding entry first
	    if (character != null)encodingTable.remove(character);
	    encodingTable.put(newChar, this);
	    character = newChar;
	}
	/** Get the X advance value. */
	public int getAdvance() { return advance; }
	/** Set the X advance value */
	void setAdvance(int value) { advance = value; }
	/** Get the shape. */
	public SWFShape getShape() { return shape; }
	/** Set the shape. */
	void setShape(SWFShape value) { shape = value; }
    }

    /**
     * Map of character codes (as Strings) to Glyph objects.
     * Used for character encoding
     */
    private Map encodingTable = new HashMap();

    /**
     * List of Glyph objects.
     * Used for character decoding and font enumeration.
     */
    private List glyphTable = new ArrayList();

    /** Get a glyph with a given index */
    protected Glyph getGlyph(int index) {
	return (Glyph)glyphTable.get(index);
    }

    /** Create a new glyph. */
    protected Glyph addGlyph() {
	return new Glyph();
    }

    /**
     * Get the glyph index for a given character.
     * @param c the character to encode
     * @return the glyph index, or -1 if this font does not contain a
     * glyph for <code>c</code>.
     */
    public int encode(char c) {
	Glyph glyph = (Glyph)encodingTable.get(new Character(c));
	if (glyph == null) return -1;
	else return glyph.getIndex();
    }

    /**
     * Get the character code for a given glyph index.
     * @param glyph the glyph index to decode
     * @return the corresponding character code.
     * @exception IndexOutOfBoundsException if <code>glyph</code> is
     * outside the range of 0 to <code>glyphCount()</code>.
     */
    public char decode(int glyph) {
	return getGlyph(glyph).getCharacter();
    }

    /**
     * Get the shape code for a given glyph index.
     * @param glyph the glyph index
     * @return the corresponding shape.
     * @exception IndexOutOfBoundsException if <code>glyph</code> is
     * outside the range of 0 to <code>glyphCount()</code>.
     */
    public SWFShape getShape(int glyph) {
	return getGlyph(glyph).getShape();
    }

    /**
     * Get the X advance for a given glyph index.
     * @param glyph the glyph index to decode
     * @return the corresponding advance value.
     * @exception IndexOutOfBoundsException if <code>glyph</code> is
     * outside the range of 0 to <code>glyphCount()</code>.
     */
    public int getAdvance(int glyph) {
	return getGlyph(glyph).getAdvance();
    }

    /** Get the number of glyphs in this font */
    public int glyphCount() { return glyphTable.size(); }
}
