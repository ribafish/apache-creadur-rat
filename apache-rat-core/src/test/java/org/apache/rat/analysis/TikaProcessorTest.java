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

import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;
import org.apache.rat.document.RatDocumentAnalysisException;
import org.apache.rat.document.impl.FileDocument;
import org.apache.rat.test.utils.Resources;
import org.apache.rat.utils.DefaultLog;
import org.apache.tika.mime.MimeTypes;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.MalformedInputException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TikaProcessorTest
{
    /**
     * Used to swallow a MalformedInputException and return false
     * because the encoding of the stream was different from the
     * platform's default encoding.
     *
     * @throws Exception
     * @see "RAT-81"
     */
    @Test
    public void RAT81() throws Exception {
        // creat a document that throws a MalformedInputException
        Document doc = getDocument(new InputStream() {
            @Override
            public int read() throws IOException {
                throw new MalformedInputException(0);
            }
        });
        assertThrows(RatDocumentAnalysisException.class, () -> TikaProcessor.process(DefaultLog.INSTANCE, doc));

    }

    @Test
    public void UTF16_input() throws Exception {
        Document doc = getDocument(Resources.getResourceStream("/binaries/UTF16_with_signature.xml"));
        //FileDocument doc = new FileDocument(Resources.getResourceFile("/binaries/UTF16_with_signature.xml"));
        TikaProcessor.process(DefaultLog.INSTANCE, doc);
        assertEquals(Document.Type.STANDARD, doc.getMetaData().getDocumentType());
    }

    @Test
    public void UTF8_input() throws Exception {
        FileDocument doc = new FileDocument(Resources.getResourceFile("/binaries/UTF8_with_signature.xml"));
        TikaProcessor.process(DefaultLog.INSTANCE, doc);
        assertEquals(Document.Type.STANDARD, doc.getMetaData().getDocumentType());
    }

    @Test
    public void missNamedBinaryTest() throws Exception {
        FileDocument doc = new FileDocument(Resources.getResourceFile("/binaries/Image-png.not"));
        TikaProcessor.process(DefaultLog.INSTANCE, doc);
        assertEquals(Document.Type.BINARY, doc.getMetaData().getDocumentType());
    }


    @Test
    public void plainTextTest() throws Exception {
        FileDocument doc = new FileDocument(Resources.getResourceFile("/elements/Text.txt"));
        TikaProcessor.process(DefaultLog.INSTANCE, doc);
        assertEquals(Document.Type.STANDARD, doc.getMetaData().getDocumentType());
    }

    @Test
    public void emptyFileTest() throws Exception {
        FileDocument doc = new FileDocument(Resources.getResourceFile("/elements/sub/Empty.txt"));
        TikaProcessor.process(DefaultLog.INSTANCE, doc);
        assertEquals(Document.Type.STANDARD, doc.getMetaData().getDocumentType());
    }


    @Test
    public void testTikaFiles() throws RatDocumentAnalysisException, IOException {
        File dir = new File("src/test/resources/tikaFiles");
        Map<String, Document.Type> unseenMime = TikaProcessor.getDocumentTypeMap();
        for (Document.Type docType : Document.Type.values()) {
            File typeDir = new File(dir, docType.name().toLowerCase(Locale.ROOT));
            if (typeDir.isDirectory()) {
                for (File file : Objects.requireNonNull(typeDir.listFiles())) {
                    Document doc = new FileDocument(file);
                    String mimeType = TikaProcessor.process(DefaultLog.INSTANCE, doc);
                    assertEquals( docType, doc.getMetaData().getDocumentType(), () -> "Wrong type for "+file.toString());
                    unseenMime.remove(mimeType);
                }
            }
        }
        MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();

        System.out.println( "untested mime types");
        unseenMime.keySet().forEach(System.out::println);

    }


    /**
     * Build a document with the specific input stream
     * @return
     */
    private static Document getDocument(final InputStream stream) {
        MetaData metaData = new MetaData();

        Document doc = new Document() {
            @Override
            public String getName() {
                return "Testing Document";
            }

            @Override
            public Reader reader() throws IOException {
                return new InputStreamReader(inputStream());
            }

            @Override
            public InputStream inputStream() throws IOException {
                return stream;
            }

            @Override
            public MetaData getMetaData() {
                return metaData;
            }

            @Override
            public boolean isComposite() {
                return false;
            }
        };
        return doc;
    }
}
