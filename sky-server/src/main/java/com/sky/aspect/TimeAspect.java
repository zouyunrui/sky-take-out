package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
/**
 * 自定义切面，实现统计业务层方法的运行耗时
 */
@Component
@Aspect
@Slf4j
public class TimeAspect {

    @Around("execution(* com.sky.service.*.*(..))")//切入点表达式
    public Object recordTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        long begin=System.currentTimeMillis();
        Object object=proceedingJoinPoint.proceed();//调用原始方法运行
        long end=System.currentTimeMillis();
        log.info(proceedingJoinPoint.getSignature()+"执行耗时: {}ms",end-begin);
        return object;
    }
}
