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

package org.urlshortener.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Evgeniy Khist
 */
@Entity
@Table(name = "SHORTENED_URL")
public class ShortenedUrl implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idGenerator")
    @SequenceGenerator(name = "idGenerator", sequenceName = "SHORTENED_URL_SEQ", allocationSize = 100, initialValue = 10000)
    @Column(name = "SHORTENED_URL_ID")
    private Long id;
    
    @NotNull
    @Column(name = "FULL_URL", nullable = false)
    private String fullUrl;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP", nullable = false)
    private Date createdTimestamp = new Date();

    @NotNull
    @Column(name = "NUM_VIEWS", nullable = false)
    private Long numberOfViews = 0L;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_VIEW_TIMESTAMP", nullable = true)
    private Date lastViewTimestamp = new Date();
    
    // Default constructor for JPA
    public ShortenedUrl() {
    }

    public ShortenedUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }
    
    public Long getId() {
        return id;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Long getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(Long numberOfViews) {
        this.numberOfViews = numberOfViews;
    }
    
    public Date getLastViewTimestamp() {
        return lastViewTimestamp;
    }

    public void setLastViewTimestamp(Date lastViewTimestamp) {
        this.lastViewTimestamp = lastViewTimestamp;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShortenedUrl other = (ShortenedUrl) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ShortenedUrl{" + "id=" + id + ", fullUrl=" + fullUrl + '}';
    }
}
