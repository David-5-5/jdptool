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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jdptool.config.ConfigConstants;
import net.sf.jdptool.config.FilterConfig;
import net.sf.jdptool.config.FilterRuleSet;
import net.sf.jdptool.config.JdpConfig;
import net.sf.jdptool.utils.Text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

/**
 * <p>
 * The <code>JdpMonitor</code> is the kernal of jdptool
 * </p>
 * 
 * @author Lu Ming
 */
public class JdpMonitor implements ConfigConstants {

    protected static DigesterHelper helper = new DigesterHelper();

    private static Log log = LogFactory.getLog(JdpMonitor.class);

    protected FilterConfig filterConfig;

    protected VirtualMachine vm;

    protected JdpConfig jdpConfig;

    private JdpConnector connector;

    private SnapshotRunner snapshotRunner;

    private EventHandlerRunner handlerRunner;

    protected boolean requestChanged = false;

    protected OutputStream vmOutput;

    private Map<ThreadReference, OutputStream> outputs;

    protected boolean vmDied;

    /**
     * Constructor JdpMonitor instance
     * 
     * @param connector
     * @param jdpConfig
     * @param filterConfig
     */
    public JdpMonitor(JdpConnector connector, JdpConfig profilerConfig) {
        this.connector = connector;
        this.jdpConfig = profilerConfig;

        // Load filter rule set for DigesterHelper
        helper.addRuleSet(new FilterRuleSet());

        init();
    }

    private void init() {
        // Load filter config file
        loadFilterConfig();
        
        vm = connector.open();

        snapshotRunner = new SnapshotRunner(this, 0);
        handlerRunner = new EventHandlerRunner(this);
        
        initVmOutput();
        
        handlerRunner.start();
        snapshotRunner.start();       
        
        vm.resume();
        
        try {
            handlerRunner.join(); 
            snapshotRunner.join();
        } catch (InterruptedException exc) {
            // we don't interrupt
        }
        
        try {
            vmOutput.close();
        } catch (IOException e) {
            log.error("Close vmOutput error", e);
        }
        clearOutputStream();
    }

    /**
     * Initialize the output for VM level log
     */
    private void initVmOutput() {

        File logDir = RecordWriter.getLogDir(jdpConfig);

        String fileName = vm.name() + "_" + new Date().getTime() + ".log";
        File logFile = new File(logDir, fileName);
        
        try {
            vmOutput = new FileOutputStream(logFile);
        } catch (FileNotFoundException e) {
            log.error("Initial vm output error", e);
        }
    }

    /**
     * <p>
     * Get a <code>FileOutputStream</code> instance for log which is
     * corresponding to <code>ThreadReference</code>
     * </p>
     * 
     * @param thread : The <code>ThreadReference</code> instance which
     *        contains output stream
     * @return
     */
    protected synchronized OutputStream outputStream(ThreadReference thread) {
        if (outputs == null) {
            outputs = new HashMap<ThreadReference, OutputStream>();
        }
        if (outputs.get(thread) != null) {
            return outputs.get(thread);
        }

        File logDir = RecordWriter.getLogDir(jdpConfig);

        String fileName = thread.name() + "_" + new Date().getTime() + ".log";
        File logFile = new File(logDir, fileName);
        try {
            outputs.put(thread, new FileOutputStream(logFile));
        } catch (FileNotFoundException e) {
            log.error("Log file not found", e);
            return null;
        }

        return outputs.get(thread);
    }

    /**
     * Get outputstream for log
     * 
     * @param thread
     * @return
     */
    protected synchronized void clearOutputStream(ThreadReference thread) {
        if (outputs == null) {
            return;
        }
        OutputStream out = outputs.remove(thread);
        try {
            out.close();
        } catch (IOException e) {
            log.error("Close log file error", e);
        }
    }

    /**
     * Get outputstream for log
     * 
     * @param thread
     * @return
     */
    private synchronized void clearOutputStream() {
        if (outputs == null) {
            return;
        }
        for (Iterator<OutputStream> it = outputs.values().iterator(); it.hasNext();) {
            try {
                it.next().close();
            } catch (IOException e) {
                log.error("Close log file error", e);
            }
        }
        outputs.clear();
    }

    /**
     * Load filter config. System should load customized filter config file at
     * first. If can't find it would use default one
     */
    private void loadFilterConfig() {
        String filterDir = Text.replace(System.getProperties(),
                                        jdpConfig.getProperty(FILTERDIR_TAG),
                                        true);
        
        if (filterDir != null && new File(filterDir).exists() &&
                new File(filterDir).isDirectory()) {
            File[] files = new File(filterDir).listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return (file.getName().lastIndexOf(".xml") != -1);
                }
            });
            
            for (int i = 0; i < files.length; i++) {
                try {
                    filterConfig = (FilterConfig)helper.parse(files[i]);
                } catch (Exception e) {
                    log.info("Can't Load filter file:" +
                             files[i].getAbsolutePath(), e);
                }
            }
        }
        
        URL url = this.getClass().getResource(DEFAULT_FILTER_XML);
        
        if (filterConfig != null) return;
        try {
            filterConfig = (FilterConfig) helper.parse(url.openStream());
        } catch (Exception e) {
            log.fatal("Can't Load default filter config", e);
            System.exit(1);
        }

    }
    
    public void run() {
        
    }
}
