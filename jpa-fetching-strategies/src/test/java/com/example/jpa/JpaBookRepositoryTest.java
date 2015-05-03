/*
 * Copyright 2015 Evgeniy Khyst.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jpa;

import java.text.SimpleDateFormat;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Evgeniy Khyst
 */
@RunWith(Arquillian.class)
public class JpaBookRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaBookRepositoryTest.class);

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "JpaBookRepositoryTest.war")
                .addPackages(true, "com.example.jpa")
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private UserTransaction ut;

    @PersistenceContext
    private EntityManager em;

    private Long poeaaId;
    private Long eipId;

    @Before
    public void setUp() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

        ut.begin();
        try {
            Category softwareDevelopment = new Category();
            softwareDevelopment.setName("Software development");
            em.persist(softwareDevelopment);

            Category systemDesign = new Category();
            systemDesign.setName("System design");
            em.persist(systemDesign);

            Author martinFowler = new Author();
            martinFowler.setFullName("Martin Fowler");
            em.persist(martinFowler);

            Book poeaa = new Book();
            poeaa.setIsbn("007-6092019909");
            poeaa.setTitle("Patterns of Enterprise Application Architecture");
            poeaa.setPublicationDate(df.parse("2002/11/15"));
            poeaa.setAuthors(asList(martinFowler));
            poeaa.setCategories(asList(softwareDevelopment, systemDesign));
            em.persist(poeaa);

            poeaaId = poeaa.getId();

            Author gregorHohpe = new Author();
            gregorHohpe.setFullName("Gregor Hohpe");
            em.persist(gregorHohpe);

            Author bobbyWoolf = new Author();
            bobbyWoolf.setFullName("Bobby Woolf");
            em.persist(bobbyWoolf);

            Book eip = new Book();
            eip.setIsbn("978-0321200686");
            eip.setTitle("Enterprise Integration Patterns");
            eip.setPublicationDate(df.parse("2003/10/20"));
            eip.setAuthors(asList(gregorHohpe, bobbyWoolf));
            eip.setCategories(asList(softwareDevelopment, systemDesign));
            em.persist(eip);

            eipId = eip.getId();

            ut.commit();
        } catch (Exception e) {
            ut.rollback();
        }

        ut.begin();
        LOGGER.info("##################################################");
    }

    @After
    public void tearDown() throws Exception {
        LOGGER.info("##################################################");
        ut.rollback();

        ut.begin();
        em.createQuery("delete from Book").executeUpdate();
        em.createQuery("delete from Author").executeUpdate();
        em.createQuery("delete from Category").executeUpdate();
        ut.commit();
    }

    private void assertHasAuthors(Book book, String... authors) {
        assertEquals(authors.length, book.getAuthors().size());

        for (String author : authors) {
            assertTrue(hasAuthor(book, author));
        }
    }

    private boolean hasAuthor(Book book, String fullName) {
        for (Author author : book.getAuthors()) {
            if (fullName.equals(author.getFullName())) {
                return true;
            }
        }
        return false;
    }

    @Test
    @InSequence(1)
    public void testFindById() {
        LOGGER.info("EntityManager#find");

        Book poeaa = em.find(Book.class, poeaaId);
        assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
        assertHasAuthors(poeaa, "Martin Fowler");

        Book eip = em.find(Book.class, eipId);
        assertEquals("Enterprise Integration Patterns", eip.getTitle());
        assertHasAuthors(eip, "Gregor Hohpe", "Bobby Woolf");
    }

    @Test
    @InSequence(2)
    public void testJpql() {
        LOGGER.info("JPQL query");
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
    @InSequence(3)
    public void testJpqlWithJoinFetch() {
        LOGGER.info("JPQL query with 'join fetch'");
        List<Book> books = em.createQuery("select b from Book b left join fetch b.authors order by b.publicationDate")
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
    @InSequence(4)
    public void testJpqlWithDistinctAndJoinFetch() {
        LOGGER.info("JPQL query with 'distinct' and 'join fetch'");
        List<Book> books = em.createQuery("select distinct b from Book b left join fetch b.authors order by b.publicationDate")
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
    @InSequence(5)
    public void testCriteria() {
        LOGGER.info("Criteria query");

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
    @InSequence(6)
    public void testCriteriaWithFetch() {
        LOGGER.info("Criteria query with 'fetch'");

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
    @InSequence(7)
    public void testCriteriaWithDistinctAndFetch() {
        LOGGER.info("Criteria query with 'distinct' and 'fetch'");

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
    @InSequence(8)
    public void testJpqlWithEntityGraph() {
        LOGGER.info("JPQL query with entity graph");

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
    @InSequence(9)
    public void testJpqlWithJoinFetchMultipleBags() {
        LOGGER.info("JPQL query with 'join fetch' of multiple bags");
        try {
            List<Book> books = em.createQuery("select b from Book b left join fetch b.authors left join fetch b.categories")
                    .getResultList();
            fail();
        } catch (PersistenceException e) {
            assertEquals("org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags", e.getMessage());
        }
    }

    @Test()
    @InSequence(10)
    public void testJpqlWithJoinFetchMaxResults() {
        LOGGER.info("JPQL query with 'join fetch' and max results");
        List<Book> books = em.createQuery("select b from Book b left join fetch b.authors order by b.publicationDate")
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        assertEquals(1, books.size());

        Book poeaa = books.get(0);
        assertEquals("Patterns of Enterprise Application Architecture", poeaa.getTitle());
        assertHasAuthors(poeaa, "Martin Fowler");
    }
}
