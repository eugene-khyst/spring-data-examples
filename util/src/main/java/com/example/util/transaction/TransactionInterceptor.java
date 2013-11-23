package com.example.util.transaction;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional 
@Interceptor
public class TransactionInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionInterceptor.class);
    
    @Resource 
    private UserTransaction tx;
    
    @AroundInvoke
    public Object manageTransaction(InvocationContext context) throws Exception {
        logger.debug("Starting transaction");
        tx.begin();
        Object result = context.proceed();
        logger.debug("Committing transaction");
        tx.commit();
        
        return result;
    }
}