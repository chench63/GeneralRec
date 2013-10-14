/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * 计算Netflix数据集，用户Rating丢失比例的主线程程序。
 * 
 * @author chenkh
 * @version $Id: NetflixRatingMainThread.java, v 0.1 2013-10-8 下午5:53:05 chenkh Exp $
 */
public final class NetflixRatingMainThread {

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
