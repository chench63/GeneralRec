/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.smartgrid.OneTimePadEngine;
import edu.tongji.engine.smartgrid.SmartGridEngine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chench
 * @version $Id: UMassGSmartGridExper.java, v 0.1 2013-12-17 下午6:35:04 chench Exp $
 */
public final class UMassOneTimePadExper {

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 实验运行次数*/
    private static int          TIMES  = 1000;

    /**
     * 引导函数
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/smartgrid/umass/umass-one-time-pad.xml");
            OneTimePadEngine engine = (OneTimePadEngine) ctx.getBean("defaultEngine");

            for (int i = 0; i < TIMES; i++) {
                engine.excute();
            }

            LoggerUtil.info(logger,
                "KL：" + engine.getKeyLens() + " Total：" + SmartGridDataSource.meterContexts.size()
                        + " Mean：" + String.format("%.3f", SmartGridEngine.STAT.getMean()) + " SD："
                        + String.format("%.3f", SmartGridEngine.STAT.getStandardDeviation()));
        } catch (Exception e) {
            ExceptionUtil.caught(e, UMassOneTimePadExper.class + " 发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
