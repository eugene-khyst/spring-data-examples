package com.example.util.jms;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsProducer extends JmsSupport {

    private static final Logger logger = LoggerFactory.getLogger(JmsProducer.class);

    private final Queue requestQueue;
    private final boolean requestReply;

    private TemporaryQueue replyQueue;

    private MessageProducer requestProducer;
    private MessageConsumer replyConsumer;

    public JmsProducer(JmsConfig config, Queue requestQueue, boolean requestReply) {
        super(config);
        this.requestQueue = requestQueue;
        this.requestReply = requestReply;
    }

    public void init() throws JMSException {
        logger.debug("Initializing {}", this);

        requestProducer = getSession().createProducer(requestQueue);
        requestProducer.setDeliveryMode(DeliveryMode.PERSISTENT);

        if (requestReply) {
            replyQueue = getSession().createTemporaryQueue();
            replyConsumer = getSession().createConsumer(replyQueue);
        }
    }

    public void destroy() throws JMSException {
        logger.debug("Destroying {}", this);

        if (replyConsumer != null) {
            replyConsumer.close();
        }
        if (replyQueue != null) {
            replyQueue.delete();
        }
        if (requestProducer != null) {
            requestProducer.close();
        }
    }

    public void send(Message request) throws JMSException {
        logger.debug("Sending to {}", requestQueue);

        requestProducer.send(request);
    }

    public Message request(Message request) throws JMSException {
        logger.debug("Sending to {}", requestQueue);

        if (!requestReply) {
            throw new IllegalStateException("JmsProducer is not configured to be used for Request-Reply");
        }

        request.setJMSReplyTo(replyQueue);
        requestProducer.send(request);

        logger.debug("Receiving from {}", replyQueue);

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < REQUEST_TIMEOUT) {

            Message reply = replyConsumer.receive(RECEIVE_TIMEOUT);
            if (reply == null) {
                continue;
            }
            if (request.getJMSMessageID().equals(reply.getJMSCorrelationID())) {
                reply.acknowledge();
                return reply;
            } else {
                getSession().recover();
            }
        }
        throw new RuntimeException("Request timeout, reply was not received in " + REQUEST_TIMEOUT + " ms");
    }
}
