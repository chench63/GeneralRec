/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixSimlarityPerformanceMainThread.java, v 0.1 2013-10-15 上午10:23:03 chenkh Exp $
 */
public final class NetflixRandomizedPerturbationExper {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/netflix/randomized-perturbation.xml");
            Engine engine = (Engine) ctx.getBean("engineForGeneratingRecommendations");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, NetflixRandomizedPerturbationExper.class + "发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
