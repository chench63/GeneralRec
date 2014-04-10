/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util;

import org.apache.log4j.Logger;

import edu.tongji.extend.noise.GaussMixtureNoise;
import edu.tongji.extend.noise.Noise;
import edu.tongji.extend.noise.NormalNoise;
import edu.tongji.log4j.LoggerDefineConstant;

/**
 * ExpectationMaximization 工具类
 * 
 * @author chench
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
     * 估计混合模型中，估计量: mean
     * 
     * @param noise             高斯混合模型
     * @param samples           样本空间
     * @param maxIterations     最大迭代次数
     * @return
     */
    public static double estimate(Noise noise, double[] samples, int maxIterations) {

        //获取GMM的参数
        NormalNoise[] normalNoises = ((GaussMixtureNoise) noise).getNormalNoise();
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
            LoggerUtil.info(
                logger,
                (new StringBuilder()).append(String.format("%3d", iterations)).append("：")
                    .append("LogLikelihood：").append(String.format("%4f", logLikelihoodNew))
                    .append(" Mean：")
                    .append(String.format("%4f", normalNoises[normalNoises.length - 1].getMean()))
                    .toString());

            //停机条件：likelihood稳定 或者 超过最大迭代次数
        } while (Math.abs((logLikelihoodNew - logLikelihoodOld) / logLikelihoodOld) > logLikelihoodThreshold
                 & iterations < maxIterations);

        return normalNoises[normalNoises.length - 1].getMean();
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
        int numComponents = components.length;
        int numSamples = samples.length;

        double sum = 0.0d;
        double mu = 0.0d;
        double sigma = 0.0d;

        //计算mu
        for (int i = 0; i < numSamples; i++) {
            mu += possbltyOfHiddenVar[i][numComponents - 1] * samples[i];
            sum += possbltyOfHiddenVar[i][numComponents - 1];
        }
        mu /= sum;

        //计算sigma
        for (int i = 0; i < numSamples; i++) {
            sigma += possbltyOfHiddenVar[i][numComponents - 1] * Math.pow(samples[i] - mu, 2);
        }
        sigma /= sum;

        //更新估计量
        NormalNoise estimation = components[numComponents - 1];
        estimation.update(mu, Math.sqrt(sigma));
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
