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
 * $Id: XMLTextMarkupItalicHandler.java,v 1.1 2001/07/02 08:07:22 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import org.xml.sax.Attributes;
import de.tivano.flash.swf.common.SWFFont;

/**
 * Handler class for XML <em>&lt;P&gt;</em> elements. The containing
 * element <em>must</em> be handled by a {@link XMLTextHandler}.
 * @author Richard Kunze
 * @see de.tivano.flash.swf.parser.SWFAnyTagReader
 */
public class XMLTextMarkupItalicHandler extends XMLTextMarkupHandlerBase {
    /** The old font layout */
    private int oldLayout;

    /** Change the font layout to "italic". */
    protected void startElement(java.lang.String name, Attributes attrib) 
	      throws SWFWriterException {
	XMLTextHandler textHandler = getTextHandler();
	oldLayout = textHandler.getNextFontLayout();
	textHandler.setNextFontLayout(oldLayout | SWFFont.ITALIC);
    }

    /** Reset the old font layout */
    protected void endElement() throws SWFWriterException {
	getTextHandler().setNextFontLayout(oldLayout);
    }
}
