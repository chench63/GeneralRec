/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import edu.tongji.cache.DataStreamTask;
import edu.tongji.cache.SimularityStreamCache;
import edu.tongji.context.ProcessorContextHelper;
import edu.tongji.function.Function;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixSimularityPerformanceRecorder.java, v 0.1 2013-10-12 下午2:51:12 chenkh Exp $
 */
public class NetflixSimularityPerformanceRecorder implements Runnable {

    /** 相似度计算函数*/
    private Function            similarityFunction;

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        LoggerUtil.debug(logger, "NetflixSimularityPerformanceRecorder 开始执行计算");

        DataStreamTask task = null;
        StopWatch stopWatch = new StopWatch();
        while ((task = SimularityStreamCache.task()) != null) {
            int i = task.i;
            int jStart = task.jStart;
            int jEnd = task.jEnd;

            //=============================
            //性能测试开始
            //=============================
            stopWatch.start();
            for (int j = jStart; j < jEnd; j++) {

                List<Rating> ratingOfI = SimularityStreamCache.get(String.valueOf(i));
                List<Rating> ratingOfJ = SimularityStreamCache.get(String.valueOf(j));
                List<Integer> valuesOfI = new ArrayList<Integer>();
                List<Integer> valuesOfJ = new ArrayList<Integer>();
                ProcessorContextHelper.doForgeRatingValues(ratingOfI, ratingOfJ, valuesOfI,
                    valuesOfJ, false);

                try {
                    similarityFunction.calculate(valuesOfI, valuesOfI);
                } catch (Exception e) {
                    ExceptionUtil.caught(e, "i: " + i + " j: " + j);
                }
            }
            //=============================
            //性能测试结束
            //=============================
            stopWatch.stop();
            SimularityStreamCache.update(stopWatch.getLastTaskTimeMillis());
        }

    }

    /**
     * Getter method for property <tt>similarityFunction</tt>.
     * 
     * @return property value of similarityFunction
     */
    public Function getSimilarityFunction() {
        return similarityFunction;
    }

    /**
     * Setter method for property <tt>similarityFunction</tt>.
     * 
     * @param similarityFunction value to be assigned to property similarityFunction
     */
    public void setSimilarityFunction(Function similarityFunction) {
        this.similarityFunction = similarityFunction;
    }

}
