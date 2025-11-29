package com.myproject.ieltsproject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello cap";

    }
}
