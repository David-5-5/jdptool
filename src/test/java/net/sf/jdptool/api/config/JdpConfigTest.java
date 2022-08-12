/*
 * Created on Jan 20, 2007
 */
package net.sf.jdptool.api.config;

import java.net.URL;

import junit.framework.TestCase;

import net.sf.jdptool.DigesterHelper;
import net.sf.jdptool.config.JdpConfig;
import net.sf.jdptool.config.JdpRuleSet;

public class JdpConfigTest extends TestCase {
    private static String TEST_CONFIG = 
        "/net/sf/jdptool/api/config/test-jdp-config.xml";
    
    private DigesterHelper helper;
    
    private JdpConfig config;
    
    /**
     * set up enviroment and context
     */
    protected void setUp() throws Exception {
        helper = new DigesterHelper();
        helper.addRuleSet(new JdpRuleSet());
        
        URL url = this.getClass().getResource(TEST_CONFIG);
        config = (JdpConfig)helper.parse(url.openStream());
        
        super.setUp();
    }
    
    /**
     * Tear downn context
     */
    protected void tearDown() throws Exception {
        helper.destroy();
        super.tearDown();
    }
    

    public void testRootTag() throws Exception {
         String expect = "c:\\temp";
         String filterDir = config.getProperty("filterDir");
         
         assertEquals(expect, filterDir);
    }
    
    
    /**
     * Test snapshot element
     * 
     * @throws Exception
     */
    public void testSnapshot() throws Exception {
        String expect = "10000";
        String interval = config.getSnapshot().getProperty("interval");
        
        assertEquals(expect, interval);
    }

}
