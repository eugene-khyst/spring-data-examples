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

package com.example.spring.data.jpa.repository;

import com.example.spring.data.jpa.entity.Book;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository
    extends AbstractBookRepository<Book>, BookRepositoryCustom {

  @EntityGraph("Book.authors")
  List<Book> findByPublicationDateAfter(LocalDate date, Sort sort);

  @EntityGraph("Book.authors")
  List<Book> findByPublicationDateAfter(LocalDate date, Pageable pageable);

  @EntityGraph("Book.authors-categories")
  List<Book> findByPublicationDateBetween(LocalDate startDate, LocalDate endDate, Sort sort);

  @Query("select b from Book b join fetch b.authors"
      + " where b.publicationDate > :date"
      + " order by b.publicationDate asc")
  List<Book> findByPublicationDateAfterJoinFetch(LocalDate date);

@Query("select distinct b from Book b join fetch b.authors"
    + " where b.publicationDate > :date"
    + " order by b.publicationDate asc")
List<Book> findByPublicationDateAfterJoinFetchDistinct(LocalDate date);
}
