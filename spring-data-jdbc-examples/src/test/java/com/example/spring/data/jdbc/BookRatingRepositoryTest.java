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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

import com.example.spring.data.jdbc.entity.Book;
import com.example.spring.data.jdbc.entity.BookRating;
import com.example.spring.data.jdbc.repository.BookRatingRepository;
import com.example.spring.data.jdbc.repository.BookRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@Transactional
@Rollback(false)
@Import(ProxyDataSourceConfig.class)
@Slf4j
class BookRatingRepositoryTest extends AbstractContainerBaseTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private BookRatingRepository bookRatingRepository;

  @Autowired
  private TransactionTemplate txTemplate;

  private Book book;
  private BookRating rating;

  @BeforeEach
  void setUp() {
    txTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);

    doInNewTransaction(() -> {
      book = Book.of(
          "007-6092019909",
          "Patterns of Enterprise Application Architecture",
          LocalDate.parse("2002-11-15")
      );
      book = bookRepository.save(book);

      rating = BookRating.of(book, new BigDecimal("4.4"), 240);
      rating = bookRatingRepository.save(rating);
    });
  }

  @AfterEach
  void cleanUp() {
    bookRatingRepository.deleteAll();
    bookRepository.deleteAll();
  }

  @Test
  void implicitOptimisticLock() {
    log.info("@Version and OptimisticLockingFailureException");

    assertThatThrownBy(() ->
        doInNewTransaction(() -> {
          BookRating ratingTx1 = bookRatingRepository.findById(this.rating.getId()).orElse(null);

          doInNewTransaction(() -> {
            BookRating ratingTx2 = bookRatingRepository.findById(this.rating.getId()).orElse(null);

            assertThat(ratingTx2).isNotNull();
            assertThat(ratingTx2.getVersion()).isEqualTo(this.rating.getVersion());

            ratingTx2.setRating(ratingTx2.getRating().add(new BigDecimal("0.1")));
            ratingTx2.setNumberOfRatings(ratingTx2.getNumberOfRatings() + 1);

            bookRatingRepository.save(ratingTx2);
          });

          assertThat(ratingTx1).isNotNull();
          assertThat(ratingTx1.getVersion()).isEqualTo(this.rating.getVersion());

          ratingTx1.setRating(ratingTx1.getRating().add(new BigDecimal("0.2")));
          ratingTx1.setNumberOfRatings(ratingTx1.getNumberOfRatings() + 1);

          bookRatingRepository.save(ratingTx1);
        }))
        .isInstanceOf(DbActionExecutionException.class)
        .hasCauseInstanceOf(OptimisticLockingFailureException.class);

    BookRating rating = bookRatingRepository.findById(this.rating.getId()).orElse(null);

    assertThat(rating).isNotNull();
    assertThat(rating.getVersion()).isEqualTo(this.rating.getVersion() + 1);
    assertThat(rating.getRating())
        .isEqualByComparingTo(this.rating.getRating().add(new BigDecimal("0.1")));
    assertThat(rating.getNumberOfRatings()).isEqualTo(this.rating.getNumberOfRatings() + 1);
  }

  private void doInNewTransaction(Runnable runnable) {
    txTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        runnable.run();
      }
    });
  }
}
