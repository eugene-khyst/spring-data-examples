URL Shortener
=============

This is URL Shortener prototype - not a production or fancy system.

* It is written in Java.
* It is an HTTP REST-based service
* It does two things:
  * Receives URL and returns "shortened" version. E.g. post "http://github.com" to "http://127.0.0.1/s/" and get back "http://127.0.0.1/s/2Bi".
  * The shortened URL can be resolved to original URL. E.g. "http://127.0.0.1/s/2Bi" will return "http://github.com".

Application uses pure Java EE 7 API. Recommended application server is Wildfly 8.1. This example can be used as Java EE 7 kick off application.

Maven is used for easy compilation and packaging.

Arquillian is used for real testing on real container. 
Maven will download Wildfly 8.1.0.Final distribution then Arquillian will deploy application and run tests. 
Take a look ate test working with REST API - `org.urlshortener.web.UrlShortenerResourceIT`.

To compile project and run tests use:

```bash
mvn clean package
```

To run specific test use:

```bash
mvn clean verify -Dit.test=UrlShortenerResourceIT
```

Each time URL is submitted a new record is inserted into the database.
Insert operations do not introduce locks in database. 
For primary key generation database sequence is used. 
The Hi/Lo algorithm allows to reduce number of database hits to improve performance. 
Application reserves range of ids that can be safely used. Each application instance increments sequence value allocating range of ids what allows to increment seqnece rarely. 

Once original URL is stored in database and primary key is assigned it is converted to radix 62 (alphabeth contains digits lower- and upper-case letters) and will be used in "shortened" URL. 
To resolve "shortened" URL string identifying original URL will be converted back to radix 10 - this is primary key. Then original URL will be found by primary key. 
Primary key column is always indexed. Search by numeric column is always faster than seach by text column. 
Radix 62 allows to save space - 10000 in radix 10 equals 2Bi in radix 62.

E.g. URL "http://github.com/" shortened to "http://127.0.0.1/s/2Bi"

* Inserting new record to database with id 10000 for original URL "http://github.com/" representing "shortened" URL 
* Converting id 10000 to radix 62 - 2Bi

Horizontal scalability of application and database is a key for handling high load like 10,000 URL generation requests per second or 10,000 URL generation requests per second. 
Absence of update operations gives application near linear scalability. 
To handle more load additional nodes with application instances can be set up - multiple Wildfly servers with HAProxy in front of them for load balancing. 
As a database Postgres-XC can be used. Postgres-XC is a write-scalable, synchronous, symmetric, and transparent PostgreSQL cluster solution. PostgreSQL nodes can be installed on more than one hardware or virtual machines. 
More database servers can handle more reads and writes.
The usage of Hi/Lo algorithm allows different application nodes not to block each other. 
Table with "shortened" URLs should be partitioned for better performance. 

How long to keep "shortened" URLs in database? By default keep them forever. If at some point we will run out of space "shortened" URLs that was not accessed longer than some specified time can be deleted.
For this reasons during each "shortened" URL resolving last view timestamp is updated in database and total number of views column is incremented.
Update operations cause locks in database. That's why such update should be asynchronous. When URL is resolved JMS message is sent to queue. Later application consumes messages from queue and updates records in database.
In high-load systems asynchronous communications should be always preffered to synchronous if it is possible.
