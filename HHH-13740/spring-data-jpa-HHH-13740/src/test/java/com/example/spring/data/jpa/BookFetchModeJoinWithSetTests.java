package com.example.spring.data.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.data.jpa.entity.Author;
import com.example.spring.data.jpa.entity.Book;
import com.example.spring.data.jpa.entity.Category;
import com.example.spring.data.jpa.repository.AuthorRepository;
import com.example.spring.data.jpa.repository.BookRepository;
import com.example.spring.data.jpa.repository.CategoryRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class BookFetchModeJoinWithSetTests {

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private AuthorRepository authorRepository;

  private Category softwareDevelopment;
  private Category systemDesign;

  private Author martinFowler;
  private Author gregorHohpe;
  private Author bobbyWoolf;

  private Book poeaa;
  private Book eip;

  @BeforeAll
  void baseSetUp() {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        softwareDevelopment = new Category("Software development");
        categoryRepository.save(softwareDevelopment);

        systemDesign = new Category("System design");
        categoryRepository.save(systemDesign);

        martinFowler = new Author("Martin Fowler");
        authorRepository.save(martinFowler);

        gregorHohpe = new Author("Gregor Hohpe");
        authorRepository.save(gregorHohpe);

        bobbyWoolf = new Author("Bobby Woolf");
        authorRepository.save(bobbyWoolf);

        poeaa = new Book();
        poeaa.setIsbn("007-6092019909");
        poeaa.setTitle("Patterns of Enterprise Application Architecture");
        poeaa.setPublicationDate(LocalDate.parse("2002-11-15"));
        poeaa.getAuthors().addAll(List.of(martinFowler));
        poeaa.getCategories().addAll(List.of(softwareDevelopment, systemDesign));
        bookRepository.save(poeaa);

        eip = new Book();
        eip.setIsbn("978-0321200686");
        eip.setTitle("Enterprise Integration Patterns");
        eip.setPublicationDate(LocalDate.parse("2003-10-20"));
        eip.getAuthors().addAll(List.of(gregorHohpe, bobbyWoolf));
        eip.getCategories().addAll(List.of(softwareDevelopment, systemDesign));
        bookRepository.save(eip);
      }
    });
  }

  @AfterAll
  void baseCleanUp() {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
      }
    });
  }

  @Transactional(readOnly = true)
  @Test
  void findByIdOneAuthor() {
    Book poeaa = bookRepository.getOne(this.poeaa.getId());
    assertThat(poeaa.getTitle()).isEqualTo(this.poeaa.getTitle());
//    The following line results in exception
//    because the actual poeaa.authors contains duplicates: ["Martin Fowler", "Martin Fowler"]
    assertThatHasAuthors(poeaa, martinFowler.getFullName());
  }

  @Transactional(readOnly = true)
  @Test
  void findByIdTwoAuthors() {
    Book eip = bookRepository.getOne(this.eip.getId());
    assertThat(eip.getTitle()).isEqualTo(this.eip.getTitle());
//    The following line results in exception
//    because the actual eip.authors contains duplicates: ["Gregor Hohpe", "Gregor Hohpe", "Bobby Woolf", "Bobby Woolf"]
    assertThatHasAuthors(eip, gregorHohpe.getFullName(), bobbyWoolf.getFullName());
  }

  private void assertThatHasAuthors(Book book, String... authors) {
    assertThat(book.getAuthors().stream().map(Author::getFullName))
        .containsExactlyInAnyOrder(authors);
  }
}
