package de.tivano.flash.swf.parser;

import org.xml.sax.SAXParseException;

import java.io.InputStream;
import de.tivano.flash.swf.common.SWFTagHeader;

/**
 * Interface for reading SWF tags.
 * <p>Implementing classes read SWF records from an
 * <code>InputStream</code> and report corresponding SAX events by
 * means of an associated <code>ContentHandler</code></p>
 * @author Richard Kunze
 * @see SWFReader
 * @see org.xml.sax.ContentHandler
 */
public interface SWFTagReader {

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
    public void setSAXDriver(SWFReader driver);

    /**
     * Read part of an SWF data stream.
     * <p>The current stream position is
     * just after the record header of this record. the record header
     * itself is provided in <code>header</code>.</p>
     * 
     * <p>Implementing classes must read the complete tag from
     * <code>input</code>, i.e. they must read exactly
     * <code>header.getLength()</code> bytes.</p>
     * @param input the SWF data stream
     * @param header the record header for this record
     */
    public void parse(InputStream input, SWFTagHeader header)
	throws SAXParseException;
}
