/*
 * Created on Feb 15, 2007
 */
package net.sf.jdptool;

import java.io.Serializable;

public class TestBean implements Serializable {

    static final long serialVersionUID = 2795615022437841258L;
    
    private long id;
    
    private String name;
    
    
    
    public TestBean(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
