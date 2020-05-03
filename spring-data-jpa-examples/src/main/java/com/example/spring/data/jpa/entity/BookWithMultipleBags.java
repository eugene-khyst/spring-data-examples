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

import static javax.persistence.FetchType.EAGER;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NaturalId;

@Entity
@NamedEntityGraph(name = "BookWithMultipleBags.authors-categories",
    attributeNodes = {
        @NamedAttributeNode("authors"),
        @NamedAttributeNode("categories")
    }
)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookWithMultipleBags {

  @Id
  @GeneratedValue
  private Long id;

  @NaturalId
  @EqualsAndHashCode.Include
  private String isbn;

  private String title;

  private LocalDate publicationDate;

  @ManyToMany(fetch = EAGER)
  private List<Author> authors = new ArrayList<>();

  @ManyToMany
  private List<Category> categories = new ArrayList<>();
  //Instead of Set<Category> like in AbstractBook and its children
  //Multiple many-to-many relations of type List with 'FetchMode.JOIN' leads to
  //MultipleBagFetchException: cannot simultaneously fetch multiple bags
}
