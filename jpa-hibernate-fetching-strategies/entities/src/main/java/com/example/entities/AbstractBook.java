/*
 * Copyright 2016 Evgeniy Khyst.
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
package com.example.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Evgeniy Khyst
 */
@MappedSuperclass
public abstract class AbstractBook implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;

    private String isbn;

    private String title;

    @Temporal(TemporalType.DATE)
    private Date publicationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public abstract List<Author> getAuthors();

    public abstract void setAuthors(List<Author> authors);

    public abstract List<Category> getCategories();

    public abstract void setCategories(List<Category> categories);

    @Override
    public String toString() {
        return "AbstractBook{" + "id=" + id + ", title=" + title + '}';
    }
}
