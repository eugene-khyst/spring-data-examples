package com.example.camel.service;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServiceImpl implements TestService {

    private static final Logger LOG = LoggerFactory.getLogger(TestServiceImpl.class);
    
    private int sleepSeconds;
    
    @Override
    public String sayHello(long sentTimestamp) {
        long receivedTimestamp = System.currentTimeMillis();
        long time = receivedTimestamp - sentTimestamp;
        
        sleep();
        
        String message = "[ Sent: " + sentTimestamp + ", Received: " + receivedTimestamp + ", In delivery: " + time + " ] Hello!";
        LOG.info(message);
        return message;
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(sleepSeconds);
        } catch (InterruptedException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public int getSleepSeconds() {
        return sleepSeconds;
    }

    public void setSleepSeconds(int sleepSeconds) {
        this.sleepSeconds = sleepSeconds;
    }
}
