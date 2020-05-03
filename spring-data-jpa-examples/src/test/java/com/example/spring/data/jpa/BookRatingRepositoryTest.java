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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

import com.example.spring.data.jpa.entity.Book;
import com.example.spring.data.jpa.entity.BookRating;
import com.example.spring.data.jpa.repository.BookRatingRepository;
import com.example.spring.data.jpa.repository.BookRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.OptimisticLockException;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(SpringExtension.class)
@TestInstance(PER_CLASS)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = NONE)
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
      book = new Book();
      book.setIsbn("007-6092019909");
      book.setTitle("Patterns of Enterprise Application Architecture");
      book.setPublicationDate(LocalDate.parse("2002-11-15"));
      bookRepository.save(book);

      rating = new BookRating();
      rating.setBook(book);
      rating.setRating(new BigDecimal("4.4"));
      rating.setNumberOfRatings(240);
      bookRatingRepository.save(rating);
    });
  }

  @AfterEach
  void cleanUp() {
    bookRatingRepository.deleteAll();
    bookRepository.deleteAll();
  }

  @Test
  void implicitOptimisticLock() {
    log.info("@Version and StaleObjectStateException");

    assertThatThrownBy(() ->
        doInNewTransaction(() -> {
          BookRating ratingTx1 = bookRatingRepository.findByBookIsbn(this.book.getIsbn());

          doInNewTransaction(() -> {
            BookRating ratingTx2 = bookRatingRepository.findByBookIsbn(this.book.getIsbn());

            assertThat(ratingTx2.getVersion()).isEqualTo(this.rating.getVersion());

            ratingTx2.setRating(ratingTx2.getRating().add(new BigDecimal("0.1")));
            ratingTx2.setNumberOfRatings(ratingTx2.getNumberOfRatings() + 1);
          });

          assertThat(ratingTx1.getVersion()).isEqualTo(this.rating.getVersion());

          ratingTx1.setRating(ratingTx1.getRating().add(new BigDecimal("0.2")));
          ratingTx1.setNumberOfRatings(ratingTx1.getNumberOfRatings() + 1);
        }))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class)
        .hasCauseInstanceOf(StaleObjectStateException.class);

    BookRating rating = bookRatingRepository.findByBookIsbn(this.book.getIsbn());

    assertThat(rating.getVersion()).isEqualTo(this.rating.getVersion() + 1);
    assertThat(rating.getRating())
        .isEqualByComparingTo(this.rating.getRating().add(new BigDecimal("0.1")));
    assertThat(rating.getNumberOfRatings()).isEqualTo(this.rating.getNumberOfRatings() + 1);
  }

  @Test
  void explicitOptimisticLock() {
    log.info("@Lock(OPTIMISTIC) and OptimisticLockException");

    assertThatThrownBy(() ->
        doInNewTransaction(() -> {
          BookRating ratingTx1 =
              bookRatingRepository.findByBookIsbnOptimisticLock(this.book.getIsbn());

          doInNewTransaction(() -> {
            BookRating ratingTx2 = bookRatingRepository.findByBookIsbn(this.book.getIsbn());

            assertThat(ratingTx2.getVersion()).isEqualTo(this.rating.getVersion());

            ratingTx2.setRating(ratingTx2.getRating().add(new BigDecimal("0.1")));
            ratingTx2.setNumberOfRatings(ratingTx2.getNumberOfRatings() + 1);
          });

          assertThat(ratingTx1.getVersion()).isEqualTo(this.rating.getVersion());
        }))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class)
        .hasCauseInstanceOf(OptimisticLockException.class);

    BookRating rating = bookRatingRepository.findByBookIsbn(this.book.getIsbn());

    assertThat(rating.getVersion()).isEqualTo(this.rating.getVersion() + 1);
    assertThat(rating.getRating())
        .isEqualByComparingTo(this.rating.getRating().add(new BigDecimal("0.1")));
    assertThat(rating.getNumberOfRatings()).isEqualTo(this.rating.getNumberOfRatings() + 1);
  }

  @Test
  void explicitOptimisticForceIncrementLock() {
    log.info("@Lock(OPTIMISTIC_FORCE_INCREMENT) and StaleObjectStateException");

    assertThatThrownBy(() ->
        doInNewTransaction(() -> {
          BookRating ratingTx1 =
              bookRatingRepository.findByBookIsbnOptimisticForceIncrementLock(this.book.getIsbn());

          doInNewTransaction(() -> {
            BookRating ratingTx2 = bookRatingRepository.findByBookIsbn(this.book.getIsbn());

            assertThat(ratingTx2.getVersion()).isEqualTo(this.rating.getVersion());

            ratingTx2.setRating(ratingTx2.getRating().add(new BigDecimal("0.1")));
            ratingTx2.setNumberOfRatings(ratingTx2.getNumberOfRatings() + 1);
          });

          assertThat(ratingTx1.getVersion()).isEqualTo(this.rating.getVersion());
        }))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class)
        .hasCauseInstanceOf(StaleObjectStateException.class);

    BookRating rating = bookRatingRepository.findByBookIsbn(this.book.getIsbn());

    assertThat(rating.getVersion()).isEqualTo(this.rating.getVersion() + 1);
    assertThat(rating.getRating())
        .isEqualByComparingTo(this.rating.getRating().add(new BigDecimal("0.1")));
    assertThat(rating.getNumberOfRatings()).isEqualTo(this.rating.getNumberOfRatings() + 1);
  }

  @Test
  void explicitPessimisticWriteLock() {
    log.info("@Lock(PESSIMISTIC_WRITE)");

    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(2);

    doInNewTransaction(() -> {
      BookRating ratingTx1 =
          bookRatingRepository.findByBookIsbnPessimisticWriteLock(this.book.getIsbn());

      CompletableFuture.runAsync(() ->
          doInNewTransaction(() -> {
            startLatch.countDown();

            BookRating ratingTx2 =
                bookRatingRepository.findByBookIsbnPessimisticWriteLock(this.book.getIsbn());

            assertThat(ratingTx2.getVersion()).isEqualTo(this.rating.getVersion() + 1);

            ratingTx2.setRating(ratingTx2.getRating().add(new BigDecimal("0.1")));
            ratingTx2.setNumberOfRatings(ratingTx2.getNumberOfRatings() + 1);
          }, doneLatch));

      await(startLatch);

      assertThat(ratingTx1.getVersion()).isEqualTo(this.rating.getVersion());

      ratingTx1.setRating(ratingTx1.getRating().add(new BigDecimal("0.2")));
      ratingTx1.setNumberOfRatings(ratingTx1.getNumberOfRatings() + 1);
    }, doneLatch);

    await(doneLatch);

    BookRating rating = bookRatingRepository.findByBookIsbn(this.book.getIsbn());

    assertThat(rating.getVersion()).isEqualTo(this.rating.getVersion() + 2);
    assertThat(rating.getRating())
        .isEqualByComparingTo(this.rating.getRating().add(new BigDecimal("0.3")));
    assertThat(rating.getNumberOfRatings()).isEqualTo(this.rating.getNumberOfRatings() + 2);
  }

  @Test
  void explicitPessimisticReadLock() {
    log.info("@Lock(PESSIMISTIC_READ)");

    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(3);

    doInNewTransaction(() -> {
      bookRatingRepository.findByBookIsbnPessimisticReadLock(this.book.getIsbn());

      doInNewTransaction(() ->
              bookRatingRepository.findByBookIsbnPessimisticReadLock(this.book.getIsbn()),
          doneLatch);

      CompletableFuture.runAsync(() ->
          doInNewTransaction(() -> {
            startLatch.countDown();

            bookRatingRepository.findByBookIsbnPessimisticWriteLock(this.book.getIsbn());
          }, doneLatch));

      await(startLatch);
    }, doneLatch);

    await(doneLatch);
  }

  private void doInNewTransaction(Runnable runnable) {
    doInNewTransaction(runnable, null);
  }

  private void doInNewTransaction(Runnable runnable, CountDownLatch doneLatch) {
    try {
      txTemplate.execute(new TransactionCallbackWithoutResult() {
        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
          runnable.run();
        }
      });
    } finally {
      if (doneLatch != null) {
        doneLatch.countDown();
      }
    }
  }

  private void await(CountDownLatch latch) {
    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
