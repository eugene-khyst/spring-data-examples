Examples of different JPA and Hibernate fetching strategies
===========================================================

Project consists of multiple Maven modules. Modules containing test are:

* jpa-fetching-strategies with `JpaBookRepositoryTest` running with Arquillian on WildFly 8.2.1.Final with Hibernate 4.3.7.Final as JPA 2.1 provider,
* hibernate-fetch-mode with `HibernateBookRepositoryTest` running with Spring with Hibernate 5.2.0.Final.

As testing framework JUnit 4.12 is used.

To build project and run all tests use Maven:

```
mvn clean install
```

JPA fetching strategies tests:

* Find by primary key
* JPQL and JPA Criteria queries
* JPQL and JPA Criteria queries with 'join fetch'
* JPQL and JPA Criteria queries with 'distinct' and 'join fetch'
* JPQL query with entity graph
* JPQL query with 'join fetch' of multiple collections
* JPQL query with 'join fetch' and 'max results'

Hibernate fetch modes tests:

* HQL query and default `FetchMode`
* Hibernate Criteria query and default `FetchMode`
* HQL and Hibernate Criteria queries and `FetchMode.SELECT`
* HQL and Hibernate Criteria queries and `FetchMode.SUBSELECT`
* HQL query and `FetchMode.JOIN`
* HQL query with 'join fetch'
* Hibernate Criteria query and `FetchMode.JOIN`
* HQL and Hibernate Criteria query and `FetchMode.SELECT` with `@BatchSize`
