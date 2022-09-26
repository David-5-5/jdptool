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

import java.util.Collection;

import org.apache.commons.collections4.Predicate;

public class PrintRecord implements HandleRecord {

    public void save(Record record) {
        System.out.println(record);
    }

    public void save(Collection<Record> records) {
        throw new UnsupportedOperationException("save method not supported");
    }

    public Collection<Record> search(Predicate<Record> predicate) {
        throw new UnsupportedOperationException("search method not supported");
    }

}
