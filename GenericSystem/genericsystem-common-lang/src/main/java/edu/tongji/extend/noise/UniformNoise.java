/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.noise;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import edu.tongji.extend.support.NoiseParamSupport;

/**
 * 平均分布噪声
 * 
 * @author chench
 * @version $Id: UniformNoise.java, v 0.1 2 Apr 2014 17:42:51 chench Exp $
 */
public class UniformNoise implements Noise {

    /** 平均分布*/
    private UniformRealDistribution uniformRealDistribution;

    /**
     * 构造函数
     */
    public UniformNoise() {

    }

    /**
     * 构造函数
     * 
     * @param noiseParamSupport
     */
    public UniformNoise(NoiseParamSupport noiseParamSupport) {
        this.uniformRealDistribution = noiseParamSupport.uniform();

    }

    /**
     * 构造函数
     * 
     * @param lower
     * @param upper
     */
    public UniformNoise(double lower, double upper) {
        uniformRealDistribution = new UniformRealDistribution(lower, upper);
    }

    /** 
     * @see edu.tongji.extend.noise.Noise#random()
     */
    @Override
    public double random() {
        return uniformRealDistribution.sample();
    }

    /** 
     * @see edu.tongji.extend.noise.Noise#perturb(double)
     */
    @Override
    public double perturb(double input) {
        return input + uniformRealDistribution.sample();
    }

    /** 
     * @see edu.tongji.extend.noise.Noise#getName()
     */
    @Override
    public String getName() {
        return "平均分布";
    }

}
