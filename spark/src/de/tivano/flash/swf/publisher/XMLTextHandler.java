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
 * $Id: XMLTextHandler.java,v 1.5 2002/01/25 13:50:10 kunze Exp $
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
import java.util.LinkedList;
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
    
    private static final SWFColorRGB DEFAULT_COLOR =
	new SWFColorRGB("000000");
    
    /** Helper class for modeling formatted text. */
    private class FormattedText {
	private FontKey fontKey = new FontKey("Times New Roman", 0);
	private SWFFont font = null;
	private SWFColorRGB color = DEFAULT_COLOR;
	private int fontSize = 240;
	private int align = SWFDefineTextField.ALIGN_LEFT;
	private boolean isParagraph = false;
	protected List content = new ArrayList();
	private StringBuffer currentText = null;
	private boolean hasFontName = false;
	private boolean hasFontSize = false;
	protected boolean hasFontStyleBold = false;
	protected boolean hasFontStyleItalic = false;
	private boolean hasFontStyle = false;
	private boolean hasColor = false;
	private boolean hasAlign = false;

	public FormattedText() {}

	public void setFontName(String value) {
	    fontKey.setName(value);
	    hasFontName = true;
	}
	public void setFontStyle(int value) {
	    fontKey.setLayout(value);
	    hasFontStyleBold = (value & SWFFont.BOLD) != 0;
	    hasFontStyleItalic = (value & SWFFont.ITALIC) != 0;
	    hasFontStyle = true;
	}
	public void setColor(SWFColorRGB value) {
	    color = value;
	    hasColor = true;
	}
	public void setFontSize(int value) {
	    fontSize = value;
	    hasFontSize = true;
	}
	public void setAlign(int value) {
	    align = value;
	    hasAlign = true;
	}
	public void setIsParagraph(boolean value) { isParagraph = value; }
	public boolean hasFontName() { return hasFontName; }
	public boolean hasFontStyle() { return hasFontStyle; }
	public boolean hasFontSize() { return hasFontSize; }
	public boolean hasColor() { return hasColor; }
	public boolean hasAlign() { return hasAlign; }
	public boolean isParagraph() { return isParagraph; }
	public boolean isEmpty() { return content.isEmpty(); }
	public String getFontName() { return fontKey.getName(); }
	public int getFontStyle() { return fontKey.getLayout(); }
	public int getFontSize() { return fontSize; }
	public SWFColorRGB getColor() { return color; }
	public int getAlign() { return align; }
	public SWFFont getFont() throws SWFWriterException {
	    if (font == null) {
		font = (SWFFont)getSWFWriter().getContextMap().get(fontKey);
		if (font == null) fatalError("Font not found: "
					     + getFontName()
					     + " (" + getStyleString() + ")");
	    }
	    return font;
	}
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

	public String getStyleString() {
	    switch (fontKey.getLayout()) {
	    case 0: return "standard";
	    case SWFFont.BOLD: return "bold";
	    case SWFFont.ITALIC: return "italic";
	    case SWFFont.BOLD | SWFFont.ITALIC: return "bold,italic";
	    default: return "unknown: " + fontKey.getLayout();
	    }
	}

	public void add(FormattedText newText) {
	    newText.fontKey.setName(getFontName());
	    newText.fontKey.setLayout(getFontStyle());
	    newText.align = getAlign();
	    newText.color = getColor();
	    newText.fontSize = getFontSize();
	    content.add(newText);
	    currentText = null;
	}
	
	public void add(char[] ch, int start, int length) {
	    if (length == 0) return;
	    if (currentText == null) {
		currentText = new StringBuffer();
		content.add(currentText);
	    }
	    currentText.append(ch, start, length);
	}

	private boolean isLayoutCompatible(FormattedText other) {
	    return (!hasFontName()||getFontName().equals(other.getFontName()))
		&& (!hasColor()||getColor().equals(other.getColor()))
		&& (!hasAlign()||getAlign()==other.getAlign())
		&& (!hasFontSize()||getFontSize()==other.getFontSize())
		&& (!hasFontStyle()||getFontStyle()==other.getFontStyle());
	}

	public boolean multiLayout() {
	    Iterator entries = content.iterator();
	    while (entries.hasNext()) {
		Object obj = entries.next();
		if (obj instanceof FormattedText) {
		    FormattedText t = (FormattedText)obj;
		    if (t.multiLayout() || !t.isLayoutCompatible(this))
			return true;
		}
	    }
	    return false;
	}

	public byte[] getBytes(String encoding, boolean useHTML)
	       throws UnsupportedEncodingException, SWFWriterException {
	    StringBuffer buf = new StringBuffer();
	    if (useHTML) {
		collectHTMLContent(buf);
	    } else {
		collectPlainContent(buf);
	    }
	    return buf.toString().getBytes(encoding);
	}

	private void collectPlainContent(StringBuffer buf)
	        throws SWFWriterException {
	    Iterator entries = content.iterator();
	    while (entries.hasNext()) {
		Object obj = entries.next();
		if (obj instanceof StringBuffer) {
		    StringBuffer text = (StringBuffer)obj;
		    getFont().markUsed(text);
		    buf.append((StringBuffer)obj);
		} else ((FormattedText)obj).collectPlainContent(buf);
	    }
	    if (isParagraph()) buf.append('\r');
	}
	
	protected void collectHTMLContent(StringBuffer buf)
	        throws SWFWriterException {
	    Iterator entries = content.iterator();
	    while (entries.hasNext()) {
		Object obj = entries.next();
		if (obj instanceof StringBuffer) {
		    StringBuffer text = (StringBuffer)obj;
		    for (int i=0; i<text.length(); i++) {
			char ch = text.charAt(i);
			// Mark the used characters so they will
			// be included in the font.
			getFont().markUsed(ch);
			switch(ch) {
			case 0xA0: buf.append("&nbsp;"); break;
			case '"':  buf.append("&quot;"); break;
			case '<':  buf.append("&lt;"); break;
			case '>':  buf.append("&gt;"); break;
			case '&':  buf.append("&amp;"); break;
			default:   buf.append(ch); break;
			}
		    }
		} else ((FormattedText)obj).collectHTMLContent(buf);
	    }
	}
    }

    /** Helper class for modeling formatted text below the root node. */
    private class InnerFormattedText extends FormattedText {
	protected void collectHTMLContent(StringBuffer buf)
	        throws SWFWriterException {
	    if (isParagraph()) {
		buf.append("<P ALIGN=\"" + getAlignString() + "\">");
	    }
	    Iterator entries = this.content.iterator();
	    if (entries.hasNext()) {
		boolean insertFont =
		    hasFontName() || hasFontSize() || hasColor();
		if (insertFont) {
		    buf.append("<FONT");
		    if (hasFontName()) {
			buf.append(" FACE=\"")
			    .append(getFontName())
			    .append("\"");
		    }
		    if (hasFontSize()) {
			buf.append(" SIZE=\"")
			    .append(getFontSize() / 20.0)
			    .append("\"");
		    }
		    if (hasColor()) {
			buf.append(" COLOR=\"#")
			    .append(getColor().toHexString())
			    .append("\"");
		    }
		    buf.append(">");
		}
		if (hasFontStyleBold) buf.append("<B>");
		if (hasFontStyleItalic) buf.append("<I>");
		super.collectHTMLContent(buf);
		if (hasFontStyleItalic) buf.append("</I>");
		if (hasFontStyleBold) buf.append("</B>");
		if (insertFont) buf.append("</FONT>");
	    }
	    if (isParagraph()) buf.append("</P>");
	}
    }

    /** Tells if this text should be selectable on the client */
    private boolean isSelectable;

    /** If set, this text includes layout information */
    private boolean hasLayout;

    /** The left margin */
    private int leftMargin;

    /** The right margin */
    private int rightMargin;

    /** The text indentation for the first line */
    private int indent;

    /** The vertical spacing between lines */
    private int lineSpacing;

    /** The variable name for this text */
    private String varName;

    /** The text ID */
    private int id;
    
    /** If set, this text has a visible border line */
    private boolean hasBorder;

    /** The bounding box for this text */
    private SWFRectangle bounds;

    /** The font header for the next text chunk. */
    private FontKey nextFont;

    /** The default font key */
    private static final FontKey DEFAULT_FONT = new FontKey("Times new roman", 0);
    /** The alpha value. By default opaque (i.e. 0xFF) */
    private int alpha;

    /** The content */
    private FormattedText content;
    
    /** The current text chunks */
    private LinkedList textStack = new LinkedList();

    /** Get the current text */
    private FormattedText getCurrentText() {
	return (FormattedText)textStack.getLast();
    }

    public XMLTextHandler() {
	registerElementHandler("P", XMLTextMarkupParHandler.class);
    }
    /** Set the font layout for the current text chunk */
    public int getFontLayout() {
	return getCurrentText().getFontStyle();
    }


    /** Set the font name for the current text chunk */
    public void changeFontName(String name) {
	if (!content.hasFontName()) content.setFontName(name);
	getCurrentText().setFontName(name);
    }

    /** Set the font layout for the current text chunk */
    public void changeFontLayout(int layout) {
	if (!content.hasFontStyle()) content.setFontStyle(layout);
	getCurrentText().setFontStyle(layout);
    }

    /** Set the font size for the current text chunk */
    public void changeFontSize(int size) {
	if (!content.hasFontSize()) content.setFontSize(size);
	getCurrentText().setFontSize(size);
    }

    /** Set the alignment for the current text chunk */
    public void changeAlign(int align) {
	if (!content.hasAlign()) content.setAlign(align);
	getCurrentText().setAlign(align);
    }

    /** Set the color for the current text chunk */
    public void changeColor(SWFColorRGB color) {
	if (!content.hasColor()) content.setColor(color);
	getCurrentText().setColor(color);
    }

    /** Add some text to the current text chunk */
    public void addText(char[] ch, int start, int length)
	    throws SWFWriterException {
	getCurrentText().add(ch, start, length);
    }

    /**
     * Close the current text chunk. The next characters will go on
     * the previous text chunk.
     */
    public void finishCurrentText() {
	textStack.removeLast();
    }

    /**
     * Start a new text chunk if the current text chunk is not empty.
     * @param newline if <code>true</code>, a new text chunk will be
     * started even if the current ext chunk is empty, and the new
     * text chunk will have the {@link FormattedText#isParagraph} flag
     * set.
     */
    public void startNewText(boolean newline) {
	FormattedText newText;
	newText = new InnerFormattedText();
	getCurrentText().add(newText);
	newText.setIsParagraph(newline);
	textStack.addLast(newText);
    }

    /**
     * Start processing a &lt;Text&gt; element. 
     */
    protected void startElement(String name, Attributes attrib)
	      throws SWFWriterException {
	String tmp;

	// Initialize state data
	content = new FormattedText();
	textStack.clear();
	textStack.addLast(content);
	hasLayout = false;
	leftMargin = 0;
	rightMargin = 0;
	indent = 0;
	lineSpacing = 0;
	alpha  = 0xFF;
	try {
	    tmp = attrib.getValue("", "id");
	    if (tmp != null) id = Integer.parseInt(tmp);
	    tmp = attrib.getValue("", "name");
	    if (tmp != null) varName = tmp;
	    else varName = "";
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
	// Flash versions < 5 can't handle embedded HTML.
	// Even with Flash 5, don't use HTML formatting if there is
	// only one text format.
	boolean useHTML =
	    (getSWFWriter().getFlashVersion() >= 5) &&
	    content.multiLayout();
	SWFDefineTextField data = new SWFDefineTextField();
	SWFFont font = content.getFont();
	data.setID(id);
	data.setBounds(bounds);
	data.setSelectable(isSelectable);
	data.setHTML(useHTML);
	data.setHasBorder(hasBorder);
	data.setVarName(varName);
	data.setFontID(font.getFontID());	
	data.setFontHeight(content.getFontSize());
	data.setTextColor(new SWFColorRGBA(content.getColor(), alpha));
	data.setReadonly(true);
	// Text fields can only use provided font outlines if the font
	// contains layout information as well
	data.setUseOutlines(font.hasMetrics());
	try {
	    data.setText(content.getBytes(font.getCanonicalEncodingName(),
					  useHTML));
	} catch (UnsupportedEncodingException e) {
	    fatalError(e);
	}
	if (hasLayout) {
	    data.setTextAlign(content.getAlign());
	    data.setLeftMargin(leftMargin);
	    data.setRightMargin(rightMargin);
	    data.setTextIndent(indent);
	    data.setLineSpacing(lineSpacing);
	}
	
	return data;
    }
}
