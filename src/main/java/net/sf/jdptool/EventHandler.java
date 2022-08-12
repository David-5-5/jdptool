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

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;

public interface EventHandler {

    public void breakpointEvent(BreakpointEvent event);
    
    public void classPrepareEvent(ClassPrepareEvent event);
    public void classUnloadEvent(ClassUnloadEvent event);

    public void exceptionEvent(ExceptionEvent event);

    public void fieldWatchEvent(WatchpointEvent event);
    
    public void methodEntryEvent(MethodEntryEvent event);
    public void methodExitEvent(MethodExitEvent event);
    
    public void stepEvent(StepEvent event);

    public void threadStartEvent(ThreadStartEvent event);
    public void threadDeathEvent(ThreadDeathEvent event);

    public void vmStartEvent(VMStartEvent event);
    public void vmDeathEvent(VMDeathEvent event);
    public void vmDisconnectEvent(VMDisconnectEvent event);

}
