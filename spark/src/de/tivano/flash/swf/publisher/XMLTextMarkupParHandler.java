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
 * $Id: XMLTextMarkupParHandler.java,v 1.4 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.publisher;
import org.xml.sax.Attributes;
import de.tivano.flash.swf.common.SWFDefineTextField;

/**
 * Handler class for XML <em>&lt;P&gt;</em> elements. The containing
 * element <em>must</em> be handled by a {@link XMLTextHandler}.
 * @author Richard Kunze
 * @see de.tivano.flash.swf.parser.SWFAnyTagReader
 */
public class XMLTextMarkupParHandler extends XMLTextMarkupHandlerBase {
    /** The old alignment */
    private int oldAlign;

    /** Set the alignment for this paragraph */
    protected void startElement(java.lang.String name, Attributes attrib) 
	      throws SWFWriterException {
	XMLTextHandler textHandler = getTextHandler();
	String tmp = attrib.getValue("", "ALIGN");
	int align;
	if ("JUSTIFY".equals(tmp)) align = SWFDefineTextField.ALIGN_JUSTIFY;
	else if ("CENTER".equals(tmp)) align = SWFDefineTextField.ALIGN_CENTER;
	else if ("RIGHT".equals(tmp)) align = SWFDefineTextField.ALIGN_RIGHT;
	else align = SWFDefineTextField.ALIGN_LEFT;
	textHandler.startNewText(true);
	textHandler.changeAlign(align);
    }

    /** Reset the text alignment to the previous value */
    protected void endElement() throws SWFWriterException {
	getTextHandler().finishCurrentText();
    }
}
