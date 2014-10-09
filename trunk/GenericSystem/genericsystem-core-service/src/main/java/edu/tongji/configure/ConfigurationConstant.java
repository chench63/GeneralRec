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
    public static String  USER_SEQ_FILE_PATH        = null;

    /** 相似度，文件路径*/
    public static String  SIMILARITY_FILE_PATH      = null;

    /** 是否加入随机噪声*/
    public static boolean IS_PERTURBATION           = false;

    /** 是否进行同态加密*/
    public static boolean IS_HOMOMORPHIC_ENCRYPTION = false;

    /**  主任务参数最大值 $Cache*/
    public static int     TASK_SIZE                 = 17770;

    /**  子任务容量 $CacheStopWatch : 使用分组，必须保证，分组数 >= 2*/
    public static int     SUB_TASK_SIZE             = 1777;

    /** 读取文件相似度：开始movieId号*/
    public static int     SIMLRTY_BEGIN             = 1;

    /** 读取文件相似度：结束movieId号*/
    public static int     SIMLRTY_END               = 885;

    /** 多线程数量*/
    public static int     THREAD_SIZE               = -1;

    /** NetflixCmpSimRecorder中，循环步长*/
    public static int     THREAD_FOR_STEP           = 800;

    /** 数值缓存容量上限*/
    public static float   NUMERIC_CACHE_LIMIT_GB    = 1.2f * 1024 * 1024 * 1024;

    /** 辅助缓存容量上限*/
    public static int     AUXILIARY_MEM_SIZE        = 100;

    /** 启用集约缓存*/
    public static boolean ENABLE_ECONOMICAL_CACHE   = false;

    /** 解析RatingVO相关文件*/
    public static boolean PARSER_RATINGVO           = true;

    /** 划分行数*/
    public static int     ROW                       = 2;

    /** 划分列数*/
    public static int     COLUMN                    = 2;

    /** 子矩阵的rank*/
    public static int     PARAM_K                   = 2;

    /**
     * Setter method for property <tt>iS_HOMOMORPHIC_ENCRYPTION</tt>.
     * 
     * @param IS_HOMOMORPHIC_ENCRYPTION value to be assigned to property iS_HOMOMORPHIC_ENCRYPTION
     */
    public static void setIS_HOMOMORPHIC_ENCRYPTION(boolean iS_HOMOMORPHIC_ENCRYPTION) {
        IS_HOMOMORPHIC_ENCRYPTION = iS_HOMOMORPHIC_ENCRYPTION;
    }

    /**
     * Setter method for property <tt>pARSER_RATINGVO</tt>.
     * 
     * @param PARSER_RATINGVO value to be assigned to property pARSER_RATINGVO
     */
    public static void setPARSER_RATINGVO(boolean pARSER_RATINGVO) {
        PARSER_RATINGVO = pARSER_RATINGVO;
    }

    /**
     * Setter method for property <tt>aUXILIARY_MEM_SIZE</tt>.
     * 
     * @param AUXILIARY_MEM_SIZE value to be assigned to property aUXILIARY_MEM_SIZE
     */
    public static void setAUXILIARY_MEM_SIZE(int aUXILIARY_MEM_SIZE) {
        AUXILIARY_MEM_SIZE = aUXILIARY_MEM_SIZE;
    }

    /**
     * Setter method for property <tt>eNABLE_ECONOMICAL_CACHE</tt>.
     * 
     * @param ENABLE_ECONOMICAL_CACHE value to be assigned to property eNABLE_ECONOMICAL_CACHE
     */
    public static void setENABLE_ECONOMICAL_CACHE(boolean eNABLE_ECONOMICAL_CACHE) {
        ENABLE_ECONOMICAL_CACHE = eNABLE_ECONOMICAL_CACHE;
    }

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

    /**
     * Setter method for property <tt>nUMERIC_CACHE_LIMIT_GB</tt>.
     * 
     * @param NUMERIC_CACHE_LIMIT_GB value to be assigned to property nUMERIC_CACHE_LIMIT_GB
     */
    public static void setNUMERIC_CACHE_LIMIT_GB(float nUMERIC_CACHE_LIMIT_GB) {
        NUMERIC_CACHE_LIMIT_GB = nUMERIC_CACHE_LIMIT_GB * 1024 * 1024 * 1024;
    }

    /**
     * Setter method for property <tt>rOW</tt>.
     * 
     * @param ROW value to be assigned to property rOW
     */
    public static void setROW(int rOW) {
        ROW = rOW;
    }

    /**
     * Setter method for property <tt>cOLUMN</tt>.
     * 
     * @param COLUMN value to be assigned to property cOLUMN
     */
    public static void setCOLUMN(int cOLUMN) {
        COLUMN = cOLUMN;
    }

    /**
     * Setter method for property <tt>pARAM_K</tt>.
     * 
     * @param PARAM_K value to be assigned to property pARAM_K
     */
    public static void setPARAM_K(int pARAM_K) {
        PARAM_K = pARAM_K;
    }

}
