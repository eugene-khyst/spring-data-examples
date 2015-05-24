/*
 * Copyright 2015 Evgeniy Khyst.
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
package com.example.hornetq;

import static java.util.Arrays.asList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Evgeniy Khyst
 */
@RunWith(Arquillian.class)
public class HornetQRaIT {

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "HornetQRaTest.war")
                .addPackages(true, "com.example.hornetq")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    @TestMessages
    private BlockingQueue<String> testMessages;
    
    @Inject
    private TestMessageSender messageSender;
    
    @Test
    public void test() throws Exception {
        messageSender.send("testQueue", "message1");
        messageSender.send("testQueue", "message2");
        
        String message1 = testMessages.poll(5, TimeUnit.SECONDS);
        String message2 = testMessages.poll(5, TimeUnit.SECONDS);
        
        assertTrue(asList(message1, message2).contains("message1"));
        assertTrue(asList(message1, message2).contains("message2"));
    }
}
