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
 * $Id: XMLTextMarkupHandlerBase.java,v 1.1 2001/07/02 08:07:22 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import org.xml.sax.Attributes;

/**
 * Base class for handlers of embedded HTML markup in
 * <em>&lt;Text&gt;</em> elements.
 * @author Richard Kunze
 * @see de.tivano.flash.swf.parser.SWFAnyTagReader
 */
public class XMLTextMarkupHandlerBase extends XMLHandlerBase {

    /** The {@link XMLTextHandler} handling the enclosing
     * <em>&lt;Text&gt;</em> element.
     */
    private XMLTextHandler textHandler = null;

    /** Constructor. Registers element handlers for &lt;B&gt;,
     * &lt;I&gt;, &lt;SPAN&gt; and &lt;FONT&gt;
     */
     
    public XMLTextMarkupHandlerBase() {
	registerElementHandler("B", XMLTextMarkupBoldHandler.class);
	registerElementHandler("I", XMLTextMarkupItalicHandler.class);
	registerElementHandler("SPAN", XMLTextMarkupHandlerBase.class);
	registerElementHandler("FONT", XMLTextMarkupFontHandler.class);
    }
    
    /**
     * Get the {@link XMLTextHandler} object handling the
     * enclosing <em>&lt;Text&gt;</em> element.
     * @return the text handler or <code>null</code> if this object is
     * currently not active.
     */
    protected XMLTextHandler getTextHandler() { return textHandler; }


    /** @see XMLHandlerBase#startElementInternal */
    protected void startElementInternal(String name, Attributes attrib,
					XMLHandlerBase parent)
	      throws SWFWriterException {
	if (parent instanceof XMLTextMarkupHandlerBase) {
	    textHandler = ((XMLTextMarkupHandlerBase)parent).getTextHandler();
	} else if (parent instanceof XMLTextHandler) {
	    textHandler = (XMLTextHandler)parent;
	} else {
	    throw new IllegalStateException(
		  "Illegal parent handler: " + parent.getClass().getName());
	}
	super.startElementInternal(name, attrib, parent);
    }
    
    /** @see XMLHandlerBase#endElementInternal */
    protected void endElementInternal() throws SWFWriterException {
	super.endElementInternal();
	textHandler = null; 
    }

    /**
     * Add some text. Calls {@link XMLTextHandler#addText} on the
     * handler for the enclosing <em>&lt;Text&gt;</em> element
     */
    protected void handleText(char[] ch, int start, int length)
              throws SWFWriterException {
	getTextHandler().addText(ch, start, length);
    }
}
