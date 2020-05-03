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

package com.example.spring.data.r2dbc;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.data.domain.Sort.Direction.ASC;

import com.example.spring.data.r2dbc.entity.Author;
import com.example.spring.data.r2dbc.entity.Book;
import com.example.spring.data.r2dbc.entity.BookAuthor;
import com.example.spring.data.r2dbc.entity.BookCategory;
import com.example.spring.data.r2dbc.entity.Category;
import com.example.spring.data.r2dbc.mapper.BookMappingService;
import com.example.spring.data.r2dbc.repository.AuthorRepository;
import com.example.spring.data.r2dbc.repository.BookAuthorRepository;
import com.example.spring.data.r2dbc.repository.BookCategoryRepository;
import com.example.spring.data.r2dbc.repository.BookRepository;
import com.example.spring.data.r2dbc.repository.CategoryRepository;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(PER_CLASS)
@Import(ConnectionFactoryConfig.class)
@Slf4j
class BookRepositoryTest extends AbstractContainerBaseTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private BookAuthorRepository bookAuthorRepository;

  @Autowired
  private BookCategoryRepository bookCategoryRepository;

  @Autowired
  private BookMappingService bookMappingService;

  private Category softwareDevelopment;
  private Category systemDesign;

  private Author martinFowler;
  private Author gregorHohpe;
  private Author bobbyWoolf;

  private Book poeaa;
  private Book eip;

  @BeforeAll
  void setUp() {
    saveCategories();
    saveAuthors();
    saveBooks();
  }

  void saveCategories() {
    softwareDevelopment = Category.of("Software development");
    softwareDevelopment = categoryRepository.save(softwareDevelopment).block();

    systemDesign = Category.of("System design");
    systemDesign = categoryRepository.save(systemDesign).block();
  }

  void saveAuthors() {
    martinFowler = Author.of("Martin Fowler");
    martinFowler = authorRepository.save(martinFowler).block();

    gregorHohpe = Author.of("Gregor Hohpe");
    gregorHohpe = authorRepository.save(gregorHohpe).block();

    bobbyWoolf = Author.of("Bobby Woolf");
    bobbyWoolf = authorRepository.save(bobbyWoolf).block();
  }

  void saveBooks() {
    poeaa = Book.of("007-6092019909",
        "Patterns of Enterprise Application Architecture",
        LocalDate.parse("2002-11-15"));
    poeaa = bookRepository.save(poeaa).block();

    bookAuthorRepository.save(BookAuthor.of(poeaa, martinFowler)).block();
    bookCategoryRepository.save(BookCategory.of(poeaa, softwareDevelopment)).block();
    bookCategoryRepository.save(BookCategory.of(poeaa, systemDesign)).block();

    eip = Book.of("978-0321200686",
        "Enterprise Integration Patterns", LocalDate.parse("2003-10-20"));
    eip = bookRepository.save(eip).block();

    bookAuthorRepository.save(BookAuthor.of(eip, gregorHohpe)).block();
    bookAuthorRepository.save(BookAuthor.of(eip, bobbyWoolf)).block();
    bookCategoryRepository.save(BookCategory.of(eip, softwareDevelopment)).block();
    bookCategoryRepository.save(BookCategory.of(eip, systemDesign)).block();
  }

  @AfterAll
  void cleanUp() {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  void findById() {
    log.info("CrudRepository.findById");

    bookRepository.findById(this.poeaa.getId())
        .as(StepVerifier::create)
        .expectNextMatches(equalTo(this.poeaa))
        .verifyComplete();
  }

  @Test
  void queryMethod() {
    log.info("Query method with Sort");

    bookRepository.findByTitleContains("Pattern", Sort.by(ASC, "publicationDate"))
        .as(StepVerifier::create)
        .expectNextMatches(equalTo(this.poeaa))
        .expectNextMatches(equalTo(this.eip))
        .verifyComplete();
  }

  @Test
  void queryMethodWithPagination() {
    log.info("@Query with SQL and pagination");

    String title = "Pattern";
    Pageable pageRequest = PageRequest.of(1, 1); //0-based page number and page size

    Mono.zip(
        bookRepository.findByTitleContains(title,
            pageRequest.getPageNumber(), pageRequest.getPageSize(),
            Sort.by(ASC, "publicationDate"))
            .collectList(),
        bookRepository.countByTitleContains(title),
        (books, count) -> new PageImpl<>(books, pageRequest, count))
        .as(StepVerifier::create)
        .expectNextMatches(page ->
            equalTo(this.eip).test(page.getContent().get(0))
                && page.getTotalPages() == 2
                && page.getNumber() == 1
                && page.getSize() == 1)
        .verifyComplete();
  }

  @Test
  void mapToDto() {
    log.info("Map to DTO");

    bookRepository.findByTitleContains("Pattern", Sort.by(ASC, "publicationDate"))
        .flatMap(bookMappingService::toBookDto)
        .doOnNext(bookDto -> log.info("{}", bookDto))
        .as(StepVerifier::create)
        .expectNextMatches(bookDto ->
            Objects.equals(bookDto.getIsbn(), this.poeaa.getIsbn())
                && Objects.equals(bookDto.getTitle(), this.poeaa.getTitle())
                && Objects.equals(bookDto.getPublicationDate(), this.poeaa.getPublicationDate())
                && bookDto.getAuthors().size() == 1
                && bookDto.getCategories().size() == 2
        )
        .expectNextMatches(bookDto ->
            Objects.equals(bookDto.getIsbn(), this.eip.getIsbn())
                && Objects.equals(bookDto.getTitle(), this.eip.getTitle())
                && Objects.equals(bookDto.getPublicationDate(), this.eip.getPublicationDate())
                && bookDto.getAuthors().size() == 2
                && bookDto.getCategories().size() == 2
        )
        .verifyComplete();
  }

  Predicate<Book> equalTo(Book expected) {
    return actual -> Objects.equals(actual.getId(), expected.getId())
        && Objects.equals(actual.getIsbn(), expected.getIsbn())
        && Objects.equals(actual.getTitle(), expected.getTitle())
        && Objects.equals(actual.getPublicationDate(), expected.getPublicationDate());
  }
}
