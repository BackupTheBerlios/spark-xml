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
 * $Id: SWFFont.java,v 1.7 2001/06/26 16:36:23 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;

/**
 * Stores and manipulates font encoding information.
 * This class is used to communicate the necessary encoding and layout
 * information between handlers for SWF fonts and texts.
 * <p>This class holds the following information:</p>
 * <ul>
 * <li>the font encoding. This can be <em>ANSI</em>,
 * <em>SHIFT-JIS</em> or <em>UNICODE</em></li>
 * <li>the font ID given in the SWF file</li>
 * <li>the font name</li>
 * <li>layout attributes such as <em>bold</em> or <em>italic</em></li>
 * <li>general metrics information like ascender or descender.</li>
 * <li>the character code for each glyph</li>
 * <li>the X advance value for each glyph</li>
 * <li>kerning information for glyph pairs</li>
 * </ul>
 *
 * <p>This class provides methods to get and set all of this
 * information. In addition, it provides convenience methods to
 * convert the glyph indizes used to store characters in SWF files to
 * Java <code>char</code> and vice versa, and to convert strings in
 * the font's encoding to Java strings and back. Note that this class
 * does <em>not</em> provide the means to read/write SWF font
 * information. This task is handled by {@link SWFDefineFont}, {@link
 * SWFDefineFontInfo} and {@link SWFDefineFont2}.</p>
 * 
 * @author Richard Kunze
 */
public class SWFFont {

    /** Constant for layout attributes */
    public static final int BOLD = 1;
    /** Constant for layout attributes */
    public static final int ITALIC = 2;

    /** Constant for the font encoding */
    public static final int UNKNOWN = 0;
    /** Constant for the font encoding */
    public static final int ANSI = 1;
    /** Constant for the font encoding */
    public static final int SHIFT_JIS = 2;
    /** Constant for the font encoding */
    public static final int UNICODE = 3;

    /** A helper class to hold per-glyph information */
    protected class Glyph {
	/** The advance value for this glyph */
	private int advance = 0;
	/** The character code */
	private Character character = null;
	/** The glyph index */
	private int index;
	/** The glyph shape */
	private SWFShape shape = null;
	/** The bounding box */
	private SWFRectangle bounds = null;

	/**
	 * Create a new glyph.
	 * @param addToTable if <code>true</code>, add this glyph to
	 * the glyph table.
	 */
	private Glyph(boolean addToTable) {
	    if (addToTable) {
		index = glyphTable.size();
		glyphTable.add(this);
	    } else {
		index = -1;
	    }
	}
	
	/**
	 * Get the glyph index. If the glyph has not yet been added
	 * to the glyph table, this is automatically done now.
	 */
	public int getIndex() {
	    if (index < 0) {
		index = glyphTable.size();
		glyphTable.add(this);
	    }
	    return index;
	}
	
	/** Get the character code. */
	public Character getCharacter() { return character; }
	
	/**
	 * Set the character code. This updates the encoding table as
	 * well.
	 * @exception IllegalArgumentException if <code>value</code>
	 * already encodes a different glyph.
	 */
	public void setCharacter(Character value) {
	    Object entry = encodingTable.get(value);
	    if (entry != null && entry != this) {
		throw new IllegalArgumentException(
		 "Encoding for '" + value + "' already present.");
	    }
	    
	    // Delete the old encoding entry first if necessary
	    if (character != null) encodingTable.remove(character);
	    encodingTable.put(value, this);
	    character = value;
	}
	
	/** Get the X advance value. */
	public int getAdvance() { return advance; }
	
	/** Set the X advance value */
	public void setAdvance(int value) {
	    advance = value;
	    hasMetrics = true;
	}

	/** Get the bounding box. */
	public SWFRectangle getBounds() { return bounds; }
	
	/** Set the boundig box */
	public void setBounds(SWFRectangle value) {
	    bounds = value;
	    hasMetrics = true;
	}
	/** Get the shape. */
	public SWFShape getShape() { return shape; }
	
	/** Set the boundig box */
	public void setShape(SWFShape value) { shape = value; }
    }

    /**
     * Map of character codes (as Character objects) to Glyph objects.
     * Used for character encoding
     */
    private Map encodingTable = new HashMap();

    /**
     * List of Glyph objects.
     * Used for character decoding and font enumeration.
     */
    private List glyphTable = new ArrayList();

    /** Map of character pairs (as String) to kerning values (as Integer)
     */
    private Map kerningTable = new HashMap();

    /** Font ID as given in the SWF file */
    private int fontID;

    /** Font name */
    private String fontName;

    /** Layout attributes for this font. A combination of the
     *  {@link #BOLD} and {@link #ITALIC} flags
     */
    private int layout;

    /** The font encoding as specified in SWF */
    int encoding;

    /** the font ascent as specified in SWF */
    private int ascent;
    
    /** the font descent as specified in SWF */
    private int descent;
    
    /** the font leading height as specified in SWF */
    private int leading;

    /** Flag, describes if this font includes metrics information */
    private boolean hasMetrics = false;

    /** Check if this font includes metrics information. If this
     * method returns <code>false</code>, the values returned by
     * {@link #getAscent}, {@link #getDescent}, {@link #getLeading},
     * {@link #getAdvance} and {@link getKerning} are meaningless.
     */
    public boolean hasMetrics() { return hasMetrics; }
    
    /** Get the font ascent */
    public int getAscent() { return ascent; }
    
    /** Set the font ascent */
    public void setAscent(int ascent) {
	this.ascent = ascent;
	hasMetrics = true;
    }
    
    /** Get the font descent */
    public int getDescent() { return descent; }
    
    /** Set the font descent */
    public void setDescent(int descent) {
	this.descent = descent;
	hasMetrics = true;
    }
    
    /** Get the font leading height */
    public int getLeading() { return leading; }
    
    /** Set the font leading height */
    public void setLeading(int leading) {
	this.leading = leading;
	hasMetrics = true;
    }
    
    
    /**
     * Get the font encoding specified in the SWF file.  This is one
     * of the constants {@link #UNKNOWN}, {@link #ANSI}, {@link
     * #SHIFT_JIS} or {@link #UNICODE}.
     */
    public int getEncoding() { return encoding; }

    /**
     * Get the canonical Java name for <code>encoding</code>.
     * As the SWF specs are very vague about font encoding specifics,
     * I can only guess at how they map to the Java encoding
     * names. The current mapping is:
     * <ul>
     * <li><code>SWFFont.ANSI</code>: ISO-8859-1
     * <li><code>SWFFont.UNICODE</code>: UTF-16BE
     * <li><code>SWFFont.SHIFT_JIS</code>: SJIS
     * </ul>
     * <code>SWFFont.UNICODE</code> and <code>SWFFont.ANSI</code>
     * should work in every Java 2
     * environment. <code>SWFFont.SHIFT_JIS</code> may need additional
     * support, but it should work on most Java implementations, too.
     * @param encoding the encoding constant as defined in this class.
     */
    public static String getCanonicalEncodingName(int encoding) {
	switch (encoding) {
	case ANSI: return "ISO-8859-1";
	case UNICODE: return "UTF-16BE";
	case SHIFT_JIS: return "SJIS";
	default:
	    // Paranoia code, should never happen
	    throw new IllegalStateException(
		   "Illegal font encoding: " + encoding);
	}
    }
    
    /**
     * Get the canonical Java name for this font's encoding.
     * As the SWF specs are very vague about font encoding specifics,
     * I can only guess at how they map to the Java encoding
     * names. The current mapping is:
     * <ul>
     * <li><code>SWFFont.ANSI</code>: ISO-8859-1
     * <li><code>SWFFont.UNICODE</code>: UTF-16LE
     * <li><code>SWFFont.SHIFT_JIS</code>: SJIS
     * </ul>
     * <code>SWFFont.UNICODE</code> and <code>SWFFont.ANSI</code>
     * should work in every Java 2
     * environment. <code>SWFFont.SHIFT_JIS</code> may need additional
     * support, but it should work on most Java implementations, too.
     */
    public String getCanonicalEncodingName() {
	return getCanonicalEncodingName(getEncoding());
    }
    
    /**
     * Set the font encoding.
     * @param encoding The new encoding.
     * @exception IllegalArgumentException if <code>encoding</code> is
     * not one of the constants {@link #UNKNOWN}, {@link #ANSI}, {@link
     * #SHIFT_JIS} or {@link #UNICODE}.
     */
    public void setEncoding(int encoding) {
	// Paranoia code: Check the parameters
	switch (encoding) {
	case UNKNOWN:
	case ANSI:
	case UNICODE:
	case SHIFT_JIS:
	    this.encoding = encoding;
	    return;
	default:
	    throw new IllegalArgumentException(
		       "Illegal font encoding: " + encoding);
	}
    }
    
    
    /**
     * Get the layout attributes. This is a combination of
     * the constants {@link #BOLD} and {@link #ITALIC}
     * @return the layout attributes of this font.
     */
    public int getLayout() {return layout;}
    
    /**
     * Set the value of layoutAttributes.
     * @param layout The new layout attributes.
     * @exception IllegalArgumentException if <code>layout</code> is
     * not a combination of {@link #BOLD} and {@link #ITALIC}
     */
    public void setLayout(int layout) {
	// Paranoia code: Check if the parameter value is OK.
	if ((layout & ~(BOLD | ITALIC)) != 0) {
	    throw new IllegalArgumentException(layout +
	      " is not a combination of SWFFont.BOLD and SWFFont.ITALIC");
	}
	this.layout = layout;
    }
        
    /** Get the font name. */
    public String getFontName() {return fontName;}
    
    /** Set the font name */
    public void setFontName(String name) {this.fontName = name;}

    /** Get the font ID */
    public int getFontID() { return fontID; }

    /** Set the font ID */
    public void setFontID(int id) { fontID = id; }

    /** Get a glyph with a given index */
    protected Glyph getGlyph(int index) {
	return (Glyph)glyphTable.get(index);
    }

    /** Get a glyph with a given character code */
    protected Glyph getGlyph(Character c) {
	return (Glyph)encodingTable.get(c);
    }

    /** Create a new glyph. */
    protected Glyph createGlyph(boolean marked) {
	return new Glyph(marked);
    }

    /** Add a new glyph entry */
    public void addGlyph(Character charcode, int advance,
			 SWFRectangle bounds, SWFShape shape,
			 boolean marked) {
	Glyph glyph = createGlyph(marked);
	glyph.setCharacter(charcode);
	glyph.setAdvance(advance);
	glyph.setBounds(bounds);
	glyph.setShape(shape);
    }

    /** Add a new glyph entry */
    public void addGlyph(Character charcode, int advance, boolean marked) {
	Glyph glyph = createGlyph(marked);
	glyph.setCharacter(charcode);
	glyph.setAdvance(advance);
    }

    /** Add a new glyph entry */
    public void addGlyph(String charcode, int advance, boolean marked) {
	addGlyph(new Character(charcode.charAt(0)), advance, marked);
    }

    /**
     * Add a new glyph entry. The glyph entry is automatically
     * assigned a glyph index. */
    public void addGlyph() {
	Glyph glyph = createGlyph(true);
    }

    /** Add a new glyph entry */
    public void addGlyph(Character charcode, boolean marked) {
	Glyph glyph = createGlyph(marked);
	glyph.setCharacter(charcode);
    }

    /** Add a new glyph entry */
    public void addGlyph(String charcode, boolean marked) {
	Glyph glyph = createGlyph(marked);
	glyph.setCharacter(new Character(charcode.charAt(0)));
    }

    /**
     * Get the glyph index for a given character.
     * The glyph is automatically marked as used.
     * @param c the character to encode
     * @return the glyph index, or -1 if this font does not contain a
     * glyph for <code>c</code>.
     */
    public int getGlyphIndex(char c) {
	Glyph glyph = getGlyph(c);
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
    public Character getCharCode(int glyph) {
	return getGlyph(glyph).getCharacter();
    }

    /**
     * Set the character code for a given glyph index.
     * @param glyph the glyph index.
     * @param charcode the character code for this glyph.
     * @exception IndexOutOfBoundsException if <code>glyph</code> is
     * outside the range of 0 to <code>glyphCount()</code>.
     */
    public void setCharCode(int glyph, Character charcode) {
	getGlyph(glyph).setCharacter(charcode);
    }

    /**
     * Set the character code for a given glyph index.
     * @param glyph the glyph index.
     * @param charcode the character code for this glyph.
     * @exception IndexOutOfBoundsException if <code>glyph</code> is
     * outside the range of 0 to <code>glyphCount()</code>.
     */
    public void setCharCode(int glyph, String charcode) {
	setCharCode(glyph, new Character(charcode.charAt(0)));
    }

    /**
     * Get the Java <code>String</code> representing
     * <code>input</code> in the font's character encoding.
     * @param input the encoded string.
     * @exception UnsupportedEncodingException if the Java environment
     * does not support this font's encoding.
     */
    public String decode(byte[] chars) throws UnsupportedEncodingException {
	return new String(chars, getCanonicalEncodingName());
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

    /**
     * Get the bounding box for a given glyph index.
     * @param glyph the glyph index
     * @return the bounding box.
     * @exception IndexOutOfBoundsException if <code>glyph</code> is
     * outside the range of 0 to <code>glyphCount()</code>.
     */
    public SWFRectangle getBounds(int glyph) {
	return getGlyph(glyph).getBounds();
    }

    /**
     * Get the shape for a given glyph index.
     * @param glyph the glyph index
     * @return the shape.
     * @exception IndexOutOfBoundsException if <code>glyph</code> is
     * outside the range of 0 to <code>glyphCount()</code>.
     */
    public SWFShape getShape(int glyph) {
	return getGlyph(glyph).getShape();
    }

    /** Get the number of glyphs in this font */
    public int glyphCount() { return glyphTable.size(); }
}
