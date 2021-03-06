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

package org.apache.isis.core.runtime.transaction.facets;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facetapi.DecoratingFacet;
import org.apache.isis.core.metamodel.facets.properties.modify.PropertySetterFacet;
import org.apache.isis.core.metamodel.facets.properties.modify.PropertySetterFacetAbstract;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.core.runtime.system.transaction.IsisTransactionManager;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosureAbstract;

public class PropertySetterFacetWrapTransaction extends PropertySetterFacetAbstract implements DecoratingFacet<PropertySetterFacet> {

    private final PropertySetterFacet underlyingFacet;

    public PropertySetterFacetWrapTransaction(final PropertySetterFacet underlyingFacet) {
        super(underlyingFacet.getFacetHolder());
        this.underlyingFacet = underlyingFacet;
    }

    @Override
    public PropertySetterFacet getDecoratedFacet() {
        return underlyingFacet;
    }

    @Override
    public void setProperty(final ObjectAdapter adapter, final ObjectAdapter referencedAdapter) {
        if (adapter.isTransient()) {
            // NOT !adapter.isPersistent();
            // (value adapters are neither persistent or transient)
            underlyingFacet.setProperty(adapter, referencedAdapter);
        } else {
            getTransactionManager().executeWithinTransaction(new TransactionalClosureAbstract() {
                @Override
                public void execute() {
                    underlyingFacet.setProperty(adapter, referencedAdapter);
                }
            });
        }
    }

    @Override
    public String toString() {
        return super.toString() + " --> " + underlyingFacet.toString();
    }

    // ///////////////////////////////////////////////////////////////
    // Dependencies (from context)
    // ///////////////////////////////////////////////////////////////

    private static IsisTransactionManager getTransactionManager() {
        return getPersistenceSession().getTransactionManager();
    }

    private static PersistenceSession getPersistenceSession() {
        return IsisContext.getPersistenceSession();
    }

}
