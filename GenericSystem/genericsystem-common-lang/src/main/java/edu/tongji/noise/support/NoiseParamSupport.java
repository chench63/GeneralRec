/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.noise.support;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.noise.NormalNoise;
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

    /** load monitoring 精度参数*/
    private double                alpha       = 0.99d;

    /** load monitoring 鲁棒性参数*/
    private double                belta       = 0.9d;

    /** 电表读数均值*/
    private double                u_1         = 196.6368841498821;

    /** 电表读数标准方差*/
    private double                sigma_1     = 264.23609122693205;

    /** 噪声均值*/
    private double                u_2         = 0;

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

        // omega_0
        NormalDistribution normal = new NormalDistribution();
        double phi = normal.inverseCumulativeProbability((1 + belta) / 2);
        double numerator = Math.pow(sigma_1 * phi, 2.0d);
        this.w_0 = numerator
                   / (Math.pow((1 - alpha) * u_1, 2.0d) * n + (2 - alpha) * alpha * numerator);

        // weight[0,2]
        weighit = new double[3];
        double w_not_0 = (1 - this.w_0) / 2;
        weighit[0] = this.w_0;
        weighit[1] = w_not_0;
        weighit[2] = w_not_0;

        NormalNoise[] gmm = new NormalNoise[3];
        gmm[0] = new NormalNoise(420, sigma_1);
        gmm[1] = new NormalNoise(mu_noise_1, param_belta * sigma_1);
        gmm[2] = new NormalNoise(mu_noise_2, param_belta * sigma_1);

        //输出日志
        LoggerUtil.info(logger, (new StringBuilder(" Gen: GMM：")).append(gmm[0]).append("  (")
            .append(weighit[0]).append("), ").append(gmm[1]).append("  (").append(weighit[1])
            .append("), ").append(gmm[2]).append("  (").append(weighit[2]).append("), "));
        return gmm;
    }

    /**
     * 计算方差
     * 
     * @return
     */
    private double sigma() {
        //噪声方差
        NormalDistribution stat = new NormalDistribution(0, 1);
        double phi = stat.inverseCumulativeProbability((belta + 1) / 2);
        double sigma_2 = Math.pow(((1 - alpha) * u_1) / phi, 2.0d) * n
                         - Math.pow((1 - alpha) * sigma_1, 2.0d);
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
     * Getter method for property <tt>weighit</tt>.
     * 
     * @return property value of weighit
     */
    public double[] getWeighit() {
        return weighit;
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
     * Setter method for property <tt>belta</tt>.
     * 
     * @param belta value to be assigned to property belta
     */
    public void setBelta(double belta) {
        this.belta = belta;
    }

}
