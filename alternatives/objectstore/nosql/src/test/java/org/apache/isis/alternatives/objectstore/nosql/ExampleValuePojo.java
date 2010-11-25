/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.isis.alternatives.objectstore.nosql;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.core.commons.exceptions.UnexpectedCallException;

public class ExampleValuePojo extends ExamplePojo {
    // {{ Name
    private String name;

    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    // }}

    // {{ Size
    private int size;

    @MemberOrder(sequence = "1")
    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }
    // }}

    // {{ Nullable
    private Long number;

    @MemberOrder(sequence = "1")
    public Long getNullable() {
        return number;
    }

    public void setNullable(final Long number) {
        this.number = number;
    }
    // }}

    // {{ NotPersisted
    @NotPersisted
    public int getNotPersisted() {
        throw new UnexpectedCallException();
    }
    // }}

}

