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
 * $Id: SWFReader.java,v 1.7 2001/06/09 17:23:59 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import de.tivano.flash.swf.common.*;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import java.util.HashMap;
import java.util.Map;

/**
 * The main SWF parser class.
 *
 * <p>This class can either work standalone or as a wrapper around any
 * SAX 2 compatible XML parser. In the first case,
 * <code>SWFReader</code> will only handle SWF data and will
 * <em>not</em> parse XML data. In the second case,
 * <code>SWFReader</code> handles both XML (by delegating to the
 * wrapped <code>XMLReader</code>) and SWF data. In both cases, the
 * data is delivered to the using application via standard SAX
 * events.</p>
 * 
 * @author Richard Kunze
 */
public class SWFReader implements XMLReader {

    /** The <code>XMLReader</code> used for delegating real XML */
    private XMLReader xmlReader = null;

    /** The content handler */
    private ContentHandler contentHandler = null;
    /** The DTD handler */
    private DTDHandler dtdHandler = null;
    /** The error handler */
    private ErrorHandler errorHandler = null;
    /** The entity resolver */
    private EntityResolver entityResolver = null;

    /** Map of SWF tag IDs to tag reader objects */
    private Map tagReaderMap = new HashMap();

    /**
     * Map of IDs to context object. Used by different SWF tag
     * readers to communicate context information (e.g. font
     * definitions)
     */
    private Map context = new HashMap();
    

    /** Special "tag ID" for tags which don't have a registered tag reader. */
    public final Integer TAGID_DEFAULT = new Integer(-1);

    /** Construct a new <code>SWFReader</code> */
    public SWFReader() {
	super();

	// Setup the default tag handlers
	registerTagReader(TAGID_DEFAULT,
			  new SWFAnyTagReader());
	registerTagReader(SWFDefineFont2.TAG_TYPE,
			  new SWFDefineFont2Reader());
	registerTagReader(SWFDefineFont.TAG_TYPE,
			  new SWFDefineFontReader());
	registerTagReader(SWFDefineFontInfo.TAG_TYPE,
			  new SWFDefineFontInfoReader());
	registerTagReader(SWFDefineTextField.TAG_TYPE,
			  new SWFDefineTextFieldReader());
    }

    /**
     * Look up the value of a feature.
     * <p>This method is delegated to the wrapped <code>XMLReader</code>. If
     * <code>SWFReader</code> works standalone, no features are supported.</p>
     * @param name The feature name, which is a fully-qualified URI.
     * @return The current state of the feature (true or false).
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the feature name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the feature name but 
     *            cannot determine its value at this time.
     * @see #setFeature
     */
    public boolean getFeature (String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
	if (xmlReader != null) return xmlReader.getFeature(name);
	else throw new SAXNotRecognizedException(name);
    }

    /**
     * Set the state of a feature.
     * <p>This method is delegated to the wrapped <code>XMLReader</code>. If
     * <code>SWFReader</code> works standalone, no features are supported.</p>
     * @param name The feature name, which is a fully-qualified URI.
     * @param state The requested state of the feature (true or false).
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the feature name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the feature name but 
     *            cannot set the requested value.
     * @see #getFeature
     */
    public void setFeature (String name, boolean value)
	throws SAXNotRecognizedException, SAXNotSupportedException {
	if (xmlReader != null) xmlReader.setFeature(name, value);
	else throw new SAXNotRecognizedException(name);
    }

    /**
     * Look up the value of a property.
     * <p>Properties with a name of
     * <code>http://tivano.de/swf/parser/tag/<em>&lt;TagID&gt;</em></code>
     * are recognized by the parser. <em>&lt;TagID&gt;</em> is a
     * hexadecimal or decimal number that designates an SWF tag ID or
     * the special name <code>DEFAULT</code>. The value of these
     * properties is a class that implements
     * <code>SWFTagReader</code></p>, this class is used to handle 
     * SWF tags with the corresponding tag id (all tags with no
     * specific handler for <code>DEFAULT</code>).
     * <p>Other properties are delegated to the wrapped
     * <code>XMLReader</code> if one is set.</p>
     * @param name The property name, which is a fully-qualified URI.
     * @return The current value of the property.
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the property name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the property name but 
     *            cannot determine its value at this time.
     * @see #setProperty
     */
    public Object getProperty (String name)
	throws SAXNotRecognizedException, SAXNotSupportedException {
	// FIXME: Property handling not yet implemented
	if (xmlReader != null) return xmlReader.getProperty(name);
	else throw new SAXNotRecognizedException(name);
    }


    /**
     * Set the value of a property.
     * <p>Properties with a name of
     * <code>http://tivano.de/swf/parser/tag/<em>&lt;TagID&gt;</em></code>
     * are recognized by the parser. <em>&lt;TagID&gt;</em> is a
     * hexadecimal or decimal number that designates an SWF tag ID or
     * the special name <code>DEFAULT</code>. The value of these
     * properties is a class that implements
     * <code>SWFTagReader</code></p>, this class is used to handle 
     * SWF tags with the corresponding tag id (all tags with no
     * specific handler for <code>DEFAULT</code>).
     * <p>Other properties are delegated to the wrapped
     * <code>XMLReader</code> if one is set.</p>
     * @param name The property name, which is a fully-qualified URI.
     * @param value The requested value for the property.
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the property name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the property name but 
     *            cannot set the requested value.
     */
    public void setProperty (String name, Object value)
	throws SAXNotRecognizedException, SAXNotSupportedException {
	// FIXME: Implement property handling
	if (xmlReader != null) xmlReader.setProperty(name, value);
	else throw new SAXNotRecognizedException(name);
    }

    /**
     * Allow an application to register an entity resolver.
     * Since SWF is <em>not</em> an XML format and as such does not
     * have the concept of entities, registering an entity resolver
     * does not make much sense. In fact, <code>SWFFileReader</code>
     * will never call the registered entity resolver. If
     * <code>SWFReader</code> acts as a wrapper around another parser,
     * this method is delegated to the wrapped <code>XMLReader</code>.
     * @param resolver The entity resolver.
     * @exception java.lang.NullPointerException If the resolver 
     *            argument is null.
     * @see #getEntityResolver
     */
    public void setEntityResolver (EntityResolver resolver) {
	if (resolver == null) throw new NullPointerException();
	entityResolver = resolver;
	if (xmlReader != null) xmlReader.setEntityResolver(resolver);
    }


    /**
     * Return the current entity resolver.
     *
     * @return The current entity resolver, or null if none
     *         has been registered.
     * @see #setEntityResolver
     */
    public EntityResolver getEntityResolver () {
	// Delegate in case the wrapped parser wants to do something
	// special. Unlikely, but who knows...
	if (xmlReader != null) return xmlReader.getEntityResolver();
	else return entityResolver;
    }


    /**
     * Allow an application to register a DTD event handler.
     * Currently, the registered DTD handler will never be
     * called. Future versions of <code>SWFFileHandler</code> may
     * generate synthetic DTD events to inform an application about
     * the (predefined and fixed) DTD used for representing SWF as
     * XML.
     * @param handler The DTD handler.
     * @exception java.lang.NullPointerException If the handler 
     *            argument is null.
     * @see #getDTDHandler
     */
    public void setDTDHandler (DTDHandler handler) {
	if (handler == null) throw new NullPointerException();
	dtdHandler = handler;
	if (xmlReader != null) xmlReader.setDTDHandler(handler);
    }


    /**
     * Return the current DTD handler.
     *
     * @return The current DTD handler, or null if none
     *         has been registered.
     * @see #setDTDHandler
     */
    public DTDHandler getDTDHandler () {
	// Delegate in case the wrapped parser wants to do something
	// special. Unlikely, but who knows...
	if (xmlReader != null) return xmlReader.getDTDHandler();
	else return dtdHandler;
    }


    /**
     * Allow an application to register a content event handler.
     *
     * @param handler The content handler.
     * @exception java.lang.NullPointerException If the handler 
     *            argument is null.
     * @see #getContentHandler
     */
    public void setContentHandler (ContentHandler handler) {
	if (handler == null) throw new NullPointerException();
	contentHandler = handler;
 	if (xmlReader != null) xmlReader.setContentHandler(handler);
   }


    /**
     * Return the current content handler.
     *
     * @return The current content handler, or null if none
     *         has been registered.
     * @see #setContentHandler
     */
    public ContentHandler getContentHandler () {
	// Delegate in case the wrapped parser wants to do something
	// special. Unlikely, but who knows...
	if (xmlReader != null) return xmlReader.getContentHandler();
	else return contentHandler;
    }


    /**
     * Allow an application to register an error event handler.
     *
     * <p>If the application does not register an error handler, all
     * error events reported by the SWF parser will be silently
     * ignored; however, normal processing may not continue.  It is
     * highly recommended that all SAX applications implement an
     * error handler to avoid unexpected bugs.</p>
     *
     * @param handler The error handler.
     * @exception java.lang.NullPointerException If the handler 
     *            argument is null.
     * @see #getErrorHandler
     */
    public void setErrorHandler (ErrorHandler handler) {
	if (handler == null) throw new NullPointerException();
	errorHandler = handler;
 	if (xmlReader != null) xmlReader.setErrorHandler(handler);
    }


    /**
     * Return the current error handler.
     *
     * @return The current error handler, or null if none
     *         has been registered.
     * @see #setErrorHandler
     */
    public ErrorHandler getErrorHandler () {
	// Delegate in case the wrapped parser wants to do something
	// special. Unlikely, but who knows...
	if (xmlReader != null) return xmlReader.getErrorHandler();
	else return errorHandler;
    }

    /**
     * Delegate parsing to the wrapped <code>XMLReader</code>.
     * @param input the data to parse
     * @exception SAXException if no wrapped XML reader exists
     */
    private void delegateParsing(InputSource input) throws SAXException,
	                                                   IOException {
	if (xmlReader == null) throw new SAXException(
	    "Cannot handle XML input without a wrapped XMLReader");
	xmlReader.parse(input);
    }
    
    /**
     * Parse an XML or SWF document.
     * <p>The input source is examined to determine if the document
     * contains XML or SWF data. In the first case, parsing is
     * delegated to the wrapped <code>XMLReader</code> or an exception
     * is thrown if there is no wrapped <code>XMLReader</code>. In the
     * second case, the binary SWF data is parsed by
     * <code>SWFReader</code>. In detail:</p>
     * <ul>
     * <li>Since SWF is a binary format, reading it from a character
     * stream does not make much sense. If a character stream is set
     * in <code>input</code>, <code>SWFReader</code> assumes that
     * the content is XML and not SWF and directly delegates to the
     * wrapped <code>XMLReader</code>.</li>
     * <li>If no character stream is set, <code>SWFReader</code>
     * examines the provided binary input stream. If the stream seems
     * to be SWF data, it handles the parsing by itself. If not, it
     * delegates parsing to the wrapped <code>XMLReader</code>.</li>
     * <li>If no stream at all is set in <code>input</code>,
     * <code>SWFReader</code> opens a binary stream to the provided
     * system ID and examines this stream. If it decides that the data
     * is not SWF, the stream is passed on to the wrapped
     * <code>XMLReader</code></li>.
     * </ul>
     * @param source The input source for the top-level of the
     *        SWF or XML document.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see org.xml.sax.InputSource
     * @see #parse(java.lang.String)
     * @see #setEntityResolver
     * @see #setDTDHandler
     * @see #setContentHandler
     * @see #setErrorHandler 
     */
    public void parse (InputSource input)
	throws IOException, SAXException {
	InputStream in = input.getByteStream();

	if (input.getCharacterStream() != null) {
	    delegateParsing(input);
	} else if (in == null) {
	    // FIXME: Handle system identifiers other than "file"
	    in = new FileInputStream(input.getSystemId());
	}

	// Examine the start of the binary stream. Use a byte array
	// for the examination instead of wrapping the stream in a
	// BitInputStream because the BitInputStream is slower than
	// the underlying stream and isn't needed when delegating to a
	// real XML parser.
	if (!in.markSupported()) {
	    in = new BufferedInputStream(in);
	    input.setByteStream(in);
	}

	byte[] buffer = new byte[SWFFileHeader.MAX_LENGTH];
	in.mark(SWFFileHeader.MAX_LENGTH);
	in.read(buffer);
	in.reset();
	
	try {
	    SWFFileHeader dummy = new SWFFileHeader(buffer);
	} catch (SWFFormatException e) {
	    // Not an SWF file -> delegate
	    delegateParsing(input);
	}

	BitInputStream bits = new BitInputStream(in);
	try {
	    parse(bits);
	} finally {
	    bits.close();
	}
    }


    /**
     * Parse an XML or SWF document from a system identifier (URI).
     *
     * <p>This method is a shortcut for the common case of reading a
     * document from a system identifier.  It is the exact
     * equivalent of the following:</p>
     *
     * <pre>
     * parse(new InputSource(systemId));
     * </pre>
     *
     * <p>If the system identifier is a URL, it must be fully resolved
     * by the application before it is passed to the parser.</p>
     *
     * @param systemId The system identifier (URI).
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see #parse(org.xml.sax.InputSource)
     */
    public void parse (String systemId)
	throws IOException, SAXException {
	parse(new InputSource(systemId));
    }

    /**
     * Parse an SWF document.
     *
     * <p>This method handles the actual parsing of the (binary) SWF
     * document. The caller is responsible for ensuring that
     * <code>input</code> actually <em>is</em> an SWF document.</p>
     * <p>The method assumes that the SWF file header has already been
     * read from the stream and that the current stream position is at
     * the first byte after the file header.</p>
     * @param header the SWF file header.
     * @param input the SWF document to parse.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     */
    private void parse (BitInputStream input)
	throws IOException, SAXException {


	// Handle the file header
	parseStartOfFile(input);
	
	// Now handle the SWF tags.
	// SWFTagHeader() throws an EOFException if there is no more
	// input available, so this loop *will* terminate
	// eventually...
	try {
	    while (true) {
		SWFTagHeader nextTag = new SWFTagHeader(input);
		getTagReader(nextTag).parse(input, nextTag);
	    }
	} catch (EOFException e) {
	    // OK, we're done
	}

	// finally, send the </SWF> tag
	parseEndOfFile(input);
    }

    /**
     * Send the XML Events for the start of the file.
     *
     * <p>This produces the follwing XML snippet:</p>
     * <pre>
     * &lt;SWF version="<em>version</em>" framerate="<em>framerate</em>"
     *         width="<em>width</em> height="<em>height</em>"
     *         x="<em>x</em>" y="<em>y</em>"&gt;
     * </pre>
     * <p>Size and position information is in pixels, the frame rate in frames
     * per second. The <em>x</em> or <em>y</em> attributes are only
     * present if they are different from 0.</p>
     * <p>The &lt;/SWF&gt tag is sent from <code>parseEndOfFile()</code>
     */
    protected void parseStartOfFile(BitInputStream input)
	           throws IOException, SAXException {
	ContentHandler handler = getContentHandler();
	SWFFileHeader header = new SWFFileHeader(input);
	SWFAttributes attr = new SWFAttributes();
	attr.addAttribute("version", header.getVersion());
	attr.addAttribute("framerate", header.getFrameRate());
	// Convert the values from "TWIPS" to pixels (divide by 20).
	double x = header.getMovieSize().getXMin() / 20.0;
	double y = header.getMovieSize().getYMin() / 20.0;
	double width  = header.getMovieSize().getXMax() / 20.0 - x;
	double height = header.getMovieSize().getYMax() / 20.0 - y;
	attr.addAttribute("width", width);
	attr.addAttribute("height", height);
	if (x != 0.0 || y != 0.0) {
	    attr.addAttribute("x", x);
	    attr.addAttribute("y", y);
	}
	// FIXME: Handle Namespaces...
	handler.startElement("", "SWF", "", attr);
    }

    /**
     * Send the XML Events for the end of the file.
     *
     * <p>This produces
     * the follwing XML snippet:
     * <pre>
     * &lt;/SWF&gt;
     */
    protected void parseEndOfFile(BitInputStream input)
	           throws IOException, SAXException {
	// FIXME: Handle Namespaces!
	getContentHandler().endElement("", "SWF", "");
    }
	    
    
    /**
     * Register a tag reader. This functionality is also available
     * via the <code>setProperty()</code> method.
     *
     * @param tagID either an <code>Integer</code> representing a SWF
     * tag id, or the special values <code>TAGID_DEFAULT</code>.
     * @param handler the handler for this tag type.
     * @return the previously registered tag handler for this tag id,
     * or <code>null</code> if no tag reader was registered.
     */
    public SWFTagReader registerTagReader(Integer tagID,
					  SWFTagReader handler) {
	handler.setSAXDriver(this);
	return (SWFTagReader)tagReaderMap.put(tagID, handler);
    }

    /** @see #registerTagReader(Integer, SWFTagReader) */
    public SWFTagReader registerTagReader(int tagID, SWFTagReader handler) {
	return registerTagReader(new Integer(tagID), handler);
    }

    /**
     * Get the tag reader for a given SWF tag ID.
     * @param tagID the tag ID or the special value
     * <code>TAGID_DEFAULT</code>.
     * @return the registered tag reader or the default tag reader if none
     * is registered.
     */
    public SWFTagReader getTagReader(Integer tagID) {
	SWFTagReader reader = (SWFTagReader)tagReaderMap.get(tagID);
	if (reader == null) reader = (SWFTagReader)tagReaderMap.get(TAGID_DEFAULT);
	// Paranoia check. There should *always* be a default reader
	if (reader == null) throw new IllegalStateException(
          "No default SWF tag reader registered. This should never happen.");
	return reader;
    }
    
    /**
     * Get the tag reader for a given SWF tag header.
     * @param tagID the tag ID or one of the special values
     * <code>TAGID_DEFAULT</code> or <code>TAGID_START_FILE</code>.
     * @return the registered tag reader or <code>null</code> if none
     * is registered.
     */
    public SWFTagReader getTagReader(SWFTagHeader header) {
	SWFTagReader reader =
	    (SWFTagReader)tagReaderMap.get(header.getIDAsInteger());
	if (reader == null) {
	    reader = (SWFTagReader)tagReaderMap.get(TAGID_DEFAULT);
	}
	return reader;
    }

    /**
     * Get the context map associated with this object.
     * The context map is used by tag readers to communicate context
     * information such as font definitions between different tag
     * readers.
     */
    Map getContextMap() { return context; }

}
