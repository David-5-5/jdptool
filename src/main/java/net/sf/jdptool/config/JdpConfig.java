/*
 * Created on Jan 20, 2007
 */
package net.sf.jdptool.config;

import net.sf.jdptool.BasicConfig;

public class JdpConfig extends BasicConfig {

    static final long serialVersionUID = 1570207605341577001L;
    
    private BasicConfig snapshot;

    /**
     * Return <code>BasicConfig</code> of snapshot
     * @return
     */
    public BasicConfig getSnapshot() {
        return snapshot;
    }

    /**
     * Set <code>BasicConfig</code> of snapshot
     * @param snapshot
     */
    public void setSnapshot(BasicConfig snapshot) {
        this.snapshot = snapshot;
    }
    
}
