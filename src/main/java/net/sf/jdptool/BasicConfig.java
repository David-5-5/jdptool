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

import net.sf.jdptool.config.BreakConfig;
import net.sf.jdptool.config.BreaksConfig;
import net.sf.jdptool.config.FilterConfig;

/**
 * <p>This is based config of profiler filter, and it would be used by
 * as following tag:
 * <ul>
 *      <li>field tag</li>
 *      <li>defaultExcludes tag</li>
 *      <li>timing tag</li>
 *      <li>variable tag</li>
 * </ul>
 * and all other fitler configs extend this class</p>
 * 
 * @see BreakConfig
 * @see BreaksConfig
 * @see FilterConfig
 * @author Lu Ming
 */
public class BasicConfig extends AbstractConfig {

    static final long serialVersionUID = 8213535115388632242L;

}
