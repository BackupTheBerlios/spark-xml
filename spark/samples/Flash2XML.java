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
 * $Id: Flash2XML.java,v 1.6 2001/07/02 19:10:55 kunze Exp $
 */

import de.tivano.flash.swf.parser.SWFReader;
import de.tivano.flash.swf.parser.SWFVerboseDefineFont2Reader;
import de.tivano.flash.swf.parser.SWFVerboseDefineFontReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * A very simple Flash to XML converter.
 * <p><b>Usage:</b> <code>java Flash2XML <em>&lt;filename&gt;</em></code></p>
 * <p>This class reads the specified SWF file and prints the
 * corresponding XML on <code>System.out</code></p>
 */
public class Flash2XML {

    /** Flag for verbose parsing */
    private boolean verbose = false;

    /** A very simplistic XML writer that probably only works
     *  for SWFML data. It does pretty printing, though :-)
     */
    private class XMLWriter extends DefaultHandler {
	private int indent = 0;
	private String lastName = null;
	private boolean needNewline = false;
	private boolean dontIndent = false;
	private PrintWriter out;

	public XMLWriter() throws UnsupportedEncodingException {
	    out = new PrintWriter(new OutputStreamWriter(System.out, "ISO-8859-1"));
	}

	private void printIndent() {
	    if (dontIndent) return;
	    if (needNewline) out.println();
	    needNewline = false;
	    for (int i=0; i<indent; i++) out.print("  ");
	}
	
	public void startDocument() {
	    out.println("<?xml  version=\"1.0\" encoding=\"" +
			       "ISO-8859-1\" standalone=\"yes\" ?> ");
	}
	public void endDocument() {
	    out.close();
	}
	
	public void startElement(String uri,
				 String localName,
				 String qName,
				 Attributes attr) {
	    // Print the missing ">" from the previos startElement()
	    // first...
	    if (lastName != null) {
		out.print(">");
		needNewline = true;
	    }	    
	    printIndent();
	    out.print("<" + localName);
	    if (attr != null) {
		for (int i=0; i<attr.getLength(); i++) {
		    out.print(" " + attr.getLocalName(i) +
				     "=\"" + attr.getValue(i) + "\"");
		}
	    }
	    if (localName.equals("P")) dontIndent = true;
	    indent++;
	    lastName = localName;
	}
	public void endElement(String uri,
			       String localName,
			       String qName) {
	    indent--;
	    // Is this an empty element?
	    if (localName.equals(lastName)) {
		out.print(" />");
	    } else {
		printIndent();
		out.print("</" + localName + ">");
	    }
	    if (localName.equals("P")) dontIndent = false;
	    needNewline = true;
	    lastName = null;
	}
	
	public void characters(char[] ch,
			       int start,
			       int length) {
	    // Print the missing ">" from the previous startElement()
	    // first, and make sure the next endElement() doesn't assume
	    // this is an empty element...
	    if (lastName!=null) {
		out.print(">");
		needNewline = true;
		printIndent();
	    } else if (!needNewline) printIndent();
	    lastName = null;

	    // Print the characters, with indentation after every
	    // newline.
	    for (int i=start; i<start+length; i++) {
		switch (ch[i]) {
		case '<': out.print("&lt;"); break;
		case '>': out.print("&gt;"); break;
		case '"': out.print("&quot;"); break;
		case '&': out.print("&amp;"); break;
		case '\n':
		    out.print(ch[i]);
		    if (i!=start+length-1) printIndent();
		    break;
		default:
		    out.print(ch[i]);
		    break;
		}
	    }
	    needNewline = ch[start+length-1] != '\n';
	}
    }

    public Flash2XML(boolean verbose) {
	this.verbose = verbose;
    }

    /** Parse an SWF file file */
    public void parse(String filename) throws Exception {
	SWFReader parser = new SWFReader();
	if (verbose) {
	    // Verbose DefineFont2 tags
	    parser.registerTagReader(48, new SWFVerboseDefineFont2Reader());
	    parser.registerTagReader(10, new SWFVerboseDefineFontReader());
	}
	parser.setContentHandler(new XMLWriter()); 	
	parser.parse(filename);
    }

    public static void main(String[] argv) throws Exception {
	if (argv.length == 0 || argv.length > 2 ||
	    (argv.length == 2 && !argv[0].equals("-v"))) {
	    System.err.println("usage: java Flash2XML [-v] <filename>");
	} else {
	    new Flash2XML(argv[0].equals("-v")).parse(argv[argv.length-1]);
	}
    }
}
