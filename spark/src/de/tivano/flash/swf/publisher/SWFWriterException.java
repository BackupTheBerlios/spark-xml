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
 * $Id: SWFWriterException.java,v 1.1 2001/06/01 17:25:53 kunze Exp $
 */

package de.tivano.flash.swf.publisher;

import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;

/**
 * Signal an error in writing SWF data.
 * @author Richard Kunze
 */
public class SWFWriterException extends SAXParseException {

    /** @see SAXParseException#SAXParseException(String, Locator) */
    public SWFWriterException(String message, Locator locator) {
	super(message, locator);
    }

    /** @see SAXParseException#SAXParseException(String, Locator, Exception) */
    public SWFWriterException(String message, Locator locator, Exception e) {
	super(message, locator, e);
    }
}
