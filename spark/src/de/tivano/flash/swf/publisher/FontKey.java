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
 * $Id: FontKey.java,v 1.1 2001/06/28 17:16:22 kunze Exp $
 */

package de.tivano.flash.swf.publisher;

/**
 * Key class for retrieving a font from the context map by its font name and
 * layout attributes. 
 * @author Richard Kunze
 */
public class FontKey {
    private String name;
    private int layout;

    public FontKey(String name, int layout) {
	this.name   = name;
	this.layout = layout;
    }

    public void setName(String name) { this.name = name; }
    public void setLayout(int layout) { this.layout = layout; }

    public boolean equals(Object obj) {
      if (obj instanceof FontKey) {
	FontKey other = (FontKey)obj;
	return layout == other.layout &&
	       ((name == null && other.name == null) ||
		(name != null && name.equals(other.name)));
      } else return false;
    }

    
    public int hashCode() {
	return 13*layout + 11*(name!=null?name.hashCode():0);
    }
}
