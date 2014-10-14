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
 * @version $Id: MoiveLensBlockSVDExper.java, v 0.1 2014-10-13 下午10:32:29 chench Exp $
 */
public class MoiveLensBlockSVDExper {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/movielens/snglrValuDecmpsRcmd.xml");
            Engine engine = (Engine) ctx.getBean("blockSVD");
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
