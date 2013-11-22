package com.example.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsConsumer extends JmsSupport {

    private static final Logger logger = LoggerFactory.getLogger(JmsConsumer.class);

    private final Queue queue;

    private MessageConsumer consumer;

    private ExecutorService executor;

    public JmsConsumer(JmsConfig config, Queue queue) {
        super(config);
        this.queue = queue;
    }

    public void init() throws JMSException {
        logger.debug("Initializing {}", this);

        consumer = getSession().createConsumer(queue);

        executor = Executors.newCachedThreadPool();
    }

    public void destroy() throws JMSException {
        logger.debug("Destroying {}", this);

        executor.shutdownNow();

        if (consumer != null) {
            consumer.close();
        }
    }

    public void setMessageListener(final MessageListener listener) throws JMSException {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                logger.debug("Started MessageListener {}", listener);
                logger.debug("Receiving from {}", queue);

                while (true) {
                    try {
                        Message message = consumer.receive(RECEIVE_TIMEOUT);
                        if (message == null) {
                            continue;
                        }
                        listener.onMessage(message);
                        message.acknowledge();
                    } catch (JMSException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
    }
}
