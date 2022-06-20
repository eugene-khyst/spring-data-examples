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

import com.example.spring.data.jpa.entity.Author;
import com.example.spring.data.jpa.entity.BookWithMultipleBags;
import com.example.spring.data.jpa.entity.Category;
import com.example.spring.data.jpa.repository.AuthorRepository;
import com.example.spring.data.jpa.repository.BookWithMultipleBagsRepository;
import com.example.spring.data.jpa.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.loader.MultipleBagFetchException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.data.domain.Sort.Direction.ASC;

@ExtendWith(SpringExtension.class)
@TestInstance(PER_CLASS)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = NONE)
@Transactional(readOnly = true)
@Import(DatasourceProxyBeanPostProcessor.class)
@Slf4j
class BookWithMultipleBagsRepositoryTest extends BaseIntegrationTest {

  Category softwareDevelopment;
  Category systemDesign;
  Author martinFowler;
  BookWithMultipleBags poeaa;
  @Autowired private BookWithMultipleBagsRepository bookRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private TransactionTemplate transactionTemplate;

  @BeforeAll
  void baseSetUp() {
    transactionTemplate.execute(
        new TransactionCallbackWithoutResult() {
          @Override
          protected void doInTransactionWithoutResult(TransactionStatus status) {
            softwareDevelopment = new Category("Software development");
            categoryRepository.save(softwareDevelopment);

            systemDesign = new Category("System design");
            categoryRepository.save(systemDesign);

            martinFowler = new Author("Martin Fowler");
            authorRepository.save(martinFowler);

            poeaa = new BookWithMultipleBags();
            poeaa.setIsbn("007-6092019909");
            poeaa.setTitle("Patterns of Enterprise Application Architecture");
            poeaa.setPublicationDate(LocalDate.parse("2002-11-15"));
            poeaa.getAuthors().addAll(List.of(martinFowler));
            poeaa.getCategories().addAll(List.of(softwareDevelopment, systemDesign));
            bookRepository.save(poeaa);
          }
        });
  }

  @AfterAll
  void baseCleanUp() {
    transactionTemplate.execute(
        new TransactionCallbackWithoutResult() {
          @Override
          protected void doInTransactionWithoutResult(TransactionStatus status) {
            bookRepository.deleteAll();
            authorRepository.deleteAll();
            categoryRepository.deleteAll();
          }
        });
  }

  @Test
  void multipleBagFetchException() {
    log.info("MultipleBagFetchException on @EntityGraph with multiple attribute nodes");

    assertThatThrownBy(
            () ->
                bookRepository.findByPublicationDateBetween(
                    LocalDate.parse("2000-01-01"),
                    LocalDate.parse("2020-01-01"),
                    Sort.by(ASC, "publicationDate")))
        .hasRootCauseInstanceOf(MultipleBagFetchException.class)
        .hasMessageContaining("cannot simultaneously fetch multiple bags");
    // Trying to fetch multiple many-to-many relations
    // List<Author> authors and List<Category> categories
    // that both have type List results in exception
    // org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags
  }
}
