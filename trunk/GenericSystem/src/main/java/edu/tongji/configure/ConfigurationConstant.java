/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.configure;

/**
 * 配置参数管理类。主要用于GenerateSim相关功能中。
 *  也可使用spring注入，提高代码的可复用度。
 * <p> 
 *  任务容量：<br>
 *      主任务参数[I, TASK_SIZE)<br>
 * </p>
 *  
 * @author chench
 * @version $Id: ConfigurationConstant.java, v 0.1 2013-10-16 上午10:51:00 chench Exp $
 */
public final class ConfigurationConstant {

    /**  最大缓存容量 $Cache*/
    public static int    MAX_CACHE_SIZE   = 10000;

    /**  单任务最大容量 $Cache*/
    public static int    SINGLE_TASK_SIZE = 1000;

    /**  主任务参数最大值 $Cache*/
    public static int    TASK_SIZE        = 885;

    /**  子任务容量 $CacheStopWatch : 使用分组，必须保证，分组数 >= 2*/
    public static int    SUB_TASK_SIZE    = 177;

    /**  主任务参数i, 分配任务式使用 $CacheTask*/
    public static int    I                = 2;

    /**  协任务参数j, 分配任务式使用 $CacheTask*/
    public static int    J                = 1;

    /** DB捞取数据：开始movieId号*/
    public static int    movieStart       = 1;

    /** DB捞取数据：结束movieId号*/
    public static int    movieEnd         = 885;

    /** DB捞取数据：开始时间轴*/
    public static String scrachTimeLine   = "2000-12-31 00:00:00";

    /** DB捞取数据：截止时间轴*/
    public static String endTimeLine      = "2005-12-31 00:00:00";

    /**
     * Setter method for property <tt>mAX_CACHE_SIZE</tt>.
     * 
     * @param MAX_CACHE_SIZE value to be assigned to property mAX_CACHE_SIZE
     */
    public static void setMAX_CACHE_SIZE(int mAX_CACHE_SIZE) {
        MAX_CACHE_SIZE = mAX_CACHE_SIZE;
    }

    /**
     * Setter method for property <tt>sINGLE_TASK_SIZE</tt>.
     * 
     * @param SINGLE_TASK_SIZE value to be assigned to property sINGLE_TASK_SIZE
     */
    public static void setSINGLE_TASK_SIZE(int sINGLE_TASK_SIZE) {
        SINGLE_TASK_SIZE = sINGLE_TASK_SIZE;
    }

    /**
     * Setter method for property <tt>tASK_SIZE</tt>.
     * 
     * @param TASK_SIZE value to be assigned to property tASK_SIZE
     */
    public static void setTASK_SIZE(int tASK_SIZE) {
        TASK_SIZE = tASK_SIZE;
    }

    /**
     * Setter method for property <tt>sUB_TASK_SIZE</tt>.
     * 
     * @param SUB_TASK_SIZE value to be assigned to property sUB_TASK_SIZE
     */
    public static void setSUB_TASK_SIZE(int sUB_TASK_SIZE) {
        SUB_TASK_SIZE = sUB_TASK_SIZE;
    }

    /**
     * Setter method for property <tt>i</tt>.
     * 
     * @param I value to be assigned to property i
     */
    public static void setI(int i) {
        I = i;
    }

    /**
     * Setter method for property <tt>j</tt>.
     * 
     * @param J value to be assigned to property j
     */
    public static void setJ(int j) {
        J = j;
    }

    /**
     * Setter method for property <tt>movieStart</tt>.
     * 
     * @param movieStart value to be assigned to property movieStart
     */
    public static void setMovieStart(int movieStart) {
        ConfigurationConstant.movieStart = movieStart;
    }

    /**
     * Setter method for property <tt>movieEnd</tt>.
     * 
     * @param movieEnd value to be assigned to property movieEnd
     */
    public static void setMovieEnd(int movieEnd) {
        ConfigurationConstant.movieEnd = movieEnd;
    }

    /**
     * Setter method for property <tt>scrachTimeLine</tt>.
     * 
     * @param scrachTimeLine value to be assigned to property scrachTimeLine
     */
    public static void setScrachTimeLine(String scrachTimeLine) {
        ConfigurationConstant.scrachTimeLine = scrachTimeLine;
    }

    /**
     * Setter method for property <tt>endTimeLine</tt>.
     * 
     * @param endTimeLine value to be assigned to property endTimeLine
     */
    public static void setEndTimeLine(String endTimeLine) {
        ConfigurationConstant.endTimeLine = endTimeLine;
    }

}
