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

package com.example.spring.data.r2dbc.repository;

import com.example.spring.data.r2dbc.entity.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookRepository extends ReactiveCrudRepository<Book, Long> {

  Flux<Book> findByTitleContains(String title, Sort sort);

  @Query("SELECT * FROM BOOK WHERE TITLE LIKE CONCAT('%', :title, '%')"
      + " ORDER BY PUBLICATION_DATE"
      + " OFFSET :start"
      + " FETCH NEXT :rowCount ROWS ONLY")
  Flux<Book> findByTitleContains(String title, int start, int rowCount, Sort sort);

  @Query("SELECT COUNT(*) FROM BOOK WHERE TITLE LIKE CONCAT('%', :title, '%')")
  Mono<Integer> countByTitleContains(String title);
}
