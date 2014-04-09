package com.example.camel.web;

import com.example.camel.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CamelTestController {

    @Autowired
    private TestService testService;
    
    @RequestMapping("/camel")
    public @ResponseBody String sayHello() {
        return testService.sayHello(System.currentTimeMillis());
    }
}
