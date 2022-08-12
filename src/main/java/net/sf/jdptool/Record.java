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

public interface Record {
    
    public long getTime();
    
    public void setTime(long time);
    
    public String getThread();
    
    public void setThread(String thread);
    
    public int getLineNumber();
    
    public void setLineNumber(int lineNumber);
    
    public String getClassName();
    
    public void setClassName(String className);
    
    public String getMethodName();
    
    public void setMethodName(String methodName);
    
    public String getFieldName();
    
    public void setFieldName(String fieldName);
    
    public Object getFieldValue();
    
    public void setFieldValue(Object fieldName);

}
