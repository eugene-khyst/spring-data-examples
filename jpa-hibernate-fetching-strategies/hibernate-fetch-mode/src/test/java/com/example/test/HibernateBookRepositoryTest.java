/*
 * Copyright 2016 Evgeniy Khyst.
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
package com.example.test;

import com.example.entities.AbstractBook;
import com.example.entities.Author;
import com.example.entities.Book;
import com.example.entities.BookBatchSize;
import com.example.entities.BookFetchModeJoin;
import com.example.entities.BookFetchModeSelect;
import com.example.entities.BookFetchModeSubselect;
import com.example.entities.Category;
import java.time.Instant;
import static java.util.Arrays.asList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.loader.MultipleBagFetchException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author Evgeniy Khyst
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Transactional
public class HibernateBookRepositoryTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @Before
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @After
    public void tearDown() {
        System.out.println("##################################################");

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus ts) {

                Session session = getCurrentSession();
                session.createQuery("delete from Book").executeUpdate();
                session.createQuery("delete from BookFetchModeSelect").executeUpdate();
                session.createQuery("delete from BookFetchModeSubselect").executeUpdate();
                session.createQuery("delete from BookFetchModeJoin").executeUpdate();
                session.createQuery("delete from BookBatchSize").executeUpdate();
                session.createQuery("delete from Author").executeUpdate();
                session.createQuery("delete from Category").executeUpdate();
            }
        });
    }

    private void persistBooks(Supplier<AbstractBook> bookSupplier) throws Exception {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus ts) {
                Session session = getCurrentSession();

                Category softwareDevelopment = new Category();
                softwareDevelopment.setName("Software development");
                session.persist(softwareDevelopment);

                Category systemDesign = new Category();
                systemDesign.setName("System design");
                session.persist(systemDesign);

                Author martinFowler = new Author();
                martinFowler.setFullName("Martin Fowler");
                session.persist(martinFowler);

                AbstractBook poeaa = bookSupplier.get();
                poeaa.setIsbn("007-6092019909");
                poeaa.setTitle("Patterns of Enterprise Application Architecture");
                poeaa.setPublicationDate(Date.from(Instant.parse("2002-11-15T00:00:00.00Z")));
                poeaa.setAuthors(asList(martinFowler));
                poeaa.setCategories(asList(softwareDevelopment, systemDesign));
                session.persist(poeaa);

                Author gregorHohpe = new Author();
                gregorHohpe.setFullName("Gregor Hohpe");
                session.persist(gregorHohpe);

                Author bobbyWoolf = new Author();
                bobbyWoolf.setFullName("Bobby Woolf");
                session.persist(bobbyWoolf);

                AbstractBook eip = bookSupplier.get();
                eip.setIsbn("978-0321200686");
                eip.setTitle("Enterprise Integration Patterns");
                eip.setPublicationDate(Date.from(Instant.parse("2003-10-20T00:00:00.00Z")));
                eip.setAuthors(asList(gregorHohpe, bobbyWoolf));
                eip.setCategories(asList(softwareDevelopment, systemDesign));
                session.persist(eip);

                Category objectOrientedSoftwareDesign = new Category();
                objectOrientedSoftwareDesign.setName("Object-Oriented Software Design");
                session.persist(objectOrientedSoftwareDesign);

                Author ericEvans = new Author();
                ericEvans.setFullName("Eric Evans");
                session.persist(ericEvans);

                AbstractBook ddd = bookSupplier.get();
                ddd.setIsbn("860-1404361814");
                ddd.setTitle("Domain-Driven Design: Tackling Complexity in the Heart of Software");
                ddd.setPublicationDate(Date.from(Instant.parse("2003-08-01T00:00:00.00Z")));
                ddd.setAuthors(asList(ericEvans));
                ddd.setCategories(asList(softwareDevelopment, systemDesign, objectOrientedSoftwareDesign));
                session.persist(ddd);

                Category networkingCloudComputing = new Category();
                networkingCloudComputing.setName("Networking & Cloud Computing");
                session.persist(networkingCloudComputing);

                Category databasesBigData = new Category();
                databasesBigData.setName("Databases & Big Data");
                session.persist(databasesBigData);

                Author pramodSadalage = new Author();
                pramodSadalage.setFullName("Pramod J. Sadalage");
                session.persist(pramodSadalage);

                AbstractBook nosql = bookSupplier.get();
                nosql.setIsbn("978-0321826626");
                nosql.setTitle("NoSQL Distilled: A Brief Guide to the Emerging World of Polyglot Persistence");
                nosql.setPublicationDate(Date.from(Instant.parse("2012-08-18T00:00:00.00Z")));
                nosql.setAuthors(asList(pramodSadalage, martinFowler));
                nosql.setCategories(asList(networkingCloudComputing, databasesBigData));
                session.persist(nosql);
            }
        });
        System.out.println("##################################################");
    }

    @Test
    public void defaultFetchModeHql() throws Exception {
        persistBooks(() -> new Book());
        System.out.println("Default FetchMode HQL");
        List books = getCurrentSession().createQuery("select b from Book b").list();
        assertEquals(4, books.size());
    }

    @Test
    public void defaultFetchModeCriteria() throws Exception {
        persistBooks(() -> new Book());
        System.out.println("Default FetchMode Criteria API");
        List books = getCurrentSession().createCriteria(Book.class).list();
        assertEquals(6, books.size()); // EIP and NoSQL books have 2 authors so present twice in the result set due to 'outer join'
    }

    @Test
    public void fetchModeSelectHql() throws Exception {
        persistBooks(() -> new BookFetchModeSelect());
        System.out.println("FetchMode.SELECT HQL");
        List books = getCurrentSession().createQuery("select b from BookFetchModeSelect b").list();
        assertEquals(4, books.size());
    }

    @Test
    public void fetchModeSelectCriteria() throws Exception {
        persistBooks(() -> new BookFetchModeSelect());
        System.out.println("FetchMode.SELECT Criteria API");
        List books = getCurrentSession().createCriteria(BookFetchModeSelect.class).list();
        assertEquals(4, books.size());
    }

    @Test
    public void fetchModeSubselectHql() throws Exception {
        persistBooks(() -> new BookFetchModeSubselect());
        System.out.println("FetchMode.SUBSELECT HQL");
        List books = getCurrentSession().createQuery("select b from BookFetchModeSubselect b").list();
        assertEquals(4, books.size());
    }

    @Test
    public void fetchModeSubselectCriteria() throws Exception {
        persistBooks(() -> new BookFetchModeSubselect());
        System.out.println("FetchMode.SUBSELECT Criteria API");
        List books = getCurrentSession().createCriteria(BookFetchModeSubselect.class).list();
        assertEquals(4, books.size());
    }

    @Test
    public void fetchModeJoinHql() throws Exception {
        persistBooks(() -> new BookFetchModeJoin());
        System.out.println("FetchMode.JOIN HQL");
        List books = getCurrentSession().createQuery("select b from BookFetchModeJoin b").list();
        assertEquals(4, books.size());
    }

    @Test
    public void fetchModeJoinHqlJoinFetch() throws Exception {
        persistBooks(() -> new BookFetchModeJoin());
        System.out.println("FetchMode.JOIN HQL with 'join fetch'");
        List books = getCurrentSession().createQuery("select b from BookFetchModeJoin b join fetch b.authors a").list();
        assertEquals(6, books.size());
    }

    @Test(expected = MultipleBagFetchException.class)
    public void fetchModeJoinCriteria() throws Exception {
        persistBooks(() -> new BookFetchModeJoin());
        System.out.println("FetchMode.JOIN Criteria API");
        List books = getCurrentSession().createCriteria(BookFetchModeJoin.class).list();
    }

    @Test
    public void batchHql() throws Exception {
        persistBooks(() -> new BookBatchSize());
        System.out.println("FetchMode.SELECT BatchSize HQL");
        List books = getCurrentSession().createQuery("select b from BookBatchSize b").list();
        assertEquals(4, books.size());
    }

    @Test
    public void batchCriteria() throws Exception {
        persistBooks(() -> new BookBatchSize());
        System.out.println("FetchMode.SELECT BatchSize Criteria API");
        List books = getCurrentSession().createCriteria(BookBatchSize.class).list();
        assertEquals(4, books.size());
    }

    private Session getCurrentSession() throws HibernateException {
        return sessionFactory.getCurrentSession();
    }
}
