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
 * $Id: BitInputStreamTest.java,v 1.3 2001/05/14 14:17:50 kunze Exp $
 */

package de.tivano.flash.swf.common;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.ByteArrayInputStream;
import java.io.EOFException;

/**
 * JUnit test case for {@link BitInputStream}.
 * @author Richard Kunze
 */
public class BitInputStreamTest extends TestCase {

    /** Bit patterns for testing the readUXXX() methods */
    private static final int[] PATTERNS = {
	0x00, 0xff, 0x55, 0xaa, 0x33, 0xcc, 0xff00
    };

    /** the input stream for testing */
    BitInputStream stream;
    
    /** Array length for the test byte array */
    private static final int ARRAY_LENGTH = 1024;

    /** The test apttern for this test */
    private byte[] pattern;

    /** @see TestCase#TestCase */
    public BitInputStreamTest(String name, int pat) {
	super(name);
	pattern = new byte[ARRAY_LENGTH];
	if (pat <= 0xFF) {
	    for (int i=0; i<pattern.length; i++) {
		pattern[i] = (byte)pat;
	    }
	} else {
	    for (int i=0; i<(pattern.length/2)*2; i+=2) {
		pattern[i]   = (byte) (pat >> 8);
		pattern[i+1] = (byte) (pat & 0xFF);
	    }
	}
    }

    /** @see TestCase#TestCase */
    public BitInputStreamTest(String name, byte[] data) {
	super(name);
	pattern = data;
    }

    /** Build the fixture */
    public void setUp() {
	stream = new BitInputStream(new ByteArrayInputStream(pattern));
    }

    /**
     * Read <code>n</code> bits from the pattern, starting at
     * bit position <code>pos</code>, with sign expansion.
     */
    private long readSBits(int n, int pos) {
	return signExpand(readUBits(n, pos), n);
    }

    /** Expand the sign of an <code>n</code>-bit value */
    private long signExpand(long unsigned, int n) {
	long wrap = 1L<<(n-1);
	if (unsigned >= wrap) {
	    return -2 * wrap + unsigned;
	} else return unsigned;
    }
    
    /**
     * Read <code>n</code> bits from the pattern, starting at
     * bit position <code>pos</code>.
     */
    private long readUBits(int n, int pos) {
	long retval = 0;
	int end = pos + n;
	if (end > pattern.length * 8) end = pattern.length * 8;
	while (pos < end) {
	    int mask  = 0x80 >>> (pos%8);
	    byte value = pattern[pos/8];
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
     * Test <code>readUBits()</code> with a given read size.
     */
    public void testReadUBits(int bits) throws Exception {
	for (int i=0; i<(pattern.length*8/bits)*bits; i += bits) {
	    long expected = readUBits(bits, i);
	    long actual   = stream.readUBits(bits);
	    assertEquals("at position " + i, expected, actual);
	}

	// finally, try to read beyond the end of the stream
	try {
	    fail("Expected an EOFException, got " + stream.readUBits(bits));
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readSBits()</code> with a given read size.
     */
    public void testReadSBits(int bits) throws Exception {
	for (int i=0; i<(pattern.length*8/bits)*bits; i += bits) {
	    long expected = readSBits(bits, i);
	    long actual   = stream.readSBits(bits);
	    assertEquals("at position " + i, expected, actual);
	}

	// finally, try to read beyond the end of the stream
	try {
	    stream.readSBits(bits);
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readBit()</code>.
     */
    public void testReadBit() throws Exception {
	for (int i=0; i<pattern.length*8; i++) {
	    boolean expected = readUBits(1, i) == 1;
	    boolean actual   = stream.readBit();
	    assertEquals("at position " + i, expected, actual);
	}

	// finally, try to read beyond the end of the stream
	try {
	    stream.readBit();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readUByte()</code>.
     */
    public void testReadUByte() throws Exception {
	for (int i=0; i<pattern.length*8; i+=8) {
	    long expected = readUBits(8, i);
	    int actual   = stream.readUByte();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readUByte();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readUW16MSB()</code>.
     */
    public void testReadUW16MSB() throws Exception {
	for (int i=0; i<pattern.length*8; i+=16) {
	    int expected = (int)readUBits(16, i);
	    int actual   = stream.readUW16MSB();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readUW16MSB();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readUW16LSB()</code>.
     */
    public void testReadUW16LSB() throws Exception {
	for (int i=0; i<pattern.length*8; i+=16) {
	    int expected = (int)(readUBits(8, i) | readUBits(8, i+8) << 8);
	    int actual   = stream.readUW16LSB();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readUW16LSB();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readUW32MSB()</code>.
     */
    public void testReadUW32LSB() throws Exception {
	for (int i=0; i<pattern.length*8; i+=32) {
	    long expected = readUBits(8, i) |
		            readUBits(8, i+8)  << 8 |
		            readUBits(8, i+16) << 16 |
		            readUBits(8, i+24) << 24;
	    long actual   = stream.readUW32LSB();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readUW32LSB();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }
    
    /**
     * Test <code>readUW32LSB()</code>.
     */
    public void testReadUW32MSB() throws Exception {
	for (int i=0; i<pattern.length*8; i+=32) {
	    long expected = readUBits(32, i);
	    long actual   = stream.readUW32MSB();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readUW32MSB();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }
    
    /**
     * Test <code>readSByte()</code>.
     */
    public void testReadSByte() throws Exception {
	for (int i=0; i<pattern.length*8; i+=8) {
	    int expected = (int)readSBits(8, i);
	    int actual   = stream.readSByte();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readSByte();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readSW16MSB()</code>.
     */
    public void testReadSW16MSB() throws Exception {
	for (int i=0; i<pattern.length*8; i+=16) {
	    int expected = (int)readSBits(16, i);
	    int actual   = stream.readSW16MSB();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readSW16MSB();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readSW16LSB()</code>.
     */
    public void testReadSW16LSB() throws Exception {
	for (int i=0; i<pattern.length*8; i+=16) {
	    int expected = (int)signExpand(readUBits(8, i) |
					   readUBits(8, i+8) << 8,
					   16);
	    int actual   = stream.readSW16LSB();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readSW16LSB();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readSW32MSB()</code>.
     */
    public void testReadSW32MSB() throws Exception {
	for (int i=0; i<pattern.length*8; i+=32) {
	    long expected = readSBits(32, i);
	    long actual   = stream.readSW32MSB();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readSW32MSB();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>readSW32LSB()</code>.
     */
    public void testReadSW32LSB() throws Exception {
	for (int i=0; i<pattern.length*8; i+=32) {
	    long expected = signExpand(readUBits(8, i) |
				       readUBits(8, i+8) << 8 |
				       readUBits(8, i+16) << 16 |
				       readUBits(8, i+24) << 24,
				       32);
	    long actual   = stream.readSW32LSB();
	    assertEquals("at position " + i, expected, actual);
	}
	
	// finally, try to read beyond the end of the stream
	try {
	    stream.readSW32LSB();
	    fail("Expected an EOFException");
	} catch (EOFException e) {}
    }

    /**
     * Test <code>countRemainingBits()</code>.
     */
    public void testCountRemainingBits() throws Exception {
	for (int i=pattern.length*8; i>0; i--) {
	    assertEquals(i%8, stream.countRemainingBits());
	    stream.readUBits(1);
	}
    }

    /**
     * Test <code>isAtByteBoundary()</code>.
     */
    public void testIsAtByteBoundary() throws Exception {
	for (int i=0; i<pattern.length*8; i++) {
	    assertEquals(i%8 == 0, stream.isAtByteBoundary());
	    stream.readUBits(1);
	}
    }

    /**
     * Test <code>read()</code> with a given bit shift.
     * This test assumes a working
     * <code>BitInputstream.readUBits()</code> method.
     */
    public void testRead(int shift) throws Exception {
	assert("bit shift must be between 0 and 7", shift >=0 && shift<8);
	stream.readUBits(shift);
	int pos = shift;
	for (int i=0; i<pattern.length; i++) {
	    int expected = (int)readUBits(8, pos);
	    int actual   = stream.read();
	    assertEquals("position " + i + ":", expected, actual);
	    pos += 8;
	}
	
	// Next read must return -1 because we're at the end of the stream
	assertEquals(-1, stream.read());
    }

    /**
     * Test <code>read(byte[])</code> with a given bit shift.
     * This test assumes a working
     * <code>BitInputstream.readUBits()</code> method.
     */
    public void testRead2(int shift) throws Exception {
	assert("bit shift must be between 0 and 7", shift >=0 && shift<8);
	stream.readUBits(shift);
	byte[] result = new byte[pattern.length];
	// Initialized the result to something different
	// than the expected result from stream.read()
	for (int i=0; i<result.length; i++) {
	    result[i] = (byte)~readUBits(8, i*8+shift);
	}

	// Try reading into a null array
	try {
	    stream.read(null);
	    fail("Expected a NullPointerException");
	} catch (NullPointerException e) {}

	// Try reading into an empty array
	byte[] empty = new byte[0];
	assertEquals(0, stream.read(empty));

	// finally, try reading the entire stream.
	int bytesRead = stream.read(result);
	assertEquals("# bytes read: ", pattern.length, bytesRead);
	for (int i=0; i<result.length; i++) {
	    assertEquals("result[" + i + "]", (byte)readUBits(8, i*8+shift), result[i]);
	}
    }

    /**
     * Test <code>read(byte[], int, int)</code> with a given bit
     * shift, offset and read length.
     * This test assumes a working
     * <code>BitInputstream.readUBits()</code> method.
     */
    public void testRead3(int shift, int offset, int len) throws Exception {
	assert("bit shift must be between 0 and 7", shift >=0 && shift<8);
	stream.readUBits(shift);
	byte[] result = new byte[offset + len + 10];

	int expectedBytesRead = (len>pattern.length?pattern.length:len);
	// Initialized the result to something unique, and make sure
	// that the positions expected to be changed by stream.read()
	// are different from the expected result from stream.read().
	for (int i=0; i<result.length; i++) {
	    if (i>=offset && i<offset+expectedBytesRead) {
		result[i] = (byte)~readUBits(8, (i-offset)*8+shift);
	    } else result[i] = (byte)i;
	}

	// Try reading into a null array
	try {
	    stream.read(null, 0, 0);
	    fail("Expected a NullPointerException");
	} catch (NullPointerException e) {}

	// Try reading with a negative offset
	try {
	    stream.read(result, -1, len);
	    fail("Expected an IndexOutOfBoundsException");
	} catch (IndexOutOfBoundsException e) {}

	// Try reading with a negative length
	try {
	    stream.read(result, 0, -1);
	    fail("Expected an IndexOutOfBoundsException");
	} catch (IndexOutOfBoundsException e) {}
		 
	// Try reading with off+len > result.length
	try {
	    stream.read(result, offset, result.length+1);
	    fail("Expected an IndexOutOfBoundsException");
	} catch (IndexOutOfBoundsException e) {}

	// Make sure the result array is still unchanged
	for (int i=0; i<result.length; i++) {
	    if (i>=offset && i<offset+expectedBytesRead) {
		assertEquals("result[" + i + "]",
			     (byte)~readUBits(8, (i-offset)*8+shift),
			     result[i]);
	    } else assertEquals("result[" + i + "]", (byte)i, result[i]);
	}
	
	// finally, try reading.
	int bytesRead = stream.read(result, offset, len);
	assertEquals("# bytes read: ", expectedBytesRead, bytesRead);
	
	for (int i=0; i<result.length; i++) {
	    if (i>=offset && i<offset+expectedBytesRead) {
		assertEquals("result[" + i + "] (" + offset + ", " +
			     (offset + expectedBytesRead) + ")",
			     (byte)readUBits(8, (i-offset)*8+shift),
			     result[i]);
	    } else assertEquals("result[" + i + "]", (byte)i, result[i]);
	}
    }

    /**
     * Test the <code>skipBits()</code> method with a given skip
     * length and bit shift.
     * This test assumes working <code>readUBits()</code> and
     * <code>countRemainingBits()</code> methods.
     */
    public void testSkipBits(int shift, int n) throws Exception {
	assert("Bit shift must be 0..16", shift>=0 && shift < 16);
	// Build a test data array where skips can be easily verified.
	byte[] data = new byte[pattern.length];
	if (n + shift < pattern.length * 8) {
	    // mark the position directly behind the skip.
	    int value = 1;
	    for (int i=0; i<7-(n+shift)%8; i++) { value = value << 1; }
	    data[(n+shift)/8] = (byte)value;
	}
	
	BitInputStream stream =
	    new BitInputStream(new ByteArrayInputStream(data));
	// Init stream state according to the bit shift
	stream.readUBits(shift);
	// Try to skip a negative amount of data. This should not
	// change the stream state at all...
	assertEquals(0, stream.skipBits(-n));

	if (n + shift >= pattern.length * 8) {
	    assertEquals("# bits skipped:", pattern.length*8-shift,
			 stream.skipBits(n));
	    // make sure we skipped to the end of the stream
	    try {
		assert("Expected EOF, but found remaining bits",
		       stream.countRemainingBits() == 0);
		// Make sure we're actually at the end of the stream
		// and not just at a byte boundary
		fail("Expected EOF, but got" + stream.readUBits(1));
	    } catch (EOFException e) {}
	} else {
	    assertEquals("# bits skipped:", n, stream.skipBits(n));
	    // nmake sure we're at the right position
	    assertEquals(1, stream.readUBits(1));
	}
    }

    /**
     * Test the <code>skip()</code> method with different bit shifts.
     * This test assumes working <code>readUBits()</code> and
     * <code>countRemainingBits()</code> methods.
     */
    public void testSkip(int shift, int n) throws Exception {
	assert("Bit shift must be 0..16", shift>=0 && shift < 16);
	// Build a test data array where skips can be easily verified.
	byte[] data = new byte[pattern.length];
	if ((n*8 + shift)/8 < pattern.length) {
	    // mark the position directly behind the skip.
	    int value = 1;
	    for (int i=0; i<7-shift%8; i++) { value = value << 1; }
	    data[n+shift/8] = (byte)value;
	}

	BitInputStream stream =
	    new BitInputStream(new ByteArrayInputStream(data));
	// Initialize state according to the bitshift...
	stream.readUBits(shift);
	// Try to skip a negative amount of data. This should not
	// change the stream state at all...
	assertEquals(0, stream.skip(-n));
	
	if ((n*8 + shift)/8 >= pattern.length) {
	    int expectedSkip = pattern.length - shift/8;
	    // skip() doesn't report the final, incomplete byte at the
	    // end of the stream. See BitInputStream.skip() javadoc.
	    if (shift%8 != 0) expectedSkip--;
	    
	    // make sure we skipped to the end of the stream EOF
	    assertEquals("# bytes skipped:",
			 expectedSkip, stream.skip(n));
	    try {
		assert("Expected EOF, but found remaining bits",
		       stream.countRemainingBits() == 0);
		// Make sure we're actually at the end of the stream
		// and not just at a byte boundary
		fail("Expected EOF, but got" + stream.readUBits(1));
	    } catch (EOFException e) {}
	} else {
	    assertEquals("# bytes skipped:", n, stream.skip(n));
	    assertEquals(1, stream.readUBits(1));
	}
    }

    /**
     * Tests the <code>availableBits()</code> method. This test
     * assumes a working <code>readUBits()</code> method.
     */
    public void testAvailableBits() throws Exception {
	for (long i=pattern.length*8; i>0; i--) {
	    assertEquals(i, stream.availableBits());
	    stream.readUBits(1);
	}
	assertEquals(0, stream.availableBits());
    }
    
    /**
     * Tests the <code>mark()</code> and <code>reset()</code> methods
     * at different bit positions.  This test assumes
     * working <code>skipBitsBits()</code>, <code>readUBits()</code>
     * and <code>availableBits()</code> methods, and it relies on the
     * behaviour of <code>ByteArrayInputStream.available()</code>
     * documented for JDK 1.2 and 1.3.
     *
     * <p><em>Note: this method only tests the case where
     * <code>markSupported() == true</code> and <code>reset()</code>
     * does not throw an <code>IOException</code>.</em></p>
     */
    public void testMark(int n) throws Exception {	
	// First test reset() without mark()
	long expectedRemaining = stream.availableBits();
	long skipped = stream.skipBits(n);
	assertEquals(expectedRemaining-skipped, stream.availableBits());
	stream.reset();
	assertEquals(expectedRemaining, stream.availableBits());

	// Now test reset() with a preceding mark() at position n
	stream.skipBits(n);
	expectedRemaining =  stream.availableBits();
	stream.mark(n);
	skipped = stream.skipBits(n);
	assertEquals(expectedRemaining - skipped,
		     stream.availableBits());
	stream.reset();
	assertEquals(expectedRemaining, stream.availableBits());
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
	    
	    // Test readUBits() and readSBits() with all possible word lengths
	    for (int bits=1; bits<57; bits++) {
		final int BITS = bits;

	        name = header + "readUBits(" + bits + ")";
		test = new BitInputStreamTest(name, PATTERNS[p]) {
			public void runTest() throws Exception {
			    this.testReadUBits(BITS);
			}
		    };
		suite.addTest(test);
		
		name  = header + "readSBits(" + bits + ")";
		test = new BitInputStreamTest(name, PATTERNS[p]) {
			public void runTest() throws Exception {
			    this.testReadSBits(BITS);
			}
		    };
		suite.addTest(test);
	    }

	    // Test the standard InputStream read(...) methods with
	    // different bit shifts.
	    for (int shift=0; shift < 8; shift++) {
		final int SHIFT = shift;
		name = header + "read() with bit shift " + shift;
		test = new BitInputStreamTest(name, PATTERNS[p]) {
			public void runTest() throws Exception {
			    this.testRead(SHIFT);
			}
		    };
		suite.addTest(test);

		name = header + "read(byte[]) with bit shift " + shift;
		test = new BitInputStreamTest(name, PATTERNS[p]) {
			public void runTest() throws Exception {
			    this.testRead2(SHIFT);
			}
		    };
		suite.addTest(test);

		name = header + "read(byte[], 0, " + ARRAY_LENGTH +
		    ") with bit shift " + shift;
		test = new BitInputStreamTest(name, PATTERNS[p]) {
			public void runTest() throws Exception {
			    this.testRead3(SHIFT, 0, ARRAY_LENGTH);
			}
		    };
		suite.addTest(test);

		name = header + "read(byte[], " + (ARRAY_LENGTH/4) +
		    ", " + (ARRAY_LENGTH/2) + ") with bit shift " + shift;
		test = new BitInputStreamTest(name, PATTERNS[p]) {
			public void runTest() throws Exception {
			    this.testRead3(SHIFT,
					   ARRAY_LENGTH/4, ARRAY_LENGTH/2);
			}
		    };
		suite.addTest(test);

		name = header + "read(byte[], 0, " + (2*ARRAY_LENGTH) +
		    ") with bit shift " + shift;
		test = new BitInputStreamTest(name, PATTERNS[p]) {
			public void runTest() throws Exception {
			    this.testRead3(SHIFT, 0, 2*ARRAY_LENGTH);
			}
		    };
		suite.addTest(test);

		name = header + "read(byte[], 123, " + (2*ARRAY_LENGTH) +
		    ") with bit shift " + shift;
		test = new BitInputStreamTest(name, PATTERNS[p]) {
			public void runTest() throws Exception {
			    this.testRead3(SHIFT, 123, 2*ARRAY_LENGTH);
			}
		    };
		suite.addTest(test);
	    }


	    // Test the read...() convenience methods.
	    name = header + "readBit()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadBit();
		    }
		};
	    suite.addTest(test);

	    name = header + "readUByte()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadUByte();
		    }
		};
	    suite.addTest(test);

	    name = header + "readUW16MSB()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadUW16MSB();
		    }
		};
	    suite.addTest(test);

	    name = header + "readUW32MSB()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadUW32MSB();
		    }
		};
	    suite.addTest(test);

	    name = header + "readUW16LSB()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadUW16LSB();
		    }
		};
	    suite.addTest(test);

	    name = header + "readUW32LSB()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadUW32LSB();
		    }
		};
	    suite.addTest(test);

	    name = header + "readSByte()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadSByte();
		    }
		};
	    suite.addTest(test);

	    name = header + "readSW16MSB()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadSW16MSB();
		    }
		};
	    suite.addTest(test);

	    name = header + "readSW32MSB()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadSW32MSB();
		    }
		};	    
	    suite.addTest(test);
	    
	    name = header + "readSW16LSB()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadSW16LSB();
		    }
		};
	    suite.addTest(test);

	    name = header + "readSW32LSB()";
	    test = new BitInputStreamTest(name, PATTERNS[p]) {
		    public void runTest() throws Exception {
			this.testReadSW32LSB();
		    }
		};
	    suite.addTest(test);
	}

	// Do pattern independent tests...
	byte[] data = new byte[ARRAY_LENGTH];
	for (int i=0; i<data.length; i++) data[i] = -1;

	name = "countRemainingBits()";
	test = new BitInputStreamTest(name, data) {
		public void runTest() throws Exception {
		    this.testCountRemainingBits();
		}
	    };
	suite.addTest(test);
	
	name ="availableBits()";
	test = new BitInputStreamTest(name, data) {
		public void runTest() throws Exception {
		    this.testAvailableBits();
		}
	    };
	suite.addTest(test);
	
	name = "isAtByteBoundary()";
	test = new BitInputStreamTest(name, data) {
		public void runTest() throws Exception {
		    this.testIsAtByteBoundary();
		}
	    };
	suite.addTest(test);

	// Test different skip lengths, including skips beyond the end
	// of the stream, and different bit shifts to begin with.
	// Make sure the initial bit shift crosses at least one byte boundary
	for (int shift = 0; shift<16; shift++) {
	    final int SHIFT = shift;
	    
	    name = "skipBits(0) with bit shift " + shift;
	    test = new BitInputStreamTest(name, data) {
		    public void runTest() throws Exception {
			this.testSkipBits(SHIFT, 0);
		    }
		};
	    suite.addTest(test);
	    for (int skip = 1; skip < 16*ARRAY_LENGTH;
		 skip += (ARRAY_LENGTH*8)/13) {
		final int SKIP = skip;
		name = "skipBits(" + skip + ") with bit shift " + SHIFT;
		test = new BitInputStreamTest(name, data) {
			public void runTest() throws Exception {
			    this.testSkipBits(SHIFT, SKIP);
			}
		    };
		suite.addTest(test);
	    }
	    
	    name = "skip(0) with bit shift " + SHIFT;
	    test = new BitInputStreamTest(name, data) {
		    public void runTest() throws Exception {
			this.testSkip(SHIFT, 0);
		    }
		};
	    suite.addTest(test);
	    for (int skip = 1; skip < 2*ARRAY_LENGTH; skip+=ARRAY_LENGTH/13) {
		final int SKIP = skip;
		name = "skip(" + skip + ") with bit shift " + SHIFT;
		test = new BitInputStreamTest(name, data) {
			public void runTest() throws Exception {
			    this.testSkip(SHIFT, SKIP);
			}
		    };
		suite.addTest(test);
	    }
	}

	// test mark()/reset() at different positions. Make sure some
	// of the positions are beyond the end of the stream.
	for (int pos = 1; pos < 16*ARRAY_LENGTH; pos+=(ARRAY_LENGTH*8)/11) {
	    final int POS = pos;
	    name = "mark()/reset() at bit position " + POS;
	    test = new BitInputStreamTest(name, data) {
		    public void runTest() throws Exception {
			this.testMark(POS);
		    }
		};
	    suite.addTest(test);
	}

	return suite;
    }
}
