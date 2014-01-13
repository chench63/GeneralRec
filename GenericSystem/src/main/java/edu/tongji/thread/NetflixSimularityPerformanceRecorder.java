/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import edu.tongji.cache.CacheHolder;
import edu.tongji.cache.CacheTask;
import edu.tongji.cache.SimularityStreamCache;
import edu.tongji.configure.TestCaseConfigurationConstant;
import edu.tongji.context.PaillierProcessorContextHelper;
import edu.tongji.context.ProcessorContextHelper;
import edu.tongji.dao.ValueOfItemsDAO;
import edu.tongji.function.Function;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.model.ValueOfItems;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chench
 * @version $Id: NetflixSimularityPerformanceRecorder.java, v 0.1 2013-10-12 下午2:51:12 chench Exp $
 */
public class NetflixSimularityPerformanceRecorder implements Runnable {

    /** 相似度计算函数*/
    private Function            similarityFunction;

    /** 相似度DAO */
    private ValueOfItemsDAO     valueOfItemsDAO;

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
            for (int j = jStart; j < jEnd; j++) {
                List<Rating> ratingOfI = SimularityStreamCache.get(String.valueOf(i));
                List<Rating> ratingOfJ = SimularityStreamCache.get(String.valueOf(j));
                List<Number> valuesOfI = new ArrayList<Number>();
                List<Number> valuesOfJ = new ArrayList<Number>();
                if (TestCaseConfigurationConstant.IS_PERTURBATION) {
                    //随机扰动对应的数据处理类
                    ProcessorContextHelper.forgeRandomizedPerturbationRatingValues(ratingOfI,
                        ratingOfJ, valuesOfI, valuesOfJ, TestCaseConfigurationConstant.IS_NORMAL);
                } else {
                    ProcessorContextHelper.forgeSymmetryRatingValues(ratingOfI, ratingOfJ,
                        valuesOfI, valuesOfJ);
                }

                stopWatch.start();
                List<Number> numeratorOfSim = new ArrayList<Number>();
                List<Number> denominatroOfSimAboutI = new ArrayList<Number>();
                List<Number> denominatroOfSimAboutJ = new ArrayList<Number>();
                PaillierProcessorContextHelper.forgeDataAsPearson(valuesOfI, valuesOfJ,
                    numeratorOfSim, denominatroOfSimAboutI, denominatroOfSimAboutJ);
                stopWatch.stop();

                //记录点
                stopWatch.start();
                try {
                    Number sim = similarityFunction.calculate(numeratorOfSim,
                        denominatroOfSimAboutI, denominatroOfSimAboutJ);
                    LoggerUtil.debug(logger, "I: " + i + " J: " + j + " sim: " + sim.doubleValue());
                    if (valueOfItemsDAO != null & !Double.isNaN(sim.doubleValue())) {
                        persistence(i, j, sim.doubleValue());
                    }
                } catch (Exception e) {
                    ExceptionUtil.caught(e, "i: " + i + " j: " + j);
                }
                stopWatch.stop();
            }
            //=============================
            //性能测试结束
            //=============================
            CacheHolder cacheHolder = new CacheHolder();
            cacheHolder.put("ELAPS", stopWatch.getTotalTimeMillis());
            cacheHolder.put("MOVIE_ID", i);
            SimularityStreamCache.update(cacheHolder);

        }

    }

    /**
     * 持久化至数据库
     * 
     * @param i
     * @param j
     * @param sim
     */
    public void persistence(int i, int j, double sim) {
        ValueOfItems valueOfItem = new ValueOfItems();
        valueOfItem.setItemI(String.valueOf(i));
        valueOfItem.setItemJ(String.valueOf(j));
        valueOfItem.setValue(sim);
        valueOfItem.setFunctionName(TestCaseConfigurationConstant.SIMILARITY_TYPE);
        valueOfItem.setGMT_CREATE(Date.valueOf("2005-12-31"));
        valueOfItemsDAO.insert(valueOfItem);
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

    /**
     * Getter method for property <tt>valueOfItemsDAO</tt>.
     * 
     * @return property value of valueOfItemsDAO
     */
    public ValueOfItemsDAO getValueOfItemsDAO() {
        return valueOfItemsDAO;
    }

    /**
     * Setter method for property <tt>valueOfItemsDAO</tt>.
     * 
     * @param valueOfItemsDAO value to be assigned to property valueOfItemsDAO
     */
    public void setValueOfItemsDAO(ValueOfItemsDAO valueOfItemsDAO) {
        this.valueOfItemsDAO = valueOfItemsDAO;
    }

}
