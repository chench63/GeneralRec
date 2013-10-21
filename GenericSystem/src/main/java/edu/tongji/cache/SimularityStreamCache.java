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
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: SimularityStreamCache.java, v 0.1 2013-10-12 下午2:53:34 chenkh Exp $
 */
public final class SimularityStreamCache extends Observable {

    /** 读写锁 */
    private static final ReadWriteLock             lock          = new ReentrantReadWriteLock();

    /** logger */
    private final static Logger                    logger        = Logger
                                                                     .getLogger(LoggerDefineConstant.SERVICE_CACHE);
    /** 本地数据缓存*/
    private final static Map<String, List<Rating>> ratingContext = new HashMap<String, List<Rating>>();

    /** 运行时间*/
    private static long                            runtimes      = 0;

    /**
     * NetflixRatingRecorder任务
     * 
     * @return
     */
    public static synchronized DataStreamTask task() {
        int I = DataStreamTask.I;
        int J = DataStreamTask.J;
        int endJ = (J + ConfigurationConstant.SINGLE_TASK_SIZE < I) ? (J + ConfigurationConstant.SINGLE_TASK_SIZE)
            : I;
        DataStreamTask task = new DataStreamTask(I, J, endJ);

        //判断任务是否结束
        if (I == ConfigurationConstant.TASK_SIZE) {
            LoggerUtil.info(logger, "DataStreamCache  任务结束.");
            return null;
        }

        //更新任务
        if (I == endJ) {
            DataStreamTask.I++;
            DataStreamTask.J = 1;
        } else {
            DataStreamTask.J = endJ;
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
     * 添加新的评分记录
     * 
     * @param ratings
     */
    public static void puts(List<Rating> ratings) {
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
