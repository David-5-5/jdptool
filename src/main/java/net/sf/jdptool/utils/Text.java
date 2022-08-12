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
package net.sf.jdptool.utils;

import java.util.Properties;

public final class Text {
    
    /**
     * Performs variable replacement on the given string value.
     * Each <code>${...}</code> sequence within the given value is replaced
     * with the value of the named parser variable. In the later case, the
     * missing variable is replaced by the empty string.
     *
     * @param props the properties to replace key
     * @param value the original value
     * @param ignoreMissing if <code>true</code>, missing variables are replaced
     *                      by the empty string.
     * @return value after variable replacements
     * @throws IllegalArgumentException if the replacement of a referenced
     *                                  variable is not found
     */
    public static String replace(Properties props, String value,
                                 boolean ignoreMissing)
            throws IllegalArgumentException {
        
        StringBuffer result = new StringBuffer();
        
        int p = 0, q = value.indexOf("${");                // Find first ${
        while (q != -1) {
            result.append(value.substring(p, q));          // Text before ${
            p = q;
            q = value.indexOf("}", q + 2);                 // Find }
            if (q != -1) {
                String variable = value.substring(p + 2, q);
                String replacement = props.getProperty(variable);
                if (replacement == null) {
                    if (ignoreMissing) {
                        replacement = "";
                    } else {
                        throw new IllegalArgumentException(
                                "Replacement not found for ${" + variable + "}.");
                    }
                }
                result.append(replacement);
                p = q + 1;
                q = value.indexOf("${", p);                // Find next ${
            }
        }
        result.append(value.substring(p, value.length())); // Trailing text

        return result.toString();
    }

}
