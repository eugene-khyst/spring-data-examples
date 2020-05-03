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

package com.example.spring.data.jdbc.mapper;

import com.example.spring.data.jdbc.dto.BookDto;
import com.example.spring.data.jdbc.entity.Book;
import com.example.spring.data.jdbc.repository.AuthorRepository;
import com.example.spring.data.jdbc.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class BookMapperDecorator implements BookMapper {

  @Qualifier("delegate")
  @Autowired
  private BookMapper delegate;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  public BookDto toBookDto(Book book) {
    BookDto dto = delegate.toBookDto(book);
    dto.setAuthors(toAuthorDtos(authorRepository.findByBook(book.getId())));
    dto.setCategories(toCategoryDtos(categoryRepository.findByBook(book.getId())));
    return dto;
  }
}
