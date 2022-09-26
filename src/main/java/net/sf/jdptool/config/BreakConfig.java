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
 *      <li>method tag</li>
 *      <li>line tag</li>
 * </ul>
 * 
 * @author Lu Ming
 */
public class BreakConfig extends BasicConfig {
    
    static final long serialVersionUID = -470428502989827731L;
    
    private LinkedList<BasicConfig> variables = new LinkedList<BasicConfig>();
    
    /**
     * Add a <code>BasicConfig</code> of variable into list
     * @param variable
     */
    public void addVariable(BasicConfig variable) {
        variables.add(variable);
    }

    /**
     * Get the size of <code>BasicConfig</code> of variables
     * @return the size of variable list
     */
    public int variableSize() {
        return variables.size();
    }
    
    /**
     * Get all <code>BreakConfig</code> variables
     * @return
     */
    public List<BasicConfig> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    /**
     * Returns the <code>BasicConfig</code> of variable at the specified
     * position in this list.
     * @param index - index of the variable to return
     * @return the the <code>BasicConfig</code> at the specified position
     */
    public BasicConfig getVariable(int index) {
        return variables.get(index);
    }
    
    /**
     * Removes all of the <code>BasicConfig</code> of lines from this list
     */
    public void clearVariable() {
        variables.clear();
    }
    
}
