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
 * $Id: SWFMoveToTest.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import junit.framework.*;
import java.io.IOException;
import java.io.EOFException;
import de.tivano.junit.ParametrizedTestSuite;

/**
 * Test case for the {@link SWFMoveTo} class.
 * Besides the parametrizable fields provided by {@link SWFDataTypeTestCase},
 * this test class has the following parametrizable properties:
 * <ul>
 * <li>expectedX: The expected X value
 * <li>expectedY: The expected Y value
 * </ul
 *
 * @author Richard Kunze
 */
public class SWFMoveToTest extends SWFDataTypeTestCase {
    
    private SWFMoveTo testMoveTo;
    private int expectedX;
    private int expectedY;
    
    public SWFMoveToTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new ParametrizedTestSuite(SWFMoveToTest.class);
        return suite;
    }
    
    /** Create the correct test object.  */
    protected SWFDataType createTestObject() throws IOException {
        testMoveTo = new SWFMoveTo(getInputData());
        return testMoveTo;
    }
    
    /** Setter for property expectedX.
     * @param expectedX New value of property expectedX.
     */
    public void setExpectedX(int expectedX) {
        this.expectedX = expectedX;
    }
    
    /** Setter for property expectedY.
     * @param expectedY New value of property expectedY.
     */
    public void setExpectedY(int expectedY) {
        this.expectedY = expectedY;
    }
    
    /** Test of getX method, of class de.tivano.flash.swf.common.SWFMoveTo. */
    public void testGetX() {
        assertEquals(expectedX, testMoveTo.getX());
    }
    
    /** Test of getY method, of class de.tivano.flash.swf.common.SWFMoveTo. */
    public void testGetY() {
        assertEquals(expectedY, testMoveTo.getY());
    }
    
}
