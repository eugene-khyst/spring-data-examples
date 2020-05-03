package com.example.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.hibernate.entity.Author;
import com.example.hibernate.entity.Book;
import com.example.hibernate.entity.Category;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookFetchModeJoinWithSetTests {

  private Category softwareDevelopment;
  private Category systemDesign;

  private Author martinFowler;
  private Author gregorHohpe;
  private Author bobbyWoolf;

  private Book poeaa;
  private Book eip;

  @BeforeEach
  void baseSetUp() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction transaction = session.beginTransaction();

      softwareDevelopment = new Category("Software development");
      session.save(softwareDevelopment);

      systemDesign = new Category("System design");
      session.save(systemDesign);

      martinFowler = new Author("Martin Fowler");
      session.save(martinFowler);

      gregorHohpe = new Author("Gregor Hohpe");
      session.save(gregorHohpe);

      bobbyWoolf = new Author();
      bobbyWoolf.setFullName("Bobby Woolf");
      session.save(bobbyWoolf);

      poeaa = new Book();
      poeaa.setIsbn("007-6092019909");
      poeaa.setTitle("Patterns of Enterprise Application Architecture");
      poeaa.setPublicationDate(LocalDate.parse("2002-11-15"));
      poeaa.getAuthors().addAll(List.of(martinFowler));
      poeaa.getCategories().addAll(List.of(softwareDevelopment, systemDesign));
      session.save(poeaa);

      eip = new Book();
      eip.setIsbn("978-0321200686");
      eip.setTitle("Enterprise Integration Patterns");
      eip.setPublicationDate(LocalDate.parse("2003-10-20"));
      eip.getAuthors().addAll(List.of(gregorHohpe, bobbyWoolf));
      eip.getCategories().addAll(List.of(softwareDevelopment, systemDesign));
      session.save(eip);

      transaction.commit();
    }
  }

  @AfterEach
  void cleanUp() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction transaction = session.beginTransaction();
      session.createQuery("delete from Book").executeUpdate();
      session.createQuery("delete from Author").executeUpdate();
      session.createQuery("delete from Category").executeUpdate();
      transaction.commit();
    }
  }

  @Test
  void findByIdOneAuthor() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction transaction = session.beginTransaction();

      Book poeaa = session.find(Book.class, this.poeaa.getId());
      assertThat(poeaa.getTitle()).isEqualTo(this.poeaa.getTitle());
//    The following line results in exception
//    because the actual poeaa.authors contains duplicates: ["Martin Fowler", "Martin Fowler"]
      assertThatHasAuthors(poeaa, martinFowler.getFullName());

      transaction.commit();
    }
  }

  @Test
  void findByIdTwoAuthors() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction transaction = session.beginTransaction();

      Book eip = session.find(Book.class, this.eip.getId());
      assertThat(eip.getTitle()).isEqualTo(this.eip.getTitle());
//    The following line results in exception
//    because the actual eip.authors contains duplicates: ["Gregor Hohpe", "Gregor Hohpe", "Bobby Woolf", "Bobby Woolf"]
      assertThatHasAuthors(eip, gregorHohpe.getFullName(), bobbyWoolf.getFullName());

      transaction.commit();
    }
  }

  private void assertThatHasAuthors(Book book, String... authors) {
    assertThat(book.getAuthors().stream().map(Author::getFullName))
        .containsExactlyInAnyOrder(authors);
  }
}
