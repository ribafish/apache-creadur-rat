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
package org.apache.rat.report.claim.util;

import java.util.List;

import org.apache.rat.api.Document;
import org.apache.rat.api.RatException;
import org.apache.rat.document.IDocumentAnalyser;
import org.apache.rat.document.RatDocumentAnalysisException;
import org.apache.rat.report.RatReport;

public class ClaimReporterMultiplexer implements RatReport {

    private final IDocumentAnalyser analyser;
    private final List<? extends RatReport> reporters;
    private final boolean dryRun;

    /**
     * A multiplexer to run multiple claim reports.
     * @param dryRun true if this is a dry run.
     * @param analyser the analyser to use.
     * @param reporters the reports to execute.
     */
    public ClaimReporterMultiplexer(final boolean dryRun, final IDocumentAnalyser analyser,
            final List<? extends RatReport> reporters) {
        this.analyser  = analyser;
        this.reporters = reporters;
        this.dryRun = dryRun;
    }

    @Override
    public void report(Document document) throws RatException {
        if (!dryRun) {
            if (analyser != null) {
                try {
                    analyser.analyse(document);
                } catch (RatDocumentAnalysisException e) {
                    throw new RatException(e.getMessage(), e);
                }
            }
            for (RatReport report : reporters) {
                report.report(document);
            }
        }
    }

    @Override
    public void startReport() throws RatException {
        for (RatReport report : reporters) {
            report.startReport();
        }
    }

    @Override
    public void endReport() throws RatException {
        for (RatReport report : reporters) {
            report.endReport();
        }
    }
}
