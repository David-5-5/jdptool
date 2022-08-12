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

import net.sf.jdptool.BasicConfig;
import net.sf.jdptool.SetConfigPropertiesRule;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

import static net.sf.jdptool.Constants.SEPRATOR;

public class FilterRuleSet extends RuleSetBase implements ConfigConstants {

    /**
     * <p>Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance, associating them with our
     * namespace URI (if any).  This method should only be called by a
     * Digester instance.
     *
     * @param digester instance to which rule set  should be added.
     */
    public void addRuleInstances(Digester digester) {
        // Root XML element "jdp-filter"
        digester.addObjectCreate(FILTER_TAG,
                                 FilterConfig.class);
        digester.addRule(FILTER_TAG, new SetConfigPropertiesRule());
        
        // XML element "jdp-filter/breaks"
        digester.addObjectCreate(FILTER_TAG + SEPRATOR + BREAKS_TAG,
                                 BreaksConfig.class);
        digester.addSetNext(FILTER_TAG + SEPRATOR + BREAKS_TAG,
                            "setBreaks", BreaksConfig.class.getName());
        
        // XML element "jdp-filter/field"
        digester.addObjectCreate(FILTER_TAG + SEPRATOR + FIELD_TAG,
                                 BasicConfig.class);
        digester.addRule(FILTER_TAG + SEPRATOR + FIELD_TAG,
                         new SetConfigPropertiesRule());
        digester.addSetNext(FILTER_TAG + SEPRATOR + FIELD_TAG,
                            "addField", BasicConfig.class.getName());
        
        // XML element "jdp-filter/timing"
        digester.addObjectCreate(FILTER_TAG + SEPRATOR + TIMING_TAG,
                                 BasicConfig.class);
        digester.addRule(FILTER_TAG + SEPRATOR + TIMING_TAG,
                         new SetConfigPropertiesRule());
        digester.addSetNext(FILTER_TAG + SEPRATOR + TIMING_TAG,
                            "addTiming", BasicConfig.class.getName());
        
        // XML element "jdp-filter/excludes"
        digester.addObjectCreate(FILTER_TAG + SEPRATOR + EXCLUDES_TAG,
                                 BasicConfig.class);
        digester.addRule(FILTER_TAG + SEPRATOR + EXCLUDES_TAG,
                         new SetConfigPropertiesRule());
        digester.addSetNext(FILTER_TAG + SEPRATOR + EXCLUDES_TAG,
                            "setExcludes", BasicConfig.class.getName());
                
        // XML element "jdp-filter/breaks/method"
        digester.addObjectCreate(FILTER_TAG + SEPRATOR + BREAKS_TAG
                                 + SEPRATOR + METHOD_TAG, BreakConfig.class);
        digester.addRule(FILTER_TAG + SEPRATOR + BREAKS_TAG + SEPRATOR
                         + METHOD_TAG, new SetConfigPropertiesRule());
        digester.addSetNext(FILTER_TAG + SEPRATOR + BREAKS_TAG
                            + SEPRATOR + METHOD_TAG,
                            "addMethod", BreakConfig.class.getName());
        
        // XML element "jdp-filter/breaks/line"
        digester.addObjectCreate(FILTER_TAG + SEPRATOR + BREAKS_TAG
                                 + SEPRATOR + LINE_TAG, BreakConfig.class);
        digester.addRule(FILTER_TAG + SEPRATOR + BREAKS_TAG + SEPRATOR
                         + LINE_TAG, new SetConfigPropertiesRule());
        digester.addSetNext(FILTER_TAG + SEPRATOR + BREAKS_TAG
                            + SEPRATOR + LINE_TAG,
                            "addLine", BreakConfig.class.getName());
        
        // XML element "jdp-filter/breaks/method/variable"
        digester.addObjectCreate(FILTER_TAG + SEPRATOR + BREAKS_TAG
                                 + SEPRATOR + METHOD_TAG + SEPRATOR + VARIABLE_TAG,
                                 BreakConfig.class);
        digester.addRule(FILTER_TAG + SEPRATOR + BREAKS_TAG
                         + SEPRATOR + METHOD_TAG +SEPRATOR + VARIABLE_TAG,
                         new SetConfigPropertiesRule());
        digester.addSetNext(FILTER_TAG + SEPRATOR + BREAKS_TAG
                            + SEPRATOR + METHOD_TAG + SEPRATOR + VARIABLE_TAG,
                            "addVariable", BasicConfig.class.getName());
        
        // XML element "jdp-filter/breaks/line/variable"
        digester.addObjectCreate(FILTER_TAG + SEPRATOR + BREAKS_TAG
                                 + SEPRATOR + LINE_TAG + SEPRATOR + VARIABLE_TAG,
                                 BreakConfig.class);
        digester.addRule(FILTER_TAG + SEPRATOR + BREAKS_TAG
                         + SEPRATOR + LINE_TAG + SEPRATOR + VARIABLE_TAG, 
                         new SetConfigPropertiesRule());
        digester.addSetNext(FILTER_TAG + SEPRATOR + BREAKS_TAG
                            + SEPRATOR + LINE_TAG + SEPRATOR + VARIABLE_TAG,
                            "addVariable", BasicConfig.class.getName());
        

    }
    
}
