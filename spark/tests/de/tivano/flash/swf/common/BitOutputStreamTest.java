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
 * $Id: BitOutputStreamTest.java,v 1.1 2001/05/21 17:52:26 kunze Exp $
 */

package de.tivano.flash.swf.common;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;

/**
 * JUnit test case for {@link BitOutputStream}.
 * @author Richard Kunze
 */
public class BitOutputStreamTest extends TestCase {

    /** Bit patterns for testing the writeXXX() methods */
    private static final int[] PATTERNS = {
	0x00000000, 0xffffffff, 0x55555555, 0xaaaaaaaa, 0x33333333,
	0xcccccccc, 0xff00ff00
    };

    /** the output stream for testing */
    BitOutputStream stream;

    /** the underlying byte stream */
    ByteArrayOutputStream streamRaw;

    /**
     * The number of patterns to write in the testWrite() methods.
     */
    private final int WRITE_REPEAT = 10 * 64;
    
    /** @see TestCase#TestCase */
    public BitOutputStreamTest(String name) {
	super(name);
    }

    /** Build the fixture */
    public void setUp() {
	streamRaw = new ByteArrayOutputStream();
	stream = new BitOutputStream(streamRaw);
    }

    /**
     * Read <code>n</code> bits from the data, starting at
     * bit position <code>pos</code>.
     */
    private long readBits(int n, int pos) {
	byte[] data = streamRaw.toByteArray();
	long retval = 0;
	int end = pos + n;
	if (end > data.length * 8) end = data.length * 8;
	while (pos < end) {
	    int mask  = 0x80 >>> (pos%8);
	    byte value = data[pos/8];
	    while (pos < end && mask != 0) {
		retval = retval << 1;
		if ((mask & value) != 0) retval |= 1;
		mask = mask >>> 1;
		pos++;
	    }
	}
	return retval;
    }
	
    /**
     * Test <code>writeBits()</code> with a given write size and bit pattern.
     */
    public void testWriteBits(long pattern, int bits) throws Exception {
	for (int i=0; i<WRITE_REPEAT; i++) {
	    stream.writeBits(pattern, bits);
	}
	for (int i=0; i<WRITE_REPEAT; i++) {
	    long expected = pattern & (-1L >>> 64-bits);
	    assertEquals("at bit position " + (i*bits),
			 expected, readBits(i*bits, bits));
	}
    }

    /**
     * Test <code>write(int)</code> with a given bit shift and pattern.
     * This test assumes a working
     * <code>BitOutputstream.writeBits()</code> method.
     */
    public void testWrite(byte pattern, int shift) throws Exception {
	assert("bit shift must be between 0 and 7", shift >=0 && shift<8);
	stream.writeBits(0, shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    stream.write(pattern);
	}
	stream.writeBits(0, 8-shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    long expected = pattern;
	    assertEquals("at bit position " + (i*8+shift),
			 expected, readBits(i*8+shift, 8));
	}
    }

    /**
     * Test <code>write(byte[])</code> with a given bit shift.
     * This test assumes a working
     * <code>BitOutputstream.writeBits()</code> method.
     */
    public void testWrite2(int shift, byte[] data) throws Exception {
    }

    /**
     * Test <code>write(byte[], int, int)</code> with a given bit
     * shift, offset and write length.
     * This test assumes a working
     * <code>BitOutputstream.writeBits()</code> method.
     */
    public void testWrite3(int shift, byte[] data, int offset, int
			   len)
	   throws Exception {
    }

    /**
     * Test <code>writeBit()</code>.
     */
    public void testWriteBit() throws Exception {
	for (int i=0; i<WRITE_REPEAT; i++) {
	    stream.writeBit(i%2 != 0);
	}
	for (int i=0; i<WRITE_REPEAT; i++) {
	    assertEquals("at bit position " + i,
			 i%2, readBits(i, 1));
	}
    }

    /**
     * Test <code>writeByte()</code> with a given bit shift and pattern.
     * This test assumes a working
     * <code>BitOutputstream.writeBits()</code> method.
     */
    public void testWriteByte(byte pattern, int shift) throws Exception {
	assert("bit shift must be between 0 and 7", shift >=0 && shift<8);
	stream.writeBits(0, shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    stream.writeByte(pattern);
	}
	stream.writeBits(0, 8-shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    long expected = pattern;
	    assertEquals("at bit position " + (i*8+shift),
			 expected, readBits(i*8+shift, 8));
	}
    }

    /**
     * Test <code>writeW16MSB()</code> with a given bit shift and pattern.
     * This test assumes a working
     * <code>BitOutputstream.writeBits()</code> method.
     */
    public void testWriteW16MSB(short pattern, int shift) throws Exception {
	assert("bit shift must be between 0 and 7", shift >=0 && shift<8);
	stream.writeBits(0, shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    stream.writeW16MSB(pattern);
	}
	stream.writeBits(0, 8-shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    long expected = pattern;
	    assertEquals("at bit position " + (i*16+shift),
			 expected, readBits(i*16+shift, 16));
	}
    }

    /**
     * Test <code>writeW32MSB()</code> with a given bit shift and pattern.
     * This test assumes a working
     * <code>BitOutputstream.writeBits()</code> method.
     */
    public void testWriteW32MSB(int pattern, int shift) throws Exception {
	assert("bit shift must be between 0 and 7", shift >=0 && shift<8);
	stream.writeBits(0, shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    stream.writeW32MSB(pattern);
	}
	stream.writeBits(0, 8-shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    long expected = pattern;
	    assertEquals("at bit position " + (i*32+shift),
			 expected, readBits(i*32+shift, 32));
	}
    }

    /**
     * Test <code>writeW16LSB()</code> with a given bit shift and pattern.
     * This test assumes a working
     * <code>BitOutputstream.writeBits()</code> method.
     */
    public void testWriteW16LSB(short pattern, int shift) throws Exception {
	assert("bit shift must be between 0 and 7", shift >=0 && shift<8);
	stream.writeBits(0, shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    stream.writeW16LSB(pattern);
	}
	stream.writeBits(0, 8-shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    long expected = (pattern & 0xFF) << 8 | (pattern >> 8);
	    assertEquals("at bit position " + (i*16+shift),
			 expected, readBits(i*16+shift, 16));
	}
    }

    /**
     * Test <code>writeW32LSB()</code> with a given bit shift and pattern.
     * This test assumes a working
     * <code>BitOutputstream.writeBits()</code> method.
     */
    public void testWriteW32LSB(int pattern, int shift) throws Exception {
	assert("bit shift must be between 0 and 7", shift >=0 && shift<8);
	stream.writeBits(0, shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    stream.writeW32LSB(pattern);
	}
	stream.writeBits(0, 8-shift);
	for (int i=0; i<WRITE_REPEAT; i++) {
	    long expected = 0;
	    for (int j=0; j<4; j++) {
		expected = (pattern & 0xFF);
		pattern  = (pattern >> 8);
	    }
	    assertEquals("at bit position " + (i*32+shift),
			 expected, readBits(i*32+shift, 32));
	}
    }

    /**
     * Test <code>countRemainingBits()</code>.
     */
    public void testCountRemainingBits() throws Exception {
	for (int i=WRITE_REPEAT*8; i>0; i--) {
	    assertEquals(i%8, stream.countRemainingBits());
	    stream.writeBits(1, 1);
	}
    }

    /**
     * Test <code>isAtByteBoundary()</code>.
     */
    public void testIsAtByteBoundary() throws Exception {
	for (int i=0; i<128; i++) {
	    assertEquals(i%8 == 0, stream.isAtByteBoundary());
	    stream.writeBits(1, 1);
	}
    }

    /**
     * Test <code>padToByteBoundary()</code>.
     * This test assumes a working
     * <code>BitOutputstream.writeBits()</code> method.
     */
    public void testPadToByteBoundary() throws Exception {
    }

    /**
     * Test the <code>flush()</code> method.
     */
    public void testFlush() throws Exception {
    }

    /**
     * Test the <code>padAndFlush()</code> method.
     */
    public void testPadAndFlush() throws Exception {
    }

    /**
     * Test the <code>close()</code> method.
     */
    public void testClose() throws Exception {
    }

    /**
     * Build the test suite
     */
    public static Test suite() {

	TestSuite suite = new TestSuite();
	String name;
	Test test;

	// Test read routines with different bit patterns.
	for (int p=0; p<PATTERNS.length; p++) {
	    String header = Integer.toString(PATTERNS[p], 2);
	    while (header.length() < 8) {
		header = "0" + header;
	    }
	    header = "pattern '" + header + "': ";
	    final int PATTERN = PATTERNS[p];
	    
	    // Test writeBits() with all possible word lengths
	    for (int bits=1; bits<57; bits++) {
		final int BITS = bits;

	        name = header + "writeBits(" + PATTERN + ", " + bits + ")";
		test = new BitOutputStreamTest(name) {
			public void runTest() throws Exception {
			    this.testWriteBits(PATTERN, BITS);
			}
		    };
		suite.addTest(test);
	    }

	    // Test the byte/word write() methods with
	    // different bit shifts.
	    for (int shift=0; shift < 8; shift++) {
		final int SHIFT = shift;
		name = header + "write(" + PATTERN + ") with bit shift "
		    + shift;
		test = new BitOutputStreamTest(name) {
			public void runTest() throws Exception {
			    this.testWrite((byte)PATTERN, SHIFT);
			}
		    };
		suite.addTest(test);

		name = header + "writeByte(" + PATTERN + ") with bit shift "
		    + shift;
		test = new BitOutputStreamTest(name) {
			public void runTest() throws Exception {
			    this.testWriteByte((byte)PATTERN, SHIFT);
			}
		    };
		suite.addTest(test);

		name = header + "writeW16MSB(" + PATTERN + ") with bit shift "
		    + shift;
		test = new BitOutputStreamTest(name) {
			public void runTest() throws Exception {
			    this.testWriteW16MSB((short)PATTERN, SHIFT);
			}
		    };
		suite.addTest(test);

		name = header + "writeW32MSB(" + PATTERN + ") with bit shift "
		    + shift;
		test = new BitOutputStreamTest(name) {
			public void runTest() throws Exception {
			    this.testWriteW32MSB((int)PATTERN, SHIFT);
			}
		    };
		suite.addTest(test);

		name = header + "writeW16LSB(" + PATTERN + ") with bit shift "
		    + shift;
		test = new BitOutputStreamTest(name) {
			public void runTest() throws Exception {
			    this.testWriteW16LSB((short)PATTERN, SHIFT);
			}
		    };
		suite.addTest(test);

		name = header + "writeW32LSB(" + PATTERN + ") with bit shift "
		    + shift;
		test = new BitOutputStreamTest(name) {
			public void runTest() throws Exception {
			    this.testWriteW32LSB((int)PATTERN, SHIFT);
			}
		    };
		suite.addTest(test);


	    }
	}

	// Do pattern independent tests...
	name = "writeBit()";
	test = new BitOutputStreamTest(name) {
		public void runTest() throws Exception {
		    this.testWriteBit();
		}
	    };
	suite.addTest(test);
	
	name = "countRemainingBits()";
	test = new BitOutputStreamTest(name) {
		public void runTest() throws Exception {
		    this.testCountRemainingBits();
		}
	    };
	suite.addTest(test);
	
	name = "isAtByteBoundary()";
	test = new BitOutputStreamTest(name) {
		public void runTest() throws Exception {
		    this.testIsAtByteBoundary();
		}
	    };
	suite.addTest(test);

	return suite;
    }
}
