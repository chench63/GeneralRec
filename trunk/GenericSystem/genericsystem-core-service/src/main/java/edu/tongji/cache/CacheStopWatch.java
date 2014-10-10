/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;

/**
 * 适用于多线程环境下的，计时累加器。
 * 
 * @author Hanke Chen
 * @version $Id: CacheStopWatch.java, v 0.1 2013-1上午11:38:59 chench Exp $
 */
public final class CacheStopWatch {

    /** 运行时间存储上下文：按子任务划分*/
    private final List<DescriptiveStatistics> stopWatchContext = new ArrayList<DescriptiveStatistics>(
                                                                   10);

    /** logger */
    private final static Logger               logger           = Logger
                                                                   .getLogger(LoggerDefineConstant.SERVICE_CORE);

    /**
     * 添加已经完成的运行时间
     * 
     * @param cacheHolder   通用信息载体
     */
    public void put(CacheHolder cacheHolder) {
        if (stopWatchContext.isEmpty()) {
            //初始化
            for (int i = 0; i < ConfigurationConstant.TASK_SIZE
                                / ConfigurationConstant.SUB_TASK_SIZE; i++) {
                stopWatchContext.add(new DescriptiveStatistics());
            }
        }

        long elapse = (long) cacheHolder.get(CacheHolder.ELAPSE);
        int movieId = ((Integer) cacheHolder.get(CacheHolder.MOVIE_ID)).intValue();
        //movie_id [2, TASK_SIZE]
        int indexOfContext = (movieId - 1) / ConfigurationConstant.SUB_TASK_SIZE;

        //1. 获取上下文,并记数据
        DescriptiveStatistics stat = null;
        try {
            stat = stopWatchContext.get(indexOfContext);
        } catch (IndexOutOfBoundsException e) {
            stat = new DescriptiveStatistics();
            stopWatchContext.add(stat);
        } finally {
            stat.addValue(elapse);
        }

        //2. 控制日志
        if (stat.getN() == ConfigurationConstant.SUB_TASK_SIZE) {
            //Movie_id [1777 + 1, 17770] 段日志
            stat.addValue(stopWatchContext.get(indexOfContext - 1).getSum());

            //输出日志
            LoggerUtil.info(
                logger,
                (new StringBuilder("SubTask："))
                    .append(indexOfContext)
                    .append(" Completes over：")
                    .append(
                        StringUtil.alignRight(
                            String.valueOf(Double.valueOf(stat.getSum()).longValue()), 20)));
        } else if ((indexOfContext == 0)
                   && (stat.getN() == (ConfigurationConstant.SUB_TASK_SIZE - 1))) {
            //Movie_id [2, 1777] 段日志
            //输出日志
            LoggerUtil.info(
                logger,
                (new StringBuilder("SubTask："))
                    .append(indexOfContext)
                    .append(" Completes over：")
                    .append(
                        StringUtil.alignRight(
                            String.valueOf(Double.valueOf(stat.getSum()).longValue()), 20)));
        }
    }

}
