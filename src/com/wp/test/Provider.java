package com.wp.test;

import com.wp.framework.RpcFramework;
import com.wp.test.service.ByeService;
import com.wp.test.service.HelloService;
import com.wp.test.service.impl.ByeServiceImpl;
import com.wp.test.service.impl.HelloServiceImpl;

public class Provider {
    public static void main(String[] args) throws Exception {
        final HelloService helloService = new HelloServiceImpl();
        new Thread(() -> {
            try {
                RpcFramework.exportService(helloService, 20880);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        final ByeService byeService = new ByeServiceImpl();
        new Thread(() -> {
            try {
                RpcFramework.exportService(byeService, 20881);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}