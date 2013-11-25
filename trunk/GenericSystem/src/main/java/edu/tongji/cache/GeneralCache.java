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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 泛型数据缓存，通过CacheHolder与具体应用场景数据类型解耦。
 * 
 * @author chenkh
 * @version $Id: GeneralCache.java, v 0.1 2013-10-31 下午4:32:59 chenkh Exp $
 */
public final class GeneralCache {

    /** 读写锁 */
    private static final ReadWriteLock                  lock     = new ReentrantReadWriteLock();

    /** logger */
    private final static Logger                         logger   = Logger
                                                                     .getLogger(LoggerDefineConstant.SERVICE_CACHE);

    /** 本地数据缓存*/
    private final static Map<String, CacheHolder>       context  = new HashMap<String, CacheHolder>();

    /** 本地数据缓存*/
    private final static Map<String, List<CacheHolder>> contexts = new HashMap<String, List<CacheHolder>>();

    /** task任务链 */
    private static List<String>                         keySet   = null;

    /**
     * 获取多线程任务
     * <p>
     *  task分配任务，其实与具体的引用场景有很强的耦合，
     *  其实可以剥离开来，另做一个类。
     * </p>
     * @return
     */
    public static synchronized CacheTask task() {
        if (keySet == null) {
            keySet = new ArrayList<String>(contexts.keySet());
            LoggerUtil.info(logger, "GeneralCache  初始化测试用户数：" + keySet.size());
        } else if (keySet.isEmpty()) {
            LoggerUtil.debug(logger, "GeneralCache  任务结束.");
            return null;
        }

        String key = Collections.synchronizedList(keySet).remove(0);
        return new CacheTask(Integer.valueOf(key), 0, 0);
    }

    /**
     * 载入缓存
     * 
     * @param cacheHolder
     */
    public static void put(CacheHolder cacheHolder) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            String key = String.valueOf(CacheHolder.KEY);
            Collections.synchronizedMap(context).put(key, cacheHolder);
        } finally {
            writeLock.unlock();
            LoggerUtil.debug(logger, "缓存加载，对象: " + cacheHolder);
        }
    }

    /**
     * 载入缓存
     * 
     * @param cacheHolder
     */
    public static void put(List<CacheHolder> cacheHolders) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            for (CacheHolder cacheHolder : cacheHolders) {
                String key = String.valueOf(cacheHolder.get(CacheHolder.KEY));
                Collections.synchronizedMap(context).put(key, cacheHolder);
            }
        } finally {
            writeLock.unlock();
            LoggerUtil.debug(logger, "缓存完成加载, 加载量: " + cacheHolders.size());
        }
    }

    /**
     * 载入缓存
     * 
     * @param cacheHolder
     */
    public static void puts(List<CacheHolder> cacheHolders) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            for (CacheHolder cacheHolder : cacheHolders) {
                String key = String.valueOf(cacheHolder.get(CacheHolder.KEY));
                List<CacheHolder> holdersOfContext = contexts.get(key);
                if (holdersOfContext == null) {
                    holdersOfContext = new ArrayList<CacheHolder>();
                }
                holdersOfContext.add(cacheHolder);
                Collections.synchronizedMap(contexts).put(key, holdersOfContext);
            }
        } finally {
            writeLock.unlock();
            LoggerUtil.debug(logger, "缓存完成加载, 加载量: " + cacheHolders.size());
        }
    }

    /**
     * 读取缓存
     * 
     * @param key
     * @return
     */
    public static CacheHolder get(String key) {
        return context.get(key);
    }

    /**
     * 读取缓存
     * 
     * @param key
     * @return
     */
    public static List<CacheHolder> gets(String key) {
        return contexts.get(key);
    }
}
