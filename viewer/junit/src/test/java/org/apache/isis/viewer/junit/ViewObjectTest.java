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


package org.apache.isis.viewer.junit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.isis.progmodel.wrapper.applib.WrapperObject;
import org.apache.isis.viewer.junit.sample.domain.Country;
import org.apache.isis.viewer.junit.sample.domain.Customer;
import org.junit.Test;


public class ViewObjectTest extends AbstractTest {

    @SuppressWarnings("unchecked")
    private WrapperObject<Customer> asViewObject() {
        return (WrapperObject<Customer>) custJsVO;
    }

	@Test
    public void canCastViewsToViewObject() {
        @SuppressWarnings("unused")
        final WrapperObject<Customer> custRpVOAsViewObject = asViewObject();
    }

    @Test
    public void shouldBeAbleToCreateAView() {
        final Customer custRpVO = getWrapperFactory().wrap(custJsDO);
        assertThat(custRpVO, instanceOf(Customer.class));
        custRpVO.setFirstName("Dick");

        assertThat("Dick", equalTo(custRpVO.getFirstName()));
    }

    @Test
    public void viewShouldPassesThroughSetterToUnderlyingDomainObject() {
        final Customer custRpVO = getWrapperFactory().wrap(custJsDO);
        custRpVO.setFirstName("Dick");

        assertThat("Dick", equalTo(custRpVO.getFirstName()));
    }

    @Test
    public void objectIsViewShouldReturnTrueWhenDealingWithView() {
        final Customer custRpVO = getWrapperFactory().wrap(custJsDO);
        assertThat(getWrapperFactory().isWrapper(custRpVO), is(true));
    }

    @Test
    public void objectIsViewShouldReturnFalseWhenDealingWithUnderlying() {
        assertThat(getWrapperFactory().isWrapper(custJsDO), is(false));
    }

    @Test
    public void collectionInstanceOfViewObjectShouldReturnTrueWhenDealingWithView() {
        custJsDO.addToVisitedCountries(countryGbrDO);
        custJsDO.addToVisitedCountries(countryUsaDO);
        final List<Country> visitedCountries = custJsVO.getVisitedCountries();
        assertThat(visitedCountries instanceof WrapperObject, is(true));
    }

    @Test
    public void containsOnViewedCollectionShouldIntercept() {
        custJsDO.addToVisitedCountries(countryGbrDO);
        custJsDO.addToVisitedCountries(countryUsaDO);
        final List<Country> visitedCountries = custJsVO.getVisitedCountries();
        assertThat(visitedCountries.contains(countryGbrDO), is(true));
    }

}
