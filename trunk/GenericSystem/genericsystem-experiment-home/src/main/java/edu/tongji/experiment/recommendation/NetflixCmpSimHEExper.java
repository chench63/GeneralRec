/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * Homomorphic encryption 
 * @author chench
 * @version $Id: NetflixCmpSimHEExper.java, v 0.1 27 Apr 2014 22:38:29 chench Exp $
 */
public class NetflixCmpSimHEExper {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/netflix/homomorphic-encryption.xml");
            Engine engine = (Engine) ctx.getBean("engineForGeneratingSimilarity");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, NetflixCmpSimRPExper.class + "发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
