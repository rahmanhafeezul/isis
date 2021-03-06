/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.applib.services.xmlsnapshot;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;

import com.google.common.io.Resources;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlSnapshotServiceAbstractTest {

    private XmlSnapshotServiceAbstract xmlSnapshotService;
    private String xmlStr;

    @Before
    public void setUp() throws Exception {
        URL resource = Resources.getResource(XmlSnapshotServiceAbstractTest.class, "XmlSnapshotServiceAbstractTest.xml");
        xmlStr = Resources.toString(resource, Charset.forName("UTF-8"));
        
        xmlSnapshotService = new XmlSnapshotServiceForUnitTesting();
    }

    @Test
    public void test() {

        Document xmlDoc = xmlSnapshotService.asDocument(xmlStr);
        Element rootEl = xmlDoc.getDocumentElement();
        
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someString", String.class), is("OXF"));
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someLocalDate", LocalDate.class), is(new LocalDate(2013,4,1)));
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someBigDecimal", BigDecimal.class), is(new BigDecimal("123456789012345678901234567890.12345678")));
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someBigInteger", BigInteger.class), is(new BigInteger("12345678901234567890123456789012345678")));
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someInteger", Integer.class), is(new Integer(123456789)));
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someLong", Long.class), is(new Long(1234567890123456789L)));
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someShort", Short.class), is(new Short((short)12345)));
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someByte", Byte.class), is(new Byte((byte)123)));
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someBoolean", Boolean.class), is(Boolean.TRUE));
        assertThat(
                xmlSnapshotService.getChildElementValue(rootEl, "app:someBoolean2", Boolean.class), is(Boolean.FALSE));
    }

    
    static class XmlSnapshotServiceForUnitTesting extends XmlSnapshotServiceAbstract {

        @Override
        public Snapshot snapshotFor(Object domainObject) {
            throw new RuntimeException();
        }

        @Override
        public Builder builderFor(Object domainObject) {
            throw new RuntimeException();
        }
    }

}
