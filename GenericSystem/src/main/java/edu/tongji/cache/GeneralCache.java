/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.List;

/**
 * 
 * @author chenkh
 * @version $Id: GeneralCache.java, v 0.1 2013-10-31 下午4:32:59 chenkh Exp $
 */
public class GeneralCache {

    /**
     * 获取多线程任务
     * 
     * @return
     */
    public static synchronized CacheTask task() {
        return null;
    }

    /**
     * 载入缓存
     * 
     * @param cacheHolder
     */
    public static void put(CacheHolder cacheHolder) {

    }

    /**
     * 载入缓存
     * 
     * @param cacheHolder
     */
    public static void puts(List<CacheHolder> cacheHolders) {

    }

    /**
     * 读取缓存
     * 
     * @param key
     * @return
     */
    public CacheHolder get(String key) {
        return null;
    }

    /**
     * 读取缓存
     * 
     * @param key
     * @return
     */
    public List<CacheHolder> gets(String key) {
        return null;
    }
}
