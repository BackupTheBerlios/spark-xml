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
 * $Id: SWFShape.java,v 1.8 2002/01/25 13:50:09 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class represents a SWF shape structure.
 * <p>A shape structure consists of a shape header and one or more
 * {@link SWFShapeRecord} structures. It has the following structure:</p>
 * <table border=1 cellspacing=2 cellpadding=7 align=center>
 * <tr>
 *  <td bgcolor="#CCCCCC"><b>Field</b></td>
 *  <td bgcolor="#CCCCCC"><b>Length (bits)</b></td>
 *  <td bgcolor="#CCCCCC"><b>Comment</b></td>
 * </tr>
 * <tr>
 *   <td>nFillBits</td>
 *   <td>4</td>
 *   <td>number of bits in each fill style index value</td>
 * </tr>
 * <tr>
 *   <td>nLineBits</td>
 *   <td>4</td>
 *   <td>number of bits in each line style index value</td>
 * </tr>
 * <tr>
 *   <td>shapes</td>
 *   <td>&gt;= 6</td>
 *   <td>A list of {@link SWFShapeRecord} values</td>
 * </tr>
 * </table>
 * @author Richard Kunze
 */
public class SWFShape extends SWFDataTypeBase {
    private final int FILL_BITS;
    private final int LINE_BITS;

    private ArrayList records = new ArrayList();

    /**
     * Read-only wrapper around an <code>Iterator</code>.
     * this helper class is used to make sure that the
     * iterator returned by <code>getShapeRecords()</code> does not
     * support <code>remove()</code> even when the underlying iterator
     * supports it.
     */
    private static class ReadOnlyIterator implements Iterator{
	private final Iterator DELEGATE;
	public ReadOnlyIterator(Iterator delegate) {
	    DELEGATE = delegate;
	}
	public boolean hasNext() { return DELEGATE.hasNext(); }
	public Object next() { return DELEGATE.next(); }
	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    /**
     * Construct a <code>SWFShape</code> from a bit input stream.
     * @param input the input stream to read from
     * @param useRGBA Flag, inidcates whether fill- and linestyle
     * definitions use RGB or RGBA values.
     * @exception SWFFormatException if the complete shape could
     * not be read from the stream.
     */
    public SWFShape(BitInputStream input, boolean useRGBA) throws IOException {
	try {
	    FILL_BITS = (int)input.readUBits(4);
	    LINE_BITS = (int)input.readUBits(4);
	    // Read the shape records. Note that the number of fill
	    // and line style index bits to used can be changed by a "new
	    // styles" shape record.
	    int currentFillBits = FILL_BITS;
	    int currentLineBits = LINE_BITS;
	    SWFShapeRecord record;
	    do {
		record = SWFShapeRecord.parse(input,
					      currentFillBits,
					      currentLineBits,
					      useRGBA);
		if (record instanceof SWFStateChange) {
		    SWFStateChange s = (SWFStateChange)record;
		    if (s.hasNewStyles()) {
			currentFillBits = s.getFillBits();
			currentLineBits = s.getLineBits();
		    }
		}
		records.add(record);
	    } while (!((record instanceof SWFStateChange)
		        && ((SWFStateChange)record).isEndOfShape()));
	} catch (EOFException e) {
	    throw new SWFFormatException(
              "Premature end of file encoutered while reading a shape");
	}
    }


    public int rawSize;
    /**
     * Construct a <code>SWFShape</code> from a byte array.
     * @param input the byte array to read from
     * @param useRGBA Flag, inidcates whether fill- and linestyle
     * definitions use RGB or RGBA values.
     * @exception SWFFormatException if the complete shape could not be read.
     */
    public SWFShape(byte[] input, boolean useRGBA) throws IOException {
	this(new BitInputStream(new ByteArrayInputStream(input)), useRGBA);
	rawSize = input.length;
    }
    

    /**
     * Get an iterator over the list of shape records (edges and state
     * changes) that make up this shape. The objects in this list are
     * all of type {@link SWFShapeRecord}.
     */
    public Iterator getShapeRecords() {
	return new ReadOnlyIterator(records.iterator());
    }

    /**
     * Get the length of this record. Note that the length is
     * expressed in bits.
     */
    public long length() {
	long length = 8;
	Iterator entries = getShapeRecords();
	while (entries.hasNext()) {
	    length += ((SWFDataType)entries.next()).length();
	}
	return length;
    }

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException {
	out.writeBits(FILL_BITS, 4);
	out.writeBits(LINE_BITS, 4);
	Iterator entries = getShapeRecords();
	while (entries.hasNext()) {
	    ((SWFDataType)entries.next()).write(out);
	}
    }
}
