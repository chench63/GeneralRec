/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.log4j.Logger;
import org.junit.Test;

import edu.tongji.log4j.LoggerDefineConstant;

/**
 * 正态分布参数计算
 * 
 * @author chench
 * @version $Id: TestParam.java, v 0.1 9 Apr 2014 15:23:18 chench Exp $
 */
public class TestParam {

    /** logger*/
    private static final Logger logger  = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /** 样本数量*/
    public double               n       = 1000;
    /** load monitoring 误差*/
    public double               alpha   = 0.01;
    /** 电表读数均值*/
    public double               u_1     = 200;
    /** 电表读数标准方差*/
    public double               sigma_1 = 200;
    /** 噪声均值*/
    public double               u_2     = 0;
    /** alpha误差下，置信概率*/
    public double               p       = 0.9;

    @Test
    public void test() {

        NormalDistribution stat = new NormalDistribution(0, 1);

        //噪声标准差
        double phi = (p + 1) / 2;
        double sigma_2 = Math.pow((alpha * u_1 - u_2) / stat.inverseCumulativeProbability(phi),
            2.0d) * n - Math.pow(alpha * sigma_1, 2.0d);
        LoggerUtil.info(logger, "n：" + n + " alpha：" + alpha + " u_1：" + u_1 + " sigma_1："
                                + sigma_1 + " sigma_2：" + String.format("%.3f", Math.sqrt(sigma_2))
                                + " P：" + String.format("%.4f", p));

    }

    //    @Test
    public void test2() {
        double param_alpha = 0.01d;
        double param_belta = 2.0d;

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

        LoggerUtil.info(logger, "mu_1t：" + mu_noise_1 + " mu_2t：" + mu_noise_2);

    }
}
