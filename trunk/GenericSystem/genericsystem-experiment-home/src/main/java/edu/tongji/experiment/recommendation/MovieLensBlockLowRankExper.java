/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: MovieLensBlockLowRankExper.java, v 0.1 2014-10-15 下午1:11:13 chench Exp $
 */
public class MovieLensBlockLowRankExper {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/lowrank/blraRcmd.xml");
            Engine engine = (Engine) ctx.getBean("blraRcmd");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, MoiveLensStandardSVDExper.class + " 发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }

    }

}
