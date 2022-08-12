/*
 * Copyright 2006 Lu Ming
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.sf.jdptool.api.config;

import java.net.URL;

import junit.framework.TestCase;

import net.sf.jdptool.BasicConfig;
import net.sf.jdptool.DigesterHelper;
import net.sf.jdptool.config.BreakConfig;
import net.sf.jdptool.config.FilterConfig;
import net.sf.jdptool.config.FilterRuleSet;

/**
 * Test Profiler Filter configuration digester 
 * 
 * @author Lu Ming
 */
public class FilterConfigTest extends TestCase {

    private static String TEST_FILTER = 
        "/net/sf/jdptool/api/config/test-jdp-filter.xml";
    
    private DigesterHelper helper;
    
    private FilterConfig config;
    
    /**
     * set up enviroment and context
     */
    protected void setUp() throws Exception {
        helper = new DigesterHelper();
        helper.addRuleSet(new FilterRuleSet());
        
        URL url = this.getClass().getResource(TEST_FILTER);
        config = (FilterConfig)helper.parse(url.openStream());
        
        super.setUp();
    }
    
    /**
     * Tear downn context
     */
    protected void tearDown() throws Exception {
        helper.destroy();
        super.tearDown();
    }
    
    /**
     * Test root profiler-filter element
     * @throws Exception
     */
    public void testRootTag() throws Exception {
        String expect = "default";
        String name = config.getProperty("name");
        
        assertEquals(expect, name);
    }
    
    /**
     * Test field element
     * @throws Exception
     */
    public void testField() throws Exception {
        assertEquals("Field size should be 1", 1, config.fieldSize());
        
        BasicConfig field = config.getField(0);
        
        assertEquals("net.sf.jdptool.monitor.Target",
                     field.getProperty("className"));
        assertEquals("name", field.getProperty("fieldName"));
        assertEquals("both", field.getProperty("watchType"));
    }

    /**
     * Test excludes element
     * 
     * @throws Exception
     */
    public void testExcludes() throws Exception {
        String expect = "java.*;javax.*";
        String packages = config.getExcludes().getProperty("packages");
        
        assertEquals(expect, packages);
    }
    
    /**
     * Test method element
     * 
     * @throws Exception
     */
    public void testMethod() throws Exception {
        assertEquals("Method size should be 1", 1,
                     config.getBreaks().methodSize());
        
        BreakConfig method = config.getBreaks().getMethod(0);
        assertEquals("net.sf.jdptool.monitor.Target",
                     method.getProperty("className"));
        assertEquals("setName", method.getProperty("methodName"));
        assertEquals("over", method.getProperty("type"));
    }
    
    /**
     * Test line element
     * 
     * @throws Exception
     */
    public void testLine() throws Exception {
        assertEquals("Line size should be 1", 1, 
                     config.getBreaks().lineSize());
        
        BreakConfig line = config.getBreaks().getLine(0);
        assertEquals("net.sf.jdptool.monitor.Target",
                     line.getProperty("className"));
        assertEquals("89", line.getProperty("line"));
        assertEquals("into", line.getProperty("type"));
    }
    
    /**
     * Test variable element which is beneath method element
     * @throws Exception
     */
    public void testVariableInMethod() throws Exception {
        assertEquals(1, config.getBreaks().getMethod(0).variableSize());
        
        BasicConfig variable = config.getBreaks().getMethod(0).getVariable(0);
        assertEquals("toTest", variable.getProperty("name"));
        assertEquals("local", variable.getProperty("type"));
    }
    
    /**
     * Test variable element which is beneath line element
     * @throws Exception
     */
    public void testVariableInLine() throws Exception {
        assertEquals(1, config.getBreaks().getLine(0).variableSize());
        
        BasicConfig variable = config.getBreaks().getLine(0).getVariable(0);
        assertEquals("toTest", variable.getProperty("name"));
        assertEquals("local", variable.getProperty("type"));
    }

}
