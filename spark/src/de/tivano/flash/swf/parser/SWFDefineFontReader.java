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
 * $Id: SWFDefineFontReader.java,v 1.2 2001/05/30 16:23:16 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import org.xml.sax.SAXException;

import java.io.IOException;

import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.SWFDefineFont;
import de.tivano.flash.swf.common.SWFDefineFontInfo;
import de.tivano.flash.swf.common.SWFFont;
import de.tivano.flash.swf.common.SWFRectangle;
import de.tivano.flash.swf.common.BitInputStream;

/**
 * Tag reader for SWF DefineFont tags.
 * If this is immediately followed by a DefineFontInfo SWF tag, it
 * will emit the following XML snippet: 
 * <pre>
 * &lt;Font id="<em>id</em>" name="<em>font name</em>" style="<em>style</em> encoding="<em>encoding</em>""&gt;
 *   &lt;!-- One or more glyph specifications --&gt;
 *   &lt;Glyph char="<em>char code</em>""&gt;
 *      &lt;ShapeRaw&gt;
 *        &lt;!-- Raw Shape data. See {@link SWFRawShapeReader} --&gt;
 *      &lt;/ShapeRaw&gt;
 *   &lt;/Glyph&gt;
 * &lt;/Font&gt;
 * </pre>
 * <p>All of the font attributes except <em>id</em> are
 * optional.</p>
 * <p>The attribute <em>style</em> has one of the values "standard",
 * "italic", "bold" or "bold-italic". Assumed default if missing is
 * "standard".</p> 
 * <p>The attribute <em>encoding</em> holds one of the values "ansi",
 * "unicode" or "shift-jis", assumed default is "ansi". Note that this
 * attribute describes the preferred font enconding in SWF, not in the
 * XML data. The <em>char</em> attribute of <em>&lt;Glyph&gt;</em> as
 * well as all texts are encoded in the XML document's character
 * encoding.</p>
 * <p>If this tag is not immediately followed by a DefineFontInfo SWF
 * tag, it emits this XML snippet:
 * <pre>
 * &lt;FontOutlines id="<em>id</em>"&gt;
 *   &lt;!-- One or more shape specifications --&gt;
 *   &lt;ShapeRaw&gt;
 *     &lt;!-- Raw Shape data. See {@link SWFRawShapeReader} --&gt;
 *   &lt;/ShapeRaw&gt;
 * &lt;/FontOutlines&gt;
 * </pre>
 *
 * 
 * <p>The font information is stored in the context map (see
 * {@link #getContextMap}) as a {@link SWFFont} instance indexed by
 * its font ID.</p>
 * @author Richard Kunze
 */
public class SWFDefineFontReader extends SWFTagReaderBase {
    /** Helper class for converting SWF shape structures to XML */
    protected SWFShapeReader shapeReader;

    /** Constructor. Sets the embedded shape handler */
    public SWFDefineFontReader() {
	shapeReader = new SWFRawShapeReader();
    }

    /** @see SWFTagReaderBase#setSAXDriver */
    public void setSAXDriver(SWFReader driver) {
	super.setSAXDriver(driver);
	shapeReader.setSAXDriver(driver);
    }
	
    /**
     * Read the tag content.
     * @param input the SWF data stream
     * @param header the record header for this record
     */
    public void parse(BitInputStream input, SWFTagHeader header)
                throws SAXException, IOException {

	SWFDefineFont fontTag = new SWFDefineFont(input);
	SWFDefineFontInfo fontInfo = null;
	SWFFont font = new SWFFont();
	font.setFontID(fontTag.getID());
	getContextMap().put(new Integer(fontTag.getID()), font);
	
	// Check if we have a DefineFontInfo immediately following
	input.mark(SWFTagHeader.MAX_LENGTH + 2);
	SWFTagHeader nextHeader = new SWFTagHeader(input);
	SWFAttributes attrib = createAttributes();
	int fontInfoID = -1;
	if (nextHeader.getID() == SWFDefineFontInfo.TAG_TYPE) {
	    // Read the font info ID
	    fontInfoID = input.readUW16LSB();
	}

	if (fontInfoID == fontTag.getID()) {
	    // OK, we have matching font info. Read the SWF structur and send
	    // a <Font> tag with the combined information.
	    fontInfo = new SWFDefineFontInfo(input,
					     fontTag.getGlyphCount(),
					     fontInfoID);
	    String style = null;
	    switch (fontInfo.getLayout()) {
	    case SWFFont.BOLD:  style = "bold"; break;
	    case SWFFont.ITALIC: style = "italic"; break;
	    case SWFFont.BOLD | SWFFont.ITALIC: style = "bold-italic"; break;
	    }
	
	    String encoding = null;
	    switch (fontInfo.getEncoding()) {
	    case SWFFont.UNICODE: encoding = "unicode"; break;
	    case SWFFont.SHIFT_JIS: encoding = "shift-jis"; break;
	    }
	    font.setEncoding(fontInfo.getEncoding());
	    font.setFontName(fontInfo.getName());
	
	    attrib.addAttribute("id", fontInfoID, SWFAttributes.TYPE_ID);
	    attrib.addAttribute("name", fontInfo.getName());
	    if (style != null) {
		attrib.addAttribute("style", style);
	    }
	    if (encoding != null) {
		attrib.addAttribute("encoding", encoding);
	    }
	    startElement("Font", attrib);
	    
	    for (int i=0; i<fontTag.getGlyphCount(); i++) {
		String charcode = font.decode(fontInfo.getCode(i));
		font.addGlyph(charcode);
		attrib.clear();
		attrib.addAttribute("char", charcode);
		startElement("Glyph", attrib);
		shapeReader.toXML(fontTag.getShape(i));
		endElement("Glyph");
	    }
	    endElement("Font");
	} else {
	    // No matching font info. Send a <FontOutlines> tag.
	    input.reset();
	    attrib.addAttribute("id", fontTag.getID(), SWFAttributes.TYPE_ID);
	    startElement("FontOutlines", attrib);
	    for (int i=0; i<fontTag.getGlyphCount(); i++) {
		// Add an empty Glyph. Needed for use by the
		// DefineFontInfoReader handling the corresponding
		// DefineFontInfo SWF tag
		font.addGlyph();
		shapeReader.toXML(fontTag.getShape(i));
	    }
	    endElement("FontOutlines");
	}
    }
}
