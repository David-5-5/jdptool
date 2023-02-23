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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import net.sf.jdptool.config.JdpConfig;
import net.sf.jdptool.utils.Text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class RecordWriter {
    
    private static Log log = LogFactory.getLog(RecordWriter.class);


    /**
     * Print log messsage into FileOutputStream
     * @param out
     * @param message
     */
    public static void write(OutputStream out, String message) {
        try {
            out.write(new Date().toString().getBytes());
            out.write('\t');
            out.write(message.toString().getBytes());
            out.write('\n');
        } catch (IOException e) {
            log.error("Unable write log into file", e);
        }
    }
    

    public static File getLogDir(JdpConfig config) {
        String logDirName = Text.replace(System.getProperties(),
                                         config.getProperty("logDir"),
                                         true);
        
        File logDir = new File(logDirName);
        if (!logDir.exists()) {
            if (!logDir.mkdir()) {
                logDir = new File(System.getProperty("user.home"));
            }
        }
        
        return logDir;
    }


    /**
     * 
     * @param out
     * @param frameCountframeCount
     * @param message
     */
    public static void write(OutputStream out, int frameCount, String message) {
        StringBuffer sb = new StringBuffer();
        Boolean indent = false;
        if (indent)
            for (int i = 0; i < frameCount; i++) 
                sb.append("|\t");
        
        sb.append(message);
        write(out, sb.toString());
    }
}
