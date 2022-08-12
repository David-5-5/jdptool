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
package net.sf.jdptool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;
import org.xml.sax.SAXException;

public final class DigesterHelper implements Constants {

    /**
     * <p>The Digester used to produce config objects from a profiler
     * configuration file.</p>
     */
    private Digester digester = null;
    
    /**
     * <p>The set of public identifiers, and corresponding resource names, for
     * the versions of the configuration file DTDs that we know about.  There
     * <strong>MUST</strong> be an even number of Strings in this list!</p>
     */
    private String[] registrations =
        {
            "-//SourceForge net//DTD Jdptool config 1.0//EN",
            "/net/sf/jdptool/resources/jdp-config_1_0.dtd",
            "-//SourceForge net//DTD Jdptool filter 1.0//EN",
            "/net/sf/jdptool/resources/jdp-filter_1_0.dtd"
        };

    public DigesterHelper() {
        init();
    }
    
    /**
     * Add rule set for configuration file
     * 
     * @param ruleSet
     */
    public void addRuleSet(RuleSetBase ruleSet) {
        digester.addRuleSet(ruleSet);
    }
    
    /**
     * Initialize the registrations, digest rules  and other resources needed
     */
    private void init() {
        if (digester != null) return;
        
        // Create a new Digester instance with standard capabilities
        digester = new Digester();
        digester.setNamespaceAware(true);
        digester.setValidating(this.isValidating());
        digester.setUseContextClassLoader(true);
        
        for (int i=0; i < registrations.length; i += 2) {
            URL url = this.getClass().getResource(registrations[i + 1]);
            
            if (url != null) {
                digester.register(registrations[i], url.toString());
            }
        }
    }
    
    /**
     * Parse the input stream of configuration
     * 
     * @param in
     * @return
     * @throws SAXException 
     * @throws IOException 
     */
    public Object parse(InputStream in) throws IOException, SAXException {
        return digester.parse(in);
    }
    
    /**
     * Parse the configuration file
     * 
     * @param file
     * @return
     * @throws SAXException 
     * @throws IOException 
     */
    public Object parse(File file) throws IOException, SAXException {
        return digester.parse(file);
    }
    
    /**
     * <p>Gracefully release any digester instance that we have created.</p>
     */
    public void destroy() {
        digester = null;
    }
    
    /**
     * <p>Check the status of the <code>validating</code> in <code>System
     * </code> properties</p>
     *
     * @return true if the module Digester should validate.
     */
    private boolean isValidating() {
        boolean validating = true;
        
        String value = System.getProperty(DIGESTER_VALIDATING_KEY);
        
        if ("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) 
                || "n".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
            validating = false;
        }
        
        return validating;
    }

}
