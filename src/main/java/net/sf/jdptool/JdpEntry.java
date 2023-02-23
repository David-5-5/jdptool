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

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jdptool.config.JdpConfig;
import net.sf.jdptool.config.JdpRuleSet;

public final class JdpEntry {
    private static Log log = LogFactory.getLog(JdpEntry.class);
    
    /**
     * Jdptool Entry : main
     */
    public static void main(String[] args) {
        JdpConfig config = null;
        String argument = null;
        String connector = JdpConnector.LAUNCH_CONNECTOR;
        boolean tracing = false;

        int inx;
        for (inx = 0; inx < args.length; ++inx) {
            String arg = args[inx];
            if (arg.charAt(0) != '-') {
                break;
            }
            
            if (arg.equals("-config")) {
                DigesterHelper helper = new DigesterHelper();
                helper.addRuleSet(new JdpRuleSet());
                String configFileName = args[++inx];
                log.info("Try to load config file " + configFileName);
                File configFile = new File(configFileName);
                if (configFile.exists()) {
                    try {
                        config = (JdpConfig) helper.parse(configFile);
                    } catch (Exception e) {
                        System.err.println("Ignore XML parse exception");
                        e.printStackTrace();
                    }
                }
            } else if (arg.equals("-trace")) {
                tracing = true;
            } else if (arg.equals("-attach")) {
                if (!connector.equals(JdpConnector.LAUNCH_CONNECTOR)) {
                    invalid("Cannot redefine existing connection");
                }
                connector = JdpConnector.ATTACH_CONNECTOR;
                argument = args[++inx];
            } else if (arg.equals("-listen")) {
                if (!connector.equals(JdpConnector.LAUNCH_CONNECTOR)) {
                    invalid("Cannot redefine existing connection");
                }
                connector = JdpConnector.LISTEN_CONNECTOR;
                argument = args[++inx];
            } else if (arg.equals("-help")) {
                JdpEntry.usage();
                System.exit(0);
            } else {
                invalid("no option: " + arg);
            }
        }
        if (inx >= args.length && connector == JdpConnector.LAUNCH_CONNECTOR) {
            invalid("class argument missing");
        } else if (connector == JdpConnector.LAUNCH_CONNECTOR) {
            StringBuffer sb = new StringBuffer("main=");
            sb.append(args[inx]);
            for (++inx; inx < args.length; ++inx) {
                sb.append(' ');
                sb.append(args[inx]);
            }
            argument = sb.toString();
        }
        
        // Retry Jdptool config file from System property or in default position
        if (config == null) {
            //TODO retry config file
            invalid("Can't file Jdptool config file");
        }
        
        JdpConnector jdpConn = new JdpConnector(connector, argument, tracing);
        new JdpMonitor(jdpConn, config);
    }
    /**
     * Print command line usage help
     */
    public static void usage() {
        System.err.println("Usage: java JdpEntry [options] class [args....]");
        System.err.println("   or: java JdpEntry [options]");
        System.err.println("where options include:");
        System.err.println("  -output <filename>");
        System.err.println("        Output trace to <filename>");
        System.err.println("  -config <filename>");
        System.err.println("        The Jdptool config file");
        System.err.println("  -launch");
        System.err.println("        Launch immediate");
        System.err.println("  -trace");
        System.err.println("        trace all debug");
        System.err.println("  -attach [transport=[dt_shmem|dt_socket],host=<hostname|ipaddress>");
        System.err.println("             address=<socket port|transport name>]");
        System.err.println("        The transport parameters, The type and address");
        System.err.println("  -listen [transport=[dt_shmem|dt_socket],host=<hostname|ipaddress>");
        System.err.println("             address=<socket port|transport name>]");
        System.err.println("        The transport parameters, The type and address");
        System.err.println("        parameter is mandatory");
        System.err.println("  -help");
        System.err.println("        Print this help message");
        System.err.println("Example: java JdpEntry -rules /var/rules.xml example.foo");
        System.err.println("     or: java JdpEntry -rules /var/rules.xml -attach \\\n"
                           + "                    -connect type=dt_socket,address=55000");
    }

    /**
     * Output invalid message to <code>System.out</code>
     * 
     * @param message
     */
    private static void invalid(String message) {
        System.err.println("Invalid options: " + message);
        JdpEntry.usage();
        System.exit(1);
    }

}
