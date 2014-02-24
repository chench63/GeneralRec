/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * 随机数生成工具类
 * 
 * @author chench
 * @version $Id: RandomUtil.java, v 0.1 2013-10-30 下午7:59:31 chench Exp $
 */
public final class RandomUtil {

    /** 随机数种子 */
    private static long   seed = System.currentTimeMillis();

    /** 随机数据生成类 */
    private static Random ran  = new Random(seed);

    /** 常量0 */
    private static double ZERO = 0.0;

    /**
     * 禁用构造函数
     */
    private RandomUtil() {

    }

    /**
     * 生成一个[leftSide, rightSide)的整数
     * 
     * @param leftSide
     * @param rightSide
     * @return
     */
    public static int nextInt(int leftSide, int rightSide) {
        int lowBound = leftSide;
        lowBound += ran.nextInt(rightSide - leftSide);

        return lowBound;
    }

    /**
     * 生成一个符合平均分布的双精度浮点数，且绝对值不大于边界
     * 
     * @param boundary
     * @return
     */
    public static double nextDouble(double boundary) {
        double result = ran.nextDouble() * boundary;
        return ran.nextDouble() > 0.5 ? result : -1 * result;
    }

    /**
     * 生成一个符合高斯分布的浮点数
     * 
     * @return
     */
    public static double nextGaussian() {
        return ran.nextGaussian();
    }

    /**
     * 生成一个符合高斯分布的浮点数；且这个数的绝对值不大于csi
     * 
     * 
     * @param csi   高斯浮点数的最大值
     * @return
     */
    public static double nextGaussian(double csi) {
        //最值为0时，无限制
        if (csi == ZERO) {
            return nextGaussian();
        }

        double nextGaussian = 0.0;
        while (Math.abs(nextGaussian = nextGaussian()) > csi)
            ;

        return nextGaussian;
    }

    /**
     * 生成一个符合设定正态分布的浮点数
     * 
     * @param mean
     * @param deviation
     * @return
     */
    public static double nextNormalDistribution(double mean, double deviation) {
        return (new NormalDistribution(mean, deviation)).sample();
    }

}
