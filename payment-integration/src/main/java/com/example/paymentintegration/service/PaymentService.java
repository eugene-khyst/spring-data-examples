package com.example.paymentintegration.service;

import com.example.util.jms.JmsConfig;
import com.example.util.jms.JmsConsumer;
import com.example.util.jms.JmsProducer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.example.util.UnitOfWork.doUnitOfWork;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentService implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(mappedName = "java:/queue/com.example.payment-integration_to_back-end")
    private Queue paymentIntegrationQueueOut;
    
    @Resource(mappedName = "java:/queue/com.example.back-end_to_payment-integration")
    private Queue paymentIntegrationQueueIn;
    
    private JmsConfig jmsConfig;
    
    private JmsProducer paymentIntegrationOut;
    private JmsConsumer paymentIntegrationIn;

    @PostConstruct
    public void init() throws JMSException {
        logger.info("Initializing {}", this);
        
        jmsConfig = new JmsConfig(connectionFactory);
        jmsConfig.init();
        
        paymentIntegrationOut = new JmsProducer(jmsConfig, paymentIntegrationQueueOut, true);
        paymentIntegrationOut.init();
        
        paymentIntegrationIn = new JmsConsumer(jmsConfig, paymentIntegrationQueueIn);
        paymentIntegrationIn.init();
        paymentIntegrationIn.setMessageListener(this);
    }
    
    @PreDestroy
    public void destroy() throws JMSException {
        logger.info("Destroying {}", this);
        
        paymentIntegrationOut.destroy();
        paymentIntegrationIn.destroy();
        jmsConfig.destroy();
    }
    
    @Override
    public void onMessage(Message request) {
        try {
            String method = request.getStringProperty("method");
            switch (method) {
                case "pay": {
                    String paymentId = request.getStringProperty("paymentId");
                    double amount = request.getDoubleProperty("amount");
                    sendSetPaymentStatusRequest(paymentId, "IN_PROGRESS");
                    sendSetPaymentStatusRequest(paymentId, pay(paymentId, amount));
                    break;
                }
            }
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void sendSetPaymentStatusRequest(String paymentId, String status) throws JMSException {
        Message setPaymentStatusRequest = jmsConfig.createMessage();
        setPaymentStatusRequest.setStringProperty("method", "setPaymentStatus");
        setPaymentStatusRequest.setStringProperty("paymentId", paymentId);
        setPaymentStatusRequest.setStringProperty("status", status);
        paymentIntegrationOut.send(setPaymentStatusRequest);
    }
    
    // Fake call to payment integration service
    // Payments with amount more that 100 will fail
    public String pay(String paymentId, double amount) throws JMSException {
        logger.info("pay {} {}", paymentId, amount);
        
        doUnitOfWork();
        
        return (((int) amount) % 10 == 1) ? "FAILED" : "DONE";
    }
}