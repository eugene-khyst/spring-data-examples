package com.example.util.jms;

import javax.jms.Session;

public class JmsSupport {

    protected static final long REQUEST_TIMEOUT = Long.parseLong(System.getProperty("com.example.jms.requestTimeout", "30000"));
    protected static final long RECEIVE_TIMEOUT = Long.parseLong(System.getProperty("com.example.jms.receiveTimeout", "1000"));

    private final JmsConfig config;

    protected JmsSupport(JmsConfig config) {
        this.config = config;
    }

    protected Session getSession() {
        return config.getSession();
    }
}
