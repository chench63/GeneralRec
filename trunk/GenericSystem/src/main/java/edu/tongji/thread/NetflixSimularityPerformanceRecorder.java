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
import edu.tongji.context.PaillierProcessorContextHelper;
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

            for (int j = jStart; j < jEnd; j++) {
                List<Rating> ratingOfI = SimularityStreamCache.get(String.valueOf(i));
                List<Rating> ratingOfJ = SimularityStreamCache.get(String.valueOf(j));
                List<Number> valuesOfI = new ArrayList<Number>();
                List<Number> valuesOfJ = new ArrayList<Number>();
                ProcessorContextHelper.doForgeRatingValues(ratingOfI, ratingOfJ, valuesOfI,
                    valuesOfJ, false);

                List<Number> numeratorOfSim = new ArrayList<Number>();
                List<Number> denominatroOfSimAboutI = new ArrayList<Number>();
                List<Number> denominatroOfSimAboutJ = new ArrayList<Number>();
                PaillierProcessorContextHelper.forgeDataAsPearson(valuesOfI, valuesOfJ,
                    numeratorOfSim, denominatroOfSimAboutI, denominatroOfSimAboutJ);

                //记录点
                stopWatch.start();
                try {
                    similarityFunction.calculate(numeratorOfSim, denominatroOfSimAboutI,
                        denominatroOfSimAboutJ);
                } catch (Exception e) {
                    ExceptionUtil.caught(e, "i: " + i + " j: " + j);
                }
                stopWatch.stop();
            }
            //=============================
            //性能测试结束
            //=============================

        }

        SimularityStreamCache.update(stopWatch.getTotalTimeMillis());
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
