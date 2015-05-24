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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Evgeniy Khyst
 */
@MessageDriven(name = "TestMessageListener",
        activationConfig = {
            @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
            @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/testQueue")
        })
public class TestMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMessageListener.class);
    
    @Inject
    @TestMessages
    private BlockingQueue<String> testMessages;
    
    @Override
    public void onMessage(Message message) {
        LOGGER.info("Received message {}", message);
        try {
            testMessages.offer(((TextMessage) message).getText(), 5, TimeUnit.SECONDS);
        } catch (JMSException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
