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

package org.apache.isis.core.progmodel.facets.members.hidden.staticmethod;

import java.lang.reflect.Method;

import org.apache.isis.core.commons.lang.MethodExtensions;
import org.apache.isis.core.commons.lang.StringExtensions;
import org.apache.isis.core.metamodel.exceptions.MetaModelException;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.methodutils.MethodScope;
import org.apache.isis.core.progmodel.facets.MethodFinderUtils;
import org.apache.isis.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;
import org.apache.isis.core.progmodel.facets.MethodPrefixConstants;

public class HiddenFacetViaAlwaysHideMethodFacetFactory extends MethodPrefixBasedFacetFactoryAbstract {

    private static final String[] PREFIXES = { MethodPrefixConstants.HIDE_PREFIX };

    /**
     * Note that the {@link Facet}s registered are the generic ones from
     * noa-architecture (where they exist)
     */
    public HiddenFacetViaAlwaysHideMethodFacetFactory() {
        super(FeatureType.MEMBERS, OrphanValidation.VALIDATE, PREFIXES);
    }

    // ///////////////////////////////////////////////////////
    // Actions
    // ///////////////////////////////////////////////////////

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        // alwaysHideXxx()
        attachHiddenFacetIfAlwaysHideMethodIsFound(processMethodContext);

    }

    public static void attachHiddenFacetIfAlwaysHideMethodIsFound(final ProcessMethodContext processMethodContext) {

        final Class<?> type = processMethodContext.getCls();
        final Method method = processMethodContext.getMethod();

        final String capitalizedName = StringExtensions.asJavaBaseNameStripAccessorPrefixIfRequired(method.getName());

        final Method alwaysHideMethod = MethodFinderUtils.findMethod(type, MethodScope.CLASS, MethodPrefixConstants.ALWAYS_HIDE_PREFIX + capitalizedName, boolean.class, new Class[] {});
        if (alwaysHideMethod == null) {
            return;
        }

        processMethodContext.removeMethod(alwaysHideMethod);
        final Boolean alwaysHideMethodReturnValue = invokeAlwaysHideMethod(alwaysHideMethod);
        if (!alwaysHideMethodReturnValue.booleanValue()) {
            return;
        }

        final FacetHolder facetedMethod = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new HiddenFacetAlwaysEverywhere(facetedMethod));
    }

    private static Boolean invokeAlwaysHideMethod(final Method alwaysHideMethod) {
        Boolean alwaysHideMethodReturnValue = null;
        try {
            alwaysHideMethodReturnValue = (Boolean) MethodExtensions.invokeStatic(alwaysHideMethod);
        } catch (final ClassCastException ex) {
            // ignore
        }
        if (alwaysHideMethodReturnValue == null) {
            throw new MetaModelException("method " + alwaysHideMethod + "must return a boolean");
        }
        return alwaysHideMethodReturnValue;
    }

}
