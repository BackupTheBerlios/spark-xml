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
 * $Id: Flash2XML.java,v 1.1 2001/03/16 16:51:08 kunze Exp $
 */

import de.tivano.flash.swf.parser.SWFReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;


/**
 * A very simple Flash to XML converter.
 * <p><b>Usage:</b> <code>java Flash2XML <em>&lt;filename&gt;</em></code></p>
 * <p>This class reads the specified SWF file and prints the
 * corresponding XML on <code>System.out</code></p>
 */
public class Flash2XML {

    /** A very simplistic XML writer that probably only works
     *  for SWFML data. It does pretty printing, though :-)
     */
    private static class XMLWriter extends DefaultHandler {
	private int indent = 0;
	private String lastName = null;

	private void printIndent() {
	    for (int i=0; i<indent; i++) System.out.print("  ");
	}
	
	public void startElement(String uri,
				 String localName,
				 String qName,
				 Attributes attr) {
	    // Print the missing ">" from the previos startElement()
	    // first...
	    if (lastName != null) {
		System.out.println(">");
	    }
	    printIndent();
	    System.out.print("<" + localName);
	    for (int i=0; i<attr.getLength(); i++) {
		System.out.print(" " + attr.getLocalName(i) +
				 "=\"" + attr.getValue(i) + "\"");
	    }
	    indent++;
	    lastName = localName;
	}
	public void endElement(String uri,
			       String localName,
			       String qName) {
	    indent--;
	    // Is this an empty element?
	    if (localName.equals(lastName)) {
		System.out.println(" />");
	    } else {
		printIndent();
		System.out.println("</" + localName + ">");
	    }
	    lastName = null;
	}
	
	public void characters(char[] ch,
			       int start,
			       int length) {
	    // Print the missing ">" from the previous startElement()
	    // first, and make sure the next endElement() doesn't assume
	    // this is an empty element...
	    if (lastName!=null) System.out.println(">");
	    lastName = null;

	    // Print the characters, with indentation after every
	    // newline.
	    printIndent();
	    for (int i=start; i<start+length; i++) {
		System.out.print(ch[i]);
		if (ch[i] == '\n' && i!=start+length-1) printIndent();
	    }
	}
    }

    /** Parse an SWF file file */
    public void parse(String filename) throws Exception {
	SWFReader parser = new SWFReader();
	parser.setContentHandler(new XMLWriter()); 	
	parser.parse(filename);
    }

    public static void main(String[] argv) throws Exception {
	if (argv.length != 1) {
	    System.err.println("usage: java Flash2XML <filename>");
	} else {
	    new Flash2XML().parse(argv[0]);
	}
    }
}
