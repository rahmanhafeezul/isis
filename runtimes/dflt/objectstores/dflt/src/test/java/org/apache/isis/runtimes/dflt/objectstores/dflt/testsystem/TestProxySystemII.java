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


package org.apache.isis.runtimes.dflt.objectstores.dflt.testsystem;

import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.metamodel.spec.SpecificationLoader;
import org.apache.isis.core.metamodel.specloader.ObjectReflectorDefault;
import org.apache.isis.runtimes.dflt.runtime.authentication.AuthenticationManager;
import org.apache.isis.runtimes.dflt.runtime.authentication.standard.SimpleSession;
import org.apache.isis.runtimes.dflt.runtime.authorization.AuthorizationManager;
import org.apache.isis.runtimes.dflt.runtime.context.IsisContext;
import org.apache.isis.runtimes.dflt.runtime.context.IsisContextStatic;
import org.apache.isis.runtimes.dflt.runtime.imageloader.TemplateImageLoader;
import org.apache.isis.runtimes.dflt.runtime.persistence.PersistenceSessionFactory;
import org.apache.isis.runtimes.dflt.runtime.persistence.internal.RuntimeContextFromSession;
import org.apache.isis.runtimes.dflt.runtime.session.IsisSessionFactoryDefault;
import org.apache.isis.runtimes.dflt.runtime.system.DeploymentType;
import org.apache.isis.runtimes.dflt.runtime.userprofile.UserProfileLoader;
import org.apache.isis.runtimes.dflt.objectstores.dflt.InMemoryPersistenceMechanismInstaller;
import org.apache.isis.defaults.progmodel.JavaReflectorInstaller;

/*
 * TODO allow to be created with specific requirements for components being set up rather than using mocks.
 */
public class TestProxySystemII {
    
    private IsisConfigurationDefault configuration;
    private List<Object> servicesList;
    private Mockery mockery = new JUnit4Mockery(){{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    public void init() {
        servicesList = Collections.emptyList();

        final TemplateImageLoader mockTemplateImageLoader = mockery.mock(TemplateImageLoader.class);
   //     final SpecificationLoader mockSpecificationLoader = mockery.mock(SpecificationLoader.class);
//        final PersistenceSessionFactory mockPersistenceSessionFactory = mockery.mock(PersistenceSessionFactory.class);
        final UserProfileLoader mockUserProfileLoader = mockery.mock(UserProfileLoader.class);
        final AuthenticationManager mockAuthenticationManager = mockery.mock(AuthenticationManager.class);
        final AuthorizationManager mockAuthorizationManager = mockery.mock(AuthorizationManager.class);

        mockery.checking(new Expectations() {
            {
                ignoring(mockTemplateImageLoader);
            //    ignoring(mockSpecificationLoader);
             //   ignoring(mockPersistenceSessionFactory);
                ignoring(mockUserProfileLoader);
                ignoring(mockAuthenticationManager);
                ignoring(mockAuthorizationManager);
            }
        });

        configuration = new IsisConfigurationDefault();

        SpecificationLoader mockSpecificationLoader;
        JavaReflectorInstaller javaReflectorInstaller = new JavaReflectorInstaller();
        javaReflectorInstaller.setConfiguration(configuration);
        mockSpecificationLoader = javaReflectorInstaller.createReflector();

        ((ObjectReflectorDefault) mockSpecificationLoader).setRuntimeContext(new RuntimeContextFromSession());

        InMemoryPersistenceMechanismInstaller persistenceMechanismInstaller = new InMemoryPersistenceMechanismInstaller();
        persistenceMechanismInstaller.setConfiguration(configuration);
        PersistenceSessionFactory mockPersistenceSessionFactory = persistenceMechanismInstaller.createPersistenceSessionFactory(DeploymentType.PROTOTYPE);
        
//        mockPersistenceSessionFactory.init();
        
        IsisSessionFactoryDefault sessionFactory = new IsisSessionFactoryDefault(
                DeploymentType.EXPLORATION,
                configuration, 
                mockTemplateImageLoader, 
                mockSpecificationLoader, 
                mockAuthenticationManager,
                mockAuthorizationManager, 
                mockUserProfileLoader, 
                mockPersistenceSessionFactory, 
                servicesList);
        IsisContext context = IsisContextStatic.createRelaxedInstance(sessionFactory);
        IsisContext.setConfiguration(sessionFactory.getConfiguration());
        sessionFactory.init();
        
        context.openSessionInstance(new SimpleSession("tester", new String[0], "001"));
    }
    
    public IsisConfigurationDefault getConfiguration() {
        return configuration;
    }

    public void addToConfiguration(String key, String value) {
        configuration.add(key, value);
    }
}

