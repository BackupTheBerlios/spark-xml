/**
 * The contents of this file are subject to the Spark Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the
 * License on the Spark web site
 * (http://www.tivano.de/opensource/flash). 
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License. 
 *
 * The Initial Developer of Spark is Tivano Software GmbH. The
 * original Spark and portions created by Tivano Software GmbH are
 * Copyright Tivano Software GmbH. All Rights Reserved. 
 *
 * Contributor(s):
 *      Richard Kunze, Tivano Software GmbH.
 *
 * $Id: SWFDefineFontInfoReader.java,v 1.2 2002/05/22 17:11:17 richard Exp $
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
 * Tag reader for SWF DefineFontInfo tags without an immediately
 * preceding DefineFont tag.
 * <p>This class produces the following XML snippet:</p>
 * <pre>
 * &lt;Font id="<em>id</em>" name="<em>font name</em>" style="<em>style</em> encoding="<em>encoding</em>""&gt;
 *   &lt;!-- One or more glyph specifications --&gt;
 *   &lt;Glyph char="<em>char code</em>" /"&gt;
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
 *
 * <p>This class assumes that the font information is already stored
 * in the context map (see {@link #getContextMap}) as a {@link
 * SWFFont} instance indexed by its font ID.</p>
 *
 * <p><em>Author's not:</em> I'm not sure if this tag reader is ever
 * used in a real life situation. In all SWF files I've seen so far,
 * DefineFontInfo tags immediately follow the corresponding DefineFont
 * tags and get handled by {@link SWFDefineFontReader}.</p>
 * @author Richard Kunze
 */
public class SWFDefineFontInfoReader extends SWFTagReaderBase {

    /**
     * Read the tag content.
     * @param input the SWF data stream
     * @param header the record header for this record
     */
    public void parse(BitInputStream input, SWFTagHeader header)
                throws SAXException, IOException {

	SWFFont font = null;
	// Need to read the font ID by hand to look up the
	// corresponding font in the context map...
	int fontInfoID = input.readUW16LSB();
	try {
	    font = (SWFFont)getContextMap().get(new Integer(fontInfoID));
	} catch (ClassCastException e) {
	    // Ignore, it's handled later by the font == null check
	}

	if (font == null) {
	    fatalError("No font with ID " + fontInfoID +
		       " found in context map.");
	    return;
	}
	SWFDefineFontInfo fontInfo =
	    new SWFDefineFontInfo(input, font.glyphCount(), fontInfoID);
	
	SWFAttributes attrib = createAttributes();
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
	    
	for (int i=0; i<fontInfo.getGlyphCount(); i++) {
	    String charcode = font.decode(fontInfo.getCode(i));
	    font.setCharCode(i, charcode);
	    attrib.clear();
	    attrib.addAttribute("char", charcode);
	    emptyElement("Glyph", attrib);
	}
	endElement("Font");
    }
}
