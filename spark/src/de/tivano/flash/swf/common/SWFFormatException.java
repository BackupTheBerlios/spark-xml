package de.tivano.flash.swf.common;

import java.io.IOException;

/**
 * Signal a format error in SWF data.
 * @author Richard Kunze
 */
public class SWFFormatException extends IOException {

    /** @see IOException#IOException() */
    public SWFFormatException() { super(); }

    /** @see IOException#IOException(String) */
    public SWFFormatException(String s) { super(s); }
}
