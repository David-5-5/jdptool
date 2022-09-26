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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.VMStartException;

public final class JdpConnector {

    public static final String COMMAND_LINE_LAUNCH = "com.sun.jdi.CommandLineLaunch";

    public static final String RAW_COMMAND_LINE_LAUNCH = "com.sun.jdi.RawCommandLineLaunch";

    public static final String SOCKET_LISTEN = "com.sun.jdi.SocketListen";

    public static final String SOCKET_ATTACH = "com.sun.jdi.SocketAttach";

    public static final String SHARED_MEMORY_ATTACH = "com.sun.jdi.SharedMemoryAttach";

    public static final String SHARED_MEMORY_LISTEN = "com.sun.jdi.SharedMemoryListen";

    public static final String LISTEN_CONNECTOR = "listen";

    public static final String ATTACH_CONNECTOR = "attach";

    public static final String LAUNCH_CONNECTOR = "launch";
    
    public static final String TP_SOCKET = "dt_socket";

    public static final String TP_SHARED_MEMORY = "dt_shmem";

    private static final Log log = LogFactory.getLog(JdpConnector.class);

    private final Connector connector;

    private final Map<String, Connector.Argument> connArgs;

    private final int traceFlags;

    /**
     * Construct JdpConnector instance
     * @param connectorName
     * @param argument
     * @param trace
     */
    public JdpConnector(String connectorName, String argument, boolean trace) {
        String transport = null;
        int begin = argument.indexOf("transport=");
        int end = argument.indexOf(",", begin);
        if (begin != -1) {
            transport = (end == -1) ? argument.substring(begin
                                                         + "transport=".length())
                                   : argument.substring(
                                                        begin
                                                                + "transport=".length(),
                                                        end);
        }
        if (connectorName.equals(JdpConnector.LAUNCH_CONNECTOR)) {
            this.connector = findConnector(COMMAND_LINE_LAUNCH);
        } else if (connectorName.equals(JdpConnector.ATTACH_CONNECTOR)) {
            if (transport != null && transport.equals(JdpConnector.TP_SHARED_MEMORY)) {
                this.connector = findConnector(SHARED_MEMORY_ATTACH);
            } else if (transport != null && transport.equals(JdpConnector.TP_SOCKET)) {
                this.connector = findConnector(SOCKET_ATTACH);
            } else {
                this.connector = null;
            }
        } else if (connectorName.equals(JdpConnector.LISTEN_CONNECTOR)) {
            if (transport != null && transport.equals(JdpConnector.TP_SHARED_MEMORY)) {
                this.connector = findConnector(SHARED_MEMORY_LISTEN);
            } else if (transport != null && transport.equals(JdpConnector.TP_SOCKET)) {
                this.connector = findConnector(SOCKET_LISTEN);
            } else {
                this.connector = null;
            }
        } else {
            this.connector = null;
        }

        if (this.connector == null) {
            log.error("Cann't get VirtualMachine connector, parameters:["
                      + connectorName + "[" + argument + "]]");
            JdpEntry.usage();
            System.exit(1);
        }

        this.connArgs = parseConnectorArgs(this.connector, argument);

        if (trace) {
            this.traceFlags = VirtualMachine.TRACE_ALL;
        } else {
            this.traceFlags = VirtualMachine.TRACE_NONE;
        }
    }

    /**
     * Open the virtual machine
     * @return
     */
    public VirtualMachine open() {
        VirtualMachine vm = null;

        try {
            if (connector instanceof LaunchingConnector) {
                LaunchingConnector launcher = (LaunchingConnector) connector;
                vm = launcher.launch(connArgs);
            } else if (connector instanceof AttachingConnector) {
                AttachingConnector attacher = (AttachingConnector) connector;
                vm = attacher.attach(connArgs);
            } else if (connector instanceof ListeningConnector) {
                ListeningConnector listener = (ListeningConnector) connector;
                String retAddress = listener.startListening(connArgs);
                log.info("Listening at address:" + retAddress);
                vm = listener.accept(connArgs);
                listener.stopListening(connArgs);
            } else {
                throw new InternalError("Invalid connect type");
            }
        } catch (IOException ioe) {
            log.error("Unable to connect to target VM.", ioe);
        } catch (IllegalConnectorArgumentsException icae) {
            log.error("Internal debug error.", icae);
        } catch (VMStartException vmse) {
            log.error("Target VM failed to initialize.", vmse);
        }
        vm.setDebugTraceMode(traceFlags);

        return vm;
    }

    /**
     * Find connector by name; the name should be one of as following:
     * <ul>
     * <li>com.sun.jdi.CommandLineLaunch</li>
     * <li>com.sun.jdi.RawCommandLineLaunch</li>
     * <li>com.sun.jdi.SocketListen</li>
     * <li>com.sun.jdi.SocketAttach</li>
     * <li>com.sun.jdi.SharedMemoryAttach</li>
     * <li>com.sun.jdi.SharedMemoryListen</li>
     * </ul>
     * @param name
     * @return
     */
    private Connector findConnector(String name) {
        List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();
        Iterator<Connector> iter = connectors.iterator();
        while (iter.hasNext()) {
            Connector connector = (Connector) iter.next();
            if (connector.name().equals(name)) {
                return connector;
            }
        }
        return null;
    }

    /**
     * parse the input argument string
     * @param connector
     * @param argString
     * @return
     */
    private Map<String, Connector.Argument> parseConnectorArgs(Connector connector,
                                                               String argString) {
        Map<String, Connector.Argument> arguments = connector.defaultArguments();

        for (String token : argString.split(",")) {
            int index = token.indexOf("=");
            if (index < 0) {
                continue;
            }

            String name = token.substring(0, index);
            String value = token.substring(index + 1, token.length());

            Connector.Argument argument = (Connector.Argument) arguments.get(name);
            if (argument != null) {
                argument.setValue(value);
            }
        }
        return arguments;
    }

}
