/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.configure;

/**
 * 
 * @author chench
 * @version $Id: TestCaseConfigurationConstant.java, v 0.1 31 Oct 2013 21:23:48 chench Exp $
 */
public final class TestCaseConfigurationConstant {

    /** 测试集大小 */
    public static int          TESTCASE_SIZE         = 200;

    /** 测试集左区间 */
    public static int          LEFT_SIDE             = 1;

    /** 测试集右区间 */
    public static int          RIGHT_SIDE            = 2649429;

    /** 测试集是否随机产生*/
    public static boolean      NEED_RANDOM_TESTCASE  = false;

    /** 测试集是否使用扰动效果*/
    public static boolean      IS_PERTURBATION       = false;

    /** 绕送是否使用高斯噪声 */
    public static boolean      IS_NORMAL             = false;

    /** 测试集，高斯分布扰动概率范围 0.67-1.96*/
    public static double       PERTURBATION_DOMAIN   = 0.67;

    /** 自定义测试集合*/
    public static String       TEST_CASE             = "700638, 65666, 2456884, 1877798, 33079, 1703932, 809671, 482597, 1771703"
                                                       + ",344444, 1025476, 1124058, 1561358, 1176808, 241923, 216623, 1402512, 722332, 2497412, 1341871, 283675, "
                                                       + "1446922, 2453475, 328595, 1778888, 427770, 2381092, 296253, 192650, 656431, 472804, "
                                                       + "165393, 2560090, 159639, 2002276, 1122405, 999027, 1735892, "
                                                       + "1948535, 1645137, 2553999, 2201630, 2229348, 2059528";
    /** 测试集使用的训练相似度*/
    public static String       TEST_CASE_SIMULARITY  = "";

    /** 分隔符正则表达式 */
    public final static String SAPERATOR_EXPRESSION  = "\\,";

    /** 載入相似度范围的右区间   *999999999*/
    public static int          SIMILARITY_RIGHT_SIDE = 885;

    /** 相似度数据标签*/
    public static String       SIMILARITY_TYPE       = null;

    /**
     * Setter method for property <tt>tESTCASE_SIZE</tt>.
     * 
     * @param TESTCASE_SIZE value to be assigned to property tESTCASE_SIZE
     */
    public static void setTESTCASE_SIZE(int tESTCASE_SIZE) {
        TESTCASE_SIZE = tESTCASE_SIZE;
    }

    /**
     * Setter method for property <tt>lEFT_SIDE</tt>.
     * 
     * @param LEFT_SIDE value to be assigned to property lEFT_SIDE
     */
    public static void setLEFT_SIDE(int lEFT_SIDE) {
        LEFT_SIDE = lEFT_SIDE;
    }

    /**
     * Setter method for property <tt>rIGHT_SIDE</tt>.
     * 
     * @param RIGHT_SIDE value to be assigned to property rIGHT_SIDE
     */
    public static void setRIGHT_SIDE(int rIGHT_SIDE) {
        RIGHT_SIDE = rIGHT_SIDE;
    }

    /**
     * Setter method for property <tt>nEED_RANDOM_TESTCASE</tt>.
     * 
     * @param NEED_RANDOM_TESTCASE value to be assigned to property nEED_RANDOM_TESTCASE
     */
    public static void setNEED_RANDOM_TESTCASE(boolean nEED_RANDOM_TESTCASE) {
        NEED_RANDOM_TESTCASE = nEED_RANDOM_TESTCASE;
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
     * Setter method for property <tt>iS_NORMAL</tt>.
     * 
     * @param IS_NORMAL value to be assigned to property iS_NORMAL
     */
    public static void setIS_NORMAL(boolean iS_NORMAL) {
        IS_NORMAL = iS_NORMAL;
    }

    /**
     * Setter method for property <tt>pERTURBATION_DOMAIN</tt>.
     * 
     * @param PERTURBATION_DOMAIN value to be assigned to property pERTURBATION_DOMAIN
     */
    public void setPERTURBATION_DOMAIN(double pERTURBATION_DOMAIN) {
        PERTURBATION_DOMAIN = pERTURBATION_DOMAIN;
    }

    /**
     * Setter method for property <tt>tEST_CASE</tt>.
     * 
     * @param TEST_CASE value to be assigned to property tEST_CASE
     */
    public static void setTEST_CASE(String tEST_CASE) {
        TEST_CASE = tEST_CASE;
    }

    /**
     * Setter method for property <tt>tEST_CASE_SIMULARITY</tt>.
     * 
     * @param TEST_CASE_SIMULARITY value to be assigned to property tEST_CASE_SIMULARITY
     */
    public static void setTEST_CASE_SIMULARITY(String tEST_CASE_SIMULARITY) {
        TEST_CASE_SIMULARITY = tEST_CASE_SIMULARITY;
    }

    /**
     * Setter method for property <tt>sIMILARITY_RIGHT_SIDE</tt>.
     * 
     * @param SIMILARITY_RIGHT_SIDE value to be assigned to property sIMILARITY_RIGHT_SIDE
     */
    public static void setSIMILARITY_RIGHT_SIDE(int sIMILARITY_RIGHT_SIDE) {
        SIMILARITY_RIGHT_SIDE = sIMILARITY_RIGHT_SIDE;
    }

    /**
     * Setter method for property <tt>sIMILARITY_TYPE</tt>.
     * 
     * @param SIMILARITY_TYPE value to be assigned to property sIMILARITY_TYPE
     */
    public static void setSIMILARITY_TYPE(String sIMILARITY_TYPE) {
        SIMILARITY_TYPE = sIMILARITY_TYPE;
    }

}
