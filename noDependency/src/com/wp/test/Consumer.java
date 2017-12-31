package com.wp.test;

import com.wp.framework.RpcFramework;
import com.wp.test.service.ByeService;
import com.wp.test.service.HelloService;

public class Consumer {
    public static void main(String[] args) {
        HelloService helloService = RpcFramework.referenceService(HelloService.class, "127.0.0.1", 20880);
        System.out.println(helloService.sayHello("tom"));

        ByeService byeService = RpcFramework.referenceService(ByeService.class, "127.0.0.1", 20881);
        System.out.println(byeService.sayBye("jack"));
    }
}