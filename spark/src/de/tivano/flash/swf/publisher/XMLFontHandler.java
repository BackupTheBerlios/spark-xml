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
 * $Id: XMLFontHandler.java,v 1.6 2001/07/04 08:37:05 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import de.tivano.flash.swf.common.SWFTypes;
import de.tivano.flash.swf.common.SWFFont;
import de.tivano.flash.swf.common.SWFRectangle;
import de.tivano.flash.swf.common.SWFShape;
import org.xml.sax.Attributes;
import java.io.UnsupportedEncodingException;

/**
 * Handler for the <em>&lt;Font&gt;</em> XML tag.
 * The <em>&lt;Font&gt;</em> tag defines a font which is expressed in
 * SWF as either a DefineFont2 structure or a
 * DefineFont/DefineFontInfo pair.
 * @see de.tivano.flash.swf.parser.SWFDefineFont2Reader
 * @see de.tivano.flash.swf.parser.SWFDefineFontReader
 * @see de.tivano.flash.swf.parser.SWFDefineFontInfoReader
 */
public class XMLFontHandler extends SWFTagHandlerBase {

    /** The font object to build */
    SWFFont font;

    /** the font writer for this font */
    SWFFontWriter fontWriter;
    
    /**
     * Construct a new <code>XMLFontHandler</code>.
     */
    public XMLFontHandler() {
	registerElementHandler("Glyph", XMLGlyphHandler.class);
	registerElementHandler("Kerning", XMLKerningHandler.class);
    }
    
    /**
     * Collect the results from the child <em>&lt;Glyph&gt;</em>
     * element handlers
     */
    protected void notify(java.lang.String element, XMLHandlerBase handler)
	      throws SWFWriterException {
	if (handler instanceof XMLKerningHandler) {
	    try {
		XMLKerningHandler kh = (XMLKerningHandler)handler;
		font.addKerningInfo(kh.getChars(), kh.getAdvance());
	    } catch (UnsupportedEncodingException e) {
		fatalError(e);
	    }
	} else {
	    XMLGlyphHandler gh = (XMLGlyphHandler)handler;
	    // XXX: Mark glyphs here if we write a SWT (i.e. generator
	    // template) file. In a generator template, all characters are
	    // included in a font, regardless of wheter they are used by
	    // texts or not.
	    SWFRectangle bounds = gh.getBounds();
	    if (bounds != null) {
		font.addGlyph(gh.getChar(), gh.getAdvance(),
			      bounds, gh.getShape(), true);
	    } else {
		font.addGlyph(gh.getChar(), gh.getShape(), true);
	    }
	}
    }

    /**
     * Start processing a &lt;Font&gt; element.
     * This method mainly collects information from the XML attributes
     */
    protected void startElement(java.lang.String name, Attributes attrib)
	      throws SWFWriterException {
	// Paranoia code.
	if (!name.equals("Font")) {
	    fatalError("Illegal element for this handler: " + name);
	}
	font = new SWFFont();
	fontWriter = new SWFFontWriter(font);
    

	// Get the attributes.
	String tmp;
	tmp = attrib.getValue("", "name");
	if (tmp!=null) font.setFontName(tmp);
	else fatalError("No font name specified");
	try {
	    tmp = attrib.getValue("", "id");
	    font.setFontID(Integer.parseInt(tmp));
	} catch (Exception e) {
	    fatalError("Illegal font id: " + tmp);
	}
	tmp = attrib.getValue("", "style");
	if ("bold".equals(tmp)) {
	    font.setLayout(SWFFont.BOLD);
	} else if ("bold-italic".equals(tmp)) {
	    font.setLayout(SWFFont.BOLD|SWFFont.ITALIC);
	} else if ("italic".equals(tmp)) {
	    font.setLayout(SWFFont.ITALIC);
	} else {
	    font.setLayout(0);
	}
	tmp = attrib.getValue("", "encoding");
	if ("unicode".equals(tmp)) {
	    font.setEncoding(SWFFont.UNICODE);
	} else if ("shift-jis".equals(tmp)) {
	    font.setEncoding(SWFFont.SHIFT_JIS);
	} else {
	    font.setEncoding(SWFFont.ANSI);
	}
	try {
	    tmp = attrib.getValue("", "ascent");
	    if (tmp!=null) font.setAscent(Integer.parseInt(tmp));
	} catch (Exception e) {
	    fatalError("Illegal font ascent value: " + tmp);
	}
	try {
	    tmp = attrib.getValue("", "descent");
	    if (tmp!=null) font.setDescent(Integer.parseInt(tmp));
	} catch (Exception e) {
	    fatalError("Illegal font descent value: " + tmp);
	}
	try {
	    tmp = attrib.getValue("", "leading");
	    if (tmp!=null) font.setLeading(Integer.parseInt(tmp));
	} catch (Exception e) {
	    fatalError("Illegal font leading height value: " + tmp);
	}
    }
    
    /**
     * Finish processing a &lt;Font&gt; element.
     * The SWF font described by this element is put in the context
     * map of the associated {@link SWFWriter} and can be retrieved
     * with a matching {@link FontKey}
     */
    protected void endElement() throws SWFWriterException {
	FontKey key = new FontKey(font.getFontName(),
				  font.getLayout());
	getSWFWriter().getContextMap().put(key, font);
	super.endElement();
    }
    
    /** @see SWFTagHandlerBase#getDataObjects */
    protected SWFTagWriter createDataObject() {
	return fontWriter;
    }
}
