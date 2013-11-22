package com.example.backend.service;

import com.example.backend.dao.PaymentRepository;
import com.example.backend.model.Payment;
import com.example.backend.model.PaymentStatus;
import com.example.backend.model.ShopOrder;
import com.example.util.JmsConfig;
import com.example.util.JmsConsumer;
import com.example.util.JmsProducer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.example.util.UnitOfWork.doUnitOfWork;

@Singleton
public class PaymentService implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(mappedName = "java:/queue/com.example.rest-api_to_back-end")
    private Queue apiQueueIn;
    
    @Resource(mappedName = "java:/queue/com.example.back-end_to_payment-integration")
    private Queue paymentIntegrationQueueOut;
    
    @Resource(mappedName = "java:/queue/com.example.payment-integration_to_back-end")
    private Queue paymentIntegrationQueueIn;
    
    private JmsConfig jmsConfig;
    
    private JmsConsumer apiIn;
    private JmsProducer paymentIntegrationOut;
    private JmsConsumer paymentIntegrationIn;

    @Inject
    private PaymentRepository paymentRepository;
    
    @PostConstruct
    public void init() throws JMSException {
        logger.info("Initializing {}", this);
        
        jmsConfig = new JmsConfig(connectionFactory);
        jmsConfig.init();
        
        apiIn = new JmsConsumer(jmsConfig, apiQueueIn);
        apiIn.init();
        apiIn.setMessageListener(this);
        
        paymentIntegrationOut = new JmsProducer(jmsConfig, paymentIntegrationQueueOut, false);
        paymentIntegrationOut.init();
        
        paymentIntegrationIn = new JmsConsumer(jmsConfig, paymentIntegrationQueueIn);
        paymentIntegrationIn.init();
        paymentIntegrationIn.setMessageListener(this);
    }
    
    @PreDestroy
    public void destroy() throws JMSException {
        logger.info("Destroying {}", this);
        
        apiIn.destroy();
        paymentIntegrationOut.destroy();
        paymentIntegrationIn.destroy();
        jmsConfig.destroy();
    }
    
    @Override
    public void onMessage(Message request) {
        try {
            String method = request.getStringProperty("method");
            switch (method) {
                case "createOrder": {
                    double amount = request.getDoubleProperty("amount");
                    Message reply = jmsConfig.createMessage();
                    reply.setStringProperty("orderId", createOrder(amount));
                    jmsConfig.send(request, reply);
                    break;
                }
                case "pay": {
                    String orderId = request.getStringProperty("orderId");
                    Message reply = jmsConfig.createMessage();
                    reply.setStringProperty("paymentId", pay(orderId));
                    jmsConfig.send(request, reply);
                    break;
                }
                case "getPaymentStatus": {
                    String paymentId = request.getStringProperty("paymentId");
                    Message reply = jmsConfig.createMessage();
                    reply.setStringProperty("status", getPaymentStatus(paymentId));
                    jmsConfig.send(request, reply);
                    break;
                }
                case "setPaymentStatus": {
                    String paymentId = request.getStringProperty("paymentId");
                    String status = request.getStringProperty("status");
                    setPaymentStatus(paymentId, status);
                    break;
                }
            }
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public String createOrder(double amount) {
        logger.info("createOrder {}", amount);
        
        doUnitOfWork();
        
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setAmount(amount);
        paymentRepository.save(shopOrder);
        
        return shopOrder.getOrderId();
    }

    public String pay(String orderId) throws JMSException {
        logger.info("pay {}", orderId);
        
        doUnitOfWork();
        
        ShopOrder order = paymentRepository.findOrder(orderId);
        if (order == null) {
            return null;
        }
        
        Payment payment = new Payment();
        payment.setOrder(order);
        
        paymentRepository.save(payment);
        
        sendPaymentIntegrationRequest(payment, order);
        
        return payment.getPaymentId();
    }

    private void sendPaymentIntegrationRequest(Payment payment, ShopOrder order) throws JMSException {
        Message paymentRequest = jmsConfig.createMessage();
        paymentRequest.setStringProperty("method", "pay");
        paymentRequest.setStringProperty("paymentId", payment.getPaymentId());
        paymentRequest.setDoubleProperty("amount", order.getAmount());
        paymentIntegrationOut.send(paymentRequest);
    }

    public String getPaymentStatus(String paymentId) {
        logger.info("getPaymentStatus {}", paymentId);
        
        Payment payment = paymentRepository.findPayment(paymentId);
        return payment != null ? payment.getStatus().toString() : null;
    }
    
    public void setPaymentStatus(String paymentId, String status) {
        logger.info("setPaymentStatus {} {}", paymentId, status);
        
        Payment payment = paymentRepository.findPayment(paymentId);
        payment.setStatus(PaymentStatus.valueOf(status));
        paymentRepository.update(payment);
    }
}