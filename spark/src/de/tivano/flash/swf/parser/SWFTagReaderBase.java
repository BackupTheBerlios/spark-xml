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
 * $Id: SWFTagReaderBase.java,v 1.7 2001/06/01 08:40:08 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;

import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitInputStream;

/**
 * Base class for SWF tag readers. 
 * <p>This class provides a number of convenience methods for SWF tag
 * readers. Most SWF tag readers want to extend this class instead of
 * implementing <code>SWFTagReader</code> directly.</p>
 * @author Richard Kunze
 * @see SWFReader
 * @see org.xml.sax.ContentHandler
 */
public abstract class SWFTagReaderBase implements SWFTagReader {
    /**
     * A helper class for wrapping a {@link SAXException} in an
     * {@link IOException}. Needed because output stream methods can
     * only throw IOExceptions
     */
    public static class SAXIOException extends IOException {
	/** The nested SAX exception */
	private final SAXException CAUSE;
	/** @see IOException#IOException() */
	public SAXIOException(SAXException cause) {
	    super(cause.getMessage());
	    CAUSE = cause;
	}
	
	/** Get the nested exception */
	public SAXException getCause() { return CAUSE; }
    }

    /**
     * A helper class for sending raw data as base64-encoded text to
     * the client.
     */
    private class Base64DataOutputStream extends OutputStream {
	/** Encoding table for Base64 according to RFC 1341 */
	private final char[] BASE64_TABLE = {
	    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
	    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
	    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
	};

	/** Maximum length for a line of Base64-encoded data according to
	 * RFC 1341
	 */
	private final int LINE_LENGTH = 76;

	/** Buffer for building the Base64-encoded strings */
	char[] buffer = new char[LINE_LENGTH + 1];

	/** Current position in the buffer */
	private int bufPos = 0;

	/** Temporary buffer for storing a byte triplet for encoding */
	private int currentTriplet = 0;

	/** Number of bytes currently in the temp buffer */
	private int bytesLeft = 0;

	/** @see OutputStream#write(int) */
	public void write(int b) throws IOException {
	    bytesLeft++;
	    currentTriplet <<= 8;
	    currentTriplet |= (b & 0xFF);
	    if (bytesLeft == 3) {
		buffer[bufPos++] =
		    BASE64_TABLE[(currentTriplet >>> 18) & 0x3F];
		buffer[bufPos++] =
		    BASE64_TABLE[(currentTriplet >>> 12) & 0x3F];
		buffer[bufPos++] =
		    BASE64_TABLE[(currentTriplet >>>  6) & 0x3F];
		buffer[bufPos++] =
		    BASE64_TABLE[currentTriplet & 0x3F];
		bytesLeft = 0;
		if (bufPos == LINE_LENGTH) {
		    buffer[bufPos++] = '\n';
		    try {
			characters(buffer, 0, bufPos);
		    } catch (SAXException e) {
			throw new SAXIOException(e);
		    }
		    bufPos = 0;
		}
	    }
	}

	/**
	 * Flush the output stream. If there are currently bytes left
	 * (i.e., if the number of bytes written since the last flush
	 * is not a multiple of 3), the remaining characters are
	 * padded as specified in RFC 1341
	 */
	public void flush() throws IOException {
	    switch (bytesLeft) {
	    case 2:
		buffer[bufPos++] =
		    BASE64_TABLE[(currentTriplet >>> 12) & 0x3F];
		buffer[bufPos++] =
		    BASE64_TABLE[(currentTriplet >>>  6) & 0x3F];
		buffer[bufPos++] =
		    BASE64_TABLE[(currentTriplet <<   2) & 0x3F];
		buffer[bufPos++] = '=';
		break;
	    case 1:
		buffer[bufPos++] =
		    BASE64_TABLE[(currentTriplet >>>  6) & 0x3F];
		buffer[bufPos++] =
		    BASE64_TABLE[(currentTriplet <<   4) & 0x3F];
		buffer[bufPos++] = '=';
		buffer[bufPos++] = '=';
	    }
	    buffer[bufPos++] = '\n';
	    try {
		characters(buffer, 0, bufPos);
	    } catch (SAXException e) {
		throw new SAXIOException(e);
	    }
	    bufPos = 0;
	    bytesLeft = 0;
	}

	/** @see OutputStream#close() */
	public void close() throws IOException { flush(); }
    }
    
    /** The <code>SWFReader</code> associated with this tag reader */
    private SWFReader saxDriver = null;

    /**
     * Associate an <code>SWFReader</code> with this object.  Among
     * other things, the associated <code>SWFReader</code> allows
     * access to the <code>ContentHandler</code> and
     * <code>ErrorHandler</code> used to report SAX events or errors
     * to the SAX application.
     * 
     * @param driver the associated <code>SWFReader</code> for this
     * tag handler. 
     */
    public void setSAXDriver(SWFReader driver) {
	saxDriver = driver;
    }

    /**
     * Get the associated <code>SWFReader</code>.
     * @exception IllegalStateException if no <code>SWFReader</code>
     * has been associated with this tag handler.
     */
    protected SWFReader getSAXDriver() {
	if (saxDriver == null) throw new IllegalStateException(
	   "No SWFReader associated with this SWFTagReader");
	return saxDriver;
    }


    /**
     * Send a "start element" event. In essence, this is a wrapper
     * around <code>ContentHandler.startElement()</code> which handles the
     * namespace stuff on its own.
     * @param name the (local) name for the XML element
     * @param attrib the attribute list for this element.
     * @see org.xml.sax.ContentHandler#startElement
     * @exception IllegalStateException if no <code>SWFReader</code>
     * has been associated with this tag handler.
     */
    protected void startElement(String name, Attributes attrib)
	throws SAXException {
	// FIXME: Really handle the name space stuff
	getSAXDriver().getContentHandler().startElement("", name, "", attrib);
    }

    /**
     * Send a "start element" event and an "end element"
     * event. Convenience method to handle the many empty elements
     * used to model SWF.
     * @param name the (local) name for the XML element
     * @param attrib the attribute list for this element.
     * @see org.xml.sax.ContentHandler#startElement
     * @exception IllegalStateException if no <code>SWFReader</code>
     * has been associated with this tag handler.
     */
    protected void emptyElement(String name, Attributes attrib) 
	throws SAXException {
	startElement(name, attrib);
	endElement(name);
    }

    /**
     * Send a "end element" event. In essence, this is a wrapper
     * around <code>ContentHandler.endElement()</code> which handles the
     * namespace stuff on its own.
     * @param name the (local) name for the XML element
     * @see org.xml.sax.ContentHandler#endElement
     * @exception IllegalStateException if no <code>SWFReader</code>
     * has been associated with this tag handler.
     */
    protected void endElement(String name)
	throws SAXException {
	// FIXME: Really handle the name space stuff
	getSAXDriver().getContentHandler().endElement("", name, "");
    }

    /**
     * Send a "characters" event. This is a wrapper around
     * <code>ContentHandler.characters()</code>.
     * @see org.xml.sax.ContentHandler#characters
     */
    protected void characters(char[] ch, int start, int length)
	           throws SAXException {
	getSAXDriver().getContentHandler().characters(ch, start, length);
    }

    /**
     * Send a "characters" event. Exactly the same as
     * <code>characters(ch, 0, ch.length)</code>
     * @see #characters(char[], int, int)
     */
    protected void characters(char[] ch) throws SAXException {
	characters(ch, 0, ch.length);
    }

    /** Get an <code>Attributes</code> object. */
    protected SWFAttributes createAttributes() {
	return new SWFAttributes();
    }

    /**
     * Read the tag content. Derived classes must implement this method.
     * @param input the SWF data stream
     * @param header the SWF tag header for this data record
     */
    public abstract void parse(BitInputStream input, SWFTagHeader header)
	throws SAXException, IOException;

    /**
     * Get the context map associated with this object's SAX driver.
     * The context map is used to communicate context information such
     * as font definitions between different tag readers.
     */
    protected Map getContextMap() { return getSAXDriver().getContextMap(); }

    /**
     * Get an output stream for sending raw data.
     * 
     * <p>Depending on the associated SWF readers settings, "raw data"
     * means either encoding the data in Base64 or simply converting
     * the data to a java String where every character has exactly the
     * same numerical value as the corresponding byte on the
     * stream. In any case, the raw data is passed on via the {@link
     * #characters} method.</p>
     * <p>If there is an error in delivering the data to the client,
     * this output stream will throw a {@link SAXIOException} that
     * contains the original cause of the error.</p>
     * <p><em>Note: Currently, only base64
     * encoding is supported. True raw encoding will be implemented
     * later.</em></p>
     * @return an output stream that sends raw data to the client application.
     */
    protected OutputStream getRawDataOutputStream() {
	return new Base64DataOutputStream();
    }

    /**
     * Send a warning to the client application.
     * @param msg the error message
     * @see org.xml.sax.ErrorHandler#warning
     */
    protected void warning(String msg) throws SAXException {
	try {
	    getSAXDriver().getErrorHandler().warning(
				      createSAXParseException(msg));
	} catch (NullPointerException e) {
	    // his means there is no error handler set. Simply ignore it.
	}
    }

    /**
     * Send a recoverable error to the client application.
     * @param msg the error message
     * @see org.xml.sax.ErrorHandler#error
     */
    protected void error(String msg) throws SAXException {
	getSAXDriver().getErrorHandler().error(
			       createSAXParseException(msg));
    }
    /**
     * Send a fatal error to the client application.
     * @param msg the error message
     * @see org.xml.sax.ErrorHandler#error
     */
    protected void fatalError(String msg)
	      throws SAXException {
	getSAXDriver().getErrorHandler().fatalError(
			       createSAXParseException(msg));
    }

    /** Helper method to construct a SAXParseException with a given
     * error message. */
    private SAXParseException createSAXParseException(String msg) {
	// XXX: Don't really know how to provide a working Locator for
	// binary SWF data. For now, take the easy way out...
	return new SAXParseException(msg, null);
    }

    /**
     * Convert from SWF "TWIPS" to the underlying unit.
     * Simply divides the value by 20.
     */
    protected double fromTwips(double value) { return value / 20; }
}

