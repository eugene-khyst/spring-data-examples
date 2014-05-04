package com.example.api.service;

import com.example.util.jms.JmsConfig;
import com.example.util.jms.JmsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import static com.example.util.UnitOfWork.doUnitOfWork;

@ApplicationScoped
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(mappedName = "java:/queue/com.example.rest-api_to_back-end")
    private Queue backEndQueue;
    
    private JmsConfig jmsConfig;
    private JmsProducer backEndInOut;

    @PostConstruct
    public void init() throws JMSException {
        logger.info("Initializing {}", this);
        
        jmsConfig = new JmsConfig(connectionFactory);
        jmsConfig.init();
        
        backEndInOut = new JmsProducer(jmsConfig, backEndQueue, true);
        backEndInOut.init();
    }
    
    @PreDestroy
    public void destroy() throws JMSException {
        logger.info("Destroying {}", this);
        
        backEndInOut.destroy();
        jmsConfig.destroy();
    }
    
    public String createOrder(double amount) {
        logger.info("createOrder {}", amount);
        
        doUnitOfWork();
        
        try {
            Message createOrderRequest = jmsConfig.createMessage();
            createOrderRequest.setStringProperty("method", "createOrder");
            createOrderRequest.setDoubleProperty("amount", amount);
            Message reply = backEndInOut.request(createOrderRequest);
            return reply.getStringProperty("orderId");
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    
    public String pay(String orderId) {
        logger.info("pay {}", orderId);
        
        doUnitOfWork();
        
        try {
            Message paymentRequest = jmsConfig.createMessage();
            paymentRequest.setStringProperty("method", "pay");
            paymentRequest.setStringProperty("orderId", orderId);
            Message reply = backEndInOut.request(paymentRequest);
            return reply.getStringProperty("paymentId");
        } catch(JMSException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public String getPaymentStatus(String paymentId) {
        logger.info("getPaymentStatus {}", paymentId);
        
        try {
            Message paymentStatusRequest = jmsConfig.createMessage();
            paymentStatusRequest.setStringProperty("method", "getPaymentStatus");
            paymentStatusRequest.setStringProperty("paymentId", paymentId);
            Message reply = backEndInOut.request(paymentStatusRequest);
            return reply.getStringProperty("status");
        } catch(JMSException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
