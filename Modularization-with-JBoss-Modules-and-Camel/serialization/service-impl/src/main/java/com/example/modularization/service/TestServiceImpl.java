package com.example.modularization.service;

import org.springframework.stereotype.Component;

@Component
public class TestServiceImpl implements TestService {

    @Override
    public String sayHello(String name) {
        return "Hello " + name + "!";
    }

    @Override
    public String sayGoodbye(String name) {
        return "Goodbye " + name + "!";
    }

}
