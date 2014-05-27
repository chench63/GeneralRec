/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.noise;

import org.apache.commons.math3.distribution.NormalDistribution;

import edu.tongji.noise.support.NoiseParamSupport;

/**
 * 正态分布噪声类
 * 
 * @author chench
 * @version $Id: NormalDistributionNoise.java, v 0.1 2014-2-24 下午4:18:10 chench Exp $
 */
public class NormalNoise implements Noise {

    /** 正态分布实体*/
    protected NormalDistribution normal;

    /** 默认icf(1)对应的值*/
    protected float              icf_limit = Float.MAX_VALUE;

    /**
     * 构造函数
     */
    public NormalNoise() {

    }

    /**
     * 构造函数
     * 
     * @param noiseParamSupport
     */
    public NormalNoise(NoiseParamSupport noiseParamSupport) {
        this.normal = noiseParamSupport.normal();
    }

    /**
     * 构造函数
     * 
     * @param mean                  均值
     * @param standardDeviation     标准差
     * @param domain                值域
     */
    public NormalNoise(double mean, double standardDeviation) {
        this.normal = new NormalDistribution(mean, standardDeviation);
    }

    /**
     * 构造函数
     * 
     * @param mean
     * @param standardDeviation
     * @param probality
     */
    public NormalNoise(double mean, double standardDeviation, double probality) {
        this.normal = new NormalDistribution(mean, standardDeviation);
        this.icf_limit = Double.valueOf(normal.inverseCumulativeProbability(probality))
            .floatValue();
    }

    /** 
     * @see edu.tongji.noise.Noise#random()
     */
    @Override
    public double random() {
        double sample = 0.0d;
        while (Math.abs(sample = normal.sample()) > icf_limit)
            ;
        return sample;
    }

    /** 
     * @see edu.tongji.noise.Noise#perturb(double)
     */
    @Override
    public double perturb(double input) {
        double sample = 0.0d;
        while (Math.abs(sample = normal.sample()) > icf_limit)
            ;
        return sample + input;
    }

    /** 
     * @see edu.tongji.noise.Noise#standardDeviation()
     */
    @Override
    public double standardDeviation() {
        return this.normal.getStandardDeviation();
    }

    /** 
     * @see edu.tongji.noise.Noise#mean()
     */
    @Override
    public double mean() {
        return this.normal.getMean();
    }

    /** 
     * @see edu.tongji.noise.Noise#getName()
     */
    @Override
    public String getName() {
        return "正态分布。";
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (new StringBuilder("N[")).append(this.mean()).append(", ")
            .append(this.getDeviation()).append("] ").toString();
    }

    /**
     * 更新正态分布
     * 
     * @param mean                  均值
     * @param standardDeviation     标准差
     * @param domain                值域
     */
    public void update(double mean, double standardDeviation) {
        this.normal = new NormalDistribution(mean, standardDeviation);
    }

    /**
     * 返回概率密度值
     * 
     * @param x
     * @return
     */
    public double density(double x) {
        return this.normal.density(x);
    }

    /**
     * Getter method for property <tt>mean</tt>.
     * 
     * @return property value of mean
     */
    public double getMean() {
        return normal.getMean();
    }

    /**
     * Getter method for property <tt>deviation</tt>.
     * 
     * @return property value of deviation
     */
    public double getStandardDeviation() {
        return normal.getStandardDeviation();
    }

    /**
     * Getter method for property <tt>deviation</tt>.
     * 
     * @return property value of deviation
     */
    public double getDeviation() {
        return Math.pow(normal.getStandardDeviation(), 2.0d);
    }

}
