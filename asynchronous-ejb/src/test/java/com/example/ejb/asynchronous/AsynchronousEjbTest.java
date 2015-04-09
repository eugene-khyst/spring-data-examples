/*
 * Copyright 2014 Evgeniy Khist.
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
package com.example.ejb.asynchronous;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Evgeniy Khist
 */
@RunWith(Arquillian.class)
public class AsynchronousEjbTest {

    @Deployment
    public static Archive<?> createServiceDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test-asynchronous-service.war")
                .addClasses(AsynchronousService.class, AsynchronousServiceBean.class);
    }

    @Inject
    private AsynchronousService exampleService;
    
    @Test
    public void testAsynchronousEjbCancellation() throws Exception {
        long timeout = 100;
        long totalTimeout = 10 * timeout;
        
        // Start long running task asynchronously 
        Future<Long> result = exampleService.performLongRunningTask(timeout);
        
        TimeUnit.MILLISECONDS.sleep(totalTimeout);
        
        // Cancel long running task
        result.cancel(true);
        
        
        // Check actual total timeout
        assertTrue(Math.abs(totalTimeout - result.get()) < timeout);
    }
}
