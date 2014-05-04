Scalable and Highly Available Distributed System with JBoss
===========================================================

Data Source
-----------

`java -cp h2*.jar org.h2.tools.Server`

name=ExampleDS

url=jdbc:h2:tcp://localhost/~/.h2/exampledb


Connection Factory
------------------

entry=java:/ConnectionFactory

Queues
------

name=java:/queue/com.example.rest-api_to_back-end

name=java:/queue/com.example.back-end_to_payment-integration

name=java:/queue/com.example.payment-integration_to_back-end

Endpoints
---------

POST http://localhost:8080/api/payment/order?amount={amount}

POST http://localhost:8080/api/payment/order/{orderId}

GET http://localhost:8080/api/payment/{paymentId}/status
