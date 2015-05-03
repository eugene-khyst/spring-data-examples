JPA Fetching Strategies
=======================

Understanding of fetching strategies in JPA is crucial for performance of your application.

There are 2 fetch types: _EAGER_ and _LAZY_.
The _EAGER_ fetching makes persistent provider fetch data eagerly.
The _LAZY_ fetching means that persistence provider should fetch data lazily when it is first accessed.

By default JPA provider lazily loads all associated collections (one-to-many or many-to-many relations). In the most cases lazy behavior is good. There is no sense to initialize all associated collections if they will not be accessed.

JPA provides 2 primary fetching strategies: _SELECT_ and _JOIN_.

The _SELECT_ fetching strategy will make persistence provider fetch associated collections with separate SQL query. Sometimes this strategy can negatively influence performance, especially when there are large number of entities in the result list. This is often called "N+1 selects problem".

The first query will selected root entities only, and each associated collection will be selected with additional query. So persistence provider generates N+1 SQL queries, where N is a number of root entities in result list of user query.

The _JOIN_ strategy makes JPA implementation fetch associated collection in the same SQL query root entity is selected using `LEFT JOIN</code> operator in generated SQL. Often this strategy is better from performance point of view, especially when there are large number of root entities in result list. There are several ways to force persistence provider to use _JOIN_ fetching strategy: `JOIN FETCH</code> JPQL operator, `fetch</code> method of `Root</code> class (Criteria API), entity graphs introduced in JPA 2.1.

There are some pitfalls of _JOIN_ fetching strategy.

JPQL and Criteria API queries with _JOIN_ fetching strategy will return cartesian product. It means that if root entity has associated collection with 3 entities, the result list will have size 3 (cartesian product). Operator `DISTINCT</code> can be used to avoid this. It will remove all duplicate rows from result list. But, if result list may contain duplicates they will be removed from result list anyway.

Only one collection that is fetched using _JOIN_ strategy can be of type List, other collection must be of type Set. Otherwise an exception will be thrown:

`HibernateException: cannot simultaneously fetch multiple bags`

When JOIN fetching strategy is used, `setMaxResults</code> and `setFirstResult</code> will not add appropriate conditions to SQL query. Result list will contain all rows without limiting and offsetting according to firstResult/maxResults. Instead it will be done in-memory. The warning will be logged:

`WARN HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!`

[Original article](http://developer-should-know.tumblr.com/post/118012584847/jpa-fetching-strategies)