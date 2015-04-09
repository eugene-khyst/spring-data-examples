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
package com.example.ejb.asynchronous;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yevhen Khyst
 */
@Stateless
@Local(AsynchronousService.class)
public class AsynchronousServiceBean implements AsynchronousService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousServiceBean.class);
    
    @Resource
    private SessionContext sessionContext;

    @Asynchronous
    @Override
    public Future<Long> performLongRunningTask(long timeout) {
        long startTime = System.currentTimeMillis();
        while (!sessionContext.wasCancelCalled()) {
            doPerformLongRunningTask(timeout);
        }
        long time = System.currentTimeMillis() - startTime;
        return new AsyncResult<>(time);
    }

    private void doPerformLongRunningTask(long timeout) {
        try {
            LOGGER.info("Sleeping {} milliseconds", timeout);
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }
}
