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
 * $Id: SWFFormatException.java,v 1.2 2001/03/14 12:27:11 kunze Exp $
 */

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
