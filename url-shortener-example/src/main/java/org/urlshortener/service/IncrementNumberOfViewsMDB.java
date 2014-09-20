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
package org.urlshortener.service;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urlshortener.repository.ShortenedUrlRepository;

/**
 *
 * @author Evgeniy Khist
 */
@MessageDriven(name = "IncrementNumberOfViewsMDB", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/IncrementNumberOfViewsQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class IncrementNumberOfViewsMDB implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncrementNumberOfViewsMDB.class);
    
    @Inject
    private ShortenedUrlRepository shortenedUrlRepository;
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            try {
                Long shortenedUrlId = ((ObjectMessage) message).getBody(Long.class);
                shortenedUrlRepository.incrementNumberOfViews(shortenedUrlId);
            } catch (JMSException ex) {
                LOGGER.warn(ex.getMessage(), ex);
            }
        }
    }
}
