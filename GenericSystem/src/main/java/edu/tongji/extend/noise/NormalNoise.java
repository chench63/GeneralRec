/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.noise;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * 
 * @author chench
 * @version $Id: NormalDistributionNoise.java, v 0.1 2014-2-24 下午4:18:10 chench Exp $
 */
public class NormalNoise implements Noise {

    /** 正态分布实体*/
    protected NormalDistribution stat;

    /**
     * 构造函数
     */
    public NormalNoise() {

    }

    /**
     * 构造函数
     * 
     * @param mean                  均值
     * @param standardDeviation     标准差
     * @param domain                值域
     */
    public NormalNoise(double mean, double standardDeviation) {
        this.stat = new NormalDistribution(mean, standardDeviation);
    }

    /** 
     * @see edu.tongji.extend.noise.Noise#random()
     */
    @Override
    public double random() {
        return stat.sample();
    }

    /** 
     * @see edu.tongji.extend.noise.Noise#perturb(double)
     */
    @Override
    public double perturb(double input) {
        return stat.sample() + input;
    }

    /** 
     * @see edu.tongji.extend.noise.Noise#getName()
     */
    @Override
    public String getName() {
        return "正态分布。";
    }

    /**
     * 更新正态分布
     * 
     * @param mean                  均值
     * @param standardDeviation     标准差
     * @param domain                值域
     */
    public void update(double mean, double standardDeviation) {
        this.stat = new NormalDistribution(mean, standardDeviation);
    }

    /**
     * Getter method for property <tt>mean</tt>.
     * 
     * @return property value of mean
     */
    public double getMean() {
        return stat.getMean();
    }

    /**
     * Getter method for property <tt>deviation</tt>.
     * 
     * @return property value of deviation
     */
    public double getStandardDeviation() {
        return stat.getStandardDeviation();
    }

    /**
     * Getter method for property <tt>deviation</tt>.
     * 
     * @return property value of deviation
     */
    public double getDeviation() {
        return Math.pow(stat.getStandardDeviation(), 2.0d);
    }

}
