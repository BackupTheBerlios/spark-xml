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
 * $Id: SWFDefineTextFieldReader.java,v 1.4 2001/07/02 19:10:55 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.SWFDefineTextField;
import de.tivano.flash.swf.common.SWFRectangle;
import de.tivano.flash.swf.common.SWFFont;
import de.tivano.flash.swf.common.SWFColorRGBA;
import de.tivano.flash.swf.common.BitInputStream;

/**
 * Tag reader for SWF DefineTextField tags.
 * <p>If the text field is editable, it will emit the following XML
 * snippet:</p>
 * <pre>
 * &lt;TextInput id="<em>id</em>" name="<em>field name</em>" maxlength="<em>length</em>"
 *               multiline="<em>yes|no</em>" password="<em>yes|no</em>" wordwrap="<em>yes|no</em>"&gt;
 *   &lt;Text xmin="<em>xmin</em>" xmax="<em>xmax</em>"& ymin="<em>ymin</em>" ymax="<em>ymax</em>"
 *            border="<em>yes|no</em>" selectable="<em>yes|no</em>"
 *            alpha="<em>alpha value</em>" indent="<em>first line indent</em>"
 *            leftmargin="<em>left margin</em>" rightmargin="<em>right margin"
 *            linespacing="<em>vertical distance between lines</em>"&gt;
 *     &gt;!-- Text data, may contain some markup tags. Example: -->
 *     &lt;p align="left"&gt;Text with &lt;i&gt;markup&lt;/i&gt;&lt;/p&gt;
 *   &lt;/Text&gt;
 * &lt;/TextInput&gt;
 * </pre>
 * <p>If the text field is not editable, the XML is basically the
 * same, only the &lt;TextInput&gt; is omitted. In this case, the
 * &lt;Text&gt; tag has the additional attributes <em>id</em> and
 * <em>name</em>.</p>
 * <p>All attributes are optional. Assumed defaults are empty for
 * <em>id</em> and <em>name</em>, "no" for all boolean and 0 for all
 * numeric attributes except <em>alpha</em>, where the default is
 * "#ff", i.e. no transparency.</p>
 * <p>The actual text data may contain the following markup tags:
 * &ltp&gt;, &lt;font&gt;, &lt;b&gt; &lt;i&gt, &lt;span&gt;,
 * &lt;hspace/&gt; and &lt;vspace/&gt;. The emtpy tags &lt;hspace/&gt;
 * and &lt;vspace/&gt; both have a mandatory attribute <em>value</em>
 * defining the horizontal space to the next character and the
 * vertical space to the next line, respectively. &lt;vspace/&gt; can
 * only occur directly behind a &lt;p&gt; tag. Semantics and allowed
 * attributes for all other markup tags are the same as in HTML.</p>
 *
 * <p>This class assumes that the font associated with the text field is
 * stored in the context map (see {@link #getContextMap}) as a
 * {@link SWFFont} object, indexed by the numerical font ID.</p>
 * @author Richard Kunze
 */
public class SWFDefineTextFieldReader extends SWFTagReaderBase {

    /**
     * Map of HTML entity names to character arrays.
     * Used in parsing the embedded HTML in Flash 5 text fields
     */
    private final static Map ENTITIES = new HashMap();

    /** Set up the entity map. */
    static {
	// XXX: If you change this map, don't forget to update the reverse
	// mapping in XMLTextHandler aus well.
	ENTITIES.put("nbsp", new char[] { 0xA0 });
	ENTITIES.put("lt", new char[] { '<' });
	ENTITIES.put("gt", new char[] { '>' });
	ENTITIES.put("amp", new char[] { '&' });
	ENTITIES.put("quot", new char[] { '"' });
    }

    /** Default character value for unknown entities */
    private static final char[] UNKNOWN_ENTITY = { 0x25AF };

    /** Attributes to use while parsing */
    private SWFAttributes pAttr    = createAttributes();
    private SWFAttributes fontAttr = createAttributes();
    private SWFAttributes htmlAttr = createAttributes();
    SWFAttributes attrib = createAttributes();
    
    /**
     * Read the tag content.
     * @param input the SWF data stream
     * @param header the record header for this record
     */
    public void parse(BitInputStream input, SWFTagHeader header)
                throws SAXException, IOException {
	SWFDefineTextField textField = new SWFDefineTextField(input);
	String encoding =
	    SWFFont.getCanonicalEncodingName(SWFFont.ANSI);
	SWFFont font = null;
	attrib.clear();
	if (textField.hasFont()) {
	    font = (SWFFont)getContextMap().get(
				  new Integer(textField.getFontID()));
	    if (font != null) {
		encoding = font.getCanonicalEncodingName();
	    } else {
		error("No context information for font " +
		      textField.getFontID() + ". Reverting to " +
		      encoding + " encoding for this text.");
	    }
	} else {
	    warning("Text field with no associated font found. Assuming " +
		    encoding + " encoding for this text.");
	}
	attrib.addAttribute("id", textField.getID());
	if (textField.getVarName() != null)
	    attrib.addAttribute("name", textField.getVarName());
	if (!textField.isReadonly()) {
	    if (textField.isMultiline())
		attrib.addAttribute("multiline", "yes");
	    if (textField.isPassword())
		attrib.addAttribute("password", "yes");
	    if (textField.useWordWrap())
		attrib.addAttribute("wordwrap", "yes");
	    if (textField.getMaxLength() != -1)
		attrib.addAttribute("maxlength",
				    textField.getMaxLength());
	    startElement("TextInput", attrib);
	    attrib.clear();
	}
	SWFRectangle bounds = textField.getBounds();
	SWFColorRGBA color  = textField.getTextColor();
	attrib.addAttribute("xmin", bounds.getXMin());
	attrib.addAttribute("xmax", bounds.getXMax());
	attrib.addAttribute("ymin", bounds.getYMin());
	attrib.addAttribute("ymax", bounds.getYMax());
	if (textField.hasBorder()) attrib.addAttribute("border", "yes");
	if (textField.isSelectable())
	    attrib.addAttribute("selectable", "yes");
	if (color.getAlpha() != 0xFF) {
	    attrib.addAttribute("alpha", "#" +
				Integer.toString(color.getAlpha(), 16));
	}
	if (textField.hasLayout()) {
	    attrib.addAttribute("leftmargin",
				fromTwips(textField.getLeftMargin()));
	    attrib.addAttribute("rightmargin",
				fromTwips(textField.getRightMargin()));
	    attrib.addAttribute("indent",
				fromTwips(textField.getTextIndent()));
	    attrib.addAttribute("linespacing",
				fromTwips(textField.getLineSpacing()));
	}
	startElement("Text", attrib);
	char[] text =
	    (new String(textField.getText(), encoding)).toCharArray();
	if (textField.isHTML()) {
	    handleHTMLText(text);
	} else {
	    String fontName = null;
	    int fontLayout  = 0;
	    int fontSize    = 12;
	    if (font != null) {
		fontName   = font.getFontName();
		fontLayout = font.getLayout();
	    }
	    handlePlainText(text, textField.getTextAlign(),
			    fontName, fontLayout,
			    fromTwips(textField.getFontHeight()),
			    color);
	}
	endElement("Text");
	if (!textField.isReadonly()) endElement("TextInput");
    }

    /**
     * Send the XML for plain text with the given layout information.
     * @param text the actual data
     * @param align the text alignment. One of the alignment constants
     * defined in {@link SWFDefineTextField}.
     * @param the font to use. May be <code>null</code>
     * @param layout the font layout (bold, italic). A combination of
     * the layout constants defined in {@link SWFFont}
     * @param color the text color
     * @param size the font size (in points)
     */
    protected void handlePlainText(char[] text, int align, String font,
				   int layout, double size,
				   SWFColorRGBA color)
	           throws SAXException {
	pAttr.clear();
	fontAttr.clear();
	switch (align) {
	case SWFDefineTextField.ALIGN_LEFT:
	    pAttr.addAttribute("ALIGN", "LEFT"); break;
	case SWFDefineTextField.ALIGN_RIGHT:
	    pAttr.addAttribute("ALIGN", "RIGHT"); break;
	case SWFDefineTextField.ALIGN_CENTER:
	    pAttr.addAttribute("ALIGN", "CENTER"); break;
	case SWFDefineTextField.ALIGN_JUSTIFY:
	    pAttr.addAttribute("ALIGN", "JUSTIFY"); break;
	default:
	    warning("Unknown text alignment. Defaulting to \"LEFT\"");
	    pAttr.addAttribute("ALIGN", "LEFT");
	    break;
	}

	if (font != null) fontAttr.addAttribute("FACE", font);
	fontAttr.addAttribute("SIZE", size);
	fontAttr.addAttribute("COLOR", "#" +
			      color.toHexString(false).toUpperCase());

	// Encode every line of text as a single paragraph.
	int start = 0;
	int end = start;
	do {
	    startElement("P", pAttr);
	    while (end < text.length && text[end] != '\r') end++;
	    end++;
	    if (start < end) {
		startElement("FONT", fontAttr);
		if ((layout & SWFFont.BOLD) != 0) startElement("B", null);
		if ((layout & SWFFont.ITALIC) != 0) startElement("I", null);
		handleGeneratorVariables(text, start, end-1);
		if ((layout & SWFFont.ITALIC) != 0) endElement("I");
		if ((layout & SWFFont.BOLD) != 0) endElement("B");
		endElement("FONT");
	    }
	    endElement("P");
	    start = end;
	} while (end < text.length);
    }

    /**
     * Send XML for text with layout information embedded as a subset
     * of HTML.
     * @param text the data
     */
    protected void handleHTMLText(char[] text)
	           throws SAXException {
	int start = 0;
	int pos = start;

	// XXX: this is a *very* simplistic parser. Assumes that all embedded
	// tags in SWF files are nested correctly and that there are
	// no empty tags. If this assumption is violated, it will
	// produce invalid XML.
	while (pos < text.length) {
	    switch (text[pos]) {
	    case '<':
		handleGeneratorVariables(text, start, pos);
		start = handleHTMLTag(text, pos) + 1;
		pos = start;
		break;
	    case '&':
		handleGeneratorVariables(text, start, pos++);
		int end = pos;
		try {
		    while (text[end] != ';') end++;
		} catch (IndexOutOfBoundsException e) {
		    // No final ";" found. Assume an unescaped "&" and
		    // send it on as a normal character...
		    warning("Unescaped '&' found.");
		    start = pos - 1;
		    continue;
		}
		char[] entity =
		    (char[])ENTITIES.get(new String(text, pos, end-pos));
		if (entity == null) {
		    warning("Unknow character entity &" +
			    new String(text, pos, end-pos-1) +
			    "; found. Substituting " + UNKNOWN_ENTITY);
		    entity = UNKNOWN_ENTITY;
		}
		characters(entity, 0, entity.length);
		start = end + 1;
		pos = start;
		break;
	    default:
		pos++;
		break;
	    }
	}
	handleGeneratorVariables(text, start, pos);
    }

    /**
     * Send the appropriate XML for a single embedded start or end tag
     * @param buffer the character buffer
     * @param start position of the initial '<' for this tag
     * @return the position of the final '>' for this tag. In case of
     * errors, start-1 is returned.
     */
    protected int handleHTMLTag(char[] buffer, int start) throws SAXException {
	int pos = start+1;
	int end = pos;
	try {
	    if (buffer[pos] == '/') {
		pos++;
		while (buffer[end] != '>') end++;
		endElement(new String(buffer, pos, end-pos));
		return end;
	    } else {
		htmlAttr.clear();
		while (buffer[end] != ' ' && buffer[end] != '>') end++;
		String tagName = new String(buffer, pos, end-pos);
		pos = end;
		while (buffer[pos] == ' ') pos++;
		end = pos;
		while (buffer[end] != '>') {
		    while (buffer[end] != '=') end++;
		    String attName = new String(buffer, pos, end-pos);
		    // XXX: I'm assuming there are no character
		    // entities embedded in attribute values....
		    end+=2;
		    pos = end;
		    while (buffer[end] != '"') end++;
		    String attValue = new String(buffer, pos, end-pos);
		    htmlAttr.addAttribute(attName, attValue);
		    end++;
		    pos = end;
		    while (buffer[pos] == ' ') pos++;
		    end = pos;
		}
		startElement(tagName, htmlAttr);
		return end;
	    }
	    
	} catch (IndexOutOfBoundsException e) {
	    warning("Illegal syntax for embedded HTML tag: \"" +
		    new String(buffer, start, buffer.length - start) +
		    "\". Assuming unescaped '<'.");
	    return start-1;
	}
    }

    /**
     * Scan a character buffer for generator variables and send the
     * appropriate XML.
     * A "generator variable" is nothing more than an arbitrary text
     * enclosed in "{}". This method scans <code>text</code> for
     * generator variables and encloses them in &lt;span&gt; tags,
     * with the ID attribute set to the variable name. Example: The
     * text <code>"This is a {variable}"</code> will be sent to the
     * client application as 
     * <pre>This is a &lt;span
     * id="variable"&gt;{variable}&lt;/span&gt;</pre>
     * @param buffer the character array to scan
     * @param start the first index to look at
     * @param end the last index to look at
     */
    protected void handleGeneratorVariables(char[] buffer, int start, int end)
	           throws SAXException {
	SWFAttributes attrib = createAttributes();
	int pos = start;
	while (start < end) {
	    while (pos < end && buffer[pos] != '{') pos++;
	    if (pos!=start) characters(buffer, start, pos-start);
	    start = pos + 1;
	    if (pos < end) {
		while (pos < end && buffer[pos] != '}') pos++;
		if (buffer[pos] != '}') {
		    warning("Unmatched \"{\" found. " +
			    "Skipping generator variable.");
		    characters(buffer, start, pos-start);
		} else {
		    attrib.clear();
		    attrib.addAttribute("id",
					new String(buffer, start,
						   pos-start));
		    startElement("span", attrib);
		    characters(buffer, start-1, pos-start+2);
		    endElement("span");
		}
		start = pos + 1;
	    }
	}
    }
}
