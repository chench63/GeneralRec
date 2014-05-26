/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.support;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.log4j.Logger;

import edu.tongji.extend.noise.NormalNoise;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 噪声参数确定支持类
 * 
 * @author chench
 * @version $Id: NoiseParamSupport.java, v 0.1 16 May 2014 10:46:49 chench Exp $
 */
public class NoiseParamSupport {

    /** 样本数量*/
    private double                n           = 1000;

    /** load monitoring 误差比例*/
    private double                alpha       = 0.01d;

    /** 电表读数均值*/
    private double                u_1         = 200;

    /** 电表读数标准方差*/
    private double                sigma_1     = 250;

    /** 噪声均值*/
    private double                u_2         = 0;

    /** alpha误差下，置信概率*/
    private double                p           = 0.9d;

    /** GMM: alpha参数*/
    private final double          param_alpha = 0.01d;

    /** GMM: belta参数*/
    private final double          param_belta = 2.0d;

    /** GMM: 隐变量参数*/
    private double[]              weighit;

    /** GMM: 隐变量,MGC参数*/
    private double                w_0;

    /** logger */
    protected final static Logger logger      = Logger.getLogger(LoggerDefineConstant.SERVICE_CORE);

    /**
     * 平均分布噪声
     * 
     * @return
     */
    public UniformRealDistribution uniform() {
        double offset = Math.pow(3.0d * sigma(), 0.5d);

        //输出日志
        LoggerUtil.info(logger, (new StringBuilder(" Gen: U[")).append(u_2 - offset).append(", ")
            .append(u_2 + offset).append("]"));
        return new UniformRealDistribution(u_2 - offset, u_2 + offset);
    }

    /**
     * 正态分布噪声
     * 
     * @return
     */
    public NormalDistribution normal() {
        double sigma = sigma();

        //输出日志
        LoggerUtil.info(logger,
            (new StringBuilder(" Gen: N[")).append(u_2).append(", ").append(sigma).append("]"));
        return new NormalDistribution(u_2, Math.sqrt(sigma));
    }

    public NormalNoise[] gmm() {
        // mu_1t = icdf( alpha, icdf(alpha, mu, sigma), belta * sigma )
        NormalDistribution gauss = new NormalDistribution(u_1, sigma_1);
        double icdf = gauss.inverseCumulativeProbability(param_alpha);
        gauss = new NormalDistribution(icdf, param_belta * sigma_1);
        double mu_noise_1 = gauss.inverseCumulativeProbability(param_alpha);

        // mu_2t = icdf( 1-alpha, icdf(1-alpha, mu, sigma), belta * sigma )
        gauss = new NormalDistribution(u_1, sigma_1);
        icdf = gauss.inverseCumulativeProbability(1 - param_alpha);
        gauss = new NormalDistribution(icdf, param_belta * sigma_1);
        double mu_noise_2 = gauss.inverseCumulativeProbability(1 - param_alpha);

        NormalNoise[] gmm = new NormalNoise[3];
        gmm[0] = new NormalNoise(420, sigma_1);
        gmm[1] = new NormalNoise(mu_noise_1, param_belta * sigma_1 );
        gmm[2] = new NormalNoise(mu_noise_2, param_belta * sigma_1 );

        //输出日志
        LoggerUtil.info(logger, (new StringBuilder(" Gen: GMM：")).append(gmm[0]).append(", ")
            .append(gmm[1]).append(", ").append(gmm[2]));
        return gmm;
    }

    /**
     * 计算方差
     * 
     * @return
     */
    private double sigma() {
        //噪声方差
        double phi = (p + 1) / 2;
        NormalDistribution stat = new NormalDistribution(0, 1);
        double sigma_2 = Math.pow((alpha * u_1 - u_2) / stat.inverseCumulativeProbability(phi),
            2.0d) * n - Math.pow(alpha * sigma_1, 2.0d);
        return sigma_2;
    }

    /**
     * Getter method for property <tt>n</tt>.
     * 
     * @return property value of n
     */
    public double getN() {
        return n;
    }

    /**
     * Setter method for property <tt>n</tt>.
     * 
     * @param n value to be assigned to property n
     */
    public void setN(double n) {
        this.n = n;
    }

    /**
     * Getter method for property <tt>alpha</tt>.
     * 
     * @return property value of alpha
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Setter method for property <tt>alpha</tt>.
     * 
     * @param alpha value to be assigned to property alpha
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Getter method for property <tt>u_1</tt>.
     * 
     * @return property value of u_1
     */
    public double getU_1() {
        return u_1;
    }

    /**
     * Setter method for property <tt>u_1</tt>.
     * 
     * @param u_1 value to be assigned to property u_1
     */
    public void setU_1(double u_1) {
        this.u_1 = u_1;
    }

    /**
     * Getter method for property <tt>sigma_1</tt>.
     * 
     * @return property value of sigma_1
     */
    public double getSigma_1() {
        return sigma_1;
    }

    /**
     * Setter method for property <tt>sigma_1</tt>.
     * 
     * @param sigma_1 value to be assigned to property sigma_1
     */
    public void setSigma_1(double sigma_1) {
        this.sigma_1 = sigma_1;
    }

    /**
     * Getter method for property <tt>u_2</tt>.
     * 
     * @return property value of u_2
     */
    public double getU_2() {
        return u_2;
    }

    /**
     * Setter method for property <tt>u_2</tt>.
     * 
     * @param u_2 value to be assigned to property u_2
     */
    public void setU_2(double u_2) {
        this.u_2 = u_2;
    }

    /**
     * Getter method for property <tt>p</tt>.
     * 
     * @return property value of p
     */
    public double getP() {
        return p;
    }

    /**
     * Setter method for property <tt>p</tt>.
     * 
     * @param p value to be assigned to property p
     */
    public void setP(double p) {
        this.p = p;
    }

    /**
     * Getter method for property <tt>param_alpha</tt>.
     * 
     * @return property value of param_alpha
     */
    public double getParam_alpha() {
        return param_alpha;
    }

    /**
     * Getter method for property <tt>param_belta</tt>.
     * 
     * @return property value of param_belta
     */
    public double getParam_belta() {
        return param_belta;
    }

    /**
     * Getter method for property <tt>weighit</tt>.
     * 
     * @return property value of weighit
     */
    public double[] getWeighit() {
        return weighit;
    }

    /**
     * Setter method for property <tt>weighit</tt>.
     * 
     * @param weighit value to be assigned to property weighit
     */
    public void setWeighit(double[] weighit) {
        this.weighit = weighit;
    }

    /**
     * Getter method for property <tt>w_0</tt>.
     * 
     * @return property value of w_0
     */
    public double getW_0() {
        return w_0;
    }

    /**
     * Setter method for property <tt>w_0</tt>.
     * 
     * @param w_0 value to be assigned to property w_0
     */
    public void setW_0(double w_0) {
        weighit = new double[3];
        double w_not_0 = (1 - w_0) / 2;
        weighit[0] = w_0;
        weighit[1] = w_not_0;
        weighit[2] = w_not_0;
        this.w_0 = w_0;
    }

}
