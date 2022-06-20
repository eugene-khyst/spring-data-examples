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

package com.example.spring.data.jpa;

import com.example.spring.data.jpa.dto.BookDto;
import com.example.spring.data.jpa.entity.Book;
import com.example.spring.data.jpa.mapper.BookMapper;
import com.example.spring.data.jpa.mapper.BookMapperImpl;
import com.example.spring.data.jpa.repository.AbstractBookRepository;
import com.example.spring.data.jpa.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Import(BookMapperImpl.class)
@Slf4j
class BookRepositoryTest extends AbstractBookRepositoryTest<Book> {

  @Autowired private BookRepository bookRepository;

  @Autowired private BookMapper bookMapper;

  @Override
  Book createBook() {
    return new Book();
  }

  @Override
  AbstractBookRepository<Book> getBookRepository() {
    return bookRepository;
  }

  @Test
  void entityGraph() {
    log.info("Query method with @EntityGraph");

    List<Book> books =
        bookRepository.findByPublicationDateAfter(
            LocalDate.parse("2000-01-01"), Sort.by(ASC, "publicationDate"));
    log.info("Books were loaded");
    assertThat(books)
        .hasSize(2)
        .satisfies(equalTo(this.poeaa), atIndex(0))
        .satisfies(equalTo(this.eip), atIndex(1));
  }

  @Test
  void entityGraphWithPageable() {
    log.info("Query method with @EntityGraph and Pageable");

    List<Book> books =
        bookRepository.findByPublicationDateAfter(
            LocalDate.parse("2000-01-01"), PageRequest.of(0, 1, ASC, "publicationDate"));
    // Usage of @EntityGraph with Pageable results in warning
    // HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
    log.info("Books were loaded");
    assertThat(books).hasSize(1).satisfies(equalTo(this.poeaa), atIndex(0));
  }

  @Test
  void entityGraphMultipleAttributeNodes() {
    log.info("@EntityGraph with multiple attribute nodes (HHH-13740)");

    List<Book> books =
        bookRepository.findByPublicationDateBetween(
            LocalDate.parse("2000-01-01"),
            LocalDate.parse("2020-01-01"),
            Sort.by(ASC, "publicationDate"));
    log.info("Books were loaded");
    assertThat(books).containsExactlyInAnyOrder(this.poeaa, this.eip);

    // Due to the issue https://hibernate.atlassian.net/browse/HHH-13740
    // List<Author> authors has duplicates:
    assertThat(books.get(0).getAuthors())
        .hasSize(2)
        .containsExactlyInAnyOrder(martinFowler, martinFowler);
    assertThat(books.get(1).getAuthors())
        .hasSize(4)
        .containsExactlyInAnyOrder(gregorHohpe, gregorHohpe, bobbyWoolf, bobbyWoolf);
    // For more details see the <Git repo>/HHH-13740/README.md
  }

  @Test
  void jpqlJoinFetch() {
    log.info("@Query with JPQL join fetch");

    List<Book> books =
        bookRepository.findByPublicationDateAfterJoinFetch(LocalDate.parse("2000-01-01"));
    log.info("Books were loaded");
    assertThat(books)
        .hasSize(3)
        .satisfies(equalTo(this.poeaa), atIndex(0))
        .satisfies(equalTo(this.eip), atIndex(1))
        .satisfies(equalTo(this.eip), atIndex(2));
    // The EIP Book is present 2 times in the result list.
    // Duplicate is caused by join of the EIP Book with 2 Authors.
    // The POEAA Book has 1 author, so doesn't have duplicates in the result list.
  }

  @Test
  void jpqlJoinFetchDistinct() {
    log.info("@Query with JPQL join fetch and distinct");

    List<Book> books =
        bookRepository.findByPublicationDateAfterJoinFetchDistinct(LocalDate.parse("2000-01-01"));
    log.info("Books were loaded");
    assertThat(books)
        .hasSize(2)
        .satisfies(equalTo(this.poeaa), atIndex(0))
        .satisfies(equalTo(this.eip), atIndex(1));
  }

  @Test
  void criteriaQuery() {
    log.info("Custom @Repository with Criteria API query");

    List<Book> books = bookRepository.findByAuthorNameAndTitle(false, false, null, "Enterprise");
    log.info("Books were loaded");
    assertThat(books)
        .hasSize(3)
        .satisfies(equalTo(this.poeaa), atIndex(0))
        .satisfies(equalTo(this.eip), atIndex(1))
        .satisfies(equalTo(this.eip), atIndex(2));
    // The EIP Book is present 2 times in the result list.
    // Duplicate is caused by join of the EIP Book with 2 Authors.
    // The POEAA Book has 1 author, so doesn't have duplicates in the result list.
  }

  @Test
  void criteriaQueryFetch() {
    log.info("Custom @Repository with Criteria API query with fetch");

    List<Book> books = bookRepository.findByAuthorNameAndTitle(true, false, null, "Enterprise");
    log.info("Books were loaded");
    assertThat(books)
        .hasSize(3)
        .satisfies(equalTo(this.poeaa), atIndex(0))
        .satisfies(equalTo(this.eip), atIndex(1))
        .satisfies(equalTo(this.eip), atIndex(2));
    // The EIP Book is present 2 times in the result list.
    // Duplicate is caused by join of the EIP Book with 2 Authors.
    // The POEAA Book has 1 author, so doesn't have duplicates in the result list.
  }

  @Test
  void criteriaQueryFetchDistinct() {
    log.info("Custom @Repository with Criteria API query with fetch and distinct");

    List<Book> books = bookRepository.findByAuthorNameAndTitle(true, true, null, "Enterprise");
    log.info("Books were loaded");
    assertThat(books)
        .hasSize(2)
        .satisfies(equalTo(this.poeaa), atIndex(0))
        .satisfies(equalTo(this.eip), atIndex(1));
  }

  @Test
  void mapToDto() {
    log.info("Mapping from entity to DTO using MapStruct");

    Optional<BookDto> eip = bookRepository.findById(this.eip.getId()).map(bookMapper::toBookDto);
    assertThat(eip)
        .hasValueSatisfying(
            book -> {
              assertThat(book.getIsbn()).isNotEmpty();
              assertThat(book.getTitle()).isNotEmpty();
              assertThat(book.getPublicationDate()).isNotNull();
              assertThat(book.getAuthors()).hasSize(2);
              assertThat(book.getCategories()).hasSize(2);
              log.info("{}", book);
            });
  }
}
