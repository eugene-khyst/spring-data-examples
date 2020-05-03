# Spring Data JDBC Examples

The example evaluates Spring Data JDBC as an alternative to Spring Data JPA.
The following topics are covered:
* one-to-one, one-to-many, many-to-many relationships
* optimistic locking
* entity to DTO mapping

## Prerequisites

* JDK 11
* Docker at least 1.6.0

## How to run tests

To build project and run all tests use command

```bash
./gradlew cleanTest test -i
```

## Implementation details

* JDK 11
* Spring Boot 2.2.x
* Spring Data Release Train Neumann-RC1
* Spring Data JDBC 2.0.0.RC1
* [MapStruct](https://mapstruct.org/) 1.3.1.Final
* JUnit 5
* [Testcontainers](https://www.testcontainers.org/)

This example has a simple domain model. 
A book has at least one author and belongs to at least one category.
A book can be rated. An average rating and a total number of ratings are tracked. 

Spring Data JDBC is inspired by Aggregate Roots and Repositories as described in the book Domain Driven Design by Eric Evans.
Aggregate Root is an entity that controls the lifecycle of related entities forming together an Aggregate.
Each Aggregate has only one Aggregate Root. 
You should have a Repository per Aggregate Root.
Related entities doesn't exist alone without an Aggregate Root.
When an Aggregate Root is deleted, all related entities get deleted too.

If two entities have different life-cycles these are separate Aggregate Roots.
Thus, in contrast to JPA, one-to-many and many-to-many relationships must be modeled by referencing the ID 
and join tables for many-to-many relationships must be mapped to a Java class and added to an Aggregate.

In the example there are 3 Aggregates:
1. `Book` (Aggregate Root), `BookAuthor`, `BookCategory`
2. `Author` (Aggregate Root)
3. `Category` (Aggregate Root)

**UML class diagram**

![Actual UML class diagram](img/classes.png)

**Entity-relationship diagram**

![Entity-relationship diagram](img/tables.png)

## Test cases

* Queries - `com.example.spring.data.jdbc.BookRepositoryTest`
  * `CrudRepository.save` - during saving all entities referenced from an aggregate root are deleted and recreated
  * `CrudRepository.findById`
  * `PagingAndSortingRepository.findAll(Pageable)`
  * `@Query` with SQL
  * `@Query` with SQL join
  * `@Query` with SQL and pagination
  * Mapping from entity to DTO using MapStruct
  * Reactive `Mono.fromCallable` wrapping synchronous call
* Locking strategies - `com.example.spring.data.jdbc.BookRatingRepositoryTest`
  * Implicit optimistic lock of entity with `@Version` on modification
