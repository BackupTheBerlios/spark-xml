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
 * $Id: XML2Flash.java,v 1.2 2001/06/09 17:23:59 kunze Exp $
 */

import de.tivano.flash.swf.publisher.SWFWriter;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A very simple XML to Flash converter.
 *
 * <p><b>Usage:</b> <code>java XML2Flash
 * <em>&lt;filename&gt;</em></code></p>
 *
 * <p>This class reads the specified XML file (conforming to the SWFML
 * DTD) and prints the corresponding SWF data to <code>System.out</code></p>
 */
public class XML2Flash {
    /** The default XML reader. Used if the property
     * org.xml.sax.driver is not specified. */
    private static final String DEFAULT_PARSER_NAME =
	"org.apache.xerces.parsers.SAXParser";
    /** Parse an XML file and write the SWF data */
    public void parse(String filename) throws Exception {
	// XMLReader parser = XMLReaderFactory.createXMLReader();
	XMLReader parser = new org.apache.xerces.parsers.SAXParser();
	parser.setContentHandler(new SWFWriter(System.out)); 	
	parser.parse(filename);
    }

    public static void main(String[] argv) throws Exception {
	if (argv.length != 1 ) {
	    System.err.println("usage: java XML2Flash <filename>");
	} else {
	    if (System.getProperty("org.xml.sax.driver") == null) {
		System.setProperty("org.xml.sax.driver", DEFAULT_PARSER_NAME);
	    }
	    new XML2Flash().parse(argv[0]);
	}
    }
}
