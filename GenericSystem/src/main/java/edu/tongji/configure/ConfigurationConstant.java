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

    /** 用户序列，文件路径*/
    public static String  USER_SEQ_FILE_PATH   = null;

    /** 相似度，文件路径*/
    public static String  SIMILARITY_FILE_PATH = null;

    /** 是否加入随机噪声*/
    public static boolean IS_PERTURBATION      = false;

    /** 是否将数据转化为，按用户分类的文件*/
    public static boolean TABULATE_SEQ_USER    = false;

    /**  最大缓存容量 $Cache*/
    public static int     MAX_CACHE_SIZE       = 10000;

    /**  单任务最大容量 $Cache*/
    public static int     SINGLE_TASK_SIZE     = 1000;

    /**  主任务参数最大值 $Cache*/
    public static int     TASK_SIZE            = 17770;

    /**  子任务容量 $CacheStopWatch : 使用分组，必须保证，分组数 >= 2*/
    public static int     SUB_TASK_SIZE        = 1777;

    /**  主任务参数i, 分配任务式使用 $CacheTask*/
    public static int     I                    = 2;

    /**  协任务参数j, 分配任务式使用 $CacheTask*/
    public static int     J                    = 1;

    /** 读取文件相似度：开始movieId号*/
    public static int     SIMLRTY_BEGIN        = 1;

    /** 读取文件相似度：结束movieId号*/
    public static int     SIMLRTY_END          = 885;

    /** 读取用户序列：开始userId号*/
    public static int     USER_BEGIN           = 1;

    /** 读取用户序列：结束userId号*/
    public static int     USER_END             = 885;

    /** 多线程数量*/
    public static int     THREAD_SIZE          = -1;

    /** NetflixCmpSimRecorder中，循环步长*/
    public static int     THREAD_FOR_STEP      = 800;

    /**
     * Setter method for property <tt>tHREAD_FOR_STEP</tt>.
     * 
     * @param THREAD_FOR_STEP value to be assigned to property tHREAD_FOR_STEP
     */
    public static void setTHREAD_FOR_STEP(int tHREAD_FOR_STEP) {
        THREAD_FOR_STEP = tHREAD_FOR_STEP;
    }

    /**
     * Setter method for property <tt>tHREAD_SIZE</tt>.
     * 
     * @param THREAD_SIZE value to be assigned to property tHREAD_SIZE
     */
    public static void setTHREAD_SIZE(int tHREAD_SIZE) {
        THREAD_SIZE = tHREAD_SIZE;
    }

    /**
     * Setter method for property <tt>mAX_CACHE_SIZE</tt>.
     * 
     * @param MAX_CACHE_SIZE value to be assigned to property mAX_CACHE_SIZE
     */
    public static void setMAX_CACHE_SIZE(int mAX_CACHE_SIZE) {
        MAX_CACHE_SIZE = mAX_CACHE_SIZE;
    }

    /**
     * Setter method for property <tt>uSER_BEGIN</tt>.
     * 
     * @param USER_BEGIN value to be assigned to property uSER_BEGIN
     */
    public static void setUSER_BEGIN(int uSER_BEGIN) {
        USER_BEGIN = uSER_BEGIN;
    }

    /**
     * Setter method for property <tt>uSER_END</tt>.
     * 
     * @param USER_END value to be assigned to property uSER_END
     */
    public static void setUSER_END(int uSER_END) {
        USER_END = uSER_END;
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
     * Setter method for property <tt>sIMLRTY_BEGIN</tt>.
     * 
     * @param SIMLRTY_BEGIN value to be assigned to property sIMLRTY_BEGIN
     */
    public static void setSIMLRTY_BEGIN(int sIMLRTY_BEGIN) {
        SIMLRTY_BEGIN = sIMLRTY_BEGIN;
    }

    /**
     * Setter method for property <tt>sIMLRTY_END</tt>.
     * 
     * @param SIMLRTY_END value to be assigned to property sIMLRTY_END
     */
    public static void setSIMLRTY_END(int sIMLRTY_END) {
        SIMLRTY_END = sIMLRTY_END;
    }

    /**
     * Getter method for property <tt>sUB_TASK_SIZE</tt>.
     * 
     * @return property value of SUB_TASK_SIZE
     */
    public static int getSUB_TASK_SIZE() {
        return SUB_TASK_SIZE;
    }

    /**
     * Setter method for property <tt>tABULATE_SEQ_USER</tt>.
     * 
     * @param TABULATE_SEQ_USER value to be assigned to property tABULATE_SEQ_USER
     */
    public static void setTABULATE_SEQ_USER(boolean tABULATE_SEQ_USER) {
        TABULATE_SEQ_USER = tABULATE_SEQ_USER;
    }

    /**
     * Setter method for property <tt>uSER_SEQ_FILE_PATH</tt>.
     * 
     * @param USER_SEQ_FILE_PATH value to be assigned to property uSER_SEQ_FILE_PATH
     */
    public static void setUSER_SEQ_FILE_PATH(String uSER_SEQ_FILE_PATH) {
        USER_SEQ_FILE_PATH = uSER_SEQ_FILE_PATH;
    }

    /**
     * Setter method for property <tt>sIMILARITY_FILE_PATH</tt>.
     * 
     * @param SIMILARITY_FILE_PATH value to be assigned to property sIMILARITY_FILE_PATH
     */
    public static void setSIMILARITY_FILE_PATH(String sIMILARITY_FILE_PATH) {
        SIMILARITY_FILE_PATH = sIMILARITY_FILE_PATH;
    }

    /**
     * Setter method for property <tt>iS_PERTURBATION</tt>.
     * 
     * @param IS_PERTURBATION value to be assigned to property iS_PERTURBATION
     */
    public static void setIS_PERTURBATION(boolean iS_PERTURBATION) {
        IS_PERTURBATION = iS_PERTURBATION;
    }

}
