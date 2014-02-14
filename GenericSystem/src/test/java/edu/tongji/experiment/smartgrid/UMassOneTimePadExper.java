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
    private static int          TIMES  = 10;

    /**
     * 引导函数
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/smartgrid/umass/umass-one-time-pad.xml");
            OneTimePadEngine engine = (OneTimePadEngine) ctx.getBean("engine");

            for (int i = 0; i < TIMES; i++) {
                engine.excute();
            }

            LoggerUtil.info(
                logger,
                "重复运行："
                        + TIMES
                        + " 平均时间："
                        + ((SmartGridEngine.runtimes[0] * 1.0 / TIMES) / 0.95)
                        + " 标准差："
                        + Math.sqrt((SmartGridEngine.runtimes[1] * 1.0 / TIMES - Math.pow(
                            SmartGridEngine.runtimes[0] * 1.0 / TIMES, 2.0)) / 0.95));
        } catch (Exception e) {
            ExceptionUtil.caught(e, UMassOneTimePadExper.class + " 发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
