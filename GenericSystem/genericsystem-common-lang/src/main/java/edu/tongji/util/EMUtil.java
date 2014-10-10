/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.noise.GaussMixtureNoise;
import edu.tongji.noise.Noise;
import edu.tongji.noise.NormalNoise;

/**
 * ExpectationMaximization 工具类
 * 
 * @author Hanke Chen
 * @version $Id: EMUtil.java, v 0.1 2 Apr 2014 20:05:30 chench Exp $
 */
public final class EMUtil {

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_CORE);

    /**
     * 禁用构造函数
     */
    private EMUtil() {

    }

    /**
     * 估计混合模型中，估计量: mean, standard deviation
     * 
     * @param noise             高斯混合模型
     * @param samples           样本空间
     * @param maxIterations     最大迭代次数
     * @return
     */
    public static double[] estimate(final Noise noise, double[] samples, int maxIterations) {

        //拷贝GMM的参数
        NormalNoise[] sourceNoises = ((GaussMixtureNoise) noise).getNormalNoise();
        int len = sourceNoises.length;
        NormalNoise[] normalNoises = new NormalNoise[len];
        for (int i = 0; i < len; i++) {
            normalNoises[i] = new NormalNoise(sourceNoises[i].getMean(),
                sourceNoises[i].getStandardDeviation());
        }

        double[] weight = ((GaussMixtureNoise) noise).getWeight();
        double[][] possbltyOfHiddenVar = new double[samples.length][normalNoises.length];

        //迭代信息
        int iterations = 0;
        double logLikelihoodThreshold = 10e-10;
        double logLikelihoodNew = logLikelihood(normalNoises, weight, samples);
        double logLikelihoodOld = 0.0d;
        do {
            //E-step: computation of matrix P (fast version, we don't compute 1/f(x) for all P[i][j])
            eStep(normalNoises, weight, samples, possbltyOfHiddenVar);

            //M-step: computation of new Gaussians and the new weights
            mStep(normalNoises, weight, samples, possbltyOfHiddenVar);

            //更新迭代信息,打印日志
            iterations++;
            logLikelihoodOld = logLikelihoodNew;
            logLikelihoodNew = logLikelihood(normalNoises, weight, samples);
            LoggerUtil.debug(
                logger,
                (new StringBuilder()).append(String.format("%3d", iterations)).append("：")
                    .append("LogLikelihood：").append(String.format("%4f", logLikelihoodNew))
                    .append(" Mean：").append(String.format("%4f", normalNoises[0].getMean()))
                    .toString());

            //停机条件：likelihood稳定 或者 超过最大迭代次数
        } while (Math.abs((logLikelihoodNew - logLikelihoodOld) / logLikelihoodOld) > logLikelihoodThreshold
                 & iterations < maxIterations);

        // 返回估计量
        double[] result = new double[2];
        result[0] = normalNoises[0].getMean();
        result[1] = normalNoises[0].getStandardDeviation();
        return result;
    }

    /**
     * E-step 固定估计量，求隐变量的分布
     * 
     * @param components
     * @param weights
     * @param samples
     * @param possbltyOfHiddenVar
     */
    private static void eStep(NormalNoise[] components, double[] weights, double[] samples,
                              double[][] possbltyOfHiddenVar) {
        int numComponents = components.length;
        int numSamples = samples.length;

        for (int i = 0; i < numSamples; i++) {
            double sum = 0.0d;

            for (int j = 0; j < numComponents; j++) {
                double p_XZ = weights[j] * components[j].density(samples[i]);

                possbltyOfHiddenVar[i][j] = p_XZ;
                sum += p_XZ;
            }

            for (int j = 0; j < numComponents; j++) {
                possbltyOfHiddenVar[i][j] /= sum;
            }
        }

    }

    /**
     * M-step 固定隐变量，放大估计量
     * 
     * @param components
     * @param weights
     * @param samples
     * @param possbltyOfHiddenVar
     */
    private static void mStep(NormalNoise[] components, double[] weights, double[] samples,
                              double[][] possbltyOfHiddenVar) {
        int numSamples = samples.length;

        double sum = 0.0d;
        double mu = 0.0d;
        double sigma = 0.0d;

        //计算mu
        for (int i = 0; i < numSamples; i++) {
            mu += possbltyOfHiddenVar[i][0] * samples[i];
            sum += possbltyOfHiddenVar[i][0];
        }
        mu /= sum;

        //计算sigma
        for (int i = 0; i < numSamples; i++) {
            sigma += possbltyOfHiddenVar[i][0] * Math.pow(samples[i] - mu, 2);
        }
        sigma /= sum;

        //更新估计量
        if (sum <= 0.01) {
            //数据集中不含此类样本
            return;
        }

        NormalNoise estimation = components[0];
        estimation.update(mu, sigma > 0.0d ? Math.sqrt(sigma) : estimation.getStandardDeviation());
    }

    /**
     * 计算likelihood
     * 
     * @param components
     * @param weights
     * @param samples
     * @return
     */
    private static double logLikelihood(NormalNoise[] components, double[] weights, double[] samples) {
        double logLikelyHood = 0.0d;
        int numComponents = components.length;
        int numSamples = samples.length;
        for (int i = 0; i < numSamples; i++) {

            double densityOfSample = 0.0;
            for (int j = 0; j < numComponents; j++) {
                densityOfSample += weights[j] * components[j].density(samples[i]);
            }

            logLikelyHood += Math.log(densityOfSample);
        }

        return logLikelyHood;
    }

}
