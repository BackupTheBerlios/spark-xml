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
 * $Id: SWFFileHeader.java,v 1.6 2001/05/23 14:58:14 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;
import java.io.ByteArrayInputStream;

import java.util.Arrays;

/**
 * This class represents the SWF file header.
 * </p>The SWF file header has a length of 14 to 29 bytes, depending on
 * the size of the bounding box. The file header has the following
 * structure: </p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *  <td>Signature</td>
 *  <td>24</td>
 *  <td>Signature, always the ASCII string "FWS"</td>
 * </tr>
 * <tr>
 *   <td>Version</td>
 *   <td>8</td>
 *   <td>Flash version as unsigned byte</td>
 * </tr>
 * <tr>
 *   <td>File Length</td>
 *   <td>32</td>
 *   <td>Length of the entire file in bytes, unsigned 32 bit word in
 *   LSB order</td>
 * </tr>
 * <tr>
 *   <td>Frame Size</td>
 *   <td>varying</td>
 *   <td>Bounding box for this Movie in TWIPS. If the length of the
 *   bounding box is not a multiple of 8, the remaining bits are set to
 *   0. See {@link SWFRectangle}.
 *   </td>
 * </tr>
 * <tr>
 *   <td>Frame Rate</td>
 *   <td>16</td>
 *   <td>Frame rate in frames per second, unsigned 16 bit word in LSB
 *   order.</td> 
 * </tr>
 * <tr>
 *   <td>Frame Count</td>
 *   <td>16</td>
 *   <td>Total number of frames in the movie, unsigned 16 bit word in
 *   LSB order</b>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFFileHeader extends SWFDataTypeBase {

    /** The maximal length (in bytes) of an SWF file header */
    public static final int MAX_LENGTH = 29;

    /** The SWF file signature, "FWS" in ASCII encoding */
    private static final byte[] SIGNATURE = { 0x46, 0x57, 0x53 };

    /** File version */
    private final int VERSION;

    /** Movie bounding box */
    private final SWFRectangle BOUNDING_BOX;

    /** File size in bytes */
    private final long FILE_SIZE;
    
    /** Frame rate */
    private final int FRAME_RATE;

    /** Number of frames */
    private final int FRAME_COUNT;


    /**
     * Construct a SWF file header from an input stream.
     * @param input the input stream
     * @exception SWFFormatException if the file header cannot be read
     * correctly.
     * @expetion IOException if some other IO error occurs
     */
    public SWFFileHeader(BitInputStream input) throws IOException {
	final String FAIL_MSG = "Invalid SWF file header";
	
	// The file header is always byte-aligned...
	if (!input.isAtByteBoundary())
	    throw new SWFFormatException("The SWF file header must be byte-aligned");
	
	byte[] signature = new byte[SIGNATURE.length];
	boolean failed = false;
	// Read the data.
	try {	    
	    input.read(signature);
	    VERSION      = input.readUByte();
	    FILE_SIZE    = input.readUW32LSB();
	    BOUNDING_BOX = new SWFRectangle(input);
	    input.skipToByteBoundary();
	    FRAME_RATE   = input.readUW16LSB();
	    FRAME_COUNT  = input.readUW16LSB();
	} catch (EOFException e) {
	    // Not enough data for the header, Can't be an SWF file
	    throw new SWFFormatException(FAIL_MSG);
	} catch (SWFFormatException e) {
	    // Bounding box could not be read, Can't be an SWF file
	    throw new SWFFormatException(FAIL_MSG);
	}

	if (!Arrays.equals(signature, SIGNATURE)) {
	    throw new SWFFormatException(FAIL_MSG);
	}
    }

    /**
     * Construct a SWF file header from a byte array.
     * @param input the byte array
     * @exception SWFFormatException if the file header cannot be read
     * correctly.
     */
    public SWFFileHeader(byte[] input) throws IOException {
	this(new BitInputStream(new ByteArrayInputStream(input)));
    }

    /** Get the SWF version */
    public int getVersion() { return VERSION ; }

    /** Get the bounding box of the movie */
    public SWFRectangle getMovieSize() { return BOUNDING_BOX; }

    /** Get the frame rate */
    public int getFrameRate() { return FRAME_RATE; }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	return 96 + paddedLength(getMovieSize().length());
    }
    
    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
    }
}
