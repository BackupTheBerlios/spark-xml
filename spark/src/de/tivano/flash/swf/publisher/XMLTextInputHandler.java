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
 * $Id: XMLTextInputHandler.java,v 1.1 2001/07/02 08:07:22 kunze Exp $
 */

package de.tivano.flash.swf.publisher;
import org.xml.sax.Attributes;
import de.tivano.flash.swf.common.SWFDefineTextField;

/**
 * Handler for the <em>&lt;TextInput&gt;</em> XML tag.
 * The <em>&lt;TextInput&gt;</em> tag represents an editable text
 * field. In SWF, this corresponds to an {@link SWFDefineTextField}
 * structure with the readonly flag set to <code>false</code>.
 * @author Richard Kunze
 * @see de.tivano.flash.swf.parser.SWFAnyTagReader
 */
public class XMLTextInputHandler extends SWFTagHandlerBase {

    /** The data object */
    SWFDefineTextField data;

    /** The text ID */
    private int id;
    
    /** The variable name for this text */
    private String varName = "";

    /** The maximum length for this field. -1 means unlimited. */
    private int maxLength = -1;

    private boolean isMultiline;
    private boolean isPassword;
    private boolean useWordWrap;

    public XMLTextInputHandler() {
	registerElementHandler("Text", XMLTextHandler.class);
    }

    /**
     * Start processing a &lt;TextInput&gt; element. 
     */
    protected void startElement(String name, Attributes attrib)
	      throws SWFWriterException {
	String tmp = null;
	try {
	    tmp = attrib.getValue("", "id");
	    if (tmp != null) id = Integer.parseInt(tmp);
	} catch (NumberFormatException e) {
	    fatalError("Invalid ID: " + tmp);
	}
	try {
	    tmp = attrib.getValue("", "maxlength");
	    if (tmp != null) maxLength = Integer.parseInt(tmp);
	} catch (NumberFormatException e) {
	    fatalError("Invalid length: " + tmp);
	}
	tmp = attrib.getValue("", "name");
	if (tmp != null) varName = tmp;
	isMultiline = "yes".equals(attrib.getValue("", "multiline"));
	isPassword  = "yes".equals(attrib.getValue("", "password"));
	useWordWrap = "yes".equals(attrib.getValue("", "wordwrap"));

    }

    /**
     * Collect the data from the embedded <em>&lt;Text&gt;</em>
     * element.
     */
    protected void notify(java.lang.String element,
				 XMLHandlerBase handler)
	      throws SWFWriterException {
	data = (SWFDefineTextField)((XMLTextHandler)handler).getTextData(true);
	data.setID(id);
	data.setVarName(varName);
	data.setMaxLength(maxLength);
	data.setReadonly(false);
	data.setPassword(isPassword);
	data.setUseWordWrap(useWordWrap);
	data.setMultiline(isMultiline);
    }
    
    /**
     * Create the SWF writer object for actually writing out the text
     * input field.
     */
    protected SWFTagWriter createDataObject() throws SWFWriterException {
	return new SWFGenericWriter(data);
    }

}
