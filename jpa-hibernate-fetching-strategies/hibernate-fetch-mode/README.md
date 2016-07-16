Examples of different Hibernate fetching strategies
===================================================

`HibernateBookRepositoryTest` runs with Spring and Hibernate 5.2.0.Final.

As testing framework JUnit 4.12 is used.

To build project and run all tests use Maven:

```
mvn clean install
```

Hibernate fetch modes tests:

* HQL query and default `FetchMode`
* Hibernate Criteria query and default `FetchMode`
* HQL and Hibernate Criteria queries and `FetchMode.SELECT`
* HQL and Hibernate Criteria queries and `FetchMode.SUBSELECT`
* HQL query and `FetchMode.JOIN`
* HQL query with 'join fetch'
* Hibernate Criteria query and `FetchMode.JOIN`
* HQL and Hibernate Criteria query and `FetchMode.SELECT` with `@BatchSize`
