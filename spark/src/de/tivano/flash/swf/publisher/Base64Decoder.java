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
 * $Id: Base64Decoder.java,v 1.3 2001/06/11 23:41:53 kunze Exp $
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
    OutputStream out;

    /** The write buffer */
    int buffer = 0;

    /** The number of valid bits in the write buffer */
    int validBits = 0;
    
    /**
     * Create a new base64 decoder.
     * @param out the stream to write the decoded data to.
     **/
    public Base64Decoder(OutputStream out) {
	this.out = out;
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
	byte value;
	if (data >= 'A' && data <= 'Z') {
	    value = (byte)(data - 'A');
	} else if (data >= 'a' && data <= 'z') {
	    value = (byte)(data - 'a' + 26);
	} else if (data >= '0' && data <= '9') {
	    value = (byte)(data - '0' + 52);
	} else if (data == '+') {
	    value = 62;
	} else if (data == '/') {
	    value = 63;
	} else if (data == '=') {
	    return;
	} else if (Character.isSpace(data)) {
	    // Ignore whitespace
	    return;
	} else {
	    throw new IllegalArgumentException(
		  "Not a legal base64 character: " + data);
	}
	validBits += 6;
	buffer <<= 6;
	buffer |= (value & 0x3F);
	if (validBits == 24) {
	    out.write((buffer >>> 16) & 0xFF);
	    out.write((buffer >>> 8) & 0xFF);
	    out.write(buffer & 0xFF);
	    buffer = 0;
	    validBits = 0;
	}
    }

    /**
     * Close the associated output stream. If there are still bits
     * pending, they are implicitly assumed to be 0.
     */
    public void close() throws IOException {
	writeRemaining();
	out.close();
    }

    /**
     * Flush the data. If there are still bits pending, they are
     * implicitly assumed to be 0.
     */
    public void flush() throws IOException {
	writeRemaining();
	buffer = 0;
	validBits = 0;
	out.flush();
    }

    /** Write the remaining bytes */
    private void writeRemaining() throws IOException {
	switch (validBits) {
	case 18:
	    out.write((buffer >>> 8) & 0xFF);
	    out.write(buffer & 0xFF);
	    break;
	case 12:
	    out.write(buffer & 0xFF);
	    break;
	case 6:
	    buffer <<= 2;
	    out.write(buffer & 0xFF);
	    break;
	case 0:
	    break;
	default:
	    // Paranoia
	    throw new IllegalStateException(
	       "Got " + validBits + " bits left at the end of decoding. " +
	       "This should never happen. Please debug.");
	}
    }
}
