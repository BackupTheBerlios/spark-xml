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
 * $Id: SWFStateChangeTest.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import junit.framework.*;
import java.io.IOException;
import java.io.EOFException;
import de.tivano.junit.ParametrizedTestSuite;

/**
 * Test case for the {@link SWFStateChange} class.
 * Besides the parametrizable fields provided by {@link SWFDataTypeTestCase},
 * this test class has the following parametrizable properties:
 * <ul>
 * <li>inputFillBits: The number of fill style bits to use in parsing
 * <li>inputLineBits: The number of line style bits to use in parsing
 * <li>inputLineBits: useRGBA: Flag, tells wether to use RGBA or RGB for parsing
 * color components. Defaults to <code>false</code> if not specified.
 * <li>expectedHasMoveTo: Flag, tells if a "move to" block is expected
 * <li>expecteMoveToX: Expected X component of the move to block (ignored if none is present)
 * <li>expecteMoveToY: Expected Y component of the move to block (ignored if none is present)
 * <li>expectedHasFillStyle0: Flag, tells wether a fill style change for slot 0 is expected or not
 * <li>expectedFillStyle0: Expected value for fill style slot 0
 * <li>expectedHasFillStyle1: Flag, tells wether a fill style change for slot 1 is expected or not
 * <li>expectedFillStyle1: Expected value for fill style slot 1
 * <li>expectedHasLineStyle: Flag, tells wether a fill style change for slot 0 is expected or not
 * <li>expectedLineStyle: Expected value for the line style
 * <li>expectedIsEndOfShape: Expected value for "end of shape" flag
 * </ul
 * <em>FIXME: Add real tests for the style definition part!</em>
 * @author Richard Kunze
 */
public class SWFStateChangeTest extends SWFDataTypeTestCase {

    private boolean expectedHasMoveTo;
    private boolean expectedHasFillStyle0;
    private boolean expectedHasFillStyle1;
    private boolean expectedHasLineStyle;
    private boolean expectedIsEndOfShape;
    private int expectedMoveToX;
    private int expectedMoveToY;
    private int expectedFillStyle0;
    private int expectedFillStyle1;
    private int expectedLineStyle;
    
    private int inputFillBits;
    private int inputLineBits;
    private boolean useRGBA = false;
    
    private SWFStateChange testStateChange;
    
    public SWFStateChangeTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new ParametrizedTestSuite(SWFStateChangeTest.class);
        return suite;
    }
    
    /** Create the correct test object.  */
    protected SWFDataType createTestObject() throws IOException {
        BitInputStream in = getInputData();
        // Skip the edge/state change flag - this is parsed by the SWFShapeRecord
        // factory method and not by the SWFStateChange constructor
        in.skipBits(1);
        testStateChange = new SWFStateChange(in, inputFillBits, inputLineBits, useRGBA);
        return testStateChange;
    }
    
    
    /** Setter for property expectedFillStyle0.
     * @param expectedFillStyle0 New value of property expectedFillStyle0.
     */
    public void setExpectedFillStyle0(int expectedFillStyle0) {
        this.expectedFillStyle0 = expectedFillStyle0;
    }
    
    /** Setter for property expectedFillStyle1.
     * @param expectedFillStyle1 New value of property expectedFillStyle1.
     */
    public void setExpectedFillStyle1(int expectedFillStyle1) {
        this.expectedFillStyle1 = expectedFillStyle1;
    }
    
    /** Setter for property expectedHasFillStyle0.
     * @param expectedHasFillStyle0 New value of property expectedHasFillStyle0.
     */
    public void setExpectedHasFillStyle0(boolean expectedHasFillStyle0) {
        this.expectedHasFillStyle0 = expectedHasFillStyle0;
    }
    
    /** Setter for property expectedHasFillStyle1.
     * @param expectedHasFillStyle1 New value of property expectedHasFillStyle1.
     */
    public void setExpectedHasFillStyle1(boolean expectedHasFillStyle1) {
        this.expectedHasFillStyle1 = expectedHasFillStyle1;
    }
    
    /** Setter for property expectedHasLineStyle.
     * @param expectedHasLineStyle New value of property expectedHasLineStyle.
     */
    public void setExpectedHasLineStyle(boolean expectedHasLineStyle) {
        this.expectedHasLineStyle = expectedHasLineStyle;
    }
    
    /** Setter for property expectedHasMoveTo.
     * @param expectedHasMoveTo New value of property expectedHasMoveTo.
     */
    public void setExpectedHasMoveTo(boolean expectedHasMoveTo) {
        this.expectedHasMoveTo = expectedHasMoveTo;
    }
    
    /** Setter for property expectedIsEndOfShape.
     * @param expectedIsEndOfShape New value of property expectedIsEndOfShape.
     */
    public void setExpectedIsEndOfShape(boolean expectedIsEndOfShape) {
        this.expectedIsEndOfShape = expectedIsEndOfShape;
    }
    
    /** Setter for property expectedLineStyle.
     * @param expectedLineStyle New value of property expectedLineStyle.
     */
    public void setExpectedLineStyle(int expectedLineStyle) {
        this.expectedLineStyle = expectedLineStyle;
    }
    
    /** Setter for property expectedMoveToX.
     * @param expectedMoveToX New value of property expectedMoveToX.
     */
    public void setExpectedMoveToX(int expectedMoveToX) {
        this.expectedMoveToX = expectedMoveToX;
    }
    
    /** Setter for property expectedMoveToY.
     * @param expectedMoveToY New value of property expectedMoveToY.
     */
    public void setExpectedMoveToY(int expectedMoveToY) {
        this.expectedMoveToY = expectedMoveToY;
    }
    
    
    /** Setter for property inputFillBits.
     * @param inputFillBits New value of property inputFillBits.
     */
    public void setInputFillBits(int inputFillBits) {
        this.inputFillBits = inputFillBits;
    }
    
    /** Setter for property inputLineBits.
     * @param inputLineBits New value of property inputLineBits.
     */
    public void setInputLineBits(int inputLineBits) {
        this.inputLineBits = inputLineBits;
    }
    
    /** Setter for property useRGBA.
     * @param useRGBA New value of property useRGBA.
     */
    public void setUseRGBA(boolean useRGBA) {
        this.useRGBA = useRGBA;
    }
    
    /** Test of isEndOfShape method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testIsEndOfShape() {
        assertEquals(expectedIsEndOfShape, testStateChange.isEndOfShape());
    }
    
    /** Test of hasMoveTo method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testHasMoveTo() {
        assertEquals(expectedHasMoveTo, testStateChange.hasMoveTo());
    }
    
    /** Test of hasFillStyle0 method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testHasFillStyle0() {
        assertEquals(expectedHasFillStyle0, testStateChange.hasFillStyle0());
    }
    
    /** Test of hasFillStyle1 method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testHasFillStyle1() {
        assertEquals(expectedHasFillStyle1, testStateChange.hasFillStyle1());
    }
    
    /** Test of hasLineStyle method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testHasLineStyle() {
        assertEquals(expectedHasLineStyle, testStateChange.hasLineStyle());
    }
    
    /** Test of hasNewStyles method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testHasNewStyles() {
        // FIXME: Add real tests here...
        assertEquals(false, testStateChange.hasNewStyles());
    }
    
    /** Test of getMoveTo method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetMoveTo() {
        if (expectedHasMoveTo) {
            SWFMoveTo moveTo = testStateChange.getMoveTo();
            assertNotNull(moveTo);
            assertEquals("SWFMoveTo X component:", expectedMoveToX, moveTo.getX());
            assertEquals("SWFMoveTo Y component:", expectedMoveToY, moveTo.getY());
        } else {
            assertNull(testStateChange.getMoveTo());
        }
    }
    
    /** Test of getFillStyle0 method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetFillStyle0() {
        if (expectedHasFillStyle0) {
            assertEquals(expectedFillStyle0, testStateChange.getFillStyle0());
        }
    }
    
    /** Test of getFillStyle1 method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetFillStyle1() {
        if (expectedHasFillStyle1) {
            assertEquals(expectedFillStyle1, testStateChange.getFillStyle1());
        }
    }
    
    /** Test of getLineStyle method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetLineStyle() {
        if (expectedHasLineStyle) {
            assertEquals(expectedLineStyle, testStateChange.getLineStyle());
        }
    }
    
    /** Test of getFillStyleCount method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetFillStyleCount() {
        // FIXME: Add real test case here
        assertEquals(0, testStateChange.getFillStyleCount());
    }
    
    /** Test of getNewFillStyle method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetNewFillStyle() {
        // FIXME: Add test case here
    }
    
    /** Test of getFillBits method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetFillBits() {
        assertEquals(inputFillBits, testStateChange.getFillBits());
    }
    
    /** Test of getLineStyleCount method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetLineStyleCount() {
        // FIXME: Add real test cases here
        assertEquals(0, testStateChange.getLineStyleCount());
    }
    
    /** Test of getNewLineStyle method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetNewLineStyle() {
        // FIXME: Add real test cases here
    }
    
    /** Test of getLineBits method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetLineBits() {
        assertEquals(inputLineBits, testStateChange.getLineBits());
    }
    
    /** Test of getNewStyleFillBits method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetNewStyleFillBits() {
        // FIXME: Add real test cases here
        assertEquals(0, testStateChange.getNewStyleFillBits());
    }
    
    /** Test of getNewStyleLineBits method, of class de.tivano.flash.swf.common.SWFStateChange. */
    public void testGetNewStyleLineBits() {
        // FIXME: Add real test cases here
        assertEquals(0, testStateChange.getNewStyleLineBits());
    }
}
