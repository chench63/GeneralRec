/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import edu.tongji.cache.CacheHolder;
import edu.tongji.cache.CacheTask;
import edu.tongji.cache.SimilarityStreamCache;
import edu.tongji.context.PaillierProcessorContextHelper;
import edu.tongji.context.ProcessorContextHelper;
import edu.tongji.function.Function;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.PaillierUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author chench
 * @version $Id: NetflixSimularityPerformancePaillierRecorder.java, v 0.1 2013-10-18 下午2:41:08 chench Exp $
 */
public class NetflixCmpSimPaillierRecorder implements Runnable {

    /** 相似度计算函数*/
    private Function                 similarityFunction;

    /** 密文缓存*/
    public final static BigInteger[] CHIPHER_CACHE = new BigInteger[7];

    /** logger */
    private final static Logger      logger        = Logger
                                                       .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    //初始化缓存
    static {
        PaillierUtil.newInstance(512, 64);
        for (int i = 0; i < CHIPHER_CACHE.length - 1; i++) {
            CHIPHER_CACHE[i] = PaillierUtil.encryptions(BigInteger.valueOf(i));
        }

        CHIPHER_CACHE[CHIPHER_CACHE.length - 1] = PaillierUtil.encryptions(BigInteger.TEN);
    }

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        LoggerUtil.debug(logger, "NetflixSimularityPerformanceRecorder 开始执行计算");

        CacheTask task = null;

        while ((task = SimilarityStreamCache.task()) != null) {
            int i = task.i;
            int jStart = task.jStart;
            int jEnd = task.jEnd;

            //=============================
            //性能测试开始
            //=============================
            StopWatch stopWatch = new StopWatch();
            List<List<Number>> numerators = new ArrayList<List<Number>>(jEnd - jStart);
            List<List<Number>> denominatroIs = new ArrayList<List<Number>>(jEnd - jStart);
            List<List<Number>> denominatroJs = new ArrayList<List<Number>>(jEnd - jStart);

            stopWatch.start();
            //1. 计算Pearson相似度的，分子和两个分母
            for (int j = jStart; j < jEnd; j++) {
                List<RatingVO> ratingOfI = SimilarityStreamCache.get(i);
                List<RatingVO> ratingOfJ = SimilarityStreamCache.get(j);
                List<Number> valuesOfI = new ArrayList<Number>();
                List<Number> valuesOfJ = new ArrayList<Number>();
                ProcessorContextHelper.forgeSymmetryChipherValues(ratingOfI, ratingOfJ, valuesOfI,
                    valuesOfJ);

                List<Number> numeratorOfSim = new ArrayList<Number>();
                List<Number> denominatroOfSimAboutI = new ArrayList<Number>();
                List<Number> denominatroOfSimAboutJ = new ArrayList<Number>();
                PaillierProcessorContextHelper.forgePaillierDataAsPearson(valuesOfI, valuesOfJ,
                    numeratorOfSim, denominatroOfSimAboutI, denominatroOfSimAboutJ);

                //载入整体列表
                numerators.add(numeratorOfSim);
                denominatroIs.add(denominatroOfSimAboutI);
                denominatroJs.add(denominatroOfSimAboutJ);

            }

            //2. 计算相似度
            for (int j = jStart; j < jEnd; j++) {
                try {
                    similarityFunction.calculate(numerators.get(j - jStart),
                        denominatroIs.get(j - jStart), denominatroJs.get(j - jStart));
                } catch (Exception e) {
                    ExceptionUtil.caught(e, "i: " + i + " j: " + j);
                }

            }
            stopWatch.stop();
            //=============================
            //性能测试结束
            //=============================

            CacheHolder cacheHolder = new CacheHolder();
            cacheHolder.put(CacheHolder.ELAPSE, stopWatch.getTotalTimeMillis());
            cacheHolder.put(CacheHolder.MOVIE_ID, i);
            SimilarityStreamCache.update(cacheHolder);
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
