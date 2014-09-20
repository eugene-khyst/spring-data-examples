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
package org.urlshortener.web;

import java.io.File;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Evgeniy Khist
 */
@RunWith(Arquillian.class)
public class UrlShortenerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerTest.class);

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "urlShortenerTest.war")
                .addPackages(true, "org.urlshortener")
                .addAsResource("config.properties")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("jbossas-ds.xml")
                .addAsWebInfResource("hornetq-jms.xml")
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
    }

    @Test
    public void testUrlShorteningAndResolving() throws Exception {
        String originalUrl = "https://github.com/";
        
        Client client = ClientBuilder.newClient();
        Response postResponse = client.target("http://127.0.0.1:8080/urlShortenerTest/")
                .request()
                .post(Entity.text(originalUrl));
        
        String shortenedUrl = postResponse.readEntity(String.class);
        LOGGER.info("{} => {}", shortenedUrl, originalUrl);
        
        Response getResponse = client.target(shortenedUrl)
                .request()
                .get();
        
        assertEquals(originalUrl, getResponse.readEntity(String.class));
    }
}
