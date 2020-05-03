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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@NamedEntityGraph(name = "Book.authors",
    attributeNodes = @NamedAttributeNode("authors")
)
@NamedEntityGraph(name = "Book.authors-categories",
    attributeNodes = {
        @NamedAttributeNode("authors"),
        @NamedAttributeNode("categories")
    }
)
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Book extends AbstractBook {

  @ManyToMany(fetch = EAGER)
  private List<Author> authors = new ArrayList<>();

  @ManyToMany
  private Set<Category> categories = new LinkedHashSet<>();
}
