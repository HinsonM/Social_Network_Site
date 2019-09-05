package com.simulationlab.QA_2.service;


import org.springframework.stereotype.Service;

@Service
public class QAService {
    public String getMsg(int userId) {
        return "Hello Message:" + String.valueOf(userId);
    }
}
