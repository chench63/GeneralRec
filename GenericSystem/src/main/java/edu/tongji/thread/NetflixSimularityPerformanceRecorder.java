/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StopWatch;

import edu.tongji.cache.SimularityStreamCache;
import edu.tongji.context.ProcessorContextHelper;
import edu.tongji.function.Function;
import edu.tongji.model.Rating;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixSimularityPerformanceRecorder.java, v 0.1 2013-10-12 下午2:51:12 chenkh Exp $
 */
public class NetflixSimularityPerformanceRecorder implements Runnable {

    private int      iStart;

    private int      iEnd;

    /** 相似度计算函数*/
    private Function similarityFunction;

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        StopWatch stopWatch = new StopWatch();
        for (int i = iStart; i < iEnd; i++) {
            List<Rating> ratingOfI = SimularityStreamCache.get(String.valueOf(i));
            for (int j = 0; j < i; j++) {
                List<Rating> ratingOfJ = SimularityStreamCache.get(String.valueOf(j));
                List<Integer> valuesOfI = new ArrayList<Integer>();
                List<Integer> valuesOfJ = new ArrayList<Integer>();
                ProcessorContextHelper.doForgeRatingValues(ratingOfI, ratingOfJ, valuesOfI,
                    valuesOfJ, false);

                //=============================
                //性能测试开始
                //=============================
                stopWatch.start();
                similarityFunction.calculate(valuesOfI, valuesOfI);
                stopWatch.stop();
                //=============================
                //性能测试结束
                //=============================

                SimularityStreamCache.update(stopWatch.getLastTaskTimeMillis());
            }
        }

    }

    /**
     * Getter method for property <tt>iStart</tt>.
     * 
     * @return property value of iStart
     */
    public int getiStart() {
        return iStart;
    }

    /**
     * Setter method for property <tt>iStart</tt>.
     * 
     * @param iStart value to be assigned to property iStart
     */
    public void setiStart(int iStart) {
        this.iStart = iStart;
    }

    /**
     * Getter method for property <tt>iEnd</tt>.
     * 
     * @return property value of iEnd
     */
    public int getiEnd() {
        return iEnd;
    }

    /**
     * Setter method for property <tt>iEnd</tt>.
     * 
     * @param iEnd value to be assigned to property iEnd
     */
    public void setiEnd(int iEnd) {
        this.iEnd = iEnd;
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
