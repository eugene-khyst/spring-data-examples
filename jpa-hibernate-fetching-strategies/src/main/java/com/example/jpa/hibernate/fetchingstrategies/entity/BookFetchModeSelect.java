/*
 * Copyright 2019 Evgeniy Khyst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jpa.hibernate.fetchingstrategies.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class BookFetchModeSelect extends AbstractBook {

  @ManyToMany(fetch = FetchType.EAGER)
  @Fetch(FetchMode.SELECT)
  private List<Author> authors = new ArrayList<>();

  @ManyToMany
  @Fetch(FetchMode.SELECT)
  private List<Category> categories = new ArrayList<>();

  @Override
  public List<Author> getAuthors() {
    return authors;
  }

  @Override
  public Collection<Category> getCategories() {
    return categories;
  }
}
