package com.example.util;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsConfig {

    private static final Logger logger = LoggerFactory.getLogger(JmsConfig.class);

    private final ConnectionFactory connectionFactory;

    private Connection connection;
    private Session session;

    public JmsConfig(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void init() throws JMSException {
        logger.debug("Initializing {}", this);

        connection = connectionFactory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
    }

    public void destroy() throws JMSException {
        logger.debug("Destroying {}", this);

        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    public Session getSession() {
        return session;
    }
    
    public Message createMessage() throws JMSException {
        return session.createMessage();
    }
    
    public void send(Message request, Message reply) throws JMSException {
        logger.debug("Sending to {}", request.getJMSReplyTo());

        reply.setJMSCorrelationID(request.getJMSMessageID());

        MessageProducer producer = getSession().createProducer(request.getJMSReplyTo());
        producer.send(reply);
    }
}
