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

package com.example.spring.data.jpa.repository;

import com.example.spring.data.jpa.entity.Book;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

  private final EntityManager em;

  @Override
  public List<Book> findByAuthorNameAndTitle(
      boolean fetchAuthor, boolean distinct, String authorName, String title) {

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Book> cq = cb.createQuery(Book.class);

    Root<Book> book = cq.from(Book.class);
    Path<Object> authors;

    if (fetchAuthor) {
      book.fetch("authors");
      authors = book.get("authors");
    } else {
      authors = book.join("authors");
    }

    List<Predicate> predicates = new ArrayList<>();
    if (authorName != null) {
      predicates.add(cb.like(authors.get("fullName"), "%" + authorName + "%"));
    }
    if (title != null) {
      predicates.add(cb.like(book.get("title"), "%" + title + "%"));
    }

    cq.select(book)
        .distinct(distinct)
        .where(predicates.toArray(new Predicate[0]))
        .orderBy(cb.asc(book.get("publicationDate")));

    return em.createQuery(cq).getResultList();
  }
}
