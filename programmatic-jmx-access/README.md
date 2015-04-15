Accessing JMX Data Programmatically
===================================

JMX is the technology used for management and monitoring Java applications.

Resources of Java application are represented by *MBeans*. *MBeans* are objects that have editable and read-only attributes and set of operations. *MBean* attributes and operations can be used for monitoring and management of Java applications.

JMX has API that allows to access management resources programmatically. 

Let's create and register simple *MBean* with editable and read-only attributes and simple operation.

```java
public class Hello implements HelloMBean {

    private String message = "Hello, world!";

    // Operation
    @Override
    public void printMessage() {
        System.out.println(message);
    }

    // A writable attribute
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    // A read-only attribute
    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }
}
```

```java
// Register MBean
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
ObjectName mbeanName = new ObjectName("com.example:type=Hello");
Hello mbean = new Hello();
mbs.registerMBean(mbean, mbeanName);
```

After *MBean* is registered in *MBeanServer*, it can be accessed programmaticaly or using one of existing GUI tools like *JConsole*.

To access JMX data from the same JVM, use local platform *MBeanServer*

```java
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
ObjectName mbeanName = new ObjectName("com.example:type=Hello");

// Read attribute
Object messageAttribute = mbs.getAttribute(mbeanName, "Message");
assertEquals("Hello, world!", messageAttribute);

// Write attribute
mbs.setAttribute(mbeanName, new Attribute("Message", "New message"));

messageAttribute = mbs.getAttribute(mbeanName, "Message");
assertEquals("New message", messageAttribute);

mbs.invoke(mbeanName, "printMessage", new Object[]{}, new String[]{});
```

If you want to access JMX data remotely, you have to start JVM with additional system properties

```
-Dcom.sun.management.jmxremote.port=9999
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
```

If you are getting *Connection refused to host* error message, add system property

```
-Djava.rmi.server.hostname=myhost.example.com
```

To access *MBean* on remote JVM, you have to use *JMXConnector*

```java
JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://myhost.example.com:9999/jmxrmi");
JMXConnector jmxc = JMXConnectorFactory.connect(url);
MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
ObjectName mbeanName = new ObjectName("com.example:type=Hello");

// Read attribute
Object messageAttribute = mbsc.getAttribute(mbeanName, "Message");
assertEquals("Hello, world!", messageAttribute);

// Write attribute
mbsc.setAttribute(mbeanName, new Attribute("Message", "New message"));

messageAttribute = mbsc.getAttribute(mbeanName, "Message");
assertEquals("New message", messageAttribute);

mbsc.invoke(mbeanName, "printMessage", new Object[]{}, new String[]{});
```

Pay attention to service URL `service:jmx:rmi:///jndi/rmi://myhost.example.com:9999/jmxrmi`.

[Original article](http://developer-should-know.tumblr.com/post/116457914632/accessing-jmx-data-programmatically-and-with-gui)
