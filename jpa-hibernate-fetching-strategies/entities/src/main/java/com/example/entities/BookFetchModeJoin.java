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
package com.example.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author Evgeniy Khyst
 */
@Entity
public class BookFetchModeJoin extends AbstractBook {

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private List<Author> authors = new ArrayList<>();

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    private List<Category> categories = new ArrayList<>();

    @Override
    public List<Author> getAuthors() {
        return authors;
    }

    @Override
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
