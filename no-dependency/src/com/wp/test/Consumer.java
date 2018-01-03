package com.wp.test;

import com.wp.framework.RpcFramework;
import com.wp.test.service.ByeService;
import com.wp.test.service.HelloService;

public class Consumer {
    public static void main(String[] args) {
//        获取代理对象
//        HelloService helloService = RpcFramework.useService(HelloService.class, "127.0.0.1", 20880);
        HelloService helloService = RpcFramework.useService(HelloService.class, "123.207.249.95", 20880);
        System.out.println(helloService.sayHello("tom"));

        ByeService byeService = RpcFramework.useService(ByeService.class, "123.207.249.95", 20881);
        System.out.println(byeService.sayBye("jack"));
    }
}