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
 * $Id: Base64Decoder.java,v 1.1 2001/06/06 18:57:46 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import java.io.OutputStream;
import java.io.IOException;
import de.tivano.flash.swf.common.BitOutputStream;

/**
 * Helper class to decode base64-encoded data.
 * This class decodes the data and writes it to the specified output stream.
 * @author Richard Kunze
 */
public class Base64Decoder {
    BitOutputStream out;
    
    /**
     * Create a new base64 decoder.
     * @param out the stream to write the decoded data to.
     **/
    public Base64Decoder(OutputStream out) {
	this.out = new BitOutputStream(out);
    }

    /**
     * Decode some data. This metod will not attemtp to read from
     * <code>data</code> outside the range of <code>start</code>
     * through <code>start + length - 1</code>. 
     * @param data the base64 data
     * @param start the first index to start reading.
     * @param length the number of characters to read.
     * @exception IOException if some I/O error occurs
     * @exception IllegalArgumentException if data contains characters
     * that are not legal in base64 encoded data.
     */
    public void write(char[] data, int start, int length) throws IOException {
	for (int i=start; i<start+length; i++) write(data[i]);
    }
    
    /**
     * Decode a single character.
     * @param data the character to decode
     * @exception IOException if some I/O error occurs
     * @exception IllegalArgumentException is not legal in base64
     * encoded data.
     */
    public void write(char data) throws IOException {
	if (data >= 'A' && data <= 'Z') {
	    out.writeBits((int)(data - 'A'), 6);
	} else if (data >= 'a' && data <= 'z') {
	    out.writeBits((int)(data - 'a') + 26, 6);
	} else if (data >= '0' && data <= '9') {
	    out.writeBits((int)(data - '0') + 52, 6);
	} else if (data == '+') {
	    out.writeBits(62, 6);
	} else if (data == '/') {
	    out.writeBits(63, 6);
	} else if (Character.isSpace(data) || data == '=') {
	    // Ignore whitespace and the padding character
	} else {
	    throw new IllegalArgumentException(
		  "Not a legal base64 character: " + data);
	}	
    }

    /** Close the associated output stream */
    public void close() throws IOException { out.close(); }

    /**
     * Flush the data. If there are bits left to be written (i.e. the
     * number of characters written so far is not a multiple of 4),
     * the remaining bits are assumed to be 0.
     */
    public void flush() throws IOException { out.padAndFlush(); }
}
