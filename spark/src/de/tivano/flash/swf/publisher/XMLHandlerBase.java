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
 * $Id: XMLHandlerBase.java,v 1.1 2001/06/01 17:25:53 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import java.util.Map;
import java.util.HashMap;
import org.xml.sax.Attributes;

/**
 * Base class for all classes that handle part of the XML data.
 *
 * <p>This class implements the handling of XML data. Besides handling
 * the actual processing of the XML start tag and direct text children
 * of the corresponding XML element, this class is notified as soon as a
 * child node of the current XML node has been processed. In addition,
 * this class provides the means to control which handler is used to
 * process child elements of the current XML element.</p>
 * @author Richard Kunze
 */
public abstract class XMLHandlerBase {

    /** The handler for the parent XML node */
    private XMLHandlerBase parent = null;

    /** The name of the XML element currently handled by this object */
    private String elementName = null;

    /** Map of XML element names to associated handlers */
    private Map handlerMap = new HashMap();

    /**
     * Start processing an XML node. After some internal
     * housekeeping, this method simply calls
     * {@link startElement(String, Attributes)}
     */
    protected final void startElement(String name, Attributes attrib,
				      XMLHandlerBase parent)
	      throws SWFWriterException {
	this.parent = parent;
	elementName = name;
	getSWFWriter().setCurrentXMLHandler(this);
	startElement(name, attrib);
    }

    /**
     * Start processing an XML node.
     */
    protected void startElement(String name, Attributes attrib)
	      throws SWFWriterException {
    }

    /**
     * Finish processing an XML node. Performs the following tasks:
     * <ul>
     * <li>notify the parent via {@link #notify}
     * <li>handle internal housekeeping
     * </ul>
     * Subclasses overriding this method <em>must</em> call
     * <code>super.endElement()</code>!
     */
    protected void endElement() throws SWFWriterException {
	parent.notify(elementName, this);
	getSWFWriter().setCurrentXMLHandler(parent);
    }

    /** Get the associated SWFWriter */
    protected SWFWriter getSWFWriter() {
	return parent.getSWFWriter();
    }

    /**
     * Notify this object that an XML child element has been processed.
     * This method is called by the handlers for child elements whenever
     * they are done processing. The repsonsible handler is passed in
     * as <code>handler</code>
     * @param element the name of the XML element
     * @param handler the handler for this element
     */
    protected void notify(String element, XMLHandlerBase handler)
	      throws SWFWriterException {
    }


    /** Associate a handler with an element name */
    protected void registerElementHandler(String name,
					  XMLHandlerBase handler) {
	handlerMap.put(name, handler);
    }

    /** Dispatch an XML element to the appropriate element handler */
    protected void dispatch(String name, Attributes attrib)
	      throws SWFWriterException {
	XMLHandlerBase handler = (XMLHandlerBase)handlerMap.get(name);
	if (handler != null) handler.startElement(name, attrib);
	else dispatchDefault(name, attrib);
    }

    /**
     * Handle an XML element for which no handler is registered with
     * this object. The default implementation is to call
     * <code>parent.dispatch()</code>.
     */
    protected void dispatchDefault(String name, Attributes attrib)
	      throws SWFWriterException {
	parent.dispatch(name, attrib);
    }

    /**
     * Handle text content of the current XML element.
     * The method <em>must not</em> attempt to read characters outside the
     * index range <code>start</code> through <code>length - start</code>.
     * The default implementation does nothing.
     * @param ch the character buffer
     * @param start index of thre first relevant character
     * @param length the number of characters to preocess
     */
    protected void handleText(char[] ch, int start, int length)
              throws SWFWriterException {
    }
}
