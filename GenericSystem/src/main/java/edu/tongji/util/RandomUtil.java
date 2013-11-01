/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import java.util.Random;

/**
 * 随机数生成工具类
 * 
 * @author chenkh
 * @version $Id: RandomUtil.java, v 0.1 2013-10-30 下午7:59:31 chenkh Exp $
 */
public final class RandomUtil {

    /** 随机数种子 */
    private static long   seed = System.currentTimeMillis();

    /** 随机数据生成类 */
    private static Random ran  = new Random(seed);

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
        double nextGaussian = 0.0;
        while (Math.abs(nextGaussian = nextGaussian()) > csi)
            ;
        return nextGaussian;
    }
}