/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.configure;

/**
 * 配置参数管理类。
 *  也可使用spring注入，提高代码的可复用度。
 * <p> 
 *  任务容量：<br>
 *      主任务参数[I, TASK_SIZE)<br>
 *
 *      
 * </p>
 *  
 * @author chenkh
 * @version $Id: ConfigurationConstant.java, v 0.1 2013-10-16 上午10:51:00 chenkh Exp $
 */
public final class ConfigurationConstant {

    /**  最大缓存容量 $Cache*/
    public final static int    MAX_CACHE_SIZE   = 10000;

    /**  单任务最大容量 $Cache*/
    public final static int    SINGLE_TASK_SIZE = 1000;

    /**  主任务参数最大值 $Cache*/
    public final static int    TASK_SIZE        = 50;

    /**  子任务容量 $CacheStopWatch : 使用分组，必须保证，分组数 >= 2*/
    public final static int    SUB_TASK_SIZE    = 25;

    /**  主任务参数i, 分配任务式使用 $CacheTask*/
    public final static int    I                = 2;

    /**  协任务参数j, 分配任务式使用 $CacheTask*/
    public final static int    J                = 1;

    /** DB捞取数据：开始movieId号*/
    public final static int    movieStart       = 1;

    /** DB捞取数据：结束movieId号*/
    public final static int    movieEnd         = 177;

    /** DB捞取数据：开始时间轴*/
    public final static String scrachTimeLine   = "2000-12-31 00:00:00";

    /** DB捞取数据：截止时间轴*/
    public final static String endTimeLine      = "2005-12-31 00:00:00";
}
