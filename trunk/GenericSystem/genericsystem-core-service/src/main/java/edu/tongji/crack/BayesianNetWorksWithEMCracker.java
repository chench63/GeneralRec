/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

import java.sql.Date;
import java.util.List;

import edu.tongji.ai.pr.BayesianNetworkPRUtil;
import edu.tongji.cache.WeatherCache;
import edu.tongji.crack.support.HashKeyCallBack;
import edu.tongji.extend.noise.Noise;
import edu.tongji.util.DateUtil;
import edu.tongji.util.EMUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 于贝叶斯网络的破解器，期间使用EM估计均值和方差
 * 
 * @author chench
 * @version $Id: BayesianNetWorksWithEMCracker.java, v 0.1 16 May 2014 19:57:47 chench Exp $
 */
public final class BayesianNetWorksWithEMCracker extends ExpectationCracker {

    /** GMM*/
    private Noise noise;

    /** 
     * @see edu.tongji.crack.PrivacyCracker#crack(edu.tongji.crack.CrackObject, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void crack(CrackObject object, int blockSize, HashKeyCallBack hashKyGen) {
        //0. 汇总数据
        List<MeterReadingVO> content = object.getTarget();
        List<ELement> baseElems = tabulate(content, 0, blockSize, blockSize);
        List<ELement> estimateElems = tabulate(content, blockSize, content.size(), blockSize);

        //1. 日子输出
        StringBuilder logMsg = new StringBuilder("BayesianNetworksCracker");
        for (int i = 0, j = baseElems.size(); i < j; i++) {
            if (baseElems.get(i).getStats().getN() < SAMPLE_NUM_LIMITS) {
                //数据不全放回
                continue;
            }

            String key = DateUtil.format(new Date(baseElems.get(i).getTimeVal()),
                DateUtil.SHORT_FORMAT);
            String temperature = String.format("%.0f",
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper());
            String date = (new StringBuilder()).append(key).append(" (")
                .append(StringUtil.alignRight(temperature, 2)).append(")").append(" W：")
                .append(DateUtil.getDayOfWeek(baseElems.get(i).getTimeVal())).toString();
            double[] resltEM = EMUtil.estimate(noise, estimateElems.get(i).getStats().getValues(),
                30);
            String mean = (new StringBuilder())
                .append(String.format("%.2f", baseElems.get(i).getStats().getMean())).append(" (")
                .append(String.format("%.2f", resltEM[0])).append(")").toString();
            String sd = (new StringBuilder())
                .append(String.format("%.2f", baseElems.get(i).getStats().getStandardDeviation()))
                .append(" (").append(String.format("%.2f", resltEM[1])).append(")").toString();
            String cp = (new StringBuilder()).append(
                String.format("%.3f", BayesianNetworkPRUtil.cp(1,
                    WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper(), resltEM[0])))
                .toString();

            logMsg.append("\n T：").append(date).append(" M：")
                .append(StringUtil.alignRight(mean.toString(), 16)).append(" SD：")
                .append(StringUtil.alignRight(sd.toString(), 16)).append(" CP：")
                .append(StringUtil.alignRight(cp, 6));
        }
        LoggerUtil.debug(logger, logMsg);
    }

    /** 
     * @see edu.tongji.crack.ExpectationSeqDayCracker#crackInnerNoise(edu.tongji.crack.CrackObject, edu.tongji.extend.noise.Noise)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void crackInnerNoise(CrackObject object, Noise noise, HashKeyCallBack hashKyGen) {
        //0. 汇总数据
        List<MeterReadingVO> content = object.getTarget();
        List<ELement> baseElems = tabulate(content, 0, content.size(), content.size());

        //1. 计算条件概率
        //   日志输出
        StringBuilder logMsg = new StringBuilder("BayesianNetworksCracker");
        for (int i = 0, j = baseElems.size(); i < j; i++) {
            if (baseElems.get(i).getStats().getN() < SAMPLE_NUM_LIMITS) {
                //数据不全放回
                continue;
            }

            //生成包含噪声的电表读数
            double[] readings = baseElems.get(i).getStats().getValues();
            int capacity = readings.length;
            double[] samples = new double[capacity];
            for (int index = 0; index < capacity; index++) {
                samples[index] = noise.perturb(readings[index]);
            }

            double[] resltEM = EMUtil.estimate(noise, samples, 30);

            //计算条件概率
            String key = DateUtil.format(new Date(baseElems.get(i).getTimeVal()),
                DateUtil.SHORT_FORMAT);
            double cpVal = BayesianNetworkPRUtil.cp(1,
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper(), resltEM[0]);
            CP_RESULT.add(cpVal);

            //输出日志
            String temperature = String.format("%.0f",
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper());
            String date = (new StringBuilder()).append(key).append(" (")
                .append(StringUtil.alignRight(temperature, 2)).append(")").append(" W：")
                .append(DateUtil.getDayOfWeek(baseElems.get(i).getTimeVal())).toString();
            String mean = (new StringBuilder())
                .append(String.format("%.2f", baseElems.get(i).getStats().getMean())).append('(')
                .append(String.format("%.2f", resltEM[0])).append(')').toString();
            String sd = (new StringBuilder()).append(
                String.format("%.2f", baseElems.get(i).getStats().getStandardDeviation()))
                .toString();
            String cp = (new StringBuilder()).append(String.format("%.3f", cpVal)).toString();
            logMsg.append("\n T：").append(date).append(" M：")
                .append(StringUtil.alignRight(mean.toString(), 16)).append(" SD：")
                .append(StringUtil.alignRight(sd.toString(), 16)).append(" CP：")
                .append(StringUtil.alignRight(cp, 6));
        }
        LoggerUtil.debug(logger, logMsg);
    }

    /**
     * Setter method for property <tt>noise</tt>.
     * 
     * @param noise value to be assigned to property noise
     */
    public void setNoise(Noise noise) {
        this.noise = noise;
    }

}
