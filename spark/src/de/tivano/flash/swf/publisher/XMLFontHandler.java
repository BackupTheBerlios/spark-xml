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
 * $Id: XMLFontHandler.java,v 1.2 2001/06/27 16:21:56 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import de.tivano.flash.swf.common.SWFTypes;

/**
 * Handler for the <em>&lt;Font&gt;</em> XML tag.
 * The <em>&lt;Font&gt;</em> tag defines a font which is expressed in
 * SWF as either a DefineFont2 structure or a
 * DefineFont/DefineFontInfo pair.
 * @see de.tivano.flash.swf.parse.SWFDefineFont2Reader
 * @see de.tivano.flash.swf.parse.SWFDefineFontReader
 * @see de.tivano.flash.swf.parse.SWFDefineFontInfoReader
 */
public class XMLFontHandler extends SWFTagHandlerBase {
    protected SWFTagWriter createDataObject() { return null; }
}
