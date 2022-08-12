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

import java.util.Vector;

public class ExceptionTest {
    
    private Vector<byte[]> list = new Vector<byte[]>();
    
    public void testNullException() {
        String nullStr = null;
        
        System.out.println(nullStr.length());
    }

    
    public void testOutOfMemoryException() {
        
        int count = 0;
        
        while(true) {
            byte[] buffer = new byte[1024];
            list.add(buffer);
            if (++count % 100 == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                System.gc();
                System.out.println("Free Memory   : " + Runtime.getRuntime().freeMemory());
                System.out.println("Total Memory  : " + Runtime.getRuntime().totalMemory());
                System.out.println("Max Memory    : " + Runtime.getRuntime().maxMemory());
                System.out.println("Large string length:" + list.size());
                
            }
            Thread.yield();
        }
    }
    
    public static void main(String[] args) {
        ExceptionTest test = new ExceptionTest();
        
        try {
            test.testNullException();
        } catch (Exception ex) {
            System.out.println(ex.getClass().getName() + ":" + ex.getMessage());
            ex.printStackTrace();
        }
        
        try {
            test.testOutOfMemoryException();
        } catch (Throwable err) {
            err.printStackTrace();
            System.out.println("Free Memory   : " + Runtime.getRuntime().freeMemory());
            System.out.println("Total Memory  : " + Runtime.getRuntime().totalMemory());
            System.out.println("Max Memory    : " + Runtime.getRuntime().maxMemory());
        }
        
        while (true) {
            System.out.println("Continue....");
            try {
                Thread.sleep(3000);
                test.list.add(new byte[1024]);
                System.out.println("Free Memory   : " + Runtime.getRuntime().freeMemory());
                System.out.println("Total Memory  : " + Runtime.getRuntime().totalMemory());
                System.out.println("Max Memory    : " + Runtime.getRuntime().maxMemory());
                System.out.println("large string lenght:" + test.list.size());
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
