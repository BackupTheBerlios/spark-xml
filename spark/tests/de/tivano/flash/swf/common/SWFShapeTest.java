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
 * $Id: SWFShapeTest.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import junit.framework.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.EOFException;
import de.tivano.junit.ParametrizedTestSuite;
import java.util.StringTokenizer;
import java.util.List;

/**
 * Test case for the {@link SWFShape} class.
 * @author Richard Kunze
 */
public class SWFShapeTest extends SWFDataTypeTestCase {
    
    private SWFShape testShape;
    private boolean useRGBA;
    private String[] expectedShapeRecordClasses = new String[0];
    
    public SWFShapeTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new ParametrizedTestSuite(SWFShapeTest.class);
        return suite;
    }

    /** Setter for property useRGBA.
     * @param useRGBA New value of property useRGBA.
     */
    public void setUseRGBA(boolean useRGBA) {
        this.useRGBA = useRGBA;
    }
    
    /** Set the expected shape record classes.
     * @param data comma seperated list of class names
     */
    public void setExpectedShapeRecordClasses(String data) {
        StringTokenizer t = new StringTokenizer(data, ", \t");
        List result = new ArrayList();
        while (t.hasMoreTokens()) {
            result.add(t.nextToken());
        }
        expectedShapeRecordClasses = (String[])result.toArray(expectedShapeRecordClasses);
    }
    
    /** Create the test object.  */
    protected SWFDataType createTestObject() throws IOException {
        testShape = new SWFShape(getInputData(), useRGBA);
        return testShape;
    }
    
    /** Test of getShapeRecords method, of class de.tivano.flash.swf.common.SWFShape. */
    public void testGetShapeRecords()  {
        Iterator records = testShape.getShapeRecords();
        for (int i=0; i<expectedShapeRecordClasses.length; i++) {
            assertTrue("Expected " + expectedShapeRecordClasses.length + 
                       "shape records, found " + i, records.hasNext());
            String expectedClassName = expectedShapeRecordClasses[i];
            // Allow local names in the expected array. Assume
            // the "de.tivano.flash.swf.common" package in this case.
            if (expectedClassName.indexOf(".") == -1) {
                expectedClassName = "de.tivano.flash.swf.common." +
                                    expectedClassName;
            }
            assertEquals("Shape record type mismatch", 
                         expectedClassName, 
                         records.next().getClass().getName());
        }
        assertTrue("Expected " + expectedShapeRecordClasses.length + 
                   " shape records, found at least " + 
                   (expectedShapeRecordClasses.length + 1),
                   !records.hasNext());
    }        
}
