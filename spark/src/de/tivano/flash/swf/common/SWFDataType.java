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
 * $Id: SWFDataType.java,v 1.1 2001/05/23 14:58:14 kunze Exp $
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
public interface SWFDataType {
    /**
     * Get the length of this objects SWF representation.
     * Note that the length is in bits.
     */
    public long length();

    /**
     * Write the SWF representation of this object to <code>out</code>.
     * @param out the output stream to write on
     * @exception IOException if an I/O error occurs.
     */
    public void write(BitOutputStream out) throws IOException;
}
