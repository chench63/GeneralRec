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
 * @author chench
 * @version $Id: MoiveLensStandardSVDExper.java, v 0.1 2014-10-7 下午7:52:08 chench Exp $
 */
public final class MoiveLensStandardSVDExper {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/movielens/standard_svd.xml");
            Engine engine = (Engine) ctx.getBean("engineForGeneratingRecommendations");
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
