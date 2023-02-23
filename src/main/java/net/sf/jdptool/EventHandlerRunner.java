/*
 * Copyright 2007 Lu Ming
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

import static net.sf.jdptool.config.ConfigConstants.EXCLUDES_TAG;

import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ModificationWatchpointRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;
import com.sun.jdi.request.VMDeathRequest;

import net.sf.jdptool.config.BreakConfig;
import net.sf.jdptool.config.BreaksConfig;

public class EventHandlerRunner extends Thread
        implements EventHandler, Constants {

    private static Log log = LogFactory.getLog(EventHandlerRunner.class);

    private final JdpMonitor monitor;

    private final EventHandler handler;

    private String[] excludes = defaultExcludes;

    private boolean connected = true; // Connected to VM

    private Map<ThreadReference, ArrayStack> entryTimes;

    /**
     * Construct a EventHandlerRunner instance
     * 
     * @param monitor
     */
    public EventHandlerRunner(JdpMonitor monitor) {
        this.monitor = monitor;
        this.handler = this;

        initExcludes();
        initRequest();
    }

    /**
     * Construct a EventHandlerRunner instance
     * 
     * @param monitor
     * @param handler
     */
    public EventHandlerRunner(JdpMonitor monitor, EventHandler handler) {
        this.monitor = monitor;
        this.handler = handler;

        initExcludes();
        initRequest();
    }

    /**
     * Add the necessary request into <code>EventRequestManager</code>
     */
    private void initRequest() {
        log.debug("Begin to initilize event request .....");
        EventRequestManager eqmr = monitor.vm.eventRequestManager();

        // Set VMDeathRequest
        VMDeathRequest vmdReq = eqmr.createVMDeathRequest();
        vmdReq.enable();

        // Set exception request and suspend all, so we can step it
        // ExceptionRequest exReq = eqmr.createExceptionRequest(null, true, true);
        // for (int i = 0; i < excludes.length; i++) {
        //     exReq.addClassExclusionFilter(excludes[i]);
        // }
        // exReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        // exReq.enable();

        // Set Thread start request
        ThreadStartRequest tsReq = eqmr.createThreadStartRequest();
        tsReq.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        tsReq.enable();

        // Set Thread death request
        ThreadDeathRequest tdReq = eqmr.createThreadDeathRequest();
        tdReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        tdReq.enable();

        // // Set ClassPrepare request, so we can set watchpoint and step
        // // request if needed
        ClassPrepareRequest cpReq = eqmr.createClassPrepareRequest();
        for (int i = 0; i < excludes.length; i++) {
            cpReq.addClassExclusionFilter(excludes[i]);
        }
        cpReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        cpReq.enable();

        // Set ClassPrepare request, so we can set watchpoint and step
        // request if needed
        ClassUnloadRequest cuReq = eqmr.createClassUnloadRequest();
        for (int i = 0; i < excludes.length; i++) {
            cuReq.addClassExclusionFilter(excludes[i]);
        }
        cuReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        cuReq.enable();

        // Want all method entry request, suspend it so we can capture
        // as more as runtime information
        // MethodEntryRequest menReq = eqmr.createMethodEntryRequest();
        // for (int i = 0; i < excludes.length; ++i) {
        //     menReq.addClassExclusionFilter(excludes[i]);
        // }
        // menReq.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        // menReq.enable();

        // // Want all method exit request, suspend it so we can capture
        // // as more as runtime information
        // MethodExitRequest mexReq = eqmr.createMethodExitRequest();
        // for (int i = 0; i < excludes.length; ++i) {
        //     mexReq.addClassExclusionFilter(excludes[i]);
        // }
        // mexReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        // mexReq.enable();
        
        setBreakpoints();

        log.debug("End event request initilizatoin");
    }

    /**
     * Add excludes package
     */
    private void initExcludes() {
        String exStr = monitor.filterConfig.getProperty(EXCLUDES_TAG);
        if (exStr != null && exStr.length() > 0) {
            excludes = exStr.split(";");
        }
    }

    /**
     * Dispatch incoming events
     * 
     * @param event
     */
    private void handle(Event event) {
        if (event instanceof ExceptionEvent) {
            handler.exceptionEvent((ExceptionEvent) event);
        } else if (event instanceof BreakpointEvent) {
            handler.breakpointEvent((BreakpointEvent) event);
        } else if (event instanceof ModificationWatchpointEvent) {
            handler.fieldWatchEvent((WatchpointEvent) event);
        } else if (event instanceof AccessWatchpointEvent) {
            handler.fieldWatchEvent((WatchpointEvent) event);
        } else if (event instanceof MethodEntryEvent) {
            handler.methodEntryEvent((MethodEntryEvent) event);
        } else if (event instanceof MethodExitEvent) {
            handler.methodExitEvent((MethodExitEvent) event);
        } else if (event instanceof StepEvent) {
            handler.stepEvent((StepEvent) event);
        } else if (event instanceof ThreadStartEvent) {
            handler.threadStartEvent((ThreadStartEvent) event);
        } else if (event instanceof ThreadDeathEvent) {
            handler.threadDeathEvent((ThreadDeathEvent) event);
        } else if (event instanceof ClassPrepareEvent) {
            handler.classPrepareEvent((ClassPrepareEvent) event);
        } else if (event instanceof ClassUnloadEvent) {
            handler.classUnloadEvent((ClassUnloadEvent) event);
        } else if (event instanceof VMStartEvent) {
            handler.vmStartEvent((VMStartEvent) event);
        } else if (event instanceof VMDeathEvent) {
            handler.vmDeathEvent((VMDeathEvent) event);
        } else if (event instanceof VMDisconnectEvent) {
            handler.vmDisconnectEvent((VMDisconnectEvent) event);
        } else if (event instanceof VMDisconnectEvent) {
            handler.vmDisconnectEvent((VMDisconnectEvent) event);
        } else {
            throw new Error("Unexpected event type");
        }

    }

    /**
     * Get the method entry stack
     * 
     * @param thread
     * @return
     */
    private ArrayStack getEntryTimes(ThreadReference thread) {
        if (entryTimes == null) {
            entryTimes = new HashMap<ThreadReference, ArrayStack>();
        }
        
        if (entryTimes.get(thread) == null) {
            ArrayStack entryStack = new ArrayStack();
            entryTimes.put(thread, entryStack);
            
            return entryStack;
        } else {
            return entryTimes.get(thread);
        }
    }

    /**
     * A VMDisconnectedException has happened while dealing with another event.
     * We need to flush the event queue, dealing only with exit events (VMDeath,
     * VMDisconnect) so that we terminate correctly.
     */
    private synchronized void handleDisconnect() {
        EventQueue queue = monitor.vm.eventQueue();
        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                EventIterator iter = eventSet.eventIterator();
                while (iter.hasNext()) {
                    Event event = iter.nextEvent();
                    if (event instanceof VMDeathEvent) {
                        handler.vmDeathEvent((VMDeathEvent) event);
                    } else if (event instanceof VMDisconnectEvent) {
                        handler.vmDisconnectEvent((VMDisconnectEvent) event);
                    }
                }
                eventSet.resume(); // Resume the VM
            } catch (InterruptedException exc) {
                log.error("Handle disconnected exception error", exc);
            }
        }
    }

    /**
     * Return operating type to field
     * 
     * @param field
     * @return operating type to field
     */
    private String filterField(String className, Field field) {

        List<BasicConfig> fields = monitor.filterConfig.getFields();
        for (int i = 0; i < fields.size(); i++) {
            BasicConfig config = fields.get(i);
            if (StringUtils.equals(config.getProperty("className"), className)
                && StringUtils.equals(config.getProperty("fieldName"), field.name())) {
                return config.getProperty("watchType");
            }
        }

        return null;
    }

    private List<BasicConfig> filterVariables(String className, int lineNumber) {
        List<BreakConfig> lines = monitor.filterConfig.getBreaks().getLines();
        for (int i=0; i<lines.size(); i++) {
            if (StringUtils.equals(className, lines.get(i).getProperty("className"))
                && lineNumber == Integer.valueOf(lines.get(i).getProperty("line")))
                return lines.get(i).getVariables();
        }
        return new LinkedList<BasicConfig>();
    }

    /**
     * Check whether class name match the excludes's regular
     * expression
     * 
     * @param className
     * @return
     */
    private boolean inExcludes(String className) {
        for (int i = 0; i < excludes.length; i++) {
            if (Pattern.matches(excludes[i], className)) {
                return true;
            }
        }
        return false;
    }

    private int getFrameCount(ThreadReference thread) {
        int frameCount = 0;
        try {
            frameCount = thread.frameCount();
        } catch (IncompatibleThreadStateException e) {
            log.error("Get frame count error", e);
        }

        return frameCount;
    }

    /**
     * Run the event handling thread. As long as we are connected, get event
     * sets off the queue and dispatch the events within them.
     */
    public void run() {
        log.debug("EventHandlerRunner start");
        EventQueue queue = monitor.vm.eventQueue();
        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                EventIterator it = eventSet.eventIterator();
                while (it.hasNext()) {
                    handle(it.nextEvent());
                }
                eventSet.resume();
            } catch (InterruptedException exc) {
                log.error("EventHandler running error", exc);
            } catch (VMDisconnectedException discExc) {
                handleDisconnect();
                break;
            }
        }
    }

    /**
     * Handle breakpoint event
     * 
     * @param event
     */
    public void breakpointEvent(BreakpointEvent event) {
        OutputStream out = monitor.outputStream("BreakpointEvent", event.thread());
        StringBuffer sb = new StringBuffer();
        sb.append("Breakpiont at location[");
        sb.append(event.location() + "];");

        try {
            // Get the current StackFrame, index is zero.
            StackFrame stack = event.thread().frame(0);
            List<BasicConfig> varConf = filterVariables(event.location().declaringType().name(), event.location().lineNumber());
            //List<LocalVariable> variables = stack.visibleVariables();
            sb.append("\n\tLocal variable[\n");
            
            if (varConf.size() > 0) {
                String name = varConf.get(0).getProperty("name");
                if ("ALL".equals(name) || "*".equals(name)) {
                    List<LocalVariable> variables = stack.visibleVariables();
                    for (int i = 0; i < variables.size(); i++) {
                        sb.append("\n\t\t" + variables.get(i).name() + "=");
                        formatterVariables(stack, sb, "\t\t", stack.getValue(variables.get(i)), 0);
                    }
                } else{
                    for (int i = 0; i < varConf.size(); i++) {
                        LocalVariable variable = stack.visibleVariableByName(varConf.get(i).getProperty("name"));
                        sb.append("\n\t\t" + variable.name() + "=");
                        formatterVariables(stack, sb, "\t\t", stack.getValue(variable), 0);
                    }
                }
            }
            
            sb.append("\n\t]");
        } catch (Exception e) {
            log.error("Handle breakpoint event error", e);
        }

        // EventRequestManager mgr = monitor.vm.eventRequestManager();
        // StepRequest request = mgr.createStepRequest(event.thread(),
        //                                             StepRequest.STEP_LINE,
        //                                             StepRequest.STEP_OVER);
        // request.addCountFilter(1);
        // request.enable();
        
        RecordWriter.write(out, getFrameCount(event.thread()) + 1,
                           sb.toString());

    }

    private void formatterVariables(StackFrame stack, StringBuffer sb, String prefix, Value value, int depth) {
        if (value instanceof PrimitiveValue ) {
            sb.append(((PrimitiveValue) value).longValue() + "\n");
        } else if (value instanceof StringReference) {
            sb.append(value + "\n");
        } else if (value instanceof ObjectReference) {
            sb.append(value + "\n");
            if (depth <= 1) {
                ObjectReference objValue = (ObjectReference) value;
                List<com.sun.jdi.Field> fields = objValue.referenceType().allFields();
                for (int i = 0; i < fields.size(); i++) {
                    sb.append(prefix + "|--" + fields.get(i).name() + "=");
                    formatterVariables(stack, sb, prefix + "|--", objValue.getValue(fields.get(i)), depth + 1);
                }
            }
        } else {
            sb.append(value + "\n");
        }
    }

    /**
     *createStepRequest
     * 
     * @param event
     */
    public void classPrepareEvent(ClassPrepareEvent event) {
        
        RecordWriter.write(monitor.vmOutput, "Class " +
                           event.referenceType().name() + " have been loaded");
        String className = event.referenceType().name();
        if (inExcludes(className)) {
            return;
        }

        EventRequestManager mgr = monitor.vm.eventRequestManager();

        // Create Access/Modification field watchpoints by filter config
        List<Field> fields = event.referenceType().allFields();
        for (Iterator<Field> it = fields.iterator(); it.hasNext();) {
            Field field = it.next();
            String type = filterField(className, field);
            if (type == null) {
                continue;
            } else if ("modify".equals(type)) {
                ModificationWatchpointRequest req = 
                    mgr.createModificationWatchpointRequest(field);
                for (int i = 0; i < excludes.length; ++i) {
                    req.addClassExclusionFilter(excludes[i]);
                }
                req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                req.enable();
            } else if ("access".equals(type)) {
                AccessWatchpointRequest req = mgr.createAccessWatchpointRequest(field);
                for (int i = 0; i < excludes.length; i++) {
                    req.addClassExclusionFilter(excludes[i]);
                }
                req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                req.enable();
            } else if ("both".equals(type)) {
                ModificationWatchpointRequest mreq = 
                    mgr.createModificationWatchpointRequest(field);
                for (int i = 0; i < excludes.length; ++i) {
                    mreq.addClassExclusionFilter(excludes[i]);
                }
                mreq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                mreq.enable();
                AccessWatchpointRequest areq = mgr.createAccessWatchpointRequest(field);
                for (int i = 0; i < excludes.length; i++) {
                    areq.addClassExclusionFilter(excludes[i]);
                }
                areq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                areq.enable();
            }
        }

        // Create breakpoint by filter config
        BreaksConfig breaks = monitor.filterConfig.getBreaks();
        for (int i = 0; i < breaks.lineSize(); i++) {
            if (StringUtils.equals(className,
                                   breaks.getLine(i).getProperty("className"))) {
                int lineNumber = Integer.valueOf(breaks.getLine(i).getProperty("line"));
                try {
                    List<Location> locations = event.referenceType().locationsOfLine(lineNumber);
                    for (Iterator<Location> it = locations.iterator(); it.hasNext(); ) {
                        Location current = it.next();
                        BreakpointRequest bpReq = mgr.createBreakpointRequest(current);
                        bpReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                        bpReq.enable();
                    }
                } catch (AbsentInformationException e) {
                    log.error("Class Prepeared error", e);
                }
            }
        }
    }

    private void setBreakpoints() {
        BreaksConfig breaks = monitor.filterConfig.getBreaks();
        EventRequestManager erm = monitor.vm.eventRequestManager();
        for (int i = 0; i < breaks.lineSize(); i++) {
            String className = breaks.getLine(i).getProperty("className");
            int lineNumber = Integer.valueOf(breaks.getLine(i).getProperty("line"));
            try {

                List<ReferenceType> referenceTypes = monitor.vm.classesByName(className);
                if (referenceTypes.size() == 0) {
                    throw new RuntimeException("Cann't Load class " + className);
                }
                List<Location> locations = referenceTypes.get(0).locationsOfLine(lineNumber);
                if (locations.size() == 0) {
                    throw new RuntimeException("Cann't locate the line number " + lineNumber + " of " + className);
                }
                
                BreakpointRequest bpReq = erm.createBreakpointRequest(locations.get(0));
                bpReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                bpReq.enable();
            } catch (AbsentInformationException e) {
                log.error("Class Prepeared error", e);
            }
            log.info("Set the breakpoint of " + className + " in line " + lineNumber);
        }
    }


    /**
     * Handle class unload event
     * 
     * @param event
     */
    public void classUnloadEvent(ClassUnloadEvent event) {
        RecordWriter.write(monitor.vmOutput,
                           event.className() +" have been unloaded");
    }

    /**
     * Handle exception event
     * 
     * @param event
     */
    public void exceptionEvent(ExceptionEvent event) {
        OutputStream out = monitor.outputStream("ExceptionEvent", event.thread());

        StringBuffer sb = new StringBuffer();
        sb.append("location[");
        sb.append(event.location() + "];");
        sb.append("exception at [");
        sb.append(event.catchLocation() + "];");

        RecordWriter.write(out, getFrameCount(event.thread()) + 1,
                           sb.toString());
    }

    /**
     * Handle field watch event
     * 
     * @param event
     */
    public void fieldWatchEvent(WatchpointEvent event) {
        OutputStream out = monitor.outputStream("FieldWatchEvent", event.thread());
        
        StringBuffer sb = new StringBuffer(event.location().toString());
        sb.append(":" + event.field().name());
        sb.append("[current]=" + event.valueCurrent());
        
        if (event instanceof ModificationWatchpointEvent) {
            sb.append("; [toBe]=");
            sb.append(((ModificationWatchpointEvent)event).valueToBe());
        }
        
        RecordWriter.write(out, getFrameCount(event.thread()) + 1,
                           sb.toString());
    }

    /**
     * Handle method entry event
     * 
     * @param event
     */
    public void methodEntryEvent(MethodEntryEvent event) {
        OutputStream out = monitor.outputStream("MethodEntryEvent", event.thread());
        
        StringBuffer sb = new StringBuffer(event.method().name());
        sb.append(" method entry.");
        ArrayStack stack = getEntryTimes(event.thread());
        stack.push(new Long(new Date().getTime()));
        
        RecordWriter.write(out, getFrameCount(event.thread()),
                           sb.toString());
    }

    /**
     * Handle method exit event
     * 
     * @param event
     */
    public void methodExitEvent(MethodExitEvent event) {
        OutputStream out = monitor.outputStream("MethodExitEvent", event.thread());
        
        StringBuffer sb = new StringBuffer(event.method().name());
        sb.append(" method exit. elapse ");
        long elapse = -1;
        ArrayStack stack = getEntryTimes(event.thread());
        Long begin = (Long) stack.pop();
        if (begin != null) {
            elapse = new Date().getTime() - begin.longValue();
        }
        sb.append(elapse + "ms.");
                
        RecordWriter.write(out, getFrameCount(event.thread()), sb.toString());
    }

    /**
     * Handle step event
     * 
     * @param event
     */
    public void stepEvent(StepEvent event) {
        OutputStream out = monitor.outputStream("StepEvent", event.thread());
        StringBuffer sb = new StringBuffer();
        sb.append("Step at location[");
        sb.append(event.location() + "];");

        try {
            // Get the current StackFrame, index is zero.
            StackFrame stack = event.thread().frame(0);
            List<LocalVariable> variables = stack.visibleVariables();
            sb.append("Local variable[");
            for (int i = 0; i < variables.size(); i++) {
                sb.append(variables.get(i).name() + "=");
                sb.append(stack.getValue(variables.get(i)) + ";");
            }
            sb.append("]");
        } catch (Exception e) {
            log.error("Handle step event error", e);
        }
        
        EventRequestManager mgr = monitor.vm.eventRequestManager();
        mgr.deleteEventRequest(event.request());
        
        RecordWriter.write(out, getFrameCount(event.thread()) + 1, sb.toString());

    }

    /**
     * Handle thread death event
     * 
     * @param event
     */
    public void threadDeathEvent(ThreadDeathEvent event) {
        OutputStream out = monitor.outputStream("ThreadDeathEvent", event.thread());
        StringBuffer sb = new StringBuffer();
        sb.append("Thread ");
        sb.append(event.thread().name());
        sb.append(" is dead.");
        
        RecordWriter.write(out, sb.toString());

        monitor.clearOutputStream(event.thread());
    }

    /**
     * Handle thread start event
     * 
     * @param event
     */
    public void threadStartEvent(ThreadStartEvent event) {
        OutputStream out = monitor.outputStream("threadStartEvent", event.thread());

        StringBuffer sb = new StringBuffer();
        sb.append("Thread ");
        sb.append(event.thread().name());
        sb.append(" have been started.");
        
        RecordWriter.write(out, sb.toString());
    }

    /**
     * Handle virtual machine death event
     * 
     * @param event
     */
    public void vmDeathEvent(VMDeathEvent event) {
        monitor.vmDied = true;

        StringBuffer sb = new StringBuffer();
        sb.append("The application exited ");
        sb.append(event.toString());
        
        RecordWriter.write(monitor.vmOutput, sb.toString());
        
    }

    /**
     * Handle virtual machine disconnect event
     * 
     * @param event
     */
    public void vmDisconnectEvent(VMDisconnectEvent event) {
        connected = false;
        if (!monitor.vmDied) {
            StringBuffer sb = new StringBuffer();
            sb.append("The application has been disconnected");
            sb.append(event.toString());
            
            RecordWriter.write(monitor.vmOutput, sb.toString());
        }
    }

    /**
     * Handle virtual machine start event
     * 
     * @param event
     */
    public void vmStartEvent(VMStartEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("VM Started ");
        sb.append(event.toString());
        
        RecordWriter.write(monitor.vmOutput, sb.toString());        
    }

}
