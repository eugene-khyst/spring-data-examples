package com.example.modularization.web;

import com.example.modularization.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @Autowired
    private TestService testService;
    
    @RequestMapping("/hello")
    public @ResponseBody String sayHello(@RequestParam("name") String name) {
        return testService.sayHello(name);
    }
    
    @RequestMapping("/goodbye")
    public @ResponseBody String sayGoodbye(@RequestParam("name") String name) {
        return testService.sayGoodbye(name);
    }
    
}
