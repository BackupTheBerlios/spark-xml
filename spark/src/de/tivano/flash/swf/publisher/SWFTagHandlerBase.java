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
 * $Id: SWFTagHandlerBase.java,v 1.6 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.publisher;

/**
 * Base class for all classes that handle the XML corresponding to an
 * SWF toplevel structure. 
 *
 * <p>After handling the XML, a new {@link SWFTagWriter} containing
 * the data read by this object is created and added to the list of
 * objects to write out.</p>
 * @author Richard Kunze
 */
public abstract class SWFTagHandlerBase extends XMLHandlerBase {

    /**
     * Create a new {@link SWFTagWriter} object that holds the current
     * data read by this handler. This method is called from
     * {@link #endElement}. Derived classes must implement this.
     */
    protected abstract SWFTagWriter createDataObject()
	throws SWFWriterException;

    /**
     * Finish processing an XML node. Performs the following tasks:
     * <ul>
     * <li>Add a new data object (obtained from
     * {@link #createDataObject} to the list of SWF objects to write.
     * <li>call <code>super.endElement()</code>
     * </ul>
     */
    protected void endElement() throws SWFWriterException {
	getSWFWriter().addData(createDataObject());
	super.endElement();
    }
}
