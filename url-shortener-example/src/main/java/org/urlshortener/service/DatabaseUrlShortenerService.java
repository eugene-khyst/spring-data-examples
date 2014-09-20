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

import javax.inject.Inject;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urlshortener.model.ShortenedUrl;
import org.urlshortener.repository.ShortenedUrlRepository;

/**
 *
 * @author Evgeniy Khist
 */
@Transactional(Transactional.TxType.REQUIRED)
public class DatabaseUrlShortenerService implements UrlShortenerService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUrlShortenerService.class);
    
    private final ShortenedUrlRepository shortenedUrlRepository;
    private final NumberConverter numberConverter;
    private final IncrementNumberOfViewsService incrementNumberOfViewsService;

    @Inject
    public DatabaseUrlShortenerService(
            ShortenedUrlRepository shortenedUrlRepository, 
            NumberConverter numberConverter, 
            IncrementNumberOfViewsService incrementNumberOfViewsService) {
        
        this.shortenedUrlRepository = shortenedUrlRepository;
        this.numberConverter = numberConverter;
        this.incrementNumberOfViewsService = incrementNumberOfViewsService;
    }
    
    @Override
    public String shortenUrl(String url) {
        ShortenedUrl shortenedUrl = new ShortenedUrl(url);
        shortenedUrlRepository.save(shortenedUrl);
        LOGGER.info("Saved ShortenedUrl with id {}", shortenedUrl.getId());
        String shortenedUrlStr = numberConverter.convertToAlphabeth(shortenedUrl.getId());
        LOGGER.info("URL {} was shortened to {}", url, shortenedUrlStr);
        return shortenedUrlStr;
    }
    
    @Override
    public String resolveShortenedUrl(String shortenedUrl) {
        long id = numberConverter.convertFromAlphabeth(shortenedUrl);
        ShortenedUrl foundShortenedUrl = shortenedUrlRepository.findById(id);
        if (foundShortenedUrl != null) {
            incrementNumberOfViewsService.incrementNumberOfViews(foundShortenedUrl.getId());
            return foundShortenedUrl.getFullUrl();
        } else {
            return null;
        }
    }
}
