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
 * $Id: SWFWriter.java,v 1.2 2001/06/06 18:57:46 kunze Exp $
 */

package de.tivano.flash.swf.publisher;

import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import de.tivano.flash.swf.common.BitOutputStream;
import de.tivano.flash.swf.common.SWFFileHeader;

/**
 * The main SWF publisher class.
 *
 * <p>This class implements the {@link org.xml.sax.ContentHandler}
 * interface for handling XML as a series of SAX events. It converts
 * XML conforming to the SWFML DTD to SWF data and writes this data on
 * a provided output stream.</p>
 *
 * <p><em>Note:</em> this class is <em>not</em> thread safe. It is
 * assumed that one instance of this class is only used by one thread
 * at a time.</p>
 * @author Richard Kunze
 */
public class SWFWriter extends XMLHandlerBase implements ContentHandler {

    /** The output stream to write on */
    private BitOutputStream out = null;

    /** Close the stream after writing? */
    private boolean closeOut = false;

    /** The current XML handler */
    private XMLHandlerBase currentHandler = this;

    /** The document locator */
    private Locator locator;

    /** A list of SWF top level structures to write */
    private List swfData = new ArrayList();

    /** The file header for the SWF file */
    private SWFFileHeader fileHeader = new SWFFileHeader();
    
    /**
     * Construct a new <code>SWFWriter</code>. The SWF data will be
     * written on <code>out</code> which will be closed afterward.
     * @param out the stream to write on
     */
    public SWFWriter(OutputStream out) { this(out, true); }

    /**
     * Construct a new <code>SWFWriter</code>. Depending on
     * <code>close</code>, the stream is closed after writing the
     * data or not.
     * @param out the stream to write on
     * @param close Flag, tells the writer whether to close the stream
     * after writing or not.
     */
    public SWFWriter(OutputStream out, boolean close) {
	this();
	setOutputStream(out, close);
    }

    /**
     * Construct a new <code>SWFWriter</code> without an associated
     * output stream. An output stream must be assigned to this SWF
     * writer before the endDocument() method is called.
     * @see #setOutputStream(OutputStream)
     * @see #setOutputStream(OutputStream, boolean)
     */
    public SWFWriter() {
	setCurrentXMLHandler(this);
    }

    /**
     * Assign an output stream to this writer.
     * The stream is <em>not</em> closed after writing the data.
     * @param out the stream to write on
     */
    public void setOutputStream(OutputStream out) {
	setOutputStream(out, false);
    }

    /**
     * Assign an output stream to this writer.
     * Depending on <code>close</code>, the stream is closed after
     * writing the data.
     * @param out the stream to write on
     * @param close Flag, tells the writer whether to close the stream
     * after writing or not.
     */
    public void setOutputStream(OutputStream out, boolean close) {
	this.out = new BitOutputStream(out);
	this.closeOut = close;
    }

    /**
     * Set the current XML handler. All further SAX events are
     * delivered to this handler.
     */
    public void setCurrentXMLHandler(XMLHandlerBase handler) {
	currentHandler = handler;
    }

    /**
     * Get the associated SWFWriter.
     * As this object <em>is</em> the SWF writer, simply return
     * <code>this</code>.
     */
    protected SWFWriter getSWFWriter() { return this; }

    /** @see ContentHandler#setDocumentLocator */
    public void setDocumentLocator(Locator locator) {
	this.locator = locator;
    }

    /** Get the associated document locator */
    public Locator getDocumentLocator() {
	return locator;
    }

    /** @see ContentHandler#startDocument */
    public void startDocument() throws SAXException {}

    /**
     * Finish processing SAX events and write the SWF data to the
     * provided output stream
     * @exception IllegalStateException if no output stream is set.
     */
    public void endDocument() throws SAXException {
	if (out == null)
	    throw new IllegalStateException("No output stream specified!");

	Iterator data = swfData.iterator();
	long totalSize = 0;
	while (data.hasNext()) {
	    totalSize += ((SWFTagWriter)data.next()).getTotalLength();
	}

	try {
	    // Total file size includes the header length (which gets
	    // returned in bits but is always a multiple of 8)
	    fileHeader.setFileSize(totalSize + fileHeader.length() / 8);
	    fileHeader.write(out);
	    data = swfData.iterator();
	    while (data.hasNext()) {
		((SWFTagWriter)data.next()).write(out);
	    }
	} catch (IOException e) {
	    throw new SWFWriterException("Error writing data", locator, e);
	}
    }

    /** @see ContentHandler#startPrefixMapping */
    public void startPrefixMapping(java.lang.String prefix,
				   java.lang.String uri)
	throws SAXException {}

    /** @see ContentHandler#endPrefixMapping */
    public void endPrefixMapping(java.lang.String prefix)
	throws SAXException {}

    /** Start processing an XML element */
    public void startElement(java.lang.String namespaceURI,
			     java.lang.String localName,
			     java.lang.String qName,
			     Attributes atts)
	    throws SAXException {
	currentHandler.dispatch(localName, atts);
    }

    /** Finish processing an XML element */
    public void endElement(java.lang.String namespaceURI,
			   java.lang.String localName,
			   java.lang.String qName)
	   throws SAXException {
	currentHandler.endElement();
    }

    /** Process text content */
    public void characters(char[] ch,  int start,  int length)
	   throws SAXException {
	currentHandler.handleText(ch, start, length);
    }

    /** Process whitespace. Simply calls <code>characters</code> */
    public void ignorableWhitespace(char[] ch,
				    int start,
				    int length)
	   throws SAXException {
	characters(ch, start, length);
    }

    /** Handle processing instructions. Does nothing */
    public void processingInstruction(java.lang.String target,
				      java.lang.String data)
	throws SAXException {}

    /**
     * Receive notification of a skipped entity. Does nothing.
     * @see ContentHandler#skippedEntity
     */
    public void skippedEntity(java.lang.String name)
	throws SAXException {}

    /**
     * Handle an XML element for which no handler is registered with
     * this object. Always throws a {@link SWFWriterException}
     */
    protected void dispatchDefault(String name, Attributes attrib)
	      throws SWFWriterException {
	throw new SWFWriterException("Unknown element: " + name, locator);
    }

    /** Add a new SWF toplevel structure to the end of the data */
    public void addData(SWFTagWriter data) {
	swfData.add(data);
    }
}
