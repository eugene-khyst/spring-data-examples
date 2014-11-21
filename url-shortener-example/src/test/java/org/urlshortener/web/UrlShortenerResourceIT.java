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
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urlshortener.model.ShortenedUrl;

/**
 *
 * @author Evgeniy Khist
 */
@RunWith(Arquillian.class)
public class UrlShortenerResourceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerResourceIT.class);

    @Deployment
    public static Archive<?> createDeployment() {
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile(new File("pom.xml"));
        File[] commonsValidator = pom.resolve("commons-validator:commons-validator").withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "urlShortenerTest.war")
                .addPackages(true, "org.urlshortener")
                .addAsLibraries(commonsValidator)
                .addAsResource("config.properties")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("jbossas-ds.xml")
                .addAsWebInfResource("hornetq-jms.xml")
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
    }

    @RunAsClient
    @Test
    public void testUrlShorteningAndResolving(@ArquillianResource URL baseUrl) throws Exception {
        String originalUrl = "https://github.com/";

        URI targetUri = new URL(baseUrl, "s/").toURI();
        LOGGER.info("POST {} => {}", targetUri, originalUrl);

        Response postResponse = Request.Post(targetUri)
                .bodyString(originalUrl, ContentType.TEXT_PLAIN)
                .execute();

        String shortenedUrl = postResponse.returnContent().asString();
        LOGGER.info("GET {} => {}", shortenedUrl, originalUrl);

        HttpClient instance = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpResponse getResponse = instance.execute(new HttpGet(shortenedUrl));

        assertEquals(303, getResponse.getStatusLine().getStatusCode());
        assertEquals(originalUrl, EntityUtils.toString(getResponse.getEntity()));
    }

    @RunAsClient
    @Test
    public void testInvalidUrlShortening(@ArquillianResource URL baseUrl) throws Exception {
        String invalidUrl = "somestring";
        URI targetUri = new URL(baseUrl, "s/").toURI();
        Response postResponse = Request.Post(targetUri)
                .bodyString(invalidUrl, ContentType.TEXT_PLAIN)
                .execute();
        assertEquals(400, postResponse.returnResponse().getStatusLine().getStatusCode());
    }

    @RunAsClient
    @Test
    public void testGetAllShortenedUrls(@ArquillianResource URL baseUrl) throws Exception {
        String originalUrl = "https://openshift.com/";

        URI targetUri = new URL(baseUrl, "s/").toURI();
        LOGGER.info("POST {} => {}", targetUri, originalUrl);

        Response postResponse = Request.Post(targetUri)
                .bodyString(originalUrl, ContentType.TEXT_PLAIN)
                .execute();

        String shortenedUrl = postResponse.returnContent().asString();

        Response getResponse = Request.Get(targetUri).execute();

        String shortenedUrls = getResponse.returnContent().asString();
        LOGGER.info("GET {} => {}", targetUri, shortenedUrls);

        assertTrue(shortenedUrls.contains("\"shortUrl\":\"" + shortenedUrl + "\""));
    }
}
