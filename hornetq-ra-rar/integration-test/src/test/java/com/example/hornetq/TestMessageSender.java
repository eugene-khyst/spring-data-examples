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

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Evgeniy Khyst
 */
@Singleton
public class TestMessageSender {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TestMessageSender.class);
    
    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;
    
    public void send(String queueName, String message) {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            Queue testQueue = context.createQueue(queueName);
            context.createProducer().send(testQueue, message);
            LOGGER.info("Sent text message \"{}\"", message);
        }
    }
}
