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
 * $Id: SWFRectangleTest.java,v 1.5 2002/05/22 17:11:17 richard Exp $
 */

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

    /** The bit input stream to read the rectangle from */
    private BitInputStream data;

    /** The rectangle value to compare against */
    private final long XMIN;
    private final long XMAX;
    private final long YMIN;
    private final long YMAX;

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
    private byte[] createRectangle(int nbits,
				   long xmin, long xmax,
				   long ymin, long ymax)
    {
	assertTrue("The field size must be between 0 and 31",
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

    /**
     * Setup the test
     * @param nbits the number of bits for the rectangle value fields
     * @param xmin the minimum X coordinate
     * @param xmax the maximum X coordinate
     * @param ymin the minimum Y coordinate
     * @param ymax the maximum Y coordinate
     */
    public SWFRectangleTest(int nbits,
			    long xmin, long xmax,
			    long ymin, long ymax) {
	super("Min: ("   + xmin + ", " + ymin +
	      ") Max: (" + xmax + ", " + ymax +
	      ") Bits: " + nbits);
	data = new BitInputStream(new ByteArrayInputStream(
		    createRectangle(nbits, xmin, xmax, ymin, ymax)));
	XMIN = xmin;
	YMIN = ymin;
	XMAX = xmax;
	YMAX = ymax;
    }

    /** Do the test */
    public void runTest() throws Exception {
	SWFRectangle rect = new SWFRectangle(data);
	assertEquals(XMIN, rect.getXMin());
	assertEquals(XMAX, rect.getXMax());
	assertEquals(YMIN, rect.getYMin());
	assertEquals(YMAX, rect.getYMax());
    }

    /** Create the test suite */
    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new SWFRectangleTest(0, 0, 0, 0, 0));
	for (int nbits=1; nbits<32; nbits++) {
	    long maxNeg = -1L << (nbits-1);
	    long maxPos = -1L >>> (64-nbits+1);
	    long wrap   = 1 << (nbits-1);
	    suite.addTest(new SWFRectangleTest(nbits,
					       1%wrap, 2%wrap,
					       3%wrap, 4%wrap));
	    suite.addTest(new SWFRectangleTest(nbits,
					       maxNeg, maxPos,
					       maxNeg, maxPos));
	    
	}
	return suite;
    }
}
