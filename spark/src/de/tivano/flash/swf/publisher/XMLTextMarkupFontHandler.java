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
 * $Id: XMLTextMarkupFontHandler.java,v 1.3 2001/07/03 16:41:05 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import org.xml.sax.Attributes;
import de.tivano.flash.swf.common.SWFColorRGB;

/**
 * Handler class for XML <em>&lt;FONT&gt;</em> elements. The containing
 * element <em>must</em> be handled by either a {@link XMLTextHandler}
 * or a {@link XMLTextMarkupBaseHandler}.
 * @Author Richard Kunze
 * @see de.tivano.flash.swf.parser.SWFAnyTagReader
 */
public class XMLTextMarkupFontHandler extends XMLTextMarkupHandlerBase {
    /** Set the font name, font size and text color according to
     * <code>attrib</code>. */
    protected void startElement(java.lang.String name, Attributes attrib) 
	      throws SWFWriterException {
	XMLTextHandler textHandler = getTextHandler();
	textHandler.startNewText(false);
	String tmp = attrib.getValue("", "FACE");
	if (tmp != null) textHandler.changeFontName(tmp);
	try {
	    tmp = attrib.getValue("", "SIZE");
	    // Convert the font size from points to TWIPS
	    // (i.e. multiply by twenty)
	    if (tmp != null) {
		textHandler.changeFontSize(
			   Math.round(Float.parseFloat(tmp)*20.0F));
	    }
	} catch (NumberFormatException e) {
	    fatalError("Not a legal font size: " + tmp);
	}
	try {
	    tmp = attrib.getValue("", "COLOR");
	    if (tmp != null) {
		textHandler.changeColor(new SWFColorRGB(tmp.substring(1)));
	    }
	} catch (IllegalArgumentException e) {
	    fatalError("Not a legal color value: " + tmp);
	}
    }

    /** Reset the text alignment to the previous value */
    protected void endElement() throws SWFWriterException {
	getTextHandler().finishCurrentText();
    }
}
