Kiev_Ciklum_Java_Saturday_2013-11-23
====================================

cd $JBOSS_HOME/standalone/configuration/

cp standalone-full-ha.xml standalone-${module}.xml

standalone.xml
urn:jboss:domain:logging:1.2
<console-handler name="CONSOLE">
  <level name="INFO"/>
  <formatter>
    <pattern-formatter pattern="%K{level}%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
  </formatter>
</console-handler>
...
<logger category="com.example">
  <level name="DEBUG"/>
</logger>
...
<root-logger>
...
  <handlers>
    <handler name="CONSOLE"/>
    <handler name="FILE"/>
  </handlers>
</root-logger>

urn:jboss:domain:datasources:1.1
urn:jboss:domain:deployment-scanner:1.1
urn:jboss:domain:messaging:1.3

./standalone.sh -Djboss.server.base.dir=$JBOSS_HOME/standalone-node/ -Djboss.socket.binding.port-offset=${n*10000} --server-config=standalone-${module}.xml

java -cp h2*.jar org.h2.tools.Server

/subsystem=datasources/data-source=ExampleDS/:write-attribute(name=connection-url, value=jdbc:h2:tcp://localhost/~/.h2/exampledb)
:shutdown(restart=true)
/subsystem=datasources/data-source=ExampleDS/:read-resource

/subsystem=deployment-scanner/scanner=default/:read-attribute(name=scan-interval)
/subsystem=deployment-scanner/scanner=default/:write-attribute(name=scan-interval, value=5000)

/system-property=com.example.node.name:write-attribute(name=value, value=${node})
/system-property=com.example.node.name:read-resource

$JBOSS_HOME/bin/add-user.sh
jbossadmin
Password1!

http://localhost:9990/console
Profile/Subsystems/Connector/Datasources
Profile/General Configuration/System Properties

/system-property=com.example.unitOfWork.duration:write-attribute(name=value, value=10)

$JBOSS_HOME/bin/add-user.sh
jmsuser
Password1!
guest

/subsystem=messaging/hornetq-server=default/pooled-connection-factory=hornetq-ra/:write-attribute(name=user, value=jmsuser)
/subsystem=messaging/hornetq-server=default/pooled-connection-factory=hornetq-ra/:write-attribute(name=password, value=jm$User)

/system-property=com.example.jms.requestTimeout:write-attribute(name=value, value=30000)
/system-property=com.example.jms.receiveTimeout:write-attribute(name=value, value=250)

/system-property=com.example.unitOfWork.duration:write-attribute(name=value, value=5000)

jms-queue add --queue-address=com.example.rest-api_to_back-end --entries=java:/queue/com.example.rest-api_to_back-end --durable=true

jms-queue add --queue-address=com.example.back-end_to_payment-integration --entries=java:/queue/com.example.back-end_to_payment-integration --durable=true

jms-queue add --queue-address=com.example.payment-integration_to_back-end --entries=java:/queue/com.example.payment-integration_to_back-end --durable=true

curl -X POST http://localhost:8080/api/payment/order?amount={amount}
curl -X POST http://localhost:8080/api/payment/order/{orderId}
curl -X GET http://localhost:8080/api/payment/{paymentId}/status
