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
 * $Id: XMLShapeHandler.java,v 1.1 2001/06/28 17:15:47 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import de.tivano.flash.swf.common.SWFShape;

/**
 * Handler for the <em>&lt;Shape&gt;</em> XML tag.
 * <p>The <em>&lt;Shape&gt;</em> tag defines a SWF shape structure which
 * can be part of one of the various DefineShape toplevel
 * structures or a glyph in a font definition.</p>
 */
public class XMLShapeHandler extends XMLHandlerBase {

    /** The shape object to build */
    protected SWFShape shape = null;

    /**
     * Get the shape object.
     * Note: If this method is called before the first call to {@link
     * #endElement}, it returns <code>null</code>. On subsequent
     * calls, the state as of the time of the last call to {@link
     * #endElement} is returned.
     * @return the SWF representation of the <em>&lt;ShapeRaw&gt;</em>
     * XML element.
     */
    public SWFShape getShape() {
	return shape;
    }
}
