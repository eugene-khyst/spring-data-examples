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
package org.urlshortener.repository;

import java.util.Date;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urlshortener.model.ShortenedUrl;
import org.urlshortener.model.ShortenedUrl_;

/**
 *
 * @author Evgeniy Khist
 */
@Transactional(Transactional.TxType.REQUIRED)
public class ShortenedUrlJpaRepository implements ShortenedUrlRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortenedUrlJpaRepository.class);
    
    @Inject
    private EntityManager em;

    @Override
    public void save(ShortenedUrl shortenedUrl) {
        em.persist(shortenedUrl);
    }

    @Override
    public ShortenedUrl findById(Long id) {
        return em.find(ShortenedUrl.class, id);
    }

    @Override
    public void incrementNumberOfViews(Long shortenedUrlId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate update = cb.createCriteriaUpdate(ShortenedUrl.class);
        Root su = update.from(ShortenedUrl.class);
        update.set(ShortenedUrl_.numberOfViews, cb.sum(su.get(ShortenedUrl_.numberOfViews), 1));
        Date currentTimestamp = getCurrentTimestamp();
        update.set(ShortenedUrl_.lastViewTimestamp, currentTimestamp);
        update.where(cb.equal(su.get(ShortenedUrl_.id), shortenedUrlId));
        Query query = em.createQuery(update);
        int rowCount = query.executeUpdate();
        if (rowCount == 1) {
            LOGGER.info("Number of views and last view timestamp for ShortenedUrl with id {} were updated", shortenedUrlId);
        } else {
            LOGGER.warn("Suspicious results of updating ShortenedUrl with id {} - {} rows updated", shortenedUrlId, rowCount);
        }
    }

    protected static Date getCurrentTimestamp() {
        return new Date();
    }
}
