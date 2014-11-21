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

import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import org.urlshortener.model.ShortenedUrl;
import org.urlshortener.util.NumberConverter;

/**
 *
 * @author Evgeniy Khist
 */
public class ShortenedUrlEntityListener {

    @Inject
    private BeanManager beanManager; //Workaround WFLY-2387
    
    @PostPersist
    @PostLoad
    public void setShortUrl(ShortenedUrl shortenedUrl) {
        shortenedUrl.setShortUrl(getNumberConverter().convertToAlphabeth(shortenedUrl.getId()));
    }

    private NumberConverter getNumberConverter() {
        Class<NumberConverter> aClass = NumberConverter.class;
        Set<Bean<?>> beans = beanManager.getBeans(aClass);
        if (beans.size() != 1) {
            throw new RuntimeException("Cannot resolve an ambiguous dependency " + aClass);
        }
        Bean<?> bean = beans.iterator().next();
        CreationalContext ctx = beanManager.createCreationalContext(bean);
        return (NumberConverter) beanManager.getReference(bean, aClass, ctx);
    }
}
