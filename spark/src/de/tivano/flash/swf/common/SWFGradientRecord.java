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
 * $Id: SWFGradientRecord.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;
import java.io.IOException;
import java.io.EOFException;

/**
 * This abstract class represents one entry in a gradient definition.
 * SWF gradient definitions can either include transparency or
 * not. Concrete subtypes of this class are used to model gradient
 * records with and without support for transparency. This class can
 * act as a factory for the different types of gradient records.
 */
public abstract class SWFGradientRecord extends SWFDataTypeBase {

    /** The ratio */
    private final int RATIO;

    /** Get the ratio. */
    public int getRatio() { return RATIO; }

    /** Get the color. If {@link #hasAlpha} returns
     * <code>false</code>, the returned color has an alpha value of
     * <code>0xff</code>.
     */
    public abstract SWFColorRGBA getColor();

    /** Check if this gradient records includes alpha values. */
    public abstract boolean hasAlpha();

    /**
     * Construct a new gradient record from <code>input</code>.
     * This constructor only reads the ratio. Derived classes must
     * call it first in their own constructors that read from an input
     * stream.
     * @param input the input stream to read from.
     * @param useRGBA flag, determines whether the fill color includes an
     * alpha value or not.
     */
    protected SWFGradientRecord(BitInputStream input)
	throws IOException {
	this(input.readUByte());
    }
    
    /**
     * Construct a new gradient record.
     * This constructor only sets the ratio. Derived classes must
     * call it first in their own constructors that build a gradient
     * record from scratch.
     * @param ration the ration value
     */
    protected SWFGradientRecord(int ratio) {
	RATIO = ratio;
    }
    
    /**
     * Write the SWF representation of this object to
     * <code>out</code>. This method only writes the ratio
     * values. Derived classes <em>must</em> override this method and
     * <em>should</em> call it first in their implementation.
     * @param out the ouput stream to write to
     */
    public void write(BitOutputStream out) throws IOException {
	out.writeBits(RATIO, 8);
    }

    /**
     * Construct a new gradient record from <code>input</code>.
     * Depending on <code>useRGBA</code>, the record is either a
     * {@link SWFSolidGradientRecord} or a {@link
     * SWFTransparentGradientRecord}.
     * @param input the input stream to read from
     * @param useRGBA flag, tells whether to include transparency or
     * not.
     */
    public static SWFGradientRecord parse(BitInputStream input, boolean useRGBA)
	   throws IOException {
	if (useRGBA) return new SWFTransparentGradientRecord(input);
	else return new SWFSolidGradientRecord(input);
    }
}
