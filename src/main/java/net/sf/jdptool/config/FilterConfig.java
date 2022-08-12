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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.sf.jdptool.BasicConfig;

/**
 * <p>This root config would be used by as following tag:</p>
 * <ul>
 *      <li>profiler-filter tag</li>
 * </ul>
 * 
 * @author Lu Ming
 */
public class FilterConfig extends BasicConfig {
    
    static final long serialVersionUID = -4113411994365855646L;

    private BreaksConfig breaks;
    
    private BasicConfig excludes;
    
    private LinkedList<BasicConfig> fields = new LinkedList<BasicConfig>();
    
    private LinkedList<BasicConfig> timings = new LinkedList<BasicConfig>();

    /**
     * Return <code>BasicConfig</code> of breaks
     * @return <code>BasicConfig</code> of breaks
     */
    public BreaksConfig getBreaks() {
        return breaks;
    }

    /**
     * Set <code>BasicConfig</code> of breaks
     * @param breaks <code>BasicConfig</code> of breaks
     */
    public void setBreaks(BreaksConfig breaks) {
        this.breaks = breaks;
    }
    
    /**
     * Add a <code>BasicConfig</code> of field into list
     * @param field
     */
    public void addField(BasicConfig field) {
        fields.add(field);
    }
    
    /**
     * Get the size of list of fields
     * @return the size of list of fields
     */
    public int fieldSize() {
        return fields.size();
    }

    /**
     * Get all <code>BasicConfig</code> of fields
     * @return
     */
    public List<BasicConfig> getFields() {
        return Collections.unmodifiableList(fields);
    }

    /**
     * Returns the <code>BasicConfig</code> of field at the specified
     * position in this list.
     * @param index - index of the field to return
     * @return the the <code>BasicConfig</code> at the specified position
     */
    public BasicConfig getField(int index) {
        return fields.get(index);
    }
    
    /**
     * Removes all of the <code>BasicConfig</code> of field from this list
     */
    public void clearField() {
        fields.clear();
    }
    
    /**
     * Add a <code>BasicConfig</code> of timing into list
     * @param timing
     */
    public void addTiming(BasicConfig timing) {
        this.timings.add(timing);
    }

    /**
     * Get the size of list of timings
     * @return the size of list of timings
     */
    public int timingSize() {
        return timings.size();
    }

    /**
     * Get all <code>BasicConfig</code> of timings
     * @return
     */
    public List getTimings() {
        return Collections.unmodifiableList(timings);
    }

    /**
     * Returns the <code>BasicConfig</code> of timing at the specified
     * position in this list.
     * @param index - index of the timing to return
     * @return the the <code>BasicConfig</code> at the specified position
     */
    public BasicConfig getTiming(int index) {
        return timings.get(index);
    }
    
    /**
     * Removes all of the <code>BasicConfig</code> of timing from this list
     */
    public void clearTiming() {
        timings.clear();
    }
    
    /**
     * Return <code>BasicConfig</code> of defaultExcludes
     * @return
     */
    public BasicConfig getExcludes() {
        return excludes;
    }

    /**
     * Set <code>BasicConfig</code> of excludes
     * @param defaultExcludes - <code>BasicConfig</code> of defaultExcludes
     */
    public void setExcludes(BasicConfig excludes) {
        this.excludes = excludes;
    }

}
