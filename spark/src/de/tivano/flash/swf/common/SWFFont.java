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
 * $Id: SWFFont.java,v 1.2 2001/05/14 17:50:42 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manipulates font related information. 
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
 * <p>This class provides methods to get and set all of this
 * information. In addition, it provides convenience methods to
 * convert the glyph indizes used to store characters in SWF files to
 * Java <code>char</code> and vice versa. Note that this class does
 * <em>not</em> provide the means to read/write SWF font
 * information. This task is handled by {@link SWFDefineFont},
 * {@link SWFDefineFontInfo} and {@link SWFDefineFont2}.</p>
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

    /** A helper class to hold kerning information */
    protected class KerningEntry {
	/** The first glyph of the kerning pair */
	public final Glyph FIRST_GLYPH;
	/** The second glyph of the kerning pair */
	public final Glyph SECOND_GLYPH;
	/** The advance value to use fpr this kerning pair */
	public final int ADVANCE;

	public KerningEntry(Glyph first, Glyph second, int kern) {
	    FIRST_GLYPH = first;
	    SECOND_GLYPH = second;
	    ADVANCE = kern;
	}
    }

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
    
    /** Get the font ascent */
    public int getAscent() { return ascent; }
    
    /** Set the font ascent */
    public void setAscent(int ascent) { this.ascent = ascent; }
    
    /** Get the font descent */
    public int getDescent() { return descent; }
    
    /** Set the font descent */
    public void setDescent(int descent) { this.descent = descent; }
    
    /** Get the font leading height */
    public int getLeading() { return leading; }
    
    /** Set the font leading height */
    public void setLeading(int leading) { this.leading = leading; }
    
    
    /**
     * Get the font encoding specified in the SWF file.  This is one
     * of the constants {@link #UNKNOWN}, {@link #ANSI}, {@link
     * #SHIFT_JIS} or {@link #UNICODE}.
     */
    public int getEncoding() { return encoding; }
    
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
	    throw new IllegalStateException(
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
