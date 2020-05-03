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

package com.example.spring.data.r2dbc.mapper;

import com.example.spring.data.r2dbc.dto.BookDto;
import com.example.spring.data.r2dbc.entity.Book;
import com.example.spring.data.r2dbc.repository.AuthorRepository;
import com.example.spring.data.r2dbc.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Transactional
@Service
public class BookMappingService {

  @Autowired
  private BookMapper bookMapper;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  public Mono<BookDto> toBookDto(Book book) {
    BookDto bookDto = bookMapper.toBookDto(book);
    return Mono.zip(
        authorRepository.findByBook(book.getId()).collectList().map(bookMapper::toAuthorDtos),
        categoryRepository.findByBook(book.getId()).collectList().map(bookMapper::toCategoryDtos),
        (authorDtos, categorieDtos) -> {
          bookDto.setAuthors(authorDtos);
          bookDto.setCategories(categorieDtos);
          return bookDto;
        });
  }
}
