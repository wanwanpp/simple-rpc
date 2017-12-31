package com.wp.test.service.impl;

import com.wp.test.service.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return name + " say hello";
    }
}