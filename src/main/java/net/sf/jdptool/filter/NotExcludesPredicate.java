/*
 * Created on Feb 15, 2007
 */
package net.sf.jdptool.filter;

import java.util.regex.Pattern;

import org.apache.commons.collections4.Predicate;

//import org.apache.commons.collections.Predicate;

import com.sun.jdi.ReferenceType;

public class NotExcludesPredicate implements Predicate<ReferenceType> {

    private String[] excludes;
    
    public NotExcludesPredicate(String[] excludes) {
        this.excludes = excludes;
    }
    
    public boolean evaluate(ReferenceType reference) {
        String className = reference.name();

        for (int i = 0; i < excludes.length; i++) {
            if (Pattern.matches(excludes[i], className)) {
                return false;
            }
        }
        return true;
    }

}
