package org.urlshortener.service;

import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yevhen Khyst
 */
@RunWith(Arquillian.class)
public class UrlShortenerServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerServiceTest.class);
    
    @Inject
    private UrlShortenerService urlShortenerService;

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "urlShortenerTest.war")
                .addPackages(true, "org.urlshortener")
                .deletePackage("org.urlshortener.web")
                .addAsResource("config.properties")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("jbossas-ds.xml")
                .addAsWebInfResource("hornetq-jms.xml");
    }

    @Test
    public void testUrlShorteningAndResolving() {
        int numberOfIterations = 100;
        for (int i = 0; i <= numberOfIterations; i++) {
            String originalUrl = "https://github.com/?" + i;
            String shortendUrl = urlShortenerService.shortenUrl(originalUrl);
            assertEquals(originalUrl, urlShortenerService.resolveShortenedUrl(shortendUrl));
            LOGGER.info("{} => {}", shortendUrl, originalUrl);
        }
    }
}
