/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.DBGenerator;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.context.RecommendationContext;
import edu.tongji.engine.FileBasedRecommendationEngine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: DefaultWorker.java, v 0.1 2013-9-12 下午3:47:47 chenkh Exp $
 */
public class DefaultWorker implements Worker {

    private ApplicationContext  ctx;

    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    private String              beanName;

    private int                 startOfItemI;

    private int                 endOfItemI;

    public DefaultWorker(ClassPathXmlApplicationContext ctx, String beanName, int startOfItemI,
                         int endOfItemI) {
        this.beanName = beanName;
        this.ctx = ctx;
        this.startOfItemI = startOfItemI;
        this.endOfItemI = endOfItemI;
    }

    /** 
     * @see edu.tongji.DBGenerator.Worker#run()
     */
    @Override
    public void run() {
        try {
            LoggerUtil.info(logger, this.beanName + " starts to Run.");

            FileBasedRecommendationEngine engine = (FileBasedRecommendationEngine) ctx.getBean(beanName);
            RecommendationContext context = (RecommendationContext) engine.getProcessorContext();

            for (int itemI = startOfItemI; itemI < endOfItemI; itemI++) {
                for (int itemJ = 1; itemJ < itemI; itemJ++) {
                    LoggerUtil.info(logger, "**************ItemI: " + itemI + "\tItemJ: " + itemJ
                                            + "********************************");
                    context.setItemI(String.valueOf(itemI));
                    context.setItemJ(String.valueOf(itemJ));
                    engine.excute();
                }
            }

        } catch (Exception e) {
            ExceptionUtil.caught(e, "edu.tongji.engine.TestEngine Thread：" + this.beanName);
        }
    }

}
