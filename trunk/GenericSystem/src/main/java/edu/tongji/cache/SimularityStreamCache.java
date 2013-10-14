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

    private final static Map<String, List<Rating>> ratingContext = new HashMap<String, List<Rating>>();

    private static long                            runtimes      = 0;

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
