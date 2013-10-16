/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.configure;

/**
 * 配置参数管理类。
 *  也可使用spring注入，提高代码的可复用度。
 * @author chenkh
 * @version $Id: ConfigurationConstant.java, v 0.1 2013-10-16 上午10:51:00 chenkh Exp $
 */
public final class ConfigurationConstant {

    /**  最大缓存容量 */
    public final static int MAX_CACHE_SIZE   = 10000;

    /**  单任务最大容量 */
    public final static int SINGLE_TASK_SIZE = 1000;

    /**  任务容量 */
    public final static int TASK_SIZE        = 177;

    /** 主任务参数i, 分配任务式使用*/
    public final static int I                = 2;

    /** 协任务参数j, 分配任务式使用*/
    public final static int J                = 1;
}
