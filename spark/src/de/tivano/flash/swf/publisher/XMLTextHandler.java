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
 * $Id: XMLTextHandler.java,v 1.1 2001/07/02 08:07:22 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import de.tivano.flash.swf.common.SWFTypes;
import de.tivano.flash.swf.common.SWFFont;
import de.tivano.flash.swf.common.SWFColorRGBA;
import de.tivano.flash.swf.common.SWFColorRGB;
import de.tivano.flash.swf.common.SWFRectangle;
import de.tivano.flash.swf.common.SWFTopLevelDataType;
import de.tivano.flash.swf.common.SWFDefineTextField;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import org.xml.sax.Attributes;

/**
 * Handler for the <em>&lt;Text&gt;</em> XML tag.
 * The <em>&lt;Text&gt;</em> tag represents formatted text. In SWF,
 * this is expressed as either a (read-only) TextField structure or
 * one of DefineText or DefineText2 (depending on whether the text
 * color is RGB or RGBA).
 * @author Richard Kunze
 * @see de.tivano.flash.swf.parser.SWFDefineTextFieldReader
 * @see de.tivano.flash.swf.parser.SWFDefineTextReader
 * @see de.tivano.flash.swf.parser.SWFDefineText2Reader
 */
public class XMLTextHandler extends SWFTagHandlerBase {

    /** Helper class for modeling a single text chunk. */
    private static class TextChunk {
	private SWFFont font;
	private SWFColorRGB color;
	private int fontSize;
	private int align;
	private boolean addNewline = false;
	private StringBuffer text;
	
	public TextChunk(SWFFont font, int fontSize,
			 SWFColorRGB color, int align) {
	    this.font = font;
	    this.fontSize = fontSize;
	    this.color = color;
	    this.align = align;
	}

	public void addText(char[] ch, int start, int length) {
	    text.append(ch, start, length);
	}
	public void setNewline(boolean value) { addNewline = value; }
	
	public SWFFont getFont() { return font; }
	public SWFColorRGB getColor() { return color; }
	public int getAlign() { return align; }
	public String getAlignString() {
	    switch (align) {
	    case SWFDefineTextField.ALIGN_LEFT:
		return "LEFT";
	    case SWFDefineTextField.ALIGN_RIGHT:
		return "RIGHT";
	    case SWFDefineTextField.ALIGN_CENTER:
		return "CENTER";
	    case SWFDefineTextField.ALIGN_JUSTIFY:
		return "JUSTIFY";
	    default:
		return "LEFT";
	    }
	}
	public int getFontSize() { return fontSize; }
	public StringBuffer getText() { return text; }
	public boolean hasNewline() { return addNewline; }
	public boolean attribEquals(TextChunk other) {
	    return getFont().equals(other.getFont()) &&
		getColor().equals(other.getColor()) &&
		getAlign() == other.getAlign() &&
		getFontSize() == other.getFontSize();
	}
    }

    /** The individual text chunks. */
    private List textChunks = new ArrayList();

    /** Tells if this text should be selectable on the client */
    private boolean isSelectable;

    /** If set, this text includes layout information */
    private boolean hasLayout = false;

    /** The left margin */
    private int leftMargin = 0;

    /** The right margin */
    private int rightMargin = 0;

    /** The text indentation for the first line */
    private int indent = 0;

    /** The vertical spacing between lines */
    private int lineSpacing = 0;

    /** The variable name for this text */
    private String varName = "";

    /** The text ID */
    private int id;
    
    /** If set, this text has a visible border line */
    private boolean hasBorder = false;

    /** The bounding box for this text */
    private SWFRectangle bounds;

    /** The font header for the next text chunk. */
    private FontKey nextFont = new FontKey("Times new roman", 0);
    
    /** font size for the next text chunk */
    private int nextFontSize = 240;
    
    /** alignment for the next text chunk */
    private int nextAlign = SWFDefineTextField.ALIGN_LEFT;

    /** The color for the next text chunk. By default, black. */
    private SWFColorRGB nextColor = new SWFColorRGB("000000");

    /** The alpha value. By default opaque (i.e. 0xFF) */
    private int alpha = 0xFF;
    
    /** The current text chunk */
    private TextChunk currentText = null;

    public XMLTextHandler() {
	registerElementHandler("P", XMLTextMarkupParHandler.class);
    }

    /** Set the font name for the next text chunk */
    public void setNextFontName(String name) { nextFont.setName(name); }

    /** Set the font layout for the next text chunk */
    public void setNextFontLayout(int layout) { nextFont.setLayout(layout); }

    /** Set the font size for the next text chunk */
    public void setNextFontSize(int size) { nextFontSize = size; }

    /** Set the alignment for the next text chunk */
    public void setNextAlign(int align) { nextAlign = align; }

    /** Set the color for the next text chunk */
    public void setNextColor(SWFColorRGB color) { nextColor = color; }

    /** Get the font name for the next text chunk */
    public String getNextFontName() { return nextFont.getName(); }

    /** Get the font layout for the next text chunk */
    public int getNextFontLayout() { return nextFont.getLayout(); }

    /** Get the font size for the next text chunk */
    public int getNextFontSize() { return nextFontSize; }

    /** Get the alignment for the next text chunk */
    public int getNextAlign() { return nextAlign; }

    /** Get the color for the next text chunk */
    public SWFColorRGB getNextColor() { return nextColor; }

    /**
     * Start processing a &lt;Text&gt; element. 
     */
    protected void startElement(String name, Attributes attrib)
	      throws SWFWriterException {
	String tmp;

	try {
	    tmp = attrib.getValue("", "id");
	    if (tmp != null) id = Integer.parseInt(tmp);
	    tmp = attrib.getValue("", "name");
	    if (tmp != null) varName = tmp;
	    try {
		bounds = new SWFRectangle(
			Integer.parseInt(attrib.getValue("", "xmin")),
			Integer.parseInt(attrib.getValue("", "xmax")),
			Integer.parseInt(attrib.getValue("", "ymin")),
                        Integer.parseInt(attrib.getValue("", "ymax")));
	    } catch (Exception e) {
		fatalError("Text bounds must be specified");
	    }
	    tmp = attrib.getValue("", "alpha");
	    if (tmp!=null) alpha = Integer.parseInt(tmp.substring(1,3), 16);
	    // Convert layout values to "TWIPS" by multiplying with twenty
	    tmp = attrib.getValue("", "indent");
	    if (tmp!=null) {
		hasLayout = true;
		indent = Math.round(Float.parseFloat(tmp) * 20.0F);
	    }
	    tmp = attrib.getValue("", "leftmargin");
	    if (tmp!=null) {
		hasLayout = true;
		leftMargin = Math.round(Float.parseFloat(tmp) * 20.0F);
	    }
	    tmp = attrib.getValue("", "rightmargin");
	    if (tmp!=null) {
		hasLayout = true;
		rightMargin = Math.round(Float.parseFloat(tmp) * 20.0F);
	    }
	    
	    tmp = attrib.getValue("", "linespacing");
	    if (tmp!=null) {
		hasLayout = true;
	        lineSpacing = Math.round(Float.parseFloat(tmp) * 20.0F);
	    }
	    hasBorder = "yes".equals(attrib.getValue("", "border"));
	    isSelectable = "yes".equals(attrib.getValue("", "selectable"));
	    
	} catch (SWFWriterException e) {
	    throw e;
	} catch (Exception e) {
	    fatalError(e);
	}
				 
    }

    /**
     * Create the SWF writer object for actually writing out the text.
     */
    protected SWFTagWriter createDataObject() throws SWFWriterException {
	return new SWFGenericWriter(getTextData(false));
    }

    /**
     * Get the SWF data structure corresponding to the collected data.
     * @param forceTextField if <code>true</code>, the returned object
     * is an instance of {@link SWFDefineTextField}. If not, one of
     * the other types may be returned
     * @return an instance of either {@link SWFDefineTextField},
     * {@link SWFDefineText} or {@link SWFDefineText2}
     */
    public SWFTopLevelDataType getTextData(boolean forceTextField)
	   throws SWFWriterException {
	if (forceTextField || isSelectable) {
	    return createDefineTextField();
	} else {
	    // FIXME: Implement creating DefineText/DefineText2
	    // structures!
	    return createDefineTextField();
	}
    }

    /**
     * Build a {@link SWFDefineTextField} object representing the
     * data collected by this XML handler.
     */
    protected SWFTopLevelDataType createDefineTextField()
	      throws SWFWriterException {
	// Flash versions < 5 can't handle embedded HTML
	boolean useHTML = getSWFWriter().getFlashVersion() >= 5;
	// Even with Flash 5, don't use HTML formatting if there is
	// only one text format.
	TextChunk firstText = (TextChunk)textChunks.get(0);
	SWFFont font = firstText.getFont();
	if (useHTML) {
	    Iterator texts = textChunks.iterator();
	    // Determine if there is more than one format in this text.
	    texts.next();
	    boolean multiFormat = false;
	    while (!multiFormat && texts.hasNext()) {
		multiFormat = firstText.attribEquals((TextChunk)texts.next());
	    }
	    useHTML = !multiFormat;
	}
	byte[] text = null;
	try {
	    if (useHTML) {
		text = getHTMLText().getBytes(font.getCanonicalEncodingName());
	    } else {
		text = getPlainText().getBytes(font.getCanonicalEncodingName());
	    }
	} catch (UnsupportedEncodingException e) {
	    fatalError(e);
	}
	SWFDefineTextField data = new SWFDefineTextField();
	data.setID(id);
	data.setBounds(bounds);
	data.setSelectable(isSelectable);
	data.setHTML(useHTML);
	data.setHasBorder(hasBorder);
	data.setVarName(varName);
	data.setFontID(font.getFontID());	
	data.setFontHeight(firstText.getFontSize());
	data.setTextColor(new SWFColorRGBA(firstText.getColor(), alpha));
	data.setReadonly(true);
	data.setText(text);
	if (hasLayout) {
	    data.setTextAlign(firstText.getAlign());
	    data.setLeftMargin(leftMargin);
	    data.setRightMargin(rightMargin);
	    data.setTextIndent(indent);
	    data.setLineSpacing(lineSpacing);
	}
	
	return data;
    }

    /**
     * Get the complete text. Layout information is discarded.
     */
    protected String getPlainText() {
	Iterator texts = textChunks.iterator();
	StringBuffer buf = new StringBuffer();
	while (texts.hasNext()) {
	    TextChunk current = (TextChunk)texts.next();
	    StringBuffer text = current.getText();
	    // Mark the used characters so they will be included in the font.
	    current.getFont().markUsed(text);
	    buf.append(text);
	    if (current.hasNewline()) buf.append('\n');
	}
	return buf.toString();
    }
    
    /**
     * Get the complete text. Layout information is
     * preserved as embedded HTML markup.
     */
    protected String getHTMLText() {
	Iterator texts = textChunks.iterator();
	StringBuffer buf = new StringBuffer();
	TextChunk current = (TextChunk)textChunks.get(0);
	buf.append("<P ALIGN=\"" + current.getAlignString() + "\">");
	String lastName = null;
	int lastSize = -1;
	SWFColorRGB lastColor = null;
	while (texts.hasNext()) {
	    current = (TextChunk)texts.next();
	    SWFFont font = current.getFont();
	    int layout = font.getLayout();
	    buf.append("<FONT");
	    if (!font.getFontName().equals(lastName)) {
		buf.append(" FACE=\"").append(font.getFontName()).append("\"");
	    }
	    if (current.getFontSize() != lastSize) {
		buf.append(" SIZE=\"").append(current.getFontSize()).append("\"");
	    }
	    if (!current.getColor().equals(lastColor)) {
		buf.append(" COLOR=\"#").append(current.getColor().toHexString()).append("\"");
	    }
	    buf.append(">");
	    if ((layout & SWFFont.BOLD) != 0) buf.append("<B>");
	    if ((layout & SWFFont.ITALIC) != 0) buf.append("<I>");
	    StringBuffer text = current.getText();
	    // Escape special characters.
	    // XXX: If you change this map, don't forget to update the reverse
	    // mapping in XMLDefineTextFieldReader aus well.
	    for (int i=0; i<text.length(); i++) {
		char ch = text.charAt(i);
		// Mark the used characters so they will be included
		// in the font.
		font.markUsed(ch);
		switch(ch) {
		case 0xA0: buf.append("&nbsp;"); break;
		case '"':  buf.append("&quot;"); break;
		case '<':  buf.append("&lt;"); break;
		case '>':  buf.append("&gt;"); break;
		case '&':  buf.append("&amp;"); break;
		default:   buf.append(ch); break;
		}
	    }
	    if ((layout & SWFFont.ITALIC) != 0) buf.append("</I>");
	    if ((layout & SWFFont.BOLD) != 0) buf.append("</B>");
	    buf.append("</FONT>");
	    if (current.hasNewline() && texts.hasNext()) {
		TextChunk next = (TextChunk)texts.next();
		buf.append("</P><P ALIGN=\"" + next.getAlignString() +
			   "\">");
	    }
	}
	buf.append("</P>");
	return buf.toString();
    }


    /** Add some text to the current text chunk */
    public void addText(char[] ch, int start, int length)
	    throws SWFWriterException {
	if (currentText == null) {
	    SWFFont font =
		(SWFFont)getSWFWriter().getContextMap().get(nextFont);
	    if (font == null) {
		String layout;
		switch (nextFont.getLayout()) {
		case SWFFont.BOLD: layout = "(bold)"; break;
		case SWFFont.BOLD|SWFFont.ITALIC:
		    layout = "(bold,italic)"; break;
		case SWFFont.ITALIC: layout = "(italic)"; break;
		default: layout = "(standard)";
		}
		fatalError("Font not found: " + nextFont.getName() + " " +
			   layout + " not found.");
	    }
	    currentText = new TextChunk(font, nextFontSize,
					nextColor, nextAlign);
	    textChunks.add(currentText);
	}
	currentText.addText(ch, start, length);
    }

    /**
     * Close the current text chunk. The next characters will go on a
     * new text chunk.
     * @param newline if <code>true</code>, the next text chunk will
     * start on a new line.
     */
    public void finishCurrentText(boolean newline) {
	currentText.setNewline(newline);
	currentText = null;
    }
	
}
