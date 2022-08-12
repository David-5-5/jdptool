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

import org.apache.commons.digester.SetPropertiesRule;
import org.xml.sax.Attributes;

/**
 * <p> A variant of the standard Digester <code>SetPropertiesRule</code>.</p>
 */
public final class SetConfigPropertiesRule extends SetPropertiesRule {
    public SetConfigPropertiesRule() {
        super();
    }

    /**
     * Process the beginning of this element.
     * 
     * @param attributes The attribute list of this element
     */
    public void begin(Attributes attributes) throws Exception {
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            
            BasicConfig top = (BasicConfig) digester.peek();
            top.setProperty(name, value);
        }
    }
}

