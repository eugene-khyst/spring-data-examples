/*
 * Copyright 2020 Evgeniy Khyst
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

package com.example.spring.data.jdbc.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Book {

  @Id
  @With
  private final Long id;

  @EqualsAndHashCode.Include
  private final String isbn;

  private final String title;

  private final LocalDate publicationDate;

  private final List<BookAuthor> authors;

  private final Set<BookCategory> categories;

  public static Book of(String isbn, String title, LocalDate publicationDate) {
    return new Book(null, isbn, title, publicationDate, new ArrayList<>(), new HashSet<>());
  }

  public void addAuthor(Author author) {
    authors.add(new BookAuthor(author.getId()));
  }

  public void addCategory(Category category) {
    categories.add(new BookCategory(category.getId()));
  }
}
