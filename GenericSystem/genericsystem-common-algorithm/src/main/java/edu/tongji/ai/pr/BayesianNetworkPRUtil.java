/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ai.pr;

/**
 * 贝叶斯网络识别网络;<br/>
 * 采取硬编码
 * 
 * @author chench
 * @version $Id: BayesianNetworkPRUtil.java, v 0.1 15 May 2014 21:58:58 chench Exp $
 */
public final class BayesianNetworkPRUtil {

    /** 给定温度和在家条件下，空调运行的条件概率  P[ O=on | H,I ] */
    protected static final float[][][] p_O_TI = new float[2][4][2];

    /** 给定空调运行情况, 耗电读数的条件概率 P[P | O] */
    protected static final float[][]   p_P_O  = new float[4][2];

    /** 温度概率分布*/
    protected static final float[]     T      = new float[4];

    /** 在家概率分布*/
    protected static final float[]     I      = new float[2];

    static {
        //1. 代码自动生成部分
        p_O_TI[0][0][1] = 1.0f;
        p_O_TI[0][1][1] = 0.9625f;
        p_O_TI[0][2][1] = 0.88076925f;
        p_O_TI[0][3][1] = 1.0f;
        p_O_TI[1][0][1] = 0.0f;
        p_O_TI[1][1][1] = 0.0375f;
        p_O_TI[1][2][1] = 0.11923077f;
        p_O_TI[1][3][1] = 0.0f;
        p_O_TI[0][0][0] = 1.0f;
        p_O_TI[0][1][0] = 1.0f;
        p_O_TI[0][2][0] = 1.0f;
        p_O_TI[0][3][0] = 1.0f;

        p_P_O[0][0] = 0.9452174f;
        p_P_O[0][1] = 0.0f;
        p_P_O[1][0] = 0.03652174f;
        p_P_O[1][1] = 0.26086956f;
        p_P_O[2][0] = 0.010434783f;
        p_P_O[2][1] = 0.32608697f;
        p_P_O[3][0] = 0.007826087f;
        p_P_O[3][1] = 0.41304347f;

        //2. 初始化  P[I]
        I[0] = 0.0f;
        I[1] = 1.0f;

        //3. 初始化  P[T]
        T[0] = 0.30769232f;
        T[1] = 0.32967034f;
        T[2] = 0.21978022f;
        T[3] = 0.14285715f;
    }

    /**
     * 
     * 
     * @param indoor        是否在家
     * @param temperature   温度
     * @param power           功率
     * @return
     */
    public static double cp(int indoor, double temperature, double power) {
        //1. 是否在家
        int i = indoor;
        //2. 温度信息
        //  离散化: (--, 16), [16, 22), [22, 28), [28, ++)
        int t = hotType(Double.valueOf(temperature).floatValue());
        //3.  功率信息
        //  离散化: (--, 16), [16, 22), [22, 28), [28, ++)
        int p = powerType(Double.valueOf(power).floatValue());

        double numerator = p_P_O[p][1] * p_O_TI[1][t][i] * T[t] * I[i];
        double denominator = numerator + p_P_O[p][0] * p_O_TI[0][t][i] * T[t] * I[i];
        return numerator / denominator;
    }

    /**
     * 离散化温度值<br/>
     * (-,16)      : 0<br/>
     * [16, 22)    : 1<br/>
     * [22, 28)    : 2<br/>
     * [28, +)     : 3<br/>
     * 
     * @param hot
     * @return
     */
    protected static int hotType(float hot) {
        if (hot < 16.0f) {
            return 0;
        }

        int type = 1;
        return (type += Float.valueOf((hot - 16) / 6).intValue()) > 3 ? 3 : type;
    }

    /**
     * 离散化能耗值<br/>
     * 0: (-, 400）<br/>
     * 1： [400， 800）<br/>
     * 2: [800, 1200)<br/>
     * 3: [1200, +)<br/>
     * 
     * @param power
     * @return
     */
    protected static int powerType(float power) {
        if (power < 400) {
            return 0;
        }

        int type = 1;
        return (type += Float.valueOf((power - 400) / 400).intValue()) > 3 ? 3 : type;
    }
}
