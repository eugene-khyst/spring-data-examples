package com.example.spring.data.jpa;

import com.example.spring.data.jpa.entity.AbstractBook;
import com.example.spring.data.jpa.entity.Author;
import com.example.spring.data.jpa.entity.Category;
import com.example.spring.data.jpa.repository.AbstractBookRepository;
import com.example.spring.data.jpa.repository.AuthorRepository;
import com.example.spring.data.jpa.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(SpringExtension.class)
@TestInstance(PER_CLASS)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = NONE)
@Transactional
@Rollback(false)
@Import(DatasourceProxyBeanPostProcessor.class)
@Slf4j
abstract class AbstractBookRepositoryTest<T extends AbstractBook> extends BaseIntegrationTest {

  @Autowired CategoryRepository categoryRepository;

  @Autowired AuthorRepository authorRepository;

  Category softwareDevelopment;
  Category systemDesign;

  Author erichGamma;
  Author richardHelm;
  Author ralphJohnson;
  Author johnVlissides;
  Author martinFowler;
  Author gregorHohpe;
  Author bobbyWoolf;

  T gof;
  T poeaa;
  T eip;

  abstract T createBook();

  abstract AbstractBookRepository<T> getBookRepository();

  @BeforeAll
  void setUp() {
    saveCategories();
    saveAuthors();
    saveBooks();
  }

  void saveCategories() {
    softwareDevelopment = new Category("Software development");
    systemDesign = new Category("System design");

    categoryRepository.saveAll(List.of(systemDesign, softwareDevelopment));
  }

  void saveAuthors() {
    erichGamma = new Author("Erich Gamma");
    richardHelm = new Author("Richard Helm");
    ralphJohnson = new Author("Ralph Johnson");
    johnVlissides = new Author("John Vlissides");

    martinFowler = new Author("Martin Fowler");

    gregorHohpe = new Author("Gregor Hohpe");
    bobbyWoolf = new Author("Bobby Woolf");

    authorRepository.saveAll(
        List.of(
            erichGamma,
            richardHelm,
            ralphJohnson,
            johnVlissides,
            martinFowler,
            gregorHohpe,
            bobbyWoolf));
  }

  void saveBooks() {
    gof = createBook();
    gof.setIsbn("978-0201633610");
    gof.setTitle("Design Patterns: Elements of Reusable Object-Oriented Software");
    gof.setPublicationDate(LocalDate.parse("1994-11-10"));
    gof.getAuthors().addAll(List.of(erichGamma, richardHelm, ralphJohnson, johnVlissides));
    gof.getCategories().add(softwareDevelopment);

    poeaa = createBook();
    poeaa.setIsbn("007-6092019909");
    poeaa.setTitle("Patterns of Enterprise Application Architecture");
    poeaa.setPublicationDate(LocalDate.parse("2002-11-15"));
    poeaa.getAuthors().add(martinFowler);
    poeaa.getCategories().addAll(List.of(softwareDevelopment, systemDesign));

    eip = createBook();
    eip.setIsbn("978-0321200686");
    eip.setTitle("Enterprise Integration Patterns");
    eip.setPublicationDate(LocalDate.parse("2003-10-20"));
    eip.getAuthors().addAll(List.of(gregorHohpe, bobbyWoolf));
    eip.getCategories().addAll(List.of(softwareDevelopment, systemDesign));

    getBookRepository().saveAll(List.of(gof, poeaa, eip));
  }

  @AfterAll
  void cleanUp() {
    getBookRepository().deleteAll();
    authorRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  void findById() {
    log.info("CrudRepository.findById");

    Optional<T> poeaa = getBookRepository().findById(this.poeaa.getId());
    log.info("A book was loaded");
    assertThat(poeaa).hasValueSatisfying(equalTo(this.poeaa));
  }

  @Test
  void queryMethod() {
    log.info("Query method");

    List<T> books =
        getBookRepository()
            .findByTitleContains("Pattern", PageRequest.of(0, 2, DESC, "publicationDate"));
    log.info("Books were loaded");
    assertThat(books)
        .hasSize(2)
        .satisfies(equalTo(this.eip), atIndex(0))
        .satisfies(equalTo(this.poeaa), atIndex(1));
  }

  Consumer<T> equalTo(T expected) {
    return actual -> {
      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertThat(actual.getIsbn()).isEqualTo(expected.getIsbn());
      assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
      assertThat(actual.getPublicationDate()).isEqualTo(expected.getPublicationDate());
      assertThat(actual.getAuthors()).containsExactlyInAnyOrderElementsOf(expected.getAuthors());
      assertThat(actual.getCategories())
          .containsExactlyInAnyOrderElementsOf(expected.getCategories());
    };
  }
}
