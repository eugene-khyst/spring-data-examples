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

package com.example.spring.data.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.data.domain.Sort.Direction.ASC;

import com.example.spring.data.jdbc.dto.BookDto;
import com.example.spring.data.jdbc.entity.Author;
import com.example.spring.data.jdbc.entity.Book;
import com.example.spring.data.jdbc.entity.Category;
import com.example.spring.data.jdbc.mapper.BookMapper;
import com.example.spring.data.jdbc.repository.AuthorRepository;
import com.example.spring.data.jdbc.repository.BookRepository;
import com.example.spring.data.jdbc.repository.CategoryRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@SpringBootTest
@TestInstance(PER_CLASS)
@Transactional
@Rollback(false)
@Import(ProxyDataSourceConfig.class)
@Slf4j
class BookRepositoryTest extends AbstractContainerBaseTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private BookMapper bookMapper;

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
    softwareDevelopment = categoryRepository.save(softwareDevelopment);

    systemDesign = Category.of("System design");
    systemDesign = categoryRepository.save(systemDesign);
  }

  void saveAuthors() {
    martinFowler = Author.of("Martin Fowler");
    martinFowler = authorRepository.save(martinFowler);

    gregorHohpe = Author.of("Gregor Hohpe");
    gregorHohpe = authorRepository.save(gregorHohpe);

    bobbyWoolf = Author.of("Bobby Woolf");
    bobbyWoolf = authorRepository.save(bobbyWoolf);
  }

  void saveBooks() {
    poeaa = Book.of("007-6092019909",
        "Patterns of Enterprise Application Architecture",
        LocalDate.parse("2002-11-15"));
    poeaa.addAuthor(martinFowler);
    poeaa.addCategory(softwareDevelopment);
    poeaa.addCategory(systemDesign);
    poeaa = bookRepository.save(poeaa);

    eip = Book.of("978-0321200686",
        "Enterprise Integration Patterns", LocalDate.parse("2003-10-20"));
    eip.addAuthor(gregorHohpe);
    eip.addAuthor(bobbyWoolf);
    eip.addCategory(softwareDevelopment);
    eip.addCategory(systemDesign);
    eip = bookRepository.save(eip);
  }

  @AfterAll
  void cleanUp() {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  void updateOneToManyRelations() {
    log.info("CrudRepository.save");

    Book eip = bookRepository.findById(this.eip.getId()).get();
    eip.getAuthors().remove(1);
    eip = bookRepository.save(eip);
    //During saving all entities referenced from an aggregate root are deleted and recreated

    eip.getCategories().removeIf(categoryRef ->
        Objects.equals(categoryRef.getCategory(), systemDesign.getId()));
    eip = bookRepository.save(eip);

    log.info("{}", eip);
  }

  @Test
  void findById() {
    log.info("CrudRepository.findById");

    Optional<Book> poeaa = bookRepository.findById(this.poeaa.getId());
    assertThat(poeaa).hasValueSatisfying(equalTo(this.poeaa));
  }

  @Test
  void findAllWithPageable() {
    log.info("PagingAndSortingRepository.findAll(Pageable)");

    Iterable<Book> result = bookRepository.findAll(
        PageRequest.of(0, 1, ASC, "publication_date"));
    List<Book> books = new ArrayList<>();
    result.forEach(books::add);

    assertThat(books)
        .hasSize(1)
        .satisfies(equalTo(this.poeaa), atIndex(0));
  }

  @Test
  void queryMethod() {
    log.info("@Query with SQL");

    List<Book> books = bookRepository.findByTitleContains("Pattern");
    assertThat(books)
        .hasSize(2)
        .satisfies(equalTo(this.poeaa), atIndex(0))
        .satisfies(equalTo(this.eip), atIndex(1));
  }

  @Test
  void queryMethodWithJoin() {
    log.info("@Query with SQL join");

    List<Book> books = bookRepository.findByPublicationDateAfter(LocalDate.parse("2000-01-01"));
    assertThat(books)
        .hasSize(3)
        .satisfies(equalTo(this.poeaa), atIndex(0))
        .satisfies(equalTo(this.eip), atIndex(1))
        .satisfies(equalTo(this.eip), atIndex(2));
    //The EIP Book is present 2 times in the result list.
    //Duplicate is caused by join of the EIP Book with 2 Authors.
    //The POEAA Book has 1 author, so doesn't have duplicates in the result list.
  }

  @Test
  void queryMethodWithPagination() {
    log.info("@Query with SQL and pagination");

    String title = "Pattern";
    Pageable pageRequest = PageRequest.of(1, 1); //0-based page number and page size
    List<Book> books = bookRepository.findByTitleContains(
        title, pageRequest.getPageNumber(), pageRequest.getPageSize());
    assertThat(books)
        .hasSize(1)
        .satisfies(equalTo(this.eip), atIndex(0));

    int count = bookRepository.countByTitleContains(title);
    assertThat(count).isEqualTo(2);

    Page<Book> page = new PageImpl<>(books, pageRequest, count);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getNumber()).isEqualTo(1);
    assertThat(page.getSize()).isEqualTo(1);
  }

  @Test
  void mapToDto() {
    log.info("Mapping from entity to DTO using MapStruct");

    Optional<BookDto> eip = bookRepository.findById(this.eip.getId())
        .map(bookMapper::toBookDto);

    assertThat(eip).hasValueSatisfying(book -> {
      assertThat(book.getIsbn()).isNotEmpty();
      assertThat(book.getTitle()).isNotEmpty();
      assertThat(book.getPublicationDate()).isNotNull();
      assertThat(book.getAuthors()).hasSize(2);
      assertThat(book.getCategories()).hasSize(2);
    });

    eip.ifPresent(dto -> log.info("{}", dto));
  }

  @Test
  void reactiveWrapBlocking() {
    log.info("Reactive Mono.fromCallable");

    Mono<Book> blockingWrapper = Mono.fromCallable(() ->
        bookRepository.findById(this.poeaa.getId()).orElse(null))
        .subscribeOn(Schedulers.boundedElastic());

    Book book = blockingWrapper.block();
    assertThat(book).satisfies(equalTo(this.poeaa));
  }

  Consumer<Book> equalTo(Book expected) {
    return actual -> {
      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertThat(actual.getIsbn()).isEqualTo(expected.getIsbn());
      assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
      assertThat(actual.getPublicationDate()).isEqualTo(expected.getPublicationDate());
      assertThat(actual.getAuthors()).containsExactlyInAnyOrderElementsOf(expected.getAuthors());
      assertThat(actual.getCategories())
          .containsExactlyInAnyOrderElementsOf(expected.getCategories());
    };
  }
}
