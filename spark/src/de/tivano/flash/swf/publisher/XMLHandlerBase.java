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
 * $Id: XMLHandlerBase.java,v 1.6 2001/07/02 08:07:22 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
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
 *
 * <p>An instance of this class may be re-used to handle more than one
 * XML element. However, it is guaranteed that once {@link
 * #startElement} has been called, the instance
 * will not be re-used until the element has been handled completely,
 * i.e. {@link #endElement} has been called.</p>
 * 
 * @author Richard Kunze
 */
public abstract class XMLHandlerBase {

    /** The handler for the parent XML node */
    private XMLHandlerBase parent = null;

    /** The name of the XML element currently handled by this object */
    private String elementName = null;

    /** Map of XML element names to associated handler classes */
    private Map handlerMap = new HashMap();

    /**
     * Handler instance pool. Contains {@link LinkedList} of
     * available instances indexed by class
     */
    private static Map instancePool = new HashMap();

    /**
     * Start processing an XML node. After some internal
     * housekeeping, this method simply calls {@link #startElement}.
     * Subclasses overriding this method <em>must</em> call
     * <code>super.startElementInternal(name, attrib, parent)</code>
     * in their implementation.
     */
    protected void startElementInternal(String name, Attributes attrib,
					XMLHandlerBase parent)
	      throws SWFWriterException {
	this.parent = parent;
	elementName = name;
	getSWFWriter().setCurrentXMLHandler(this);
	startElement(name, attrib);
    }

    /**
     * Finish processing an XML node. First calls {@link #endElement},
     * then {@link #notify} on the parent handler and finally does
     * some internal housekeeping.
     * Subclasses overriding this method <em>must</em> call
     * <code>super.endElementInternal()</code> in their implementation.
     */
    protected void endElementInternal() throws SWFWriterException {
	endElement();
	parent.notify(elementName, this);
	getSWFWriter().setCurrentXMLHandler(parent);
	parent = null;
	// Put this instance back into the pool....
	LinkedList instances = (LinkedList)instancePool.get(this.getClass());
	if (instances == null) {
	    instances = new LinkedList();
	    instancePool.put(this.getClass(), instances);
	}
	instances.add(this);
    }

    /**
     * Start processing an XML node. The default implementation does nothing.
     */
    protected void startElement(String name, Attributes attrib)
	      throws SWFWriterException {}

    /**
     * Finish processing an XML node. The default implementation does nothing.
     */
    protected void endElement() throws SWFWriterException {}

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


    /**
     * Associate a handler class with an element name
     * @exception IllegalArgumentException if
     * {@link XMLHandler} is not assignable from <code>handlerClass</code>.
     */
    protected void registerElementHandler(String name,
					  Class handlerClass) {
	if (!XMLHandlerBase.class.isAssignableFrom(handlerClass)) {
	    throw new IllegalArgumentException(
		   "Not a valid XMLHandler: " + handlerClass.getName());
	}
	handlerMap.put(name, handlerClass);
    }

    /** Dispatch an XML element to the appropriate element handler */
    protected void dispatch(String name, Attributes attrib)
	      throws SWFWriterException {

	Class handlerClass = (Class)handlerMap.get(name);
	if (handlerClass != null) {
	    // Get an instance from the pool or create a new one if
	    // necessary.
	    LinkedList instances =(LinkedList)instancePool.get(handlerClass);
	    XMLHandlerBase handler = null;
	    if (instances != null) {
		handler = (XMLHandlerBase)instances.removeFirst();
	    } else {
		try {
		    handler = (XMLHandlerBase)handlerClass.newInstance();
		} catch (Exception e) {
		    fatalError(e);
		}
	    }
	    handler.startElementInternal(name, attrib, this);
	} else dispatchDefault(name, attrib);
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

    /**
     * Signal a fatal error to the application.
     * This convenience method always throws a {@link SWFWriterException}
     * with the specified message and cause. The document locator is
     * automatically filled in. This method never returns. 
     * @param message the error message
     * @param cause the exception causing the error
     */
    protected void fatalError(String message, Exception cause)
              throws SWFWriterException {
	throw new SWFWriterException(message,
				     getSWFWriter().getDocumentLocator(),
				     cause);
    }

    /**
     * Signal a fatal error to the application.
     * This convenience method always throws a {@link SWFWriterException}
     * with the specified message. The document locator is
     * automatically filled in. This method never returns. 
     * @param message the error message
     * @param cause the exception causing the error
     */
    protected void fatalError(String message)
              throws SWFWriterException {
	throw new SWFWriterException(message,
				     getSWFWriter().getDocumentLocator());
    }

    /**
     * Signal a fatal error to the application.
     * This convenience method always throws a {@link SWFWriterException}
     * with the specified cause. The document locator is
     * automatically filled in. This method never returns. 
     * @param message the error message
     * @param cause the exception causing the error
     */
    protected void fatalError(Exception cause)
              throws SWFWriterException {
	throw new SWFWriterException(cause.getMessage(),
				     getSWFWriter().getDocumentLocator(),
				     cause);
    }

    /**
     * Get the handler for the parent (enclosing) XML element.
     */
    protected XMLHandlerBase getParent() { return parent; }
    
}
