Apache Camel JCA JMS Consumer
=============================

Pooled Connection Factory (hornetq-ra)
------------------

java:/JmsXA

Queue
-----

java:/queue/testQueue

Endpoint
--------

GET http://localhost:8080/producer/camel

System properties
-----------------

`$JBOSS_HOME/bin/standalone.sh -DconnectionFactory.jndiName=java:/JmsXA -DtestService.destination=java:/queue/testQueue -DtestService.endpoint="broker-hq:queue:java:/queue/testQueue" -DsleepSeconds=2`

`$JBOSS_HOME/bin/standalone.sh -DconnectionFactory.jndiName=java:/JmsXA -DtestService.endpoint=broker-hq:queue:java:/queue/testQueue?requestTimeout=60000`
