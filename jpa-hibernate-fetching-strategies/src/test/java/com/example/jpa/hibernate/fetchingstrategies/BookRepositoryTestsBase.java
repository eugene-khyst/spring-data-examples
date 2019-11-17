/*
 * Copyright 2019 Evgeniy Khyst
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

package com.example.jpa.hibernate.fetchingstrategies;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jpa.hibernate.fetchingstrategies.entity.AbstractBook;
import com.example.jpa.hibernate.fetchingstrategies.entity.Author;
import com.example.jpa.hibernate.fetchingstrategies.entity.Category;
import com.example.jpa.hibernate.fetchingstrategies.repository.AuthorRepository;
import com.example.jpa.hibernate.fetchingstrategies.repository.CategoryRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Slf4j
public abstract class BookRepositoryTestsBase<T extends AbstractBook> {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private AuthorRepository authorRepository;

  private Category softwareDevelopment;
  private Category systemDesign;

  private Author martinFowler;
  private Author gregorHohpe;
  private Author bobbyWoolf;

  private Long poeaaId;
  private Long eipId;

  protected abstract T newBook();

  protected abstract JpaRepository<T, Long> getBookRepository();

  @BeforeEach
  void baseSetUp() {
    softwareDevelopment = new Category();
    softwareDevelopment.setName("Software development");
    categoryRepository.save(softwareDevelopment);

    systemDesign = new Category();
    systemDesign.setName("System design");
    categoryRepository.save(systemDesign);

    martinFowler = new Author();
    martinFowler.setFullName("Martin Fowler");
    authorRepository.save(martinFowler);

    gregorHohpe = new Author();
    gregorHohpe.setFullName("Gregor Hohpe");
    authorRepository.save(gregorHohpe);

    bobbyWoolf = new Author();
    bobbyWoolf.setFullName("Bobby Woolf");
    authorRepository.save(bobbyWoolf);

    T poeaa = newBook();
    poeaa.setIsbn("007-6092019909");
    poeaa.setTitle("Patterns of Enterprise Application Architecture");
    poeaa.setPublicationDate(LocalDate.parse("2002-11-15"));
    poeaa.getAuthors().addAll(List.of(martinFowler));
    poeaa.getCategories().addAll(List.of(softwareDevelopment, systemDesign));
    getBookRepository().save(poeaa);

    poeaaId = poeaa.getId();

    T eip = newBook();
    eip.setIsbn("978-0321200686");
    eip.setTitle("Enterprise Integration Patterns");
    eip.setPublicationDate(LocalDate.parse("2003-10-20"));
    eip.getAuthors().addAll(List.of(gregorHohpe, bobbyWoolf));
    eip.getCategories().addAll(List.of(softwareDevelopment, systemDesign));
    getBookRepository().save(eip);

    eipId = eip.getId();
  }

  @AfterEach
  void baseCleanUp() {
    categoryRepository.deleteAll();
    authorRepository.deleteAll();
    getBookRepository().deleteAll();
  }

  private void assertHasAuthors(T book, String... authors) {
    assertThat(book.getAuthors().stream().map(Author::getFullName))
        .containsExactlyInAnyOrder(authors);
  }

  @Test
  @Order(1)
  public void testFindById() {
    log.info(getBookRepository().getClass().getSimpleName() + "#getOne(long)");

    T poeaa = getBookRepository().getOne(poeaaId);
    assertThat(poeaa.getTitle()).isEqualTo("Patterns of Enterprise Application Architecture");
    assertHasAuthors(poeaa, "Martin Fowler");

    T eip = getBookRepository().getOne(eipId);
    assertThat(eip.getTitle()).isEqualTo("Enterprise Integration Patterns");
    assertHasAuthors(eip, "Gregor Hohpe", "Bobby Woolf");
  }

  /*@Test
  @Order(2)
  public void testJpql() {
    System.out.println("JPQL query");
    List<Book> books = em.createQuery("select b from Book b order by b.publicationDate")
        .getResultList();
    assertEquals(2, books.size());

    Book poeaa = books.get(0);
    assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
    //assertHasAuthors(poeaa, "Martin Fowler");

    Book eip = books.get(1);
    assertEquals("Enterprise Integration Patterns", eip.getTitle());
    //assertHasAuthors(eip, "Gregor Hohpe", "Bobby Woolf");
  }

  @Test
  @Order(3)
  public void testJpqlWithJoinFetch() {
    System.out.println("JPQL query with 'join fetch'");
    List<Book> books = em
        .createQuery("select b from Book b left join fetch b.authors order by b.publicationDate")
        .getResultList();
    assertEquals(3, books.size());

    Book poeaa = books.get(0);
    assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
    assertHasAuthors(poeaa, "Martin Fowler");

    Book eip = books.get(2);
    assertEquals("Enterprise Integration Patterns", eip.getTitle());
    assertHasAuthors(eip, "Gregor Hohpe", "Bobby Woolf");
  }

  @Test
  @Order(4)
  public void testJpqlWithDistinctAndJoinFetch() {
    System.out.println("JPQL query with 'distinct' and 'join fetch'");
    List<Book> books = em.createQuery(
        "select distinct b from Book b left join fetch b.authors order by b.publicationDate")
        .getResultList();
    assertEquals(2, books.size());

    Book poeaa = books.get(0);
    assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
    assertHasAuthors(poeaa, "Martin Fowler");

    Book eip = books.get(1);
    assertEquals("Enterprise Integration Patterns", eip.getTitle());
    assertHasAuthors(eip, "Gregor Hohpe", "Bobby Woolf");
  }

  @Test
  @Order(5)
  public void testCriteria() {
    System.out.println("Criteria query");

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Book> cq = cb.createQuery(Book.class);
    Root<Book> book = cq.from(Book.class);
    cq.orderBy(cb.asc(book.get(Book_.publicationDate)));
    TypedQuery<Book> q = em.createQuery(cq);
    List<Book> books = q.getResultList();

    assertEquals(2, books.size());

    Book poeaa = books.get(0);
    assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
    assertHasAuthors(poeaa, "Martin Fowler");

    Book eip = books.get(1);
    assertEquals("Enterprise Integration Patterns", eip.getTitle());
    assertHasAuthors(eip, "Gregor Hohpe", "Bobby Woolf");
  }

  @Test
  @Order(6)
  public void testCriteriaWithFetch() {
    System.out.println("Criteria query with 'fetch'");

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Book> cq = cb.createQuery(Book.class);
    Root<Book> book = cq.from(Book.class);
    book.fetch(Book_.authors, JoinType.LEFT);
    cq.orderBy(cb.asc(book.get(Book_.publicationDate)));
    TypedQuery<Book> q = em.createQuery(cq);
    List<Book> books = q.getResultList();

    assertEquals(3, books.size());

    Book poeaa = books.get(0);
    assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
    assertHasAuthors(poeaa, "Martin Fowler");

    Book eip = books.get(2);
    assertEquals("Enterprise Integration Patterns", eip.getTitle());
    assertHasAuthors(eip, "Gregor Hohpe", "Bobby Woolf");
  }

  @Test
  @Order(7)
  public void testCriteriaWithDistinctAndFetch() {
    System.out.println("Criteria query with 'distinct' and 'fetch'");

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Book> cq = cb.createQuery(Book.class);
    Root<Book> book = cq.from(Book.class);
    cq.distinct(true);
    book.fetch(Book_.authors, JoinType.LEFT);
    cq.orderBy(cb.asc(book.get(Book_.publicationDate)));
    TypedQuery<Book> q = em.createQuery(cq);
    List<Book> books = q.getResultList();

    assertEquals(2, books.size());

    Book poeaa = books.get(0);
    assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
    assertHasAuthors(poeaa, "Martin Fowler");

    Book eip = books.get(1);
    assertEquals("Enterprise Integration Patterns", eip.getTitle());
    assertHasAuthors(eip, "Gregor Hohpe", "Bobby Woolf");
  }

  @Test
  @Order(8)
  public void testJpqlWithEntityGraph() {
    System.out.println("JPQL query with entity graph");

    EntityGraph<Book> fetchAuthors = em.createEntityGraph(Book.class);
    fetchAuthors.addSubgraph(Book_.authors);
    List<Book> books = em.createQuery("select b from Book b order by b.publicationDate")
        .setHint("javax.persistence.fetchgraph", fetchAuthors)
        .getResultList();

    assertEquals(3, books.size());

    Book poeaa = books.get(0);
    assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
    assertHasAuthors(poeaa, "Martin Fowler");

    Book eip = books.get(2);
    assertEquals("Enterprise Integration Patterns", eip.getTitle());
    assertHasAuthors(eip, "Gregor Hohpe", "Bobby Woolf");
  }

  @Test()
  @Order(9)
  public void testJpqlWithJoinFetchMultipleBags() {
    System.out.println("JPQL query with 'join fetch' of multiple bags");
    try {
      List<Book> books = em.createQuery(
          "select b from Book b left join fetch b.authors left join fetch b.categories")
          .getResultList();
      fail();
    } catch (PersistenceException e) {
      assertEquals(
          "org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags",
          e.getMessage());
    }
  }

  @Test()
  @Order(10)
  public void testJpqlWithJoinFetchMaxResults() {
    System.out.println("JPQL query with 'join fetch' and max results");
    List<Book> books = em
        .createQuery("select b from Book b left join fetch b.authors order by b.publicationDate")
        .setFirstResult(0)
        .setMaxResults(1)
        .getResultList();
    assertEquals(1, books.size());

    Book poeaa = books.get(0);
    assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
    assertHasAuthors(poeaa, "Martin Fowler");
  }*/
}