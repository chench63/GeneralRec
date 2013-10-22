/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * Rating数据丢失比例计算数据缓存，
 *      多线程环境下，保证暂存数据
 * 
 * @author chenkh
 * @version $Id: DataStreamCache.java, v 0.1 2013-10-8 上午11:57:47 chenkh Exp $
 */
public final class DataStreamCache extends Observable {

    /** 数据缓存, 此处不使用Stack, 自行实现线程安全 */
    private static List<DataStreamHolder> stack  = new ArrayList<DataStreamHolder>();

    /** 读写锁 */
    private static final ReadWriteLock    lock   = new ReentrantReadWriteLock();

    /** logger */
    private final static Logger           logger = Logger
                                                     .getLogger(LoggerDefineConstant.SERVICE_CACHE);

    /**
     * 禁用构造函数
     */
    private DataStreamCache() {

    }

    /**
     * NetflixRatingDBReader任务
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
            LoggerUtil.info(logger, "DataStreamCache  任务结束.");
            return null;
        }

        //更新任务
        if (I == endJ) {
            CacheTask.I++;
            CacheTask.J = 1;
        } else {
            CacheTask.J = endJ;
        }
        return task;
    }

    /**
     * 获取缓存信息。
     * 
     * @return
     */
    public static DataStreamHolder get() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            //缓存为空返回Null
            if (stack.isEmpty()) {
                LoggerUtil.info(logger, "DataStreamCache 缓存容量为零");
                return null;
            }

            //获取缓存，并删除，此处使用并发控制，防止重复读
            DataStreamHolder dataStreamHolder = Collections.synchronizedList(stack).remove(0);
            return dataStreamHolder;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 填充缓存
     * 
     * @param dataStreamHolder
     * @return
     */
    public static boolean put(DataStreamHolder dataStreamHolder) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            if (willOverflow()) {
                LoggerUtil.info(logger, "DataStreamCache 缓存已满...");
                return false;
            }

            return Collections.synchronizedList(stack).add(dataStreamHolder);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 填充缓存
     * 
     * @param dataStreamHolders
     * @return
     */
    public static boolean put(List<DataStreamHolder> dataStreamHolders) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            if (willOverflow()) {
                LoggerUtil.info(logger, "DataStreamCache 缓存已满...");
                return false;
            }

            return Collections.synchronizedList(stack).addAll(dataStreamHolders);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 为保证效率，此函数非线程安全
     * 
     * @return
     */
    public static boolean willOverflow() {
        return stack.size() >= ConfigurationConstant.MAX_CACHE_SIZE;
    }
}
