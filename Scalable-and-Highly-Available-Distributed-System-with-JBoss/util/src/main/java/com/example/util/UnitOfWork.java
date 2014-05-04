package com.example.util;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitOfWork {
    
    private static final Logger logger = LoggerFactory.getLogger(UnitOfWork.class);
    
    private static final String UNIT_OF_WORK_DURATION = "com.example.unitOfWork.duration";
    
    private UnitOfWork() {
    }
    
    public static void doUnitOfWork() {
        String timeToSleepStr = System.getProperty(UNIT_OF_WORK_DURATION);
        if (timeToSleepStr == null) {
            return;
        }
        
        int timeToSleep = Integer.parseInt(timeToSleepStr);
        try {
            TimeUnit.MILLISECONDS.sleep(timeToSleep);
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
