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
 * $Id: SWFDataTypeBase.java,v 1.6 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;

/**
 * This class serves as base class for most SWF data types.
 * Besides implementing the {@link SWFDataType} interface, this class
 * provides a number of convenience methods for derived classes.
 * @author Richard Kunze
 */
public abstract class SWFDataTypeBase implements SWFDataType {
    /**
     * Get the length of this objects SWF representation.
     * Note that the length is in bits.
     */
    public abstract long length();

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public abstract void write(BitOutputStream out) throws IOException;

    /**
     * Calculate the number of bits needed to represent
     * <code>val</code> interpreted as an unsigned integer
     * @param val a value
     * @return the minimum number of bits needed to represent
     * <code>val</code>
     */
    protected static int minBitsU(long val) {
	int minBits = 0;
	if ((val & 0xffffffff00000000L) != 0) { minBits += 32; val >>>= 32; }
	if ((val & 0x00000000ffff0000L) != 0) { minBits += 16; val >>>= 16; }
	if ((val & 0x000000000000ff00L) != 0) { minBits += 8;  val >>>= 8; }
	if ((val & 0x00000000000000f0L) != 0) { minBits += 4;  val >>>= 4; }
	if ((val & 0x000000000000000CL) != 0) { minBits += 2;  val >>>= 2; }
	if ((val & 0x0000000000000002L) != 0) { minBits += 1;  val >>>= 1; }
	if ((val & 0x0000000000000001L) != 0) { minBits += 1; }
	return minBits;
    }

    /**
     * Calculate the number of bits needed to represent
     * <code>val</code> interpreted as a signed integer
     * @param val a value
     * @return the minimum number of bits needed to represent
     * <code>val</code>
     */
    protected static int minBitsS(long val) {
        if (val == 0) return 0;
	if (val<0) val = -val - 1;
	return minBitsU(val) + 1;
    }

    /**
     * Calculate the "padded length" of a value that is
     * <code>length</code> bits long.
     *
     * The "padded length" is 8 times the minimum number of bytes to
     * hold the value.
     */
    protected static long paddedLength(long length) {
	return (length/8)*8 + (length%8!=0?8:0);
    }

    /**
     * Get the floating point representaion of a fixed value.
     * The lowest 16 bits are treated as the fractional part of the value.
     */
    protected static double fromFixed(long fixed) {
	return ((double)fixed) / 0xFFFFL;
    }

    /**
     * Get the fixed point representaion of a value.
     * The lowest 16 bits are treated as the fractional part of the value.
     */
    protected static long toFixed(double value) {
	return Math.round(value * 0xFFFFL);
    }
}
