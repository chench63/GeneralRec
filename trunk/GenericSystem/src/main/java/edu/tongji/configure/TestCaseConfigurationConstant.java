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
    public final static int     TESTCASE_SIZE        = 200;

    /** 测试集左区间 */
    public final static int     LEFT_SIDE            = 1;

    /** 测试集右区间 */
    public final static int     RIGHT_SIDE           = 2649429;

    /** 测试集是否随机产生*/
    public final static boolean NEED_RANDOM_TESTCASE = false;

    /** 测试集是否使用扰动效果*/
    public final static boolean IS_PERTURBATION      = true;
    
    /** 绕送是否使用高斯噪声 */
    public final static boolean IS_NORMAL      = false;

    /** 测试集，高斯分布扰动概率范围 0.67-1.96*/
    public final static double  PERTURBATION_DOMAIN  = 1.96;

    /** 自定义测试集合*/
    public final static String  TEST_CASE            = "700638, 65666, 2456884, 1877798, 33079, 1703932, 809671, 482597, 1771703"
                                                       + ",344444, 1025476, 1124058, 1561358, 1176808, 241923, 216623, 1402512, 722332, 2497412, 1341871, 283675, "
                                                       + "1446922, 2453475, 328595, 1778888, 427770, 2381092, 296253, 192650, 656431, 472804, "
                                                       + "165393, 2560090, 159639, 2002276, 1122405, 999027, 1735892, "
                                                       + "1948535, 1645137, 2553999, 2201630, 2229348, 2059528";

    /** 分隔符正则表达式 */
    public static String        SAPERATOR_EXPRESSION = "\\,";

}
