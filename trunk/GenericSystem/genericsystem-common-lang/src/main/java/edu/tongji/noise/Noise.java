/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.noise;

/**
 * 噪声
 * 
 * @author Hanke Chen
 * @version $Id: Noise.java, v 0.1 2014-2-24 下午4:17:13 chench Exp $
 */
public interface Noise {

    /**
     * 生成随机数
     * 
     * @return
     */
    public double random();

    /**
     * 返回扰动结果
     * 
     * @param input
     * @return
     */
    public double perturb(double input);

    /**
     * 获得标准差
     * 
     * @return
     */

    public double standardDeviation();

    /**
     * 获得均值期望
     * 
     * @return
     */
    public double mean();

    /**
     * 噪声名称
     * 
     * @return
     */
    public String getName();
}
