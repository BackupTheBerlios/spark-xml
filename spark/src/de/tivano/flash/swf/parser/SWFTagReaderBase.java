package de.tivano.flash.swf.parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

import java.io.IOException;

import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitInputStream;

/**
 * Base class for SWF tag readers. 
 * <p>This class provides a number ov convenience methods for SWF tag
 * readers. Most SWF tag readers want to extend this class instead of
 * implementing <code>SWFTagReader</code> directly.</p>
 * @author Richard Kunze
 * @see SWFReader
 * @see org.xml.sax.ContentHandler
 */
public abstract class SWFTagReaderBase {
    /**
     * A convenience class for handling attributes. This class
     * provides convenience methods for setting attributes without
     * having to worry about namespace support. 
     * @see Attributes
     */
    protected static class SWFAttributes extends AttributesImpl {
	/** Attribute type constant */
	public static final String TYPE_CDATA = "CDATA";

	/** Attribute type constant */
	public static final String TYPE_ID = "ID";
	
	/** Add an attribute. This is a wrapper around
	 * <code>AttributeImpl.addAttribute()</code> that handles
	 * namespaces on its own.
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public void addAttribute(String name, String type, String value) {
	    // FIXME: Handle the name space stuff
	    super.addAttribute("", name, "", type, value);
	}
    }
     
    
    /** The <code>SWFReader</code> associated with this tag reader */
    SWFReader saxDriver = null;

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
}

