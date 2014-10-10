/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.recommendation.thread;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import edu.tongji.cache.CacheHolder;
import edu.tongji.cache.CacheTask;
import edu.tongji.cache.SimilarityStreamCache;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.context.PaillierProcessorContextHelper;
import edu.tongji.context.ProcessorContextHelper;
import edu.tongji.function.Function;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.PaillierUtil;
import edu.tongji.vo.RatingVO;
import edu.tongji.vo.SimilarityVO;

/**
 * 
 * @author Hanke Chen
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

            StopWatch stopWatch = new StopWatch();
            long performance = 0L;
            List<SimilarityVO> sims = new ArrayList<SimilarityVO>(jEnd - jStart);

            for (int forStart = jStart, forEnd = 0; forStart <= jEnd;) {
                //计算本次循环结束位
                forEnd = ((forEnd = (forStart + ConfigurationConstant.THREAD_FOR_STEP)) > jEnd) ? jEnd
                    : forEnd;

                //=============================
                //性能测试开始
                //=============================
                //1. 计算Pearson相似度的，分子和两个分母
                //[forStart, forEnd]
                stopWatch.start();
                List<List<Number>> numerators = new ArrayList<List<Number>>();
                List<List<Number>> denominatroIs = new ArrayList<List<Number>>();
                List<List<Number>> denominatroJs = new ArrayList<List<Number>>();
                cmpElemtInner(i, forStart, forEnd, numerators, denominatroIs, denominatroJs);
                stopWatch.stop();
                //使用RP算法，计算量由服务器承担，固记入
                if (ConfigurationConstant.IS_PERTURBATION) {
                    performance += stopWatch.getLastTaskTimeMillis();
                }

                //2. 计算相似度
                //[forStart, forEnd]
                stopWatch.start();
                cmpSimlrtyInner(i, forStart, forEnd, numerators, denominatroIs, denominatroJs, sims);
                stopWatch.stop();
                performance += stopWatch.getLastTaskTimeMillis();
                //=============================
                //性能测试结束
                //=============================

                //计算下次循环的开始位置
                forStart = (forEnd == jEnd) ? Integer.MAX_VALUE : forEnd;
            }

            CacheHolder cacheHolder = new CacheHolder();
            cacheHolder.put(CacheHolder.ELAPSE, performance);
            cacheHolder.put(CacheHolder.MOVIE_ID, i);
            SimilarityStreamCache.update(cacheHolder);
        }

    }

    /**
     * 计算分子分母数值, [jStart, jEnd]
     * 
     * @param i                 item_i
     * @param jStart            item_j 起始
     * @param jEnd              item_j 结束
     * @param numerators        分子向量
     * @param denominatroIs     分母向量
     * @param denominatroJs     分母向量
     */
    protected void cmpElemtInner(int i, int jStart, int jEnd, List<List<Number>> numerators,
                                 List<List<Number>> denominatroIs, List<List<Number>> denominatroJs) {
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
    }

    /**
     * 计算相似度
     * 
     * @param i                 item_i
     * @param jStart            item_j 起始
     * @param jEnd              item_j 结束
     * @param numerators        分子向量
     * @param denominatroIs     分母向量
     * @param denominatroJs     分母向量
     * @param sims              相似度数组
     */
    protected void cmpSimlrtyInner(int i, int jStart, int jEnd, List<List<Number>> numerators,
                                   List<List<Number>> denominatroIs,
                                   List<List<Number>> denominatroJs, List<SimilarityVO> sims) {
        for (int j = jStart; j < jEnd; j++) {
            try {
                similarityFunction.calculate(numerators.get(j - jStart),
                    denominatroIs.get(j - jStart), denominatroJs.get(j - jStart));
            } catch (Exception e) {
                ExceptionUtil.caught(e, "i: " + i + " j: " + j);
            }

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
