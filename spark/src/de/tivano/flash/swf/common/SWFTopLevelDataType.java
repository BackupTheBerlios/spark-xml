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
 * $Id: SWFTopLevelDataType.java,v 1.1 2001/07/02 08:07:22 kunze Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;

/**
 * This interface is implemented by all classes that represent SWF data.
 *
 * It defines basic methods common to all SWF data types.
 *
 * @author Richard Kunze
 */
public interface SWFTopLevelDataType extends SWFDataType {
    /** Get the type id for this data type */
    public int getTagType();
}
