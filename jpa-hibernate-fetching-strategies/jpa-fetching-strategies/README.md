Examples of different JPA fetching strategies
=============================================

`JpaBookRepositoryTest` runs with Arquillian on WildFly 8.2.1.Final with Hibernate 4.3.7.Final as JPA 2.1 provider.

As testing framework JUnit 4.12 is used.

To build project and run all tests use Maven:

```
mvn clean install
```

JPA fetchig strategies tests:

* Find by primary key
* JPQL and JPA Criteria queries
* JPQL and JPA Criteria queries with 'join fetch'
* JPQL and JPA Criteria queries with 'distinct' and 'join fetch'
* JPQL query with entity graph
* JPQL query with 'join fetch' of multiple collections
* JPQL query with 'join fetch' and 'max results'
