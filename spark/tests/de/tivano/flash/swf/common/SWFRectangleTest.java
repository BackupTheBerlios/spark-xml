package de.tivano.flash.swf.common;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.BitSet;

import java.io.ByteArrayInputStream;
import java.io.EOFException;

/**
 * JUnit test case for {@link SWFRectangle}.
 * <p>This test case assumes a working {@link BitInputStream}</p>
 * @author Richard Kunze
 */
public class SWFRectangleTest extends TestCase {

    /**
     * Construct a byte array with the data for an SWF rectangle
     * structure.
     * @param nbits the size of the value bits.
     * @param xmin the minimum X coordinate
     * @param xmax the maximum X coordinate
     * @param ymin the minimum Y coordinate
     * @param ymax the maximum Y coordinate
     * @return a new byte array holding the SWF data structure
     * @throws AssertionFailedError with an appropriate error message
     * if one of the parameters exceeds the range given in the SWF
     * documentation.
     */
    public static byte[] createRectangle(int nbits,
					 long xmin, long xmax,
					 long ymin, long ymax)
    {
	assert("The field size must be between 0 and 31",
	       nbits>=0 && nbits<32);
	BitSet bits = new BitSet(133);
	int pos = 0;
	long mask = 0x10;
	// Put the field length into the bit vector
	for (int i=0; i<5; i++) {
	    if ((mask & nbits) != 0) bits.set(pos);
	    pos++;
	    mask = mask >>> 1;
	}
	long values[] = { xmin, xmax, ymin, ymax };
	for (int i = 0; i < 4; i++) {
	    mask = 1 << (nbits-1);
	    for (int j=0; j<nbits; j++) {
		if ((mask & values[i]) != 0) bits.set(pos);
		pos++;
		mask = mask >>> 1;
	    }
	}

	// Finally, read the bits into a new byte array
	byte[] retval = new byte[pos/8+1];
	for (int i=0; i<=pos/8; i++) {
	    mask  = 0x80;
	    byte value = 0;
	    for (int j=0;j<8;j++) {
		if (bits.get(i*8+j)) value |= mask;
		mask = mask >>> 1;
	    }
	    retval[i] = value;
	}
	return retval;
    }
}
