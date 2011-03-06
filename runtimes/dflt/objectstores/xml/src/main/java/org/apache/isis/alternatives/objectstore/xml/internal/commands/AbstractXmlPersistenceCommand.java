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


package org.apache.isis.alternatives.objectstore.xml.internal.commands;

import java.util.List;

import org.apache.log4j.Logger;

import org.apache.isis.alternatives.objectstore.xml.internal.data.DataManager;
import org.apache.isis.alternatives.objectstore.xml.internal.data.ObjectData;
import org.apache.isis.alternatives.objectstore.xml.internal.version.FileVersion;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facets.object.encodeable.EncodableFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.runtimes.dflt.runtime.persistence.objectstore.transaction.PersistenceCommandAbstract;
import org.apache.isis.runtimes.dflt.runtime.persistence.oidgenerator.simple.SerialOid;

abstract class AbstractXmlPersistenceCommand extends PersistenceCommandAbstract {
	private static final Logger LOG = Logger.getLogger(AbstractXmlPersistenceCommand.class);
	
	private final DataManager dataManager;

	public AbstractXmlPersistenceCommand(final ObjectAdapter adapter, final DataManager dataManager) {
		super(adapter);
		this.dataManager = dataManager;
	}

	protected DataManager getDataManager() {
		return dataManager;
	}

    protected ObjectData createObjectData(final ObjectAdapter adapter, final boolean ensurePersistent) {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("compiling object data for " + adapter);
    	}

        ObjectSpecification adapterSpec = adapter.getSpecification();
		ObjectData data = new ObjectData(adapterSpec, (SerialOid) adapter.getOid(), (FileVersion) adapter.getVersion());

        final List<ObjectAssociation> associations = adapterSpec.getAssociations();
        for(ObjectAssociation association: associations) { 
			if (association.isNotPersisted()) {
                continue;
            }

            final ObjectAdapter associatedObject = association.get(adapter);
            final boolean isEmpty = association.isEmpty(adapter);
            final String associationId = association.getId();

            if (association.isOneToManyAssociation()) {
                saveCollection(associationId, data, associatedObject, ensurePersistent);
            } else if (association.getSpecification().isEncodeable()) {
                saveEncoded(data, associationId, associatedObject, isEmpty);
            } else if (association.isOneToOneAssociation()) {
                saveReference(data, associationId, associatedObject, ensurePersistent);
            }
        }

        return data;
    }

	private void saveReference(ObjectData data, final String associationId,
			final ObjectAdapter associatedObject, final boolean ensurePersistent) {
		data.addAssociation(associatedObject, associationId, ensurePersistent);
	}

	private void saveCollection(final String associationId, ObjectData data,
			final ObjectAdapter associatedObject, final boolean ensurePersistent) {
		data.addInternalCollection(associatedObject, associationId, ensurePersistent);
	}

	private void saveEncoded(ObjectData data, final String associationId,
			final ObjectAdapter associatedObject, final boolean isEmpty) {
		if (associatedObject == null || isEmpty) {
		    data.saveValue(associationId, isEmpty, null);
		} else {
		    final EncodableFacet facet = associatedObject.getSpecification().getFacet(EncodableFacet.class);
		    final String encodedValue = facet.toEncodedString(associatedObject);
		    data.saveValue(associationId, isEmpty, encodedValue);
		}
	}
	

}