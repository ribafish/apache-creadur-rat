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
package org.apache.rat.document.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

/**
 * Document wrapping a file of undetermined composition.
 *
 */
public class FileDocument extends Document {

    private final File file;
    
    public FileDocument(final File file) {
        super(DocumentImplUtils.toName(file));
        this.file = file;
    }

    @Override
    public Path getPath() {
        return file.toPath();
    }

    public boolean isComposite() {
        return DocumentImplUtils.isZip(file);
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public SortedSet<Document> listChildren() {
        if (isDirectory()) {
            SortedSet<Document> result = new TreeSet<>();
            Arrays.stream(file.listFiles()).map(f -> new FileDocument(f)).forEach(result::add);
            return result;
        }
        return Collections.emptySortedSet();
    }

    public Reader reader() throws IOException {
        return new FileReader(file);
    }

    public InputStream inputStream() throws IOException {
        return Files.newInputStream(file.toPath());
    }
}
