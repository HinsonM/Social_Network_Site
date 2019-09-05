package com.simulationlab.QA_2.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.simulationlab.QA_2.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            if(arg != null) sb.append("arg:" + arg.toString() + "|");
            // 防止这里出现空指针
            // java.lang.NullPointerException: null
            //	at com.simulationlab.QA_2.aspect.LogAspect.beforeMethod(LogAspect.java:23) ~[classes/:na]
        }
        logger.info("before method:" + sb.toString());
    }

    @After("execution(* com.simulationlab.QA_2.controller.*Controller.*(..))")
    public void afterMethod() {
        logger.info("after method" + new Date());
    }
}
