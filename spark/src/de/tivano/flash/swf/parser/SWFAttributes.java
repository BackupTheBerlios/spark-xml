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
 * $Id: SWFAttributes.java,v 1.3 2001/05/30 16:23:16 kunze Exp $
 */

package de.tivano.flash.swf.parser;

import org.xml.sax.helpers.AttributesImpl;

/**
 * A convenience class for handling attributes. This class
 * provides convenience methods for setting attributes without
 * having to worry about namespace support. 
 * @see org.xml.sax.Attributes
 */
public class SWFAttributes extends AttributesImpl {
    /** Attribute type constant */
    public static final String TYPE_CDATA = "CDATA";
    
    /** Attribute type constant */
    public static final String TYPE_ID = "ID";
    
    /** Add an attribute. This is a wrapper around
     * <code>AttributeImpl.addAttribute()</code> that handles
     * namespaces on its own.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @param type the type of the attribute
     */
    public void addAttribute(String name, String value, String type) {
	// FIXME: Really handle the name space stuff
	super.addAttribute("", name, "", type, value);
    }
    
    /** Add an attribute. This is a wrapper around
     * <code>AttributeImpl.addAttribute()</code> that handles
     * namespaces on its own.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @param type the type of the attribute
     */
    public void addAttribute(String name, long value, String type) {
	// FIXME: Really handle the name space stuff
	super.addAttribute("", name, "", type, Long.toString(value));
    }
    
    /** Add an attribute. This is a wrapper around
     * <code>AttributeImpl.addAttribute()</code> that handles
     * namespaces on its own.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @param type the type of the attribute
     */
    public void addAttribute(String name, double value, String type) {
	// FIXME: Really handle the name space stuff
	super.addAttribute("", name, "", type, Double.toString(value));
    }
    
    /** Add an attribute. This is a wrapper around
     * <code>AttributeImpl.addAttribute()</code> that automatically
     * converts the value to a string. The type is assumed to be
     * <code>TYPE_CDATA</code>. 
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public void addAttribute(String name, String value) {
	addAttribute(name, value, TYPE_CDATA);
    }
    
    /** Add an attribute. This is a wrapper around
     * <code>AttributeImpl.addAttribute()</code> that automatically
     * converts the value to a string. The type is assumed to be
     * <code>TYPE_CDATA</code>. 
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public void addAttribute(String name, long value) {
	addAttribute(name, value, TYPE_CDATA);
    }
    
    /** Add an attribute. This is a wrapper around
     * <code>AttributeImpl.addAttribute()</code> that automatically
     * converts the value to a string. The type is assumed to be
     * <code>TYPE_CDATA</code>. 
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public void addAttribute(String name, double value) {
	addAttribute(name, value, TYPE_CDATA);
    }
}
