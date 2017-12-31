package com.wp.test.service.impl;

import com.wp.test.service.ByeService;

public class ByeServiceImpl implements ByeService {
    @Override
    public String sayBye(String name) {
        return name +" bye bye";
    }
}
