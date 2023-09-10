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
package org.apache.rat.analysis;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;
import org.apache.rat.license.ILicenseFamily;
/**
 * Matches text headers to known licenses.
 */
public interface IHeaderMatcher {

    /**
     * Get the identifier for this matcher.
     * @return the Identifier for this matcher.
     */
    String getId();
    /**
     * Resets this matches.
     * Subsequent calls to {@link #match} will accumulate new text.
     */
    void reset();

    /**
     * Matches the text accumulated to licenses.
     * TODO probably a poor design choice - hope to fix later
     * @param subject current document.
     * @param line next line of text, not null
     * 
     * @return whether the current line matched in the document.
     * 
     * @throws RatHeaderAnalysisException in case of internal RAT errors.
     */
    boolean matches(String line) throws RatHeaderAnalysisException;
    
    /**
     * Report the license family for this and any contained implementations.
     * @param consumer the consumer to report to.
     */
    void reportFamily(Consumer<ILicenseFamily> consumer);
    
    /**
     * If this HeaderMatcher has an associated LicenseFamily and it matches the comparator then call
     * the consumer. If this HeaderMatcher is a collection of HeaderMatchers then make the call on each of the 
     * enclosed HeaderMatchers.
     * 
     * @param consumer The consumer for the HeaderMatcher.
     * @param comparator the test for calling the consumer.
     */
    void extractMatcher(Consumer<IHeaderMatcher> consumer, Predicate<ILicenseFamily> comparator);
    
    
}
