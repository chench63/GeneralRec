/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import edu.tongji.cache.CacheHolder;
import edu.tongji.cache.CacheTask;
import edu.tongji.cache.SimularityStreamCache;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.context.PaillierProcessorContextHelper;
import edu.tongji.context.ProcessorContextHelper;
import edu.tongji.function.Function;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;
import edu.tongji.vo.SimilarityVO;

/**
 * 针对netflix数据集，计算相似度并持久化至文件
 * 
 * 
 * @author chench
 * @version $Id: NetflixSimularityPerformanceRecorder.java, v 0.1 2013-10-12 下午2:51:12 chench Exp $
 */
public class NetflixCmpSimRecorder implements Runnable {

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

        CacheTask task = null;

        while ((task = SimularityStreamCache.task()) != null) {
            int i = task.i;
            int jStart = task.jStart;
            int jEnd = task.jEnd;

            //=============================
            //性能测试开始
            //=============================
            StopWatch stopWatch = new StopWatch();
            long performance = 0L;
            List<List<Number>> numerators = new ArrayList<List<Number>>(jEnd - jStart);
            List<List<Number>> denominatroIs = new ArrayList<List<Number>>(jEnd - jStart);
            List<List<Number>> denominatroJs = new ArrayList<List<Number>>(jEnd - jStart);

            //1. 计算Pearson相似度的，分子和两个分母
            stopWatch.start();
            for (int j = jStart; j < jEnd; j++) {
                List<RatingVO> ratingOfI = SimularityStreamCache.get(i);
                List<RatingVO> ratingOfJ = SimularityStreamCache.get(j);
                List<Number> valuesOfI = new ArrayList<Number>();
                List<Number> valuesOfJ = new ArrayList<Number>();
                ProcessorContextHelper.forgeSymmetryRatingValues(ratingOfI, ratingOfJ, valuesOfI,
                    valuesOfJ);

                List<Number> numeratorOfSim = new ArrayList<Number>();
                List<Number> denominatroOfSimAboutI = new ArrayList<Number>();
                List<Number> denominatroOfSimAboutJ = new ArrayList<Number>();
                PaillierProcessorContextHelper.forgeDataAsPearson(valuesOfI, valuesOfJ,
                    numeratorOfSim, denominatroOfSimAboutI, denominatroOfSimAboutJ);

                //载入整体列表
                numerators.add(numeratorOfSim);
                denominatroIs.add(denominatroOfSimAboutI);
                denominatroJs.add(denominatroOfSimAboutJ);

            }
            stopWatch.stop();
            //使用RP算法，计算量由服务器承担，固记入
            if (ConfigurationConstant.IS_PERTURBATION) {
                performance += stopWatch.getLastTaskTimeMillis();
            }

            //2. 计算相似度
            stopWatch.start();
            List<SimilarityVO> sims = new ArrayList<SimilarityVO>(jEnd - jStart);
            for (int j = jStart; j < jEnd; j++) {
                try {
                    Number sim = similarityFunction.calculate(numerators.get(j - jStart),
                        denominatroIs.get(j - jStart), denominatroJs.get(j - jStart));

                    //记录日志
                    LoggerUtil.debug(logger, "I: " + i + " J: " + j + " sim: " + sim.doubleValue());
                    if (!Double.isNaN(sim.doubleValue())) {
                        sims.add(new SimilarityVO(i, j, sim.floatValue()));
                    } else {
                        LoggerUtil.warn(logger, "I: " + i + " J: " + j + " sim: NaN");
                    }
                } catch (Exception e) {
                    ExceptionUtil.caught(e, "i: " + i + " j: " + j);
                }

            }
            stopWatch.stop();
            performance += stopWatch.getLastTaskTimeMillis();
            //=============================
            //性能测试结束
            //=============================
            CacheHolder cacheHolder = new CacheHolder();
            cacheHolder.put(CacheHolder.ELAPSE, performance);
            cacheHolder.put(CacheHolder.MOVIE_ID, i);
            SimularityStreamCache.update(cacheHolder);

            //持久化文件
            //文件名 [0000001.txt]
            StringBuilder fileName = (new StringBuilder(ConfigurationConstant.SIMILARITY_FILE_PATH))
                .append(StringUtil.alignRight(String.valueOf(i), 7, FileUtil.ZERO_PAD_CHAR))
                .append(FileUtil.TXT_FILE_SUFFIX);
            ;
            StringBuilder content = new StringBuilder();
            for (SimilarityVO sim : sims) {
                content.append(sim.toString()).append(FileUtil.BREAK_LINE);
            }
            FileUtil.write(fileName.toString(), content.toString());
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
