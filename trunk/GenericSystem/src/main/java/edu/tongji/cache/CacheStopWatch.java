/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.function.FunctionHelper;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 适用于多线程环境下的，计时累加器。
 * 
 * @author chenkh
 * @version $Id: CacheStopWatch.java, v 0.1 2013-1上午11:38:59 chenkh Exp $
 */
public final class CacheStopWatch {

    /** 运行时间存储上下文：按子任务划分*/
    private final Map<Integer, List<Long>> stopWatchContext   = new HashMap<Integer, List<Long>>();

    /** 当前任务编号*/
    private Integer                        indexOfCurrentTask = 0;

    /** logger */
    private final static Logger            logger             = Logger
                                                                  .getLogger(LoggerDefineConstant.SERVICE_CACHE);

    /**
     * 添加已经完成的运行时间
     * 
     * @param cacheHolder
     */
    public void put(CacheHolder cacheHolder) {
        Long elaps = (long) cacheHolder.get("ELAPS");
        Integer indexOfContext = (Integer) cacheHolder.get("MOVIE_ID")
                                 / ConfigurationConstant.SUB_TASK_SIZE;

        List<Long> elapses = stopWatchContext.get(indexOfContext);
        if (elapses == null) {
            elapses = new ArrayList<Long>();
        }
        Collections.synchronizedCollection(elapses).add(elaps);
        stopWatchContext.put(indexOfContext, elapses);
    }

    /**
     * 递归检查，是否已经完成所有的输出。
     */
    public void check() {
        if (indexOfCurrentTask > (ConfigurationConstant.TASK_SIZE / ConfigurationConstant.SUB_TASK_SIZE)) {
            //分组索引号 > 最大分组号，说明已完成所有输出。
            return;
        }

        List<Long> elapses = stopWatchContext.get(indexOfCurrentTask);
        if (elapses == null) {
            return;
        } else if (indexOfCurrentTask == (ConfigurationConstant.TASK_SIZE / ConfigurationConstant.SUB_TASK_SIZE)
                   && elapses.size() != (ConfigurationConstant.TASK_SIZE
                                         % ConfigurationConstant.SUB_TASK_SIZE)) {
            //最后一个分组任务时，
            //满足：分组个数为   ConfigurationConstant.TASK_SIZE % ConfigurationConstant.SUB_TASK_SIZE 数据时，标志子任务结束
            return;
        } else if (indexOfCurrentTask == 0
                   && elapses.size() != ConfigurationConstant.SUB_TASK_SIZE - 2) {
            //第一个分组任务是，
            //应该任务从2开始分配，少了0,1两个参数
            //满足：分组个数为 ConfigurationConstant.SUB_TASK_SIZE - 2，标志子任务结束
            return;
        } else if (indexOfCurrentTask != 0
                   && indexOfCurrentTask != (ConfigurationConstant.TASK_SIZE / ConfigurationConstant.SUB_TASK_SIZE)
                   && elapses.size() != ConfigurationConstant.SUB_TASK_SIZE) {
            //介于头尾分组之间的分组，
            //满足：分子格式为 ConfigurationConstant.SUB_TASK_SIZE，标志子任务结束
            return;
        }

        Long total = FunctionHelper.sumValue(elapses).longValue();
        LoggerUtil.info(logger, "Task: " + indexOfCurrentTask.intValue() + " Finished. Consume: "
                                + total);
        //递归继续坚持
        indexOfCurrentTask++;
        check();
    }

}
