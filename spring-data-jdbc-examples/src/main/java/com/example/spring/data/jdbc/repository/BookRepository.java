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

package com.example.spring.data.jdbc.repository;

import com.example.spring.data.jdbc.entity.Book;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

  @Query("SELECT * FROM BOOK WHERE TITLE LIKE CONCAT('%', :title, '%')"
      + " ORDER BY PUBLICATION_DATE")
  List<Book> findByTitleContains(String title);

  @Query("SELECT * FROM BOOK WHERE TITLE LIKE CONCAT('%', :title, '%')"
      + " ORDER BY PUBLICATION_DATE"
      + " OFFSET :start"
      + " FETCH NEXT :rowCount ROWS ONLY")
  List<Book> findByTitleContains(String title, int start, int rowCount);

  @Query("SELECT COUNT(*) FROM BOOK WHERE TITLE LIKE CONCAT('%', :title, '%')")
  int countByTitleContains(String title);

  //JOIN in SQL query leads to duplicates in the result set.
  //Separate queries are still generated for List<AuthorRef> authors and Set<CategoryRef> categories.
  @Query("SELECT * FROM BOOK b"
      + " LEFT JOIN BOOK_AUTHOR ba ON b.ID = ba.BOOK"
      + " WHERE b.PUBLICATION_DATE > :date")
  List<Book> findByPublicationDateAfter(LocalDate date);
}
