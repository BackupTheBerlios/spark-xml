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
 * $Id: XMLKerningHandler.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.publisher;
import de.tivano.flash.swf.common.SWFTypes;
import de.tivano.flash.swf.common.SWFFont;
import de.tivano.flash.swf.common.SWFRectangle;
import de.tivano.flash.swf.common.SWFShape;
import org.xml.sax.Attributes;

/**
 * Handler for the <em>&lt;Kerning /&gt;</em> XML tag.
 * The <em>&lt;Kerning /&gt;</em> tag defines a kerning entry in a
 * <em>&lt;Font&gt;</em> element.
 */
public class XMLKerningHandler extends XMLHandlerBase {
    private String chars;
    private int advance = 0;
    
    public XMLKerningHandler() {}

    /** Get the character pair for this kerning record. */
    public String getChars() { return chars; }

    /**
     * Get the X advance value of this kerning pair.
     */
    public int getAdvance() { return advance; }

    /** Collect information from the XML attributes */
    protected void startElement(java.lang.String name, Attributes attrib)
	      throws SWFWriterException {
	// Paranoia code.
	if (!name.equals("Kerning")) {
	    fatalError("Illegal element for this handler: " + name);
	}
	String tmp;
	chars = attrib.getValue("", "chars");
	tmp = attrib.getValue("", "advance");
	if (tmp!=null) advance = Integer.parseInt(tmp);
    }
}
