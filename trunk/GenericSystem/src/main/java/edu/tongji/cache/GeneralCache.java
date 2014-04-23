/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 泛型数据缓存，通过CacheHolder与具体应用场景数据类型解耦。
 * 
 * @author chench
 * @version $Id: GeneralCache.java, v 0.1 2013-10-31 下午4:32:59 chench Exp $
 */
public final class GeneralCache {

    /** 读写锁 */
    private static final ReadWriteLock      lock         = new ReentrantReadWriteLock();

    /** logger */
    protected final static Logger           logger       = Logger
                                                             .getLogger(LoggerDefineConstant.SERVICE_CACHE);

    /** 构建二维，数值缓存*/
    private final static List<List<Number>> numericCache = new ArrayList<List<Number>>(
                                                             ConfigurationConstant.TASK_SIZE);

    /** 任务缓存*/
    protected final static List<CacheTask>  tasks        = new ArrayList<CacheTask>();

    /**
     * 获取任务
     * 
     * @return
     */
    public static synchronized CacheTask task() {
        if (tasks.isEmpty()) {
            LoggerUtil.info(logger, "CacheTask  Completes.....");
            return null;
        } else {
            return tasks.remove(0);
        }
    }

    /**
     * 添加任务
     * 
     * @param task
     */
    public static void store(CacheTask task) {
        tasks.add(task);
    }

    /**
     * 插入数值
     * 
     * @param x         横坐标
     * @param y         纵坐标
     * @param num       数值
     */
    public static void put(int x, int y, Number num) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            //首次使用初始化
            if (numericCache.isEmpty()) {
                for (int i = 0; i < ConfigurationConstant.TASK_SIZE; i++) {
                    numericCache.add(new ArrayList<Number>(1));
                }
            }

            List<Number> content = numericCache.get(x);
            //首次接触该x对应的List，
            //节约内存，对角初始化
            //x列，包含x-1行
            if (content.isEmpty()) {
                ((ArrayList<Number>) content).ensureCapacity(x);
                for (int i = 1; i < x; i++) {
                    content.add(0L);
                }
            }
            content.set(y, num);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 返回数值
     * 
     * @param x         横坐标
     * @param y         纵坐标
     * @return
     */
    public static Number get(int x, int y) {
        return numericCache.get(x).get(y);

    }

}
