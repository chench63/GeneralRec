/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.configure.TestCaseConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.util.BeanUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.RandomUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author chench
 * @version $Id: SimularityStreamCache.java, v 0.1 2013-10-12 下午2:53:34 chench Exp $
 */
public final class SimularityStreamCache extends Observable {

    /** 读写锁 */
    private static final ReadWriteLock      lock           = new ReentrantReadWriteLock();

    /** logger */
    private final static Logger             logger         = Logger
                                                               .getLogger(LoggerDefineConstant.SERVICE_CACHE);
    /** 本地数据缓存*/
    private final static List<List<Rating>> ratingContext  = new ArrayList<List<Rating>>(17770);

    /** 运行时间*/
    private final static CacheStopWatch     catchStopWatch = new CacheStopWatch();

    /** 运行时间*/
    private static long                     runtimes       = 0;

    /**
     * NetflixRatingRecorder任务
     * 
     * @return
     */
    public static synchronized CacheTask task() {
        CacheTask task = new CacheTask(CacheTask.I, 1, CacheTask.I);

        //判断任务是否结束
        if (CacheTask.I > ConfigurationConstant.TASK_SIZE) {
            LoggerUtil.info(logger, "CacheTask  Completes.....");
            return null;
        }

        //更新任务
        CacheTask.I++;

        LoggerUtil.info(logger, "Release Task: " + task);
        return task;
    }

    /**
     * 累计计算运行时间
     * 
     * @param laps
     */
    public static synchronized void update(long laps) {
        runtimes += laps;
        LoggerUtil.info(logger, "运行时间: " + runtimes);
    }

    /**
     * 累计计算运行时间
     * 
     * @param laps
     */
    public static synchronized void update(CacheHolder cacheHolder) {
        catchStopWatch.put(cacheHolder);
    }

    /**
     * 快速添加评分记录
     * 
     * @param movieId   
     * @param ratings
     */
    public static void put(int movieId, List<Rating> ratings) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            ratingContext.add(ratings);
        } finally {
            writeLock.unlock();
            LoggerUtil.info(logger, (new StringBuilder("movieId ：")).append(movieId));
        }
    }

    /**
     * 添加新的评分记录,同时使用高斯噪声处理源数据，已保护用户隐私。
     * 
     * @param ratings
     */
    public static void putAndDisguise(int movieId, List<Rating> ratings, boolean isGaussian) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            for (int i = 0, j = ratings.size(); i < j; i++) {
                Rating rating = ratings.get(i);

                //破坏数据，加入高斯噪声0.67
                double disguisedValue = 0.0;
                if (isGaussian) {
                    disguisedValue = rating.getRating()
                                     - RandomUtil
                                         .nextGaussian(TestCaseConfigurationConstant.PERTURBATION_DOMAIN);
                } else {
                    disguisedValue = rating.getRating()
                                     - RandomUtil
                                         .nextDouble(TestCaseConfigurationConstant.PERTURBATION_DOMAIN);
                }

                //覆盖原数据
                RatingVO ratingVO = BeanUtil.toBeans(rating);
                ratingVO.put("DISGUISED_VALUE", disguisedValue);
                ratings.set(i, ratingVO);
            }

            ratingContext.add(ratings);
        } finally {
            writeLock.unlock();
            LoggerUtil.info(logger, (new StringBuilder("movieId ：")).append(movieId));
        }
    }

    /**
     * 此处直接得到List以后，其实后续的List的[读写操作]不是线程安全的。
     * 
     * @param movieId
     * @return
     */
    public static List<Rating> get(int movieId) {
        Lock readLock = lock.readLock();
        readLock.lock();

        try {
            //Movie_id [1, 17770]
            //对应的索引  [0, 17770-1]
            return ratingContext.get(movieId - 1);
        } finally {
            readLock.unlock();
        }
    }

}
