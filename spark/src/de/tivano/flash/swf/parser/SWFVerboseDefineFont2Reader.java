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
 * $Id: SWFVerboseDefineFont2Reader.java,v 1.1 2001/05/28 17:51:28 kunze Exp $
 */

package de.tivano.flash.swf.parser;

/**
 * Tag reader for SWF DefineFont2 tags.
 *
 * <p>This tag reader will produce nearly the same structure as {@link
 * SWFDefineFont2Reader}, the sole difference is that the more verbose
 * <code>&lt;Shape&gt;</code> element (see {@link SWFShapereader}) is
 * produced instead of <code>&lt;ShapRaw&gt;</code>.
 * Replace the handler for SWF tag id 48 with an instance of this
 * class if you want structured access to the outlines of each glyph
 * in the font, but keep in mind that this will drastically increase
 * the size of the output document.</p>
 *
 * @author Richard Kunze
 */
public class SWFVerboseDefineFont2Reader extends SWFDefineFont2Reader {
    /** Constructor. Sets the embedded shape handler */
    public SWFVerboseDefineFont2Reader() {
	shapeReader = new SWFShapeReader();
    }
}
