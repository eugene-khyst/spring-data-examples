package com.example.camel.messaging;

import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.EndpointMessageListener;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.util.ExchangeHelper;

public class JcaMessageListener implements MessageListener {
    
    @EndpointInject(ref = "testServiceEndpoint")
    private JmsEndpoint endpoint;

    @EndpointInject(uri = "direct:testServiceExport")
    private ProducerTemplate producer;
    
    @Override
    public void onMessage(final Message message) {
        Processor processor = new Processor() {
            
            @Override
            public void process(Exchange exchange) throws Exception {
                Exchange result = producer.send(endpoint.createExchange(message));
                ExchangeHelper.copyResults(exchange, result);
            }
        };
        
        EndpointMessageListener messageListener = new EndpointMessageListener(endpoint, processor);
        endpoint.getConfiguration().configureMessageListener(messageListener);
        messageListener.setBinding(endpoint.getBinding());
        messageListener.setAsync(endpoint.getConfiguration().isAsyncConsumer());
        
        messageListener.onMessage(message);
    }
}
