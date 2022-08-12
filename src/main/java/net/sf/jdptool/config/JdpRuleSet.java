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
package net.sf.jdptool.config;

import static net.sf.jdptool.Constants.SEPRATOR;

import net.sf.jdptool.BasicConfig;
import net.sf.jdptool.SetConfigPropertiesRule;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class JdpRuleSet extends RuleSetBase implements ConfigConstants {

    /**
     * <p>Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance, associating them with our
     * namespace URI (if any).  This method should only be called by a
     * Digester instance.
     *
     * @param digester instance to which rule set  should be added.
     */
    public void addRuleInstances(Digester digester) {
        
        // Root XML element "jdp-config"
        digester.addObjectCreate(CONFIG_TAG,
                                 JdpConfig.class);
        digester.addRule(CONFIG_TAG, new SetConfigPropertiesRule());

        // XML element "jdp-config/snapshot"
        digester.addObjectCreate(CONFIG_TAG + SEPRATOR + SNAPSHOT_TAG,
                                 BasicConfig.class);
        digester.addRule(CONFIG_TAG + SEPRATOR + SNAPSHOT_TAG,
                         new SetConfigPropertiesRule());
        digester.addSetNext(CONFIG_TAG + SEPRATOR + SNAPSHOT_TAG,
                            "setSnapshot", BasicConfig.class.getName());
    }

}
