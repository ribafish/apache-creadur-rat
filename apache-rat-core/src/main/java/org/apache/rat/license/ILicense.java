/*
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 */
package org.apache.rat.license;

import java.util.Comparator;
import java.util.SortedSet;

import org.apache.rat.analysis.IHeaderMatcher;

public interface ILicense extends IHeaderMatcher, Comparable<ILicense> {
    ILicenseFamily getLicenseFamily();
    String getNotes();
    ILicense derivedFrom();
    
    /**
     * Search a set
     * @param licenseId
     * @param licenses
     * @return
     */
    static ILicense search(String licenseId, SortedSet<ILicense> licenses) {
        ILicenseFamily searchFamily = new SimpleLicenseFamily(licenseId, "searching proxy");
        ILicense target = new ILicense() {
    
            @Override
            public String getId() {
                return licenseId;
            }
    
            @Override
            public void reset() {
                // do nothing
            }
    
            @Override
            public boolean matches(String line) {
                return false;
            }
    
            @Override
            public int compareTo(ILicense arg0) {
                return searchFamily.compareTo(arg0.getLicenseFamily());
            }
    
            @Override
            public ILicenseFamily getLicenseFamily() {
                return searchFamily;
            }
    
            @Override
            public String getNotes() {
                return null;
            }
    
            @Override
            public ILicense derivedFrom() {
                return null;
            }
            
        };
        return search(target,licenses);
    }
    
    static ILicense search(ILicense target, SortedSet<ILicense> licenses) {
        SortedSet<ILicense> part = licenses.tailSet(target);
        return (!part.isEmpty() && part.first().compareTo(target) == 0) ? part.first() : null;
    }
    
    static Comparator<ILicense> getComparator() {
        return (x,y) -> x.getLicenseFamily().compareTo(y.getLicenseFamily());
    }
}
