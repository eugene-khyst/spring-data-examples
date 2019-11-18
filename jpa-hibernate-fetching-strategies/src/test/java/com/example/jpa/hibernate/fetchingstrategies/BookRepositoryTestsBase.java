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

package com.example.jpa.hibernate.fetchingstrategies;

import com.example.jpa.hibernate.fetchingstrategies.entity.AbstractBook;
import com.example.jpa.hibernate.fetchingstrategies.entity.Author;
import com.example.jpa.hibernate.fetchingstrategies.entity.Category;
import com.example.jpa.hibernate.fetchingstrategies.repository.AuthorRepository;
import com.example.jpa.hibernate.fetchingstrategies.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public abstract class BookRepositoryTestsBase<T extends AbstractBook> {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private AuthorRepository authorRepository;

  protected Category softwareDevelopment;
  protected Category systemDesign;

  protected Author martinFowler;
  protected Author gregorHohpe;
  protected Author bobbyWoolf;

  protected T poeaa;
  protected T eip;

  protected abstract T newBook();

  protected abstract JpaRepository<T, Long> getBookRepository();

  @BeforeEach
  void baseSetUp() {
    softwareDevelopment = new Category();
    softwareDevelopment.setName("Software development");
    categoryRepository.save(softwareDevelopment);

    systemDesign = new Category();
    systemDesign.setName("System design");
    categoryRepository.save(systemDesign);

    martinFowler = new Author();
    martinFowler.setFullName("Martin Fowler");
    authorRepository.save(martinFowler);

    gregorHohpe = new Author();
    gregorHohpe.setFullName("Gregor Hohpe");
    authorRepository.save(gregorHohpe);

    bobbyWoolf = new Author();
    bobbyWoolf.setFullName("Bobby Woolf");
    authorRepository.save(bobbyWoolf);

    poeaa = newBook();
    poeaa.setIsbn("007-6092019909");
    poeaa.setTitle("Patterns of Enterprise Application Architecture");
    poeaa.setPublicationDate(LocalDate.parse("2002-11-15"));
    poeaa.getAuthors().addAll(List.of(martinFowler));
    poeaa.getCategories().addAll(List.of(softwareDevelopment, systemDesign));
    getBookRepository().save(poeaa);

    eip = newBook();
    eip.setIsbn("978-0321200686");
    eip.setTitle("Enterprise Integration Patterns");
    eip.setPublicationDate(LocalDate.parse("2003-10-20"));
    eip.getAuthors().addAll(List.of(gregorHohpe, bobbyWoolf));
    eip.getCategories().addAll(List.of(softwareDevelopment, systemDesign));
    getBookRepository().save(eip);
  }

  @AfterEach
  void baseCleanUp() {
    getBookRepository().deleteAll();
    authorRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  protected void assertThatHasAuthors(T book, String... authors) {
    assertThat(book.getAuthors().stream().map(Author::getFullName))
        .containsExactlyInAnyOrder(authors);
  }
}