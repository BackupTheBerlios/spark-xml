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
 * $Id: SWFDataTypeTestCase.java,v 1.2 2002/05/22 17:11:17 richard Exp $
 */

package de.tivano.flash.swf.common;

import junit.framework.TestCase;
import de.tivano.flash.swf.publisher.Base64Decoder;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Base class for testing SWF data type classes. This class provides parametrizable fields
 * (see {@link de.tivano.junit.ParametrizedTestSuite} common to all SWF data type tests.
 * Parametrizable properties are:
 * <ul>
 * <li>base64InputData: The base64 encoded SWF data. Padded with 0 bits if not a multiple of 8 bits.
 * <li>binaryInputData: The binary SWF data. Padded with 0 bits if not a multiple of 8 bits. "1" in the string
 * is interpreted as binary 1, "0" as binary 0, all other characters are ingnored
 * <li>base64ExpectedOutputData: The base64 encoded expected output data. Padded with 0 bits if not a multiple of 8 bits.
 * Assumed to be identical to the input data if not explicitly parametrized.
 * <li>binaryExpectedOutputData: The binary expected output data. Padded with 0 bits if not a multiple of 8 bits.
 * Assumed to be identical to the input data if not explicitly parametrized.
 * <li>expectedLength: The expected length in bits of the SWF data. If not given, this is automatically
 * calculated from <em>inputData</em>
 * </ul>
 * @author  Richard Kunze
 */
public abstract class SWFDataTypeTestCase extends TestCase {

    private byte[] rawInputData;
    private byte[] expectedOutputData;
    private long expectedLength = -1;
    
    private SWFDataType testObject;

    /** Creates a new instance of SWFDataTypeTestCase */
    public SWFDataTypeTestCase(java.lang.String testName) {
        super(testName);
    }
    
    /** Set up the test environment. This method constructs and sets the test object
     *  by calling {@link #createTestObject}. Derived classes overloading this method 
     * <em>must</em> call <code>super.setUp()</code> in their own setup methods.
     */
    protected void setUp() throws IOException {
        setTestObject(createTestObject());
        if (expectedLength == -1) {
            expectedLength = rawInputData.length * 8;
        }
        if (expectedOutputData == null) {
            expectedOutputData = rawInputData;
        }
    }
    
    /** Create the correct test object. */
    protected abstract SWFDataType createTestObject() throws IOException;
    
    /** Set the input data. The data is expected as a base64 encoded String.
     * @param data the test data 
     */
    public void setBase64InputData(String data) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64Decoder decoder = new Base64Decoder(out);
        decoder.write(data.toCharArray(), 0, data.length());
        decoder.close();
        rawInputData = out.toByteArray();
    }
    
    /** Set the input data. Every "1" in the string is interpreted as a binary 1,
     * every "0" as binary 0. All other characters are ignored.
     * @param data the input data
     */
    public void setBinaryInputData(String data) throws IOException {
        ByteArrayOutputStream outRaw = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(outRaw);
        for (int i=0; i<data.length(); i++) {
            switch (data.charAt(i)) {
                case '1': out.writeBit(true); break;
                case '0': out.writeBit(false); break;
            }
        }
        out.close();
        rawInputData = outRaw.toByteArray();
    }
    
    /** Set the output data. The data is expected as a base64 encoded String.
     * @param data the output data 
     */
    public void setBase64ExpectedOutputData(String data) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64Decoder decoder = new Base64Decoder(out);
        decoder.write(data.toCharArray(), 0, data.length());
        decoder.close();
        expectedOutputData = out.toByteArray();
    }
    
    /** Set the output data. Every "1" in the string is interpreted as a binary 1,
     * every "0" as binary 0. All other characters are ignored.
     * @param data the output data
     */
    public void setBinaryExpectedOutputData(String data) throws IOException {
        ByteArrayOutputStream outRaw = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(outRaw);
        for (int i=0; i<data.length(); i++) {
            switch (data.charAt(i)) {
                case '1': out.writeBit(true); break;
                case '0': out.writeBit(false); break;
            }
        }
        out.close();
        expectedOutputData = outRaw.toByteArray();
    }
    
    /** Get the input data as a {@link BitInputStream} */
    protected BitInputStream getInputData() {
        return new BitInputStream(new ByteArrayInputStream(rawInputData));
    }
    
    /** Setter for property expectedLength.
     * @param expectedLength New value of property expectedLength.
     */
    public void setExpectedLength(long expectedLength) {
        this.expectedLength = expectedLength;
    }
    
    /** Getter for property expectedLength.
     * @return Value of property expectedLength.
     */
    protected long getExpectedLength() {
        return expectedLength;
    }
    
    /** Getter for property testObject.
     * @return Value of property testObject.
     */
    protected SWFDataType getTestObject() {
        return testObject;
    }
    
    /** Setter for property testObject.
     * @param testObject New value of property testObject.
     */
    protected void setTestObject(SWFDataType testObject) {
        this.testObject = testObject;
    }
    
    /** Test the {@link SWFDataType#length} method. The expected result can be
     * parametrized via the {@link #setExpectedLength expectedLength} property
     */
    public void testLength() {
        assertEquals(getExpectedLength(), getTestObject().length());
    }

    /** Test the {@link SWFDataType#write} method. The data written is expected
     * to match the raw input data.
     */
    public void testWrite() throws IOException {
        ByteArrayOutputStream outputData = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(outputData);
        getTestObject().write(out);
        out.close();
        assertTrue("Output data does not match input data", 
                   Arrays.equals(expectedOutputData, outputData.toByteArray()));
    }    
}
