/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * 根据Homomorphic encryption & Secure multiple party computation 计算所得相似度
 * 预测评分
 * 
 * @author chenkh
 * @version $Id: NetflixSimlarityPerformanceMainThread.java, v 0.1 2013-10-15 上午10:23:03 chenkh Exp $
 */
public final class NetflixAsynchronizedSMPExper {

    /**
     * 
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
            ExceptionUtil.caught(e, "edu.tongji.engine.TestEngine 测试用例发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
