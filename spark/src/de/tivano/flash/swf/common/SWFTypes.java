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
 * $Id: SWFTypes.java,v 1.3 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import java.io.IOException;

/**
 * Constants for the type ID of SWF toplevel sturctures.
 * @author Richard Kunze
 */
public interface SWFTypes {
    public static final int END                 = 0;
    public static final int SHOW_FRAME          = 1;

    public static final int DEFINE_FONT         = 10;
    public static final int DEFINE_FONT2        = 48;
    public static final int DEFINE_FONTINFO     = 13;

    public static final int DEFINE_SHAPE        = 2;

    public static final int DEFINE_TEXTFIELD    = 37;
	
}
