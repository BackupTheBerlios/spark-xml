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
 * $Id: XMLShowFrameHandler.java,v 1.2 2001/07/02 08:07:22 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import de.tivano.flash.swf.common.SWFTypes;

/**
 * Handler for the <em>&lt;ShowFrame /&gt;</em> XML tag.
 * The <em>&lt;ShowFrame /&gt;</em> tag denotes the end of a frame in
 * SWF. This emtpy marker tag does not hold any additional data. 
 * @author Richard Kunze
 * @see de.tivano.flash.swf.parser.SWFShowFrameReader
 */
public class XMLShowFrameHandler extends SWFTagHandlerBase {
    /**
     * Create the SWF writer object for actually writing out raw data.
     */
    protected SWFTagWriter createDataObject() {
	return new SWFEmptyTagWriter(SWFTypes.SHOW_FRAME);
    }
}