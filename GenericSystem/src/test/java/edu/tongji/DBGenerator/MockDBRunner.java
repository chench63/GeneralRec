/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.DBGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author chenkh
 * @version $Id: MockDBRunner.java, v 0.1 2013-9-12 下午4:29:46 chenkh Exp $
 */
public class MockDBRunner {

    private static ClassPathXmlApplicationContext ctx = null;

    /**
     * 
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {

        String[] taskNames = { "RecommendationEngineTaskII"  };
        int endOfItemI = 1926;
        

        ctx = new ClassPathXmlApplicationContext("META-INF/spring/application-context.xml");
        ExecutorService exec = Executors.newFixedThreadPool(4);
        exec.execute(new EnginerRunner(Thread.MAX_PRIORITY, new FatherWorker(ctx, endOfItemI)));

        TimeUnit.SECONDS.sleep(15);

        
        
        for (String beanName : taskNames) {
            exec.execute(new EnginerRunner(Thread.MAX_PRIORITY, new DefaultWorker(ctx, beanName,
                2326, 3952)));
        }
        
        exec.shutdown();
    }

}
