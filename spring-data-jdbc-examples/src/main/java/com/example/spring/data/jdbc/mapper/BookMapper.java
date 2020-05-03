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

import com.example.spring.data.jdbc.dto.AuthorDto;
import com.example.spring.data.jdbc.dto.BookDto;
import com.example.spring.data.jdbc.dto.CategoryDto;
import com.example.spring.data.jdbc.entity.Author;
import com.example.spring.data.jdbc.entity.Book;
import com.example.spring.data.jdbc.entity.Category;
import java.util.List;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(BookMapperDecorator.class)
public interface BookMapper {

  @Mapping(target = "authors", ignore = true)
  @Mapping(target = "categories", ignore = true)
  BookDto toBookDto(Book book);

  @Mapping(source = "fullName", target = "name")
  AuthorDto toAuthorDtos(Author author);

  @Mapping(source = "name", target = "label")
  CategoryDto toCategoryDto(Category category);

  List<AuthorDto> toAuthorDtos(List<Author> author);

  List<CategoryDto> toCategoryDtos(List<Category> category);
}