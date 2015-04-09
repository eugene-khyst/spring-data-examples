/*
 * Copyright 2015 Yevhen Khyst.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jmx;

import java.lang.management.ManagementFactory;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Yevhen Khyst
 */
public class ProgrammaticJmxAccessTest {

    @Before
    public void setUp() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName mbeanName = new ObjectName("com.example:type=Hello");
        Hello mbean = new Hello();
        mbs.registerMBean(mbean, mbeanName);
    }
    
    @After
    public void tearDown() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName mbeanName = new ObjectName("com.example:type=Hello");
        mbs.unregisterMBean(mbeanName);
    }

    @Test
    public void testAccessMBeanServerSameJvm() throws Exception {
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
    }

    @Test
    public void testAccessMBeanServerRemoteJvm() throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
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
    }
}
