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
 * $Id: SWFMatrix.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;
import java.io.EOFException;

import java.util.Arrays;

/**
 * This class represents a SWF "matrix" structure. In the context of
 * the SWF specs, "matrix" means transformation matrix.
 * <p>A matrix structure has varying length and always starts at a
 * byte boundary. It has the following structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>hasScale</td>
 *   <td>1</td>
 *   <td>Flag, indicates that the follwing fields are present</td>
 * </tr>
 * <tr>
 *   <td>scaleBits</td>
 *   <td>5</td>
 *   <td>Number of bits used for the following values. Only present if
 *   <em>hasScale</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>scaleX</td>
 *   <td><em>scaleBits</em></td>
 *   <td>X component of the scale factor, stored as the lower
 *   <em>scaleBits</em> bits of a signed 16/16-bit fixed decimal.
 *   <Only present if <em>hasScale</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>scaleY</td>
 *   <td><em>scaleBits</em></td>
 *   <td>Y component of the scale factor, stored as the lower
 *   <em>scaleBits</em> bits of a signed 16/16-bit fixed decimal.
 *   <Only present if <em>hasScale</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>hasRotate</td>
 *   <td>1</td>
 *   <td>Flag, indicates that the follwing fields are present</td>
 * </tr>
 * <tr>
 *   <td>rotateBits</td>
 *   <td>5</td>
 *   <td>Number of bits used for the following values. Only present if
 *   <em>hasRotate</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>rotate1</td>
 *   <td><em>rotateBits</em></td>
 *   <td>First component of the rotate factor, stored as the lower
 *   <em>rotateBits</em> bits of a signed 16/16-bit fixed decimal.
 *   <Only present if <em>hasRotate</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>rotate2</td>
 *   <td><em>rotateBits</em></td>
 *   <td>Second component of the rotate factor, stored as the lower
 *   <em>rotateBits</em> bits of a signed 16/16-bit fixed decimal.
 *   <Only present if <em>hasRotate</em> is set.</td>
 * </tr>
 * <tr>
 *   <td>translateBits</td>
 *   <td>5</td>
 *   <td>Number of bits used for the following values.</td>
 * </tr>
 * <tr>
 *   <td>translateX</td>
 *   <td><em>translateBits</em></td>
 *   <td>X component of the translate factor, stored as the lower
 *   <em>translateBits</em> bits of a signed 16/16-bit fixed decimal.</td>
 * </tr>
 * <tr>
 *   <td>translateY</td>
 *   <td><em>translateBits</em></td>
 *   <td>Y component of the translate factor, stored as the lower
 *   <em>translateBits</em> bits of a signed 16/16-bit fixed decimal.</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFMatrix extends SWFDataTypeBase {
    /** The scale factors */
    private boolean hasScale;
    private double scaleX;
    private double scaleY;

    /** The rotate factors */
    private boolean hasRotate;
    private double rotate1;
    private double rotate2;

    /** The translate vector */
    private double translateX;
    private double translateY;
    
    /**
     * Construct a <code>SWFMatrix</code> from a bit input stream.
     * @exception SWFFormatException if the complete structure could
     * not be read from the stream.
     * @param input the bit stream to read from
     */
    public SWFMatrix(BitInputStream input) throws IOException {
	try {
	    int nBits;
	    hasScale = input.readBit();
	    if (hasScale) {
		nBits = (int)input.readUBits(5);
		scaleX = fromFixed(input.readSBits(nBits));
		scaleY = fromFixed(input.readSBits(nBits));
	    }
	    hasRotate = input.readBit();
	    if (hasRotate) {
		nBits = (int)input.readUBits(5);
		rotate1 = fromFixed(input.readSBits(nBits));
		rotate2 = fromFixed(input.readSBits(nBits));
	    }
	    nBits = (int)input.readUBits(5);
	    translateX = fromFixed(input.readSBits(nBits));
	    translateY = fromFixed(input.readSBits(nBits));
	} catch (EOFException e) {
	    throw new SWFFormatException(
	         "Premature end of file encoutered while reading a matrix structure");
	}
    }

    /** Check if this transformation includes scaling */
    public boolean hasScale() { return hasScale; }
    
    /**
     * Get the X component of the scaling vector.
     * This is only valid if {@link #hasScale} returns <code>true</code>;
     */
    public double getScaleX() { return scaleX; }

    /**
     * Get the Y component of the scaling vector.
     * This is only valid if {@link #hasScale} returns <code>true</code>;
     */
    public double getScaleY() { return scaleY; }

    /** Check if this transformation includes scaling */
    public boolean hasRotate() { return hasRotate; }
    
    /**
     * Get the first component of the rotate vector.
     * This is only valid if {@link #hasRotate} returns <code>true</code>;
     */
    public double getRotate1() { return rotate1; }

    /**
     * Get the second component of the rotate vector.
     * This is only valid if {@link #hasRotate} returns <code>true</code>;
     */
    public double getRotate2() { return rotate2; }

    /**
     * Get X component of the translate vector.
     */
    public double getTranslateX() { return translateX; }

    /**
     * Get Y component of the translate vector.
     */
    public double getTranslateY() { return translateY; }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	int nBits;
	long x, y;
	long length = 7 + Math.max(minBitsS(toFixed(getTranslateX())),
				   minBitsS(toFixed(getTranslateY())));
	if (hasScale()) {
	    length += 5 +  Math.max(minBitsS(toFixed(getScaleX())),
				    minBitsS(toFixed(getScaleY())));
	}
	if (hasRotate()) {
	    length += 5 +  Math.max(minBitsS(toFixed(getRotate1())),
				    minBitsS(toFixed(getRotate2())));
	}
	return length;
    }

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	int nBits;
	long x, y;
	out.writeBit(hasScale());
	if (hasScale()) {
	    x = toFixed(getScaleX());
	    y = toFixed(getScaleY());
	    nBits = Math.max(minBitsS(x), minBitsS(y));
	    out.writeBits(x, nBits);
	    out.writeBits(y, nBits);
	}
	out.writeBit(hasRotate());
	if (hasRotate()) {
	    x = toFixed(getRotate1());
	    y = toFixed(getRotate2());
	    nBits = Math.max(minBitsS(x), minBitsS(y));
	    out.writeBits(x, nBits);
	    out.writeBits(y, nBits);
	}
	x = toFixed(getTranslateX());
	y = toFixed(getTranslateY());
	nBits = Math.max(minBitsS(x), minBitsS(y));
	out.writeBits(x, nBits);
	out.writeBits(y, nBits);
    }
}
