HornetQ Maven PluginOS
====================

HornetQ Maven plugin ([maven-hornetq-plugin](https://github.com/hornetq/maven-hornetq-plugin)) is a useful tool for integration testing. 
The article describes how to configure Maven project to start HornetQ server and run JMS client.

Add plugin definition with configuration to *pom.xml*

```xml
<plugin>
	<groupId>org.hornetq</groupId>
	<artifactId>hornetq-maven-plugin</artifactId>
	<version>1.2.0</version>
	<configuration>
		<waitOnStart>false</waitOnStart>
		<hornetqConfigurationDir>${project.build.testOutputDirectory}/hornetq/</hornetqConfigurationDir>
		<systemProperties>
			<data.dir>${project.build.directory}/hornetq/data</data.dir>
		</systemProperties>
	</configuration>
</plugin>
```

Plugin requires HornetQ configuration files inorder to start HornetQ server. 
The location to search for configuration files is configured with `hornetqConfigurationDir` configuration element. 
HornetQ requires at least 3 configuration files: [hornetq-configuration.xml](https://github.com/hornetq/hornetq/blob/master/distribution/hornetq/src/main/resources/config/non-clustered/hornetq-configuration.xml), [hornetq-jms.xml](https://github.com/hornetq/hornetq/blob/master/distribution/hornetq/src/main/resources/config/non-clustered/hornetq-jms.xml), [hornetq-users.xml](https://github.com/hornetq/hornetq/blob/master/distribution/hornetq/src/main/resources/config/non-clustered/hornetq-users.xml). 
The default standalone non-clustered HornetQ configuration is sufficient for integration tests. 
Put these 3 files into *src/test/resources/hornetq*.

Using directory under *target* as a HornetQ data directory can be considered as a good practice. 
HornetQ data directory can be set with *data.dir* system property (as defined by default in *hornetq-configuration.xml*) that can be passed using `systemProperties` configuration element.

HornetQ dependencies are required to run the plugin. 
Plugin supports most of the latest HornetQ versions. 
Dependencies should be added not as pom.xml dependencies, but as plugin dependencies

```xml
<dependencies>
	<dependency>
		<groupId>org.jboss.spec.javax.jms</groupId>
		<artifactId>jboss-jms-api_2.0_spec</artifactId>
		<version>${versions.jms-api}</version>
	</dependency>
	<dependency>
		<groupId>org.hornetq</groupId>
		<artifactId>hornetq-core-client</artifactId>
		<version>${versions.hornetq}</version>
	</dependency>
	<dependency>
		<groupId>org.hornetq</groupId>
		<artifactId>hornetq-server</artifactId>
		<version>${versions.hornetq}</version>
	</dependency>
	<dependency>
		<groupId>org.hornetq</groupId>
		<artifactId>hornetq-jms-client</artifactId>
		<version>${versions.hornetq}</version>
	</dependency>
	<dependency>
		<groupId>org.hornetq</groupId>
		<artifactId>hornetq-jms-server</artifactId>
		<version>${versions.hornetq}</version>
	</dependency>
	<dependency>
		<groupId>io.netty</groupId>
		<artifactId>netty-all</artifactId>
		<version>${versions.netty}</version>
	</dependency>
	<dependency>
		<groupId>org.jboss.naming</groupId>
		<artifactId>jnpserver</artifactId>
		<version>${versions.jnp}</version>
	</dependency>
</dependencies>
```

Configure plugin executions to start and stop HornetQ server

```xml
<executions>
	<execution>
		<id>start</id>
		<goals>
			<goal>start</goal>
		</goals>
		<phase>pre-integration-test</phase>
	</execution>
	<execution>
		<id>stop</id>
		<goals>
			<goal>stop</goal>
		</goals>
		<phase>post-integration-test</phase>
	</execution>
</executions>
```

In order to make JNDI lookup from HornetQ naming server and use JMS API add the following dependencies to *pom.xml*

```xml
<dependencies>
	<!-- JMS 2.0 API -->
	<dependency>
		<groupId>org.jboss.spec.javax.jms</groupId>
		<artifactId>jboss-jms-api_2.0_spec</artifactId>
		<version>${versions.jms-api}</version>
	</dependency>
	<!-- Dependencies required for JNDI lookup -->
	<dependency>
		<groupId>org.hornetq</groupId>
		<artifactId>hornetq-jms-client</artifactId>
		<version>${versions.hornetq}</version>
	</dependency>
	<dependency>
		<groupId>org.jboss.naming</groupId>
		<artifactId>jnp-client</artifactId>
		<version>${versions.jnp}</version>
	</dependency>
</dependencies>
```

To lookup `ConnectionFactory` and `Queue` from JNDI, `InitialContext` should be created with the following properties

```java
Properties properties = new Properties();
properties.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
properties.setProperty("java.naming.provider.url", "jnp://localhost:1099");
properties.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
InitialContext ic = new InitialContext(properties);
ConnectionFactory cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
Queue queue = (Queue) ic.lookup("/queue/exampleQueue");
String textMessage = "This is a text message";
try (JMSContext context = cf.createContext("guest", "guest")) {
    context.createProducer().send(queue, textMessage);
    JMSConsumer consumer = context.createConsumer(queue);
    String receiveTextMessage = consumer.receiveBody(String.class, 5000);
    //...
}
```

[Original article](http://developer-should-know.tumblr.com/post/112504569452/hornetq-maven-plugin)
