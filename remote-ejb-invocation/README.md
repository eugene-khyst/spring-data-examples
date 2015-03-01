JNDI Lookup and Remote EJB Invocation Inside the Server Instance
================================================================

To lookup object bound in JNDI in application inside JBoss EAP/WildFly server instance create an `InitialContext` using the `javax.naming.*` API.

It might be required to specify initial context factory property in order to create `InitialContext`:

```java
Properties props = new Properties();
props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.as.naming.InitialContextFactory");
InitialContext ic = new InitialContext(props);
```

If you are using Spring JndiTemplate:

```xml
<bean id="jndiTemplate" class="org.springframework.jndi.JndiTemplate">
    <property name="environment">
        <props>
            <prop key="java.naming.factory.initial">org.jboss.as.naming.InitialContextFactory
        </props>
    </property>
</bean>
```

Multiple JNDI bindings for EJB implementing remote interface are created. To invoke remote EJB deployed in different deployment unit, just lookup it by global JNDI name. You can easily call EJBs in the same container but different deployment units (EAR/WAR).

Checkout example of remote EJB invocation atomated with Arquillian. EJB itself is packeged inside test-calculator-service.war and is invoked from test-calculator-client.war using simple JNDI lookup. Both test-calculator-service.war and test-calculator-client.war are deployed to the same container.

```java
@Stateless
@Remote(Calculator.class)
public class CalculatorBean implements Calculator {

    @Override
    public BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    @Override
    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }
}
```

```java
@RunWith(Arquillian.class)
public class RemoteEjbInvocationTest {

    @Deployment(name = "service")
    public static Archive> createServiceDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test-calculator-service.war")
                .addClasses(Calculator.class, CalculatorBean.class);
    }
    
    @Deployment(name = "client")
    public static Archive> createClientDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test-calculator-client.war")
                .addClasses(Calculator.class)
                .add(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @OperateOnDeployment("client")
    public void testRemoteEjbInvocation() throws Exception {
        InitialContext ic = new InitialContext();
        Calculator calculator = (Calculator) ic.lookup("java:global/test-calculator-service/CalculatorBean");
        assertNotNull(calculator);
        
        BigDecimal result = calculator.add(BigDecimal.ONE, BigDecimal.ONE);
        assertEquals(0, new BigDecimal("2").compareTo(result));
    }
}
```

[Original article](http://developer-should-know.tumblr.com/post/104167696222/jndi-lookup-and-remote-ejb-invocation-inside-the)