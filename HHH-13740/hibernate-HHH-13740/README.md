# Problem with duplicates when fetching multiple many-to-many relations with Hibernate FetchMode.JOIN

* Java version - OpenJDK 11.0.6
* Hibernate version - 5.4.12.Final

When there are multiple many-to-many relations with FetchMode.JOIN, 
`session.find` returns a result with a child relation of type `List` containing duplicates.

To avoid `MultipleBagFetchException: cannot simultaneously fetch multiple bags` when multiple `@ManyToMany` collections have `@Fetch(FetchMode.JOIN)`, 
only one collection has type `List` and others have type `Set`.

```java
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @NaturalId
  @EqualsAndHashCode.Include
  private String isbn;

  private String title;

  private LocalDate publicationDate;

  @ManyToMany
  @Fetch(FetchMode.JOIN)
  private List<Author> authors = new ArrayList<>();

  @ManyToMany
  @Fetch(FetchMode.JOIN)
  private Set<Category> categories = new LinkedHashSet<>();
}
```

With the following test data

```java
softwareDevelopment = new Category("Software development");
session.save(softwareDevelopment);

systemDesign = new Category("System design");
session.save(systemDesign);

martinFowler = new Author("Martin Fowler");
session.save(martinFowler);

gregorHohpe = new Author("Gregor Hohpe");
session.save(gregorHohpe);

gregorHohpe = new Author();
gregorHohpe.setFullName("Gregor Hohpe");
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
```

a `Book` entity found by ID contains duplicates in `List<Author> authors`
because `Set<Category> categories` has size 2: `["Software development", "System design"]`.

```java
@Test
void findByIdOneAuthor() {
  try (Session session = HibernateUtil.getSessionFactory().openSession()) {
    Transaction transaction = session.beginTransaction();
        
    Book poeaa = session.find(Book.class, this.poeaa.getId());
    assertThat(poeaa.getTitle()).isEqualTo(this.poeaa.getTitle());
// The following line results in exception
// because the actual poeaa.authors contains duplicates: ["Martin Fowler", "Martin Fowler"]
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
// The following line results in exception
// because the actual eip.authors contains duplicates: ["Gregor Hohpe", "Gregor Hohpe", "Bobby Woolf", "Bobby Woolf"]
    assertThatHasAuthors(eip, gregorHohpe.getFullName(), bobbyWoolf.getFullName());
        
    transaction.commit();
  }
}
```

See the test [`com.example.hibernate.BookFetchModeJoinWithSetTests`](src/test/java/com/example/hibernate/BookFetchModeJoinWithSetTests.java).

There are no duplicates in `List<Author> authors` when `Set<Category> categories` has size 1.