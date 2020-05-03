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

import static javax.persistence.LockModeType.OPTIMISTIC;
import static javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT;
import static javax.persistence.LockModeType.PESSIMISTIC_READ;
import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.example.spring.data.jpa.entity.BookRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface BookRatingRepository extends JpaRepository<BookRating, Long> {

  BookRating findByBookIsbn(String isbn);

  @Lock(OPTIMISTIC)
  @Query("select br from BookRating br where br.book.isbn = :isbn")
  BookRating findByBookIsbnOptimisticLock(String isbn);

  @Lock(OPTIMISTIC_FORCE_INCREMENT)
  @Query("select br from BookRating br where br.book.isbn = :isbn")
  BookRating findByBookIsbnOptimisticForceIncrementLock(String isbn);

  @Lock(PESSIMISTIC_WRITE)
  @Query("select br from BookRating br where br.book.isbn = :isbn")
  BookRating findByBookIsbnPessimisticWriteLock(String isbn);

  @Lock(PESSIMISTIC_READ)
  @Query("select br from BookRating br where br.book.isbn = :isbn")
  BookRating findByBookIsbnPessimisticReadLock(String isbn);
}
