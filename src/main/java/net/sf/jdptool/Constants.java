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

public interface Constants {

    public static final String SEPRATOR = "/";
    
    public static final String INDENT = "\t";

    public static String[] defaultExcludes = {"java.*", "javax.*", "sun.*",
                                              "com.sun.*", "org.apache.*"};
    
    // System properties
    /**
     * <code>System</code> properties -Ddigester.validating=true|false. It
     * imply whether config file should be check by DTD
     */
    public static final String DIGESTER_VALIDATING_KEY = "digester.validating";
    
    
}
