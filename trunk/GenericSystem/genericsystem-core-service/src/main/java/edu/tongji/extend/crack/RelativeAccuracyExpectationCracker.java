/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.noise.GaussMixtureNoise;
import edu.tongji.noise.Noise;
import edu.tongji.util.EMUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 
 * @author chench
 * @version $Id: RelativeAccuracyExpectationCracker.java, v 0.1 2014-5-22 下午2:26:16 chench Exp $
 */
public class RelativeAccuracyExpectationCracker extends ExpectationCracker {

    /** 
     * @see edu.tongji.extend.crack.PrivacyCracker#crack(edu.tongji.extend.crack.CrackObject, int, edu.tongji.extend.crack.support.HashKeyCallBack)
     */
    @Override
    public void crack(CrackObject object, int blockSize, Noise noise, HashKeyCallBack hashKyGen) {
        throw new OwnedException(FunctionErrorCode.ILLEGAL_PARAMETER);
    }

    /** 
     * @see edu.tongji.extend.crack.PrivacyCracker#crackInnerNoise(edu.tongji.extend.crack.CrackObject, edu.tongji.noise.Noise, edu.tongji.extend.crack.support.HashKeyCallBack)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void crackInnerNoise(CrackObject object, Noise noise, HashKeyCallBack hashKyGen) {
        //0. 汇总数据
        List<MeterReadingVO> content = object.getTarget();
        List<ELement> baseElems = tabulate(content, 0, content.size(), hashKyGen);

        //1. 计算条件概率
        //   日志输出
        DescriptiveStatistics meanStat = (DescriptiveStatistics) object.get(CrackObject.MEAN_STAT);
        DescriptiveStatistics sdStat = (DescriptiveStatistics) object.get(CrackObject.SD_STAT);
        for (int i = 0, j = baseElems.size(); i < j; i++) {
            if (baseElems.get(i).getStats().getN() < SAMPLE_NUM_LIMITS) {
                //数据不全返回
                continue;
            }

            if (noise instanceof GaussMixtureNoise) {
                //混合模型，使用EM算法
                crackEM(baseElems.get(i), noise, meanStat, sdStat);
            } else {
                //扁平模型，使用统计
                crackStat(baseElems.get(i), noise, meanStat, sdStat);
            }

        }
    }

    /**
     * 使用统计器，统计Mean和Sd
     * 
     * @param element
     * @param noise
     * @param meanStat
     * @param sdStat
     */
    protected void crackStat(ELement element, Noise noise, DescriptiveStatistics meanStat,
                             DescriptiveStatistics sdStat) {
        //1. 生成包含噪声的电表读数
        DescriptiveStatistics statInner = new DescriptiveStatistics();
        for (double sample : element.getStats().getValues()) {
            statInner.addValue(noise.perturb(sample));
        }

        //2. 统计误差
        //Estimation of the mean of smart meter reading : E(R)
        //MAE of Mean =  E(Report) - E(Reading)
        double meanAE = Math.abs(statInner.getMean() - element.getStats().getMean());
        meanStat.addValue(meanAE / element.getStats().getMean());
        //D(Report) = D(Noise + Reading) = D(Noise) + D(Reading)
        //Estimation ot Deviation of Smart meter reading : D(Report) - D(Noise)
        //MAE of SD
        double sdEstmt = ((sdEstmt = (Math.pow(statInner.getStandardDeviation(), 2.0d) - Math.pow(
            noise.standardDeviation(), 2.0d))) > 0.0d) ? Math.sqrt(sdEstmt) : 0.0d;
        double sdAE = Math.abs(element.getStats().getStandardDeviation() - sdEstmt);
        sdStat.addValue(sdAE / element.getStats().getStandardDeviation());
    }

    /**
     * 使用EM算法，估计Mean和Sd
     * 
     * @param element
     * @param noise
     * @param meanStat
     * @param sdStat
     */
    protected void crackEM(ELement element, Noise noise, DescriptiveStatistics meanStat,
                           DescriptiveStatistics sdStat) {
        //1. 生成包含噪声的电表读数
        double[] readings = element.getStats().getValues();
        int capacity = readings.length;
        double[] samples = new double[capacity];
        for (int index = 0; index < capacity; index++) {
            samples[index] = noise.perturb(readings[index]);
        }

        //2. 估计Mean和Sd
        double[] resltEM = EMUtil.estimate(noise, samples, 30);

        //3. 统计误差
        //均值误差
        double meanAE = Math.abs(resltEM[0] - element.getStats().getMean());
        meanStat.addValue(meanAE / element.getStats().getMean());
        //标准差误差
        double sdAE = Math.abs(resltEM[1] - element.getStats().getStandardDeviation());
        sdStat.addValue(sdAE / element.getStats().getStandardDeviation());
    }

}
