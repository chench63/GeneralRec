/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.DBGenerator;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import edu.tongji.context.ContextEnvelope;
import edu.tongji.context.RecommendationContext;
import edu.tongji.engine.DefaultRecommendationEngine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.orm.MovielensDatasource;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: FatherWorker.java, v 0.1 2013-9-12 下午3:45:22 chenkh Exp $
 */
public class FatherWorker implements Worker {
    private ApplicationContext  ctx;

    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    private int                 endOfItemI;

    public FatherWorker(ApplicationContext ctx, int endOfItemI) {
        this.ctx = ctx;
        this.endOfItemI = endOfItemI;
    }

    /** 
     * @see edu.tongji.DBGenerator.Worker#run()
     */
    @Override
    public void run() {
        try {
            LoggerUtil.info(logger, "FatherWorker Starts to Run.");

            DefaultRecommendationEngine engine = (DefaultRecommendationEngine) ctx
                .getBean("RecommendationEngine");
            RecommendationContext context = (RecommendationContext) engine.getProcessorContext();
            MovielensDatasource dataSource = (MovielensDatasource) engine.getDataSource();
            ContextEnvelope contextEnvelope = engine.getContextEnvelope();

            //优化多线程情况下，内存模型，提高运行效率
            engine.getDataSource().reload();
            dataSource.setLazy(false);
            contextEnvelope.sampling(dataSource);
            contextEnvelope.setChanged(false);

            for (int itemI = 1359; itemI < endOfItemI; itemI++) {
                for (int itemJ = 1; itemJ < itemI; itemJ++) {
                    LoggerUtil.info(logger, "**************ItemI: " + itemI + "\tItemJ: " + itemJ
                                            + "********************************");
                    context.setItemI(String.valueOf(itemI));
                    context.setItemJ(String.valueOf(itemJ));
                    engine.excute();
                }
            }

        } catch (Exception e) {
            ExceptionUtil.caught(e, "FatherWorker 发生错误");
        }
    }

}
