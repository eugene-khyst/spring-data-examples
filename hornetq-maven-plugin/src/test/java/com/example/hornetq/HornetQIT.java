/*
 * Copyright 2015 Yevhen Khyst.
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

import java.util.Properties;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.naming.InitialContext;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Yevhen Khyst
 */
public class HornetQIT {

    @Test
    public void test() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        properties.setProperty("java.naming.provider.url", "jnp://localhost:1099");
        properties.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        InitialContext ic = new InitialContext(properties);
        
        ConnectionFactory cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
        Queue queue = (Queue) ic.lookup("/queue/exampleQueue");
        String textMessage = "This is a text message";
        try (JMSContext context = cf.createContext("guest", "guest")) {
            context.createProducer().send(queue, textMessage);
            JMSConsumer consumer = context.createConsumer(queue);
            String receiveTextMessage = consumer.receiveBody(String.class, 5000);
            assertEquals(textMessage, receiveTextMessage);
        }
    }
}
