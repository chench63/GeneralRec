/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * CMP对应，生成评分启动器
 * 
 * @author Hanke Chen
 * @version $Id: NetflixEvaPredctCMPExper.java, v 0.1 25 Apr 2014 11:40:20 chench Exp $
 */
public final class NetflixEvaPredctSMPExper {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/netflix/asynchronized-secure-mulitiparty.xml");
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
