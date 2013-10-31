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
import java.util.Observable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.util.BeanUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.RandomUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author chenkh
 * @version $Id: SimularityStreamCache.java, v 0.1 2013-10-12 下午2:53:34 chenkh Exp $
 */
public final class SimularityStreamCache extends Observable {

    /** 读写锁 */
    private static final ReadWriteLock             lock           = new ReentrantReadWriteLock();

    /** logger */
    private final static Logger                    logger         = Logger
                                                                      .getLogger(LoggerDefineConstant.SERVICE_CACHE);
    /** 本地数据缓存*/
    private final static Map<String, List<Rating>> ratingContext  = new HashMap<String, List<Rating>>();

    /** 运行时间*/
    private final static CacheStopWatch            catchStopWatch = new CacheStopWatch();

    /** 运行时间*/
    private static long                            runtimes       = 0;

    /**
     * NetflixRatingRecorder任务
     * 
     * @return
     */
    public static synchronized CacheTask task() {
        int I = CacheTask.I;
        int J = CacheTask.J;
        int endJ = (J + ConfigurationConstant.SINGLE_TASK_SIZE < I) ? (J + ConfigurationConstant.SINGLE_TASK_SIZE)
            : I;
        CacheTask task = new CacheTask(I, J, endJ);

        //判断任务是否结束
        if (I == ConfigurationConstant.TASK_SIZE) {
            LoggerUtil.debug(logger, "SimularityStreamCache  任务结束.");
            return null;
        }

        //更新任务
        if (I == endJ) {
            CacheTask.I++;
            CacheTask.J = 1;
        } else {
            CacheTask.J = endJ;
        }

        LoggerUtil.debug(logger, "释放任务: " + task);
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
        catchStopWatch.check();
    }

    /**
     * 添加新的评分记录
     * 
     * @param rating
     */
    public static void put(Rating rating) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            String movieId = String.valueOf(rating.getMovieId());
            List<Rating> ratings = ratingContext.get(movieId);

            if (ratings == null) {
                ratings = new ArrayList<Rating>();
            }
            Collections.synchronizedCollection(ratings).add(rating);
            ratingContext.put(movieId, ratings);
        } finally {
            writeLock.unlock();
            LoggerUtil.info(logger, "缓存加载数据完毕，加载量：1");
        }
    }

    /**
     * 添加新的评分记录
     * 
     * @param ratings
     */
    public static void put(List<Rating> ratings) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            for (Rating rating : ratings) {
                String movieId = String.valueOf(rating.getMovieId());
                List<Rating> ratingsOfContext = ratingContext.get(movieId);

                if (ratingsOfContext == null) {
                    ratingsOfContext = new ArrayList<Rating>();
                }
                Collections.synchronizedCollection(ratingsOfContext).add(rating);
                ratingContext.put(movieId, ratingsOfContext);
            }
        } finally {
            writeLock.unlock();
            LoggerUtil.info(logger, "缓存加载数据完毕，加载量：" + ratings.size());
        }
    }

    /**
     * 添加新的评分记录,同时使用高斯噪声处理源数据，已保护用户隐私。
     * 
     * @param ratings
     */
    public static void putAndDisguise(List<Rating> ratings) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            for (Rating rating : ratings) {
                String movieId = String.valueOf(rating.getMovieId());
                List<Rating> ratingsOfContext = ratingContext.get(movieId);

                if (ratingsOfContext == null) {
                    ratingsOfContext = new ArrayList<Rating>();
                }

                //破坏数据，加入高斯噪声
                double disguisedValue = rating.getRating() - RandomUtil.nextGaussian(0.67);
                RatingVO ratingVO = BeanUtil.toBean(rating);
                ratingVO.put("DISGUISED_VALUE", disguisedValue);
                Collections.synchronizedCollection(ratingsOfContext).add(ratingVO);
                //载入缓存
                ratingContext.put(movieId, ratingsOfContext);
            }
        } finally {
            writeLock.unlock();
            LoggerUtil.info(logger, "缓存加载数据完毕，加载量：" + ratings.size());
        }
    }

    /**
     * 此处直接得到List以后，其实后续的List的[读写操作]不是线程安全的。
     * 
     * @param movieId
     * @return
     */
    public static List<Rating> get(String movieId) {
        Lock readLock = lock.readLock();
        readLock.lock();

        try {
            return ratingContext.get(movieId);
        } finally {
            readLock.unlock();
        }
    }

}
