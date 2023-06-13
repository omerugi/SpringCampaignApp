package com.example.mabaya.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AppLogger {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("within(com.example.mabaya.controllers..*)")
    public Object logAroundControllers(ProceedingJoinPoint jp) throws Throwable {
        log.info("Entering method: {} {}", jp.getSignature().getName(), jp.getArgs());
        Object object = jp.proceed();

        log.info("Exiting method: {}", jp.getSignature().getName());
        return object;
    }

    @Before("within(com.example.mabaya.exeption.handler.*)")
    public void logExceptions(JoinPoint jp){
        log.error("An exception was thrown: ", jp.getArgs());
    }

    @Around("within(com.example.mabaya.schedulers.*)")
    public Object logAroundScheduler(ProceedingJoinPoint jp) throws Throwable {
        log.info("Staring Scheduler");
        Object object = jp.proceed();

        log.info("Ending Scheduler");
        return object;
    }

}
