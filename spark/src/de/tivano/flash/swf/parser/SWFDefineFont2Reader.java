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
 * $Id: SWFDefineFont2Reader.java,v 1.4 2001/05/30 16:23:16 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import org.xml.sax.SAXException;

import java.io.IOException;

import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.SWFDefineFont2;
import de.tivano.flash.swf.common.SWFFont;
import de.tivano.flash.swf.common.SWFRectangle;
import de.tivano.flash.swf.common.BitInputStream;

/**
 * Tag reader for SWF DefineFont2 tags.
 * It will emit the following XML snippet:
 * <pre>
 * &lt;Font id="<em>id</em>" name="<em>font name</em>" style="<em>style</em> encoding="<em>encoding</em>"
 *          ascent="<em>ascent</em>" descent="<em>descent</em>" leading="<em>leading height</em>"&gt;
 *   &lt;!-- One or more glyph specifications --&gt;
 *   &lt;Glyph char="<em>char code</em>" advance="<em>advance</em>"
 *             width="<em>width</em>" height="<em>height</em>"&gt;
 *      &lt;ShapeRaw&gt;
 *        &lt;!-- Raw Shape data. See {@link SWFRawShapeReader} --&gt;
 *      &lt;/ShapeRaw&gt;
 *   &lt;/Glyph&gt;
 *   &lt;!-- 0 or more kerning specifications --&gt;
 *   &lt;Kerning chars="<em>char pair</em>" advance="<em>advance</em>"/&gt;
 * &lt;/Font&gt;
 * </pre>
 * <p>All of the font attributes except <em>id</em> are
 * optional. Default values for missing attributes are the empty
 * string for <em>name</em> and 0 for all other attributes except
 * <em>style</em> and <em>encoding</em>.</p>
 * <p>The attribute <em>style</em> has one of the values "standard",
 * "italic", "bold" or "bold-italic". Assumed default if missing is
 * "standard".</p> 
 * <p>The attribute <em>encoding</em> holds one of the values "ansi",
 * "unicode" or "shift-jis", assumed default is "ansi". Note that this
 * attribute describes the preferred font enconding in SWF, not in the
 * XML data. The <em>char</em> attribute of <em>&lt;Glyph&gt;</em> as
 * well as all texts are encoded in the XML document's character
 * encoding.</p>
 * <p>The <em>advance</em> attribute of <em>&lt;Glyph&gt;</em> holds
 * the X position for the origin of the next glyph, relative to this
 * glyphs origin.</p>
 * <p>The <em>chars</em> attribute of <em>&lt;Kerning&gt;</em> holds the
 * character pair for the kerning adjustment (e.g "ff"). The
 * <em>advance</em> attribute overrides the corresponding attribute of
 * <em>&lt;Glyph&gt;</em> for this particular character pair.</p>
 * 
 * <p>The font information is stored in the context map (see
 * {@link #getContextMap}) as a {@link SWFFont}
 * instance indexed by its font ID.</p>
 * @author Richard Kunze
 */
public class SWFDefineFont2Reader extends SWFTagReaderBase {
    /** Helper class for converting SWF shape structures to XML */
    protected SWFShapeReader shapeReader;


    /** Constructor. Sets the embedded shape handler */
    public SWFDefineFont2Reader() {
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

	SWFDefineFont2 fontTag = new SWFDefineFont2(input);
	SWFFont font = new SWFFont();
	font.setFontID(fontTag.getID());
	font.setFontName(fontTag.getName());
	getContextMap().put(new Integer(fontTag.getID()), font);

	boolean hasGlyphLayout = fontTag.hasGlyphLayout();
	
	String style = null;
	switch (fontTag.getLayout()) {
	case SWFFont.BOLD:  style = "bold"; break;
	case SWFFont.ITALIC: style = "italic"; break;
	case SWFFont.BOLD | SWFFont.ITALIC: style = "bold-italic"; break;
	}
	
	String encoding = null;
	switch (fontTag.getEncoding()) {
	case SWFFont.UNICODE: encoding = "unicode"; break;
	case SWFFont.SHIFT_JIS: encoding = "shift-jis"; break;
	}
	font.setEncoding(fontTag.getEncoding());
	
	SWFAttributes attrib = createAttributes();
	attrib.addAttribute("id", fontTag.getID(), SWFAttributes.TYPE_ID);
	attrib.addAttribute("name", fontTag.getName());
	if (style != null) {
	    attrib.addAttribute("style", style);
	}
	if (encoding != null) {
	    attrib.addAttribute("encoding", encoding);
	}
	if (hasGlyphLayout) {
	    attrib.addAttribute("ascent", fontTag.getAscent());
	    attrib.addAttribute("descent", fontTag.getDescent());
	    attrib.addAttribute("leading", fontTag.getLeadingHeight());
	}
	startElement("Font", attrib);

	for (int i=0; i<fontTag.getGlyphCount(); i++) {
	    String charcode = font.decode(fontTag.getCode(i));
	    int advance = fontTag.getAdvance(i);
	    font.addGlyph(charcode, fontTag.getAdvance(i));
	    attrib.clear();
	    attrib.addAttribute("char", charcode);
	    if (hasGlyphLayout) {
		attrib.addAttribute("advance", advance);
	    }
	    if (hasGlyphLayout) {
		SWFRectangle bounds = fontTag.getBounds(i);
		attrib.addAttribute("xmin",  bounds.getXMin());
		attrib.addAttribute("ymin",  bounds.getYMin());
		attrib.addAttribute("xmax",  bounds.getXMax());
		attrib.addAttribute("ymax",  bounds.getYMax());
	    }
	    startElement("Glyph", attrib);
	    shapeReader.toXML(fontTag.getShape(i));
	    endElement("Glyph");
	}
	for (int i=0; i<fontTag.getKerningCount(); i++) {
	    SWFDefineFont2.KerningRecord entry = fontTag.getKerningRecord(i);
	    attrib.clear();
	    attrib.addAttribute("chars",
				font.decode(entry.CHAR_1) +
				font.decode(entry.CHAR_2));
	    attrib.addAttribute("advance", entry.KERNING);
	    emptyElement("Kerning", attrib);
	}
	endElement("Font");
    }
}
