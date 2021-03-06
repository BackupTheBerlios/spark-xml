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
 * $Id: SWFFontWriter.java,v 1.7 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.publisher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.BitOutputStream;
import de.tivano.flash.swf.common.SWFFont;
import de.tivano.flash.swf.common.SWFTagHeader;
import de.tivano.flash.swf.common.SWFDefineFont;
import de.tivano.flash.swf.common.SWFDefineFontInfo;
import de.tivano.flash.swf.common.SWFDefineFont2;
import de.tivano.flash.swf.common.SWFTypes;
import de.tivano.flash.swf.common.SWFDataType;

/**
 * Represents SWF fonts. In SWF, a font is either expressed as a
 * DefineFont2 structure or as a DefineFont/DefineFontInfo pair. This
 * class decides what to use based on the following criteria:
 * <ul>
 * <li>If the font contains layout information, use DefineFont2
 * <li>In all other cases, use a DefineFont/DefineFontInfo pair.
 * </ul>
 * These rules are preliminary and may change to reflect the behaviour
 * of existing software.
 * @author Richard Kunze
 */
public class SWFFontWriter extends SWFTagWriter {

    /** Helper class to write a DefineFont/DefineFontInfo pair */
    private static class DefineFontPair implements SWFDataType {
	private SWFDefineFont font;
	private SWFTagHeader fontInfoHeader =
	    new SWFTagHeader(SWFTypes.DEFINE_FONTINFO);
	private SWFDefineFontInfo fontInfo;

	public DefineFontPair(SWFFont fontDef)
	    throws UnsupportedEncodingException {
	    font = new SWFDefineFont(fontDef);
	    fontInfo = new SWFDefineFontInfo(fontDef);
	}

	public long length() {
	    long infoLength = fontInfo.length();
	    fontInfoHeader.setRecordLength(infoLength/8);
	    return infoLength + fontInfoHeader.length() + font.length();
	}

	public long fontLength() {
	    return font.length();
	}

	public void write(BitOutputStream out) throws IOException {
	    font.write(out);
	    fontInfoHeader.setRecordLength(fontInfo.length()/8);
	    fontInfoHeader.write(out);
	    fontInfo.write(out);
	}
    }
    
    /** The font data */
    private SWFFont font;

    /** The actual writer object */
    SWFDataType fontWriter = null;
    
    /**
     * Construct a new font writer for the given font.
     * @param font the font information to use.
     * @exception IllegalArgumentException if <code>tagID</code> is
     * not a legal SWF data type ID
     */
    public SWFFontWriter(SWFFont font) {
	this.font = font;
    }

    /**
     * Get the length (excluding the header) in bytes.
     * @returns 0
     */
    protected long getDataLength() {
	// We're assuming here that initWrite() has already been called.
	return fontWriter.length() / 8;
    }

    /**
     * Initialize the data.
     * <p>This method decides wheter to write a SWF
     * DefineFont2 tag or a DefineFont/DefineFontInfo pair. Subsequent
     * changes to the {@link SWFFont} object associated with this
     * object will not be reflected in the SWF data.</p>
     */
    protected void initWriteData() throws IOException {
	if (font.hasMetrics()) {
	    fontWriter = new SWFDefineFont2(font);
	    HEADER.setID(SWFTypes.DEFINE_FONT2);
	} else {
	    fontWriter = new DefineFontPair(font); 	    
	    HEADER.setID(SWFTypes.DEFINE_FONT);
	}

	// Drop the reference to the font, it's no longer needed
	font = null;
    }

    /**
     * Write the actual SWF data (excluding the header)
     * to <code>out</code>.
     */
    public void writeData(BitOutputStream out) throws IOException {
	// We're assuming here that initWrite() has already been called.
	fontWriter.write(out);
    }
    
    /** Get the total length (including the header) in bytes. */
    public long getTotalLength() throws IOException {
	doInitDataLength();
	// The length of the header varies with the length of the
	// following record, so we have to set it here to get correct
	// results...
	long dataLength = getDataLength();
	// If the data is a DefineFont/DefineFontInfo pair, the header
	// must be set to the length of the font only. The
	// DefineFontInfo has its own header...
	if (fontWriter instanceof DefineFontPair) {
	    HEADER.setRecordLength(
		      ((DefineFontPair)fontWriter).fontLength()/8);
	} else { 
	    HEADER.setRecordLength(dataLength);
	}
	// SWFHeader.length() returns the length in bits, but for an SWF
	// header, this is always a multiple of 8
	return dataLength + HEADER.length() / 8;
    }

    /**
     * Write the complete SWF data (including the header)
     * to <code>out</code>.
     */
    public void write(BitOutputStream out) throws IOException {
	doInitWriteData();
	// If the data is a DefineFont/DefineFontInfo pair, the header
	// must be set to the length of the font only. The
	// DefineFontInfo has its own header...
	if (fontWriter instanceof DefineFontPair) {
	    HEADER.setRecordLength(
		      ((DefineFontPair)fontWriter).fontLength()/8);
	} else { 
	    HEADER.setRecordLength(getDataLength());
	}
	HEADER.write(out);
	writeData(out);
    }


}
