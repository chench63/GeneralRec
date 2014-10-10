/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * RP对应，生成评分启动器
 * 
 * @author Hanke Chen
 * @version $Id: NetflixEvaPredctRPExper.java, v 0.1 25 Apr 2014 11:40:43 chench Exp $
 */
public final class NetflixEvaPredctRPExper {

    /**
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
            ExceptionUtil.caught(e, NetflixCmpSimSMPExper.class + "发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
