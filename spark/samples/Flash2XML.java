/**
 * The contents of this file are subject to the Spark Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the
 * License on the Spark web site
 * (http://www.tivano.de/opensource/flash). 
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License. 
 *
 * The Initial Developer of Spark is Tivano Software GmbH. The
 * original Spark and portions created by Tivano Software GmbH are
 * Copyright Tivano Software GmbH. All Rights Reserved. 
 *
 * Contributor(s):
 *      Richard Kunze, Tivano Software GmbH.
 *
 * $Id: Flash2XML.java,v 1.9 2002/05/23 08:50:38 richard Exp $
 */

import de.tivano.flash.swf.parser.SWFReader;
import de.tivano.flash.swf.parser.SWFVerboseDefineFont2Reader;
import de.tivano.flash.swf.parser.SWFVerboseDefineFontReader;
import de.tivano.flash.swf.common.SWFTypes;
import org.xml.sax.ContentHandler;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.xml.sax.Attributes;
import java.io.OutputStreamWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * A very simple Flash to XML converter.
 * <p><b>Usage:</b> <code>java Spark <em>&lt;filename&gt;</em></code></p>
 * <p>This class reads the specified SWF file and prints the
 * corresponding XML on <code>System.out</code></p>
 */
public class Flash2XML {

    /** Flag for verbose parsing */
    private boolean verbose = false;

    public Flash2XML(boolean verbose) {
	this.verbose = verbose;
    }

    /** Parse an SWF file file */
    public void parse(String filename) throws Exception {
	SWFReader parser = new SWFReader();
	if (verbose) {
	    // Verbose DefineFont tags
	    parser.registerTagReader(SWFTypes.DEFINE_FONT2, 
				     new SWFVerboseDefineFont2Reader());
	    parser.registerTagReader(SWFTypes.DEFINE_FONT, 
				     new SWFVerboseDefineFontReader());
	}
	OutputStreamWriter out = new OutputStreamWriter(System.out);
	OutputFormat format = 
	    new OutputFormat("xml", out.getEncoding(), true);
	parser.setContentHandler(new XMLSerializer(out, format));
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
