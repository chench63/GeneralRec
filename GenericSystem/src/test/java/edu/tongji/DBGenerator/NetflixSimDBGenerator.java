/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.DBGenerator;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * netflix数据，
 * 计算Item间相似度
 * 
 * @author chench
 * @version $Id: NetflixSimDBGenerator.java, v 0.1 2014-1-12 下午7:14:45 chench Exp $
 */
public class NetflixSimDBGenerator {

    /** Secure multiple party computation */
    public static String SMP = "experiment/recommendation/netflix/asynchronized-secure-mulitiparty.xml";

    /** Homomorphic encryption */
    public static String HE  = "experiment/recommendation/netflix/homomorphic-encryption.xml";

    /** Randomized perturbation */
    public static String RP  = "experiment/recommendation/netflix/randomized-perturbation.xml";
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(SMP);
            Engine engine = (Engine) ctx.getBean("engineForGeneratingSimilarity");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, NetflixSimDBGenerator.class + "发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
