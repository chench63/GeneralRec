/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.smartgrid.HomomorphicEngine;
import edu.tongji.engine.smartgrid.SmartGridEngine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chench
 * @version $Id: UMassHomomorphismExper.java, v 0.1 2013-12-18 上午11:16:53 chench Exp $
 */
public class UMassHomomorphismExper {

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 实验运行次数*/
    private static int          TIMES  = 20;

    /**
     * 引导函数
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/smartgrid/umass/umass-homomorphic-encryption.xml");
            HomomorphicEngine engine = (HomomorphicEngine) ctx.getBean("engine");

            for (int i = 0; i < TIMES; i++) {
                engine.excute();
            }

            LoggerUtil.info(
                logger,
                "Repeats：" + TIMES + " Mean："
                        + String.format("%.2f", SmartGridEngine.STAT.getMean()) + " SD："
                        + String.format("%.2f", SmartGridEngine.STAT.getStandardDeviation()));
        } catch (Exception e) {
            ExceptionUtil.caught(e, UMassHomomorphismExper.class + " 发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
