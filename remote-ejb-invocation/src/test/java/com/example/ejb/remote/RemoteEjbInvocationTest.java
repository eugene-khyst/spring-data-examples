/*
 * Copyright 2014 Evgeniy Khist.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.ejb.remote;

import java.math.BigDecimal;
import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Evgeniy Khist
 */
@RunWith(Arquillian.class)
public class RemoteEjbInvocationTest {

    @Deployment(name = "service")
    public static Archive<?> createServiceDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test-calculator-service.war")
                .addClasses(Calculator.class, CalculatorBean.class);
    }
    
    @Deployment(name = "client")
    public static Archive<?> createClientDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test-calculator-client.war")
                .addClasses(Calculator.class)
                .add(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @OperateOnDeployment("client")
    public void testRemoteEjbInvocation() throws Exception {
        InitialContext ic = new InitialContext();
        Calculator calculator = (Calculator) ic.lookup("java:global/test-calculator-service/CalculatorBean");
        assertNotNull(calculator);
        
        BigDecimal result = calculator.add(BigDecimal.ONE, BigDecimal.ONE);
        assertEquals(0, new BigDecimal("2").compareTo(result));
    }
}
