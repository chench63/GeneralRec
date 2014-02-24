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
public class NormalDistributionNoise implements Noise {

    /** 正态分布期望*/
    private double               mean;

    /** 正态分布方差*/
    private double               deviation;

    /** 需要参数对称的边界*/
    private double               domain;

    /** 正态分布实体*/
    protected NormalDistribution stat;

    /**
     * 构造函数
     */
    public NormalDistributionNoise() {

    }

    /**
     * @param mean
     * @param deviation
     * @param domain
     */
    public NormalDistributionNoise(double mean, double deviation, double domain) {
        this.mean = mean;
        this.deviation = deviation;
        this.domain = domain;
        this.stat = new NormalDistribution(mean, deviation);
    }

    /** 
     * @see edu.tongji.extend.noise.Noise#random()
     */
    @Override
    public double random() {
        return stat.sample();
    }

    /** 
     * @see edu.tongji.extend.noise.Noise#getName()
     */
    @Override
    public String getName() {
        return "正态分布。";
    }

    /**
     * Getter method for property <tt>mean</tt>.
     * 
     * @return property value of mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * Setter method for property <tt>mean</tt>.
     * 
     * @param mean value to be assigned to property mean
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * Getter method for property <tt>deviation</tt>.
     * 
     * @return property value of deviation
     */
    public double getDeviation() {
        return deviation;
    }

    /**
     * Setter method for property <tt>deviation</tt>.
     * 
     * @param deviation value to be assigned to property deviation
     */
    public void setDeviation(double deviation) {
        this.deviation = deviation;
    }

    /**
     * Getter method for property <tt>domain</tt>.
     * 
     * @return property value of domain
     */
    public double getDomain() {
        return domain;
    }

    /**
     * Setter method for property <tt>domain</tt>.
     * 
     * @param domain value to be assigned to property domain
     */
    public void setDomain(double domain) {
        this.domain = domain;
    }

}
