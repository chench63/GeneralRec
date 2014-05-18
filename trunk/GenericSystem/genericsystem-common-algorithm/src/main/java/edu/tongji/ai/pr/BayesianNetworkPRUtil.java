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
        //1. 初始化  P[ O=on | H,I ]
        //      当indoor = 1时
        p_O_TI[0][0][1] = 1.0f;
        p_O_TI[1][0][1] = 0.0f;
        p_O_TI[0][1][1] = 1.0f;
        p_O_TI[1][1][1] = 0.0f;
        p_O_TI[0][2][1] = 5.0f / 6;
        p_O_TI[1][2][1] = 1.0f - 5.0f / 6;
        p_O_TI[0][3][1] = 1.0f / 2;
        p_O_TI[1][3][1] = 1.0f - 1.0f / 2;
        //      当indoor = 0时
        p_O_TI[0][0][0] = 1.0f;
        p_O_TI[0][1][0] = 1.0f;
        p_O_TI[0][2][0] = 1.0f;
        p_O_TI[0][3][0] = 1.0f;

        //2. 初始化  P[P | O]
        //      当On = 0时
        p_P_O[0][0] = 8.0f / 17;
        p_P_O[1][0] = 5.0f / 17;
        p_P_O[2][0] = 3.0f / 17;
        p_P_O[3][0] = 1.0f / 17;
        //      当On = 1时
        p_P_O[0][1] = 0.0f;
        p_P_O[1][1] = 0.0f;
        p_P_O[2][1] = 1.0f / 3;
        p_P_O[3][1] = 2.0f / 3;

        //3. 初始化  P[I]
        I[0] = 0.0f;
        I[1] = 1.0f;

        //4. 初始化  P[T]
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
        int t = 0;
        if (temperature >= 16.0d) {
            t = temperature >= 28.0d ? 3 : ((Double) ((temperature - 16.0d) / 6 + 1)).intValue();
        }
        //3.  功率信息
        //  离散化: (--, 16), [16, 22), [22, 28), [28, ++)
        int p = 0;
        if (power >= 150) {
            p = power >= 250.0d ? 3 : ((Double) ((power - 150.0d) / 50 + 1)).intValue();
        }

        double numerator = p_P_O[p][1] * p_O_TI[1][t][i] * T[t] * I[i];
        double denominator = numerator + p_P_O[p][0] * p_O_TI[0][t][i] * T[t] * I[i];
        return numerator / denominator;
    }
}
