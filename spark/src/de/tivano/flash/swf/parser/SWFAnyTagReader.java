package de.tivano.flash.swf.parser;

import org.xml.sax.SAXException;

import java.io.IOException;

import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitInputStream;

/**
 * Tag reader for arbitrary SWF tags.
 * This class can read arbitrary SWF tags. It will emit the following XML snippet:
 * <pre>
 * &lt;UnknownTag type="<em>TagType</em>"&gt;
 *   <em>Content of the unkown tag as base64-encoded data</em>
 * &lt;/UnknownTag&gt;
 * </pre>
 * <em>TagType</em> is the ID of the SWF tag.
 * @author Richard Kunze
 */
public class SWFAnyTagReader extends SWFTagReaderBase {
   /**
     * Read the tag content.
     * This class simply reads <code>header.getLength()</code> bytes
     * from <code>input</code>, encodes it in base64 and outputs it as
     * text child of an &lt;UnknownTag&gt;.
     * @param input the SWF data stream
     * @param header the record header for this record
     */
    public void parse(BitInputStream input, SWFTagHeader header)
                throws SAXException, IOException {
	SWFAttributes attrib = createAttributes();
	attrib.addAttribute("type", SWFAttributes.TYPE_CDATA,
			    Integer.toString(header.getID()));
	startElement("UnknownTag", attrib);
	endElement("UnknownTag");
    }
}
