package com.ipaper.myapp.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.ipaper.myapp.MemoryUsage;



@Component
@Aspect
public class TimeExecutionProfiler {

    //private static final Logger logger = LoggerFactory.getLogger(TimeExecutionProfiler.class);
    @Pointcut("execution(* com.ipaper.myapp.EpaperTask.*(..))")
    public void businessController() {
    }

    @Around("businessController()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        //long start = System.currentTimeMillis();
        //logger.info("ServicesProfiler.profile(): Going to call the method: {}", pjp.getSignature().getName());
        System.out.println("ServicesProfiler.profile(): Going to call the method: {" + pjp.getSignature().getName()+"}");
        Object output = pjp.proceed();
//        //logger.info("ServicesProfiler.profile(): Method execution completed.");
//        System.out.println("ServicesProfiler.profile(): Method execution completed.");
//        long elapsedTime = System.currentTimeMillis() - start;
//        //logger.info("ServicesProfiler.profile(): Method execution time: " + elapsedTime + " milliseconds.");
//        System.out.println("ServicesProfiler.profile(): Method execution time: " + elapsedTime + " milliseconds.");

        return output;
    }
    
    
    @Before("businessController()")
    public void profileMemory2() {
        //logger.info("JVM memory in use = {}", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
    	long mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024);
    	System.out.println("JVM memory in use = {" + mem +"}");
    	MemoryUsage.setMaxMemoryUsed(mem);
    }

    @After("businessController()")
    public void profileMemory() {
        //logger.info("JVM memory in use = {}", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
    	long mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024);
    	System.out.println("JVM memory in use = {" + mem +"}");
    	MemoryUsage.setMaxMemoryUsed(mem);
    }
}

