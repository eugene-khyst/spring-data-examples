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

package com.example.spring.data.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NaturalId;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractBook implements Serializable {

  @Id @GeneratedValue private Long id;

  @NaturalId @EqualsAndHashCode.Include private String isbn;

  private String title;

  private LocalDate publicationDate;

  public abstract List<Author> getAuthors();

  public abstract void setAuthors(List<Author> authors);

  public abstract Set<Category> getCategories();

  public abstract void setCategories(Set<Category> categories);
}
