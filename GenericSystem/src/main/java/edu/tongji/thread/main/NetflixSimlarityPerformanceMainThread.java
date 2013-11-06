/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixSimlarityPerformanceMainThread.java, v 0.1 2013-10-15 上午10:23:03 chenkh Exp $
 */
public final class NetflixSimlarityPerformanceMainThread {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "META-INF/spring/application-context-netflix-statistic.xml");
                        Engine engine = (Engine) ctx.getBean("engine");
//            Engine engine = (Engine) ctx.getBean("engineForSim");

            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, "edu.tongji.engine.TestEngine 测试用例发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
