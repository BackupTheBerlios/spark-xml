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
 * $Id: FontKey.java,v 1.4 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.publisher;

/**
 * Key class for retrieving a font from the context map by its font name and
 * layout attributes. 
 * @author Richard Kunze
 */
public class FontKey {
    private String name = null;
    private int layout = 0;

    public FontKey() {
	this(null, 0);
    }
    
    public FontKey(String name, int layout) {
	this.name   = name;
	this.layout = layout;
    }

    public void setName(String name) { this.name = name; }
    public void setLayout(int layout) { this.layout = layout; }
    public String getName() { return name; }
    public int getLayout() { return layout; }
    
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
