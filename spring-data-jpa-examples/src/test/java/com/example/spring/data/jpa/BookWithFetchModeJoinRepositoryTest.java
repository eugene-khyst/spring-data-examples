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

import com.example.spring.data.jpa.entity.BookWithFetchModeJoin;
import com.example.spring.data.jpa.repository.AbstractBookRepository;
import com.example.spring.data.jpa.repository.BookWithFetchModeJoinRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class BookWithFetchModeJoinRepositoryTest
    extends AbstractBookRepositoryTest<BookWithFetchModeJoin> {

  @Autowired private BookWithFetchModeJoinRepository bookRepository;

  @Override
  BookWithFetchModeJoin createBook() {
    return new BookWithFetchModeJoin();
  }

  @Override
  AbstractBookRepository<BookWithFetchModeJoin> getBookRepository() {
    return bookRepository;
  }

  @Test
  @Override
  void findById() {
    log.info("CrudRepository.findById (HHH-13740)");

    Optional<BookWithFetchModeJoin> poeaa = bookRepository.findById(this.poeaa.getId());
    Optional<BookWithFetchModeJoin> eip = bookRepository.findById(this.eip.getId());

    log.info("Books were loaded");

    // Due to the issue https://hibernate.atlassian.net/browse/HHH-13740
    // List<Author> authors has duplicates:
    assertThat(poeaa)
        .hasValueSatisfying(
            book ->
                assertThat(book.getAuthors())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(martinFowler, martinFowler));

    assertThat(eip)
        .hasValueSatisfying(
            book ->
                assertThat(book.getAuthors())
                    .hasSize(4)
                    .containsExactlyInAnyOrder(gregorHohpe, gregorHohpe, bobbyWoolf, bobbyWoolf));
    // For more details see the <Git repo>/HHH-13740/README.md
  }
}
