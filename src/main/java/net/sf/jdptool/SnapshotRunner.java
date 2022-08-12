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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.jdptool.filter.NotExcludesPredicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;

public class SnapshotRunner extends Thread implements Constants {

    private static Log log = LogFactory.getLog(SnapshotRunner.class);

    private JdpMonitor monitor;

    private int interval;

    /**
     * Construct a SnapshotRunner instance
     * @param monitor
     * @param interval : the value is zero, the SnapshotRunner will be exit.
     *          and if the value should be overrided by <b>"interval"</b>
     *          attribute in config file
     */
    public SnapshotRunner(JdpMonitor monitor, int interval) {
        this.monitor = monitor;
        this.interval = interval;
    }

    /**
     * 
     */
    public void run() {
        BasicConfig snapshot = monitor.jdpConfig.getSnapshot();
        try {
            this.interval = Integer.valueOf(snapshot.getProperty("interval"));
        } catch (Exception ex) {
            //The may be NullPointException or ParseException
            log.error("Parse snapshot interval error", ex);
        }
        File logDir = RecordWriter.getLogDir(monitor.jdpConfig);

        String fileName = "snapshot_" + new Date().getTime() + ".log";
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(logDir, fileName));
            while (!monitor.vmDied && this.interval > 0) {
                List<ThreadReference> threads = monitor.vm.allThreads();
                
                for (int i=0; i<threads.size(); i++) {
                    ThreadReference thread = threads.get(i);
                    StringBuffer sb = new StringBuffer("Thread:");
                    sb.append(thread.name());
                    sb.append(thread.toString());
                    RecordWriter.write(out, sb.toString());
                }
                
                Collection classes = CollectionUtils.select(monitor.vm.allClasses(),
                                                            new NotExcludesPredicate(defaultExcludes));
                for (Iterator it = classes.iterator(); it!=null && it.hasNext(); ) {
                    ReferenceType clazz = (ReferenceType)it.next();
                    StringBuffer sb = new StringBuffer("Class:");
                    sb.append(clazz.name());
                    sb.append("[" + clazz.classObject().referringObjects(0).size() + "]");
                    RecordWriter.write(out, sb.toString());
                }
                                
                Thread.sleep(interval);
            }
        } catch (FileNotFoundException ex) {
            log.error(fileName + " create error", ex);
        } catch (InterruptedException ex) {
            log.error("Snapshot function error", ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException ioe) {
                log.error(fileName + " close error", ioe);
            }

        }
    }
}
