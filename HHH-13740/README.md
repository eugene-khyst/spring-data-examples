# [HHH-13740](https://hibernate.atlassian.net/browse/HHH-13740) - Problem with duplicates when fetching multiple many-to-many relations with Hibernate FetchMode.JOIN

When entity has multiple many-to-many relations with `FetchMode.JOIN` (one of type `List` and others of type `Set`), searching for such entity Hibernate returns a result with a child relation of type `List` containing duplicates.

The bug can be reproduced on Hibernate 5.4.12.Final and OpenJDK 11.0.6 using both plain Hibernate API and Spring Data JPA 2.2.6.RELEASE:

* [HHH-13740 plain Hibernate sample](hibernate-fetch-mode-join/)
* [HHH-13740 Spring Data JPA sample](spring-data-jpa-fetch-mode-join/)

The detailed description of the problem is available in the samples.