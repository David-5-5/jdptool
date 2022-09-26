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
 * <p>This config would be used by as following tag:</p>
 * <ul>
 *      <li>breaks tag</li>
 * </ul>
 * 
 * @author mlu
 */
public class BreaksConfig extends BasicConfig {

    static final long serialVersionUID = -6730753388896767420L;

    private LinkedList<BreakConfig> methods = new LinkedList<BreakConfig>();

    private LinkedList<BreakConfig> lines = new LinkedList<BreakConfig>();
    
    /**
     * Add a <code>BreakConfig</code> of method into list
     * @param method
     */
    public void addMethod(BreakConfig method) {
        methods.add(method);
    }
    
    /**
     * Add a <code>BreakConfig</code> of line into list
     * @param line
     */
    public void addLine(BreakConfig line) {
        lines.add(line);
    }
    
    /**
     * Get the size of <code>BreakConfig</code> of methods
     * @return the count of methods
     */
    public int methodSize() {
        return methods.size();
    }

    /**
     * Get all <code>BreakConfig</code> of methods
     * @return
     */
    public List<BreakConfig> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    /**
     * Returns the <code>BreakConifg</code> of method at the specified
     * position in this list.
     * @param index - index of the method to return
     * @return the the <code>BreakConifg</code> at the specified position
     */
    public BreakConfig getMethod(int index) {
        return methods.get(index);
    }
    
    /**
     * Removes all of the <code>BreakConifg</code> of method from this list
     */
    public void clearMethod() {
        methods.clear();
    }
    
    /**
     * Get the size of <code>BreakConfig</code> of lines
     * @return the size of lines
     */
    public int lineSize() {
        return lines.size();
    }
    
    /**
     * Get all <code>BreakConfig</code> lines
     * @return
     */
    public List<BreakConfig> getLines() {
        return Collections.unmodifiableList(lines);
    }

    /**
     * Returns the <code>BreakConifg</code> of line at the specified
     * position in this list.
     * @param index - index of the line to return
     * @return the the <code>BreakConifg</code> at the specified position
     */
    public BreakConfig getLine(int index) {
        return lines.get(index);
    }
    
    /**
     * Removes all of the <code>BreakConifg</code> of lines from this list
     */
    public void clearLine() {
        lines.clear();
    }
    
}
