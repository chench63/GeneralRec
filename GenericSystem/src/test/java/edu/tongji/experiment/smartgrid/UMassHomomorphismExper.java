/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.smartgrid.HomomorphicEngine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: UMassHomomorphismExper.java, v 0.1 2013-12-18 上午11:16:53 chenkh Exp $
 */
public class UMassHomomorphismExper {

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 实验运行次数*/
    private static int          TIMES  = 100;

    /**
     * 引导函数
     * @param args
     */
    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/smartgrid/umass/umass-homomorphic-encryption.xml");
            HomomorphicEngine engine = (HomomorphicEngine) ctx.getBean("engine");

            for (int i = 0; i < TIMES; i++) {
                engine.excute();
            }

            LoggerUtil.info(logger, "重复运行：" + TIMES + " 平均时间：" + (engine.runtimes * 1.0 / TIMES));
        } catch (Exception e) {
            ExceptionUtil.caught(e, UMassHomomorphismExper.class + " 发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
