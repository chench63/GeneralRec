/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.ai.pr.BayesianNetworkPRUtil;
import edu.tongji.cache.WeatherCache;
import edu.tongji.extend.crack.support.PrivacyCrackObject;
import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.noise.GaussMixtureNoise;
import edu.tongji.noise.Noise;
import edu.tongji.util.DateUtil;
import edu.tongji.util.EMUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.BayesianEventVO;
import edu.tongji.vo.MeterReadingVO;

/**
 * 基于贝叶斯网络的破解器
 * 
 * @author chench
 * @version $Id: BayesianNetworksCracker.java, v 0.1 16 May 2014 09:46:27 chench Exp $
 */
public class BayesianNetworksCracker extends ExpectationCracker {

    /** 
     * @see edu.tongji.extend.crack.PrivacyCracker#crack(edu.tongji.extend.crack.support.PrivacyCrackObject, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void crack(PrivacyCrackObject object, int blockSize, Noise noise,
                      HashKeyCallBack hashKyGen) {
        //0. 汇总数据
        List<MeterReadingVO> content = object.getTarget();
        List<ELement> baseElems = tabulate(content, 0, blockSize, blockSize);
        List<ELement> estimateElems = tabulate(content, blockSize, content.size(), blockSize);

        //1. 日志输出
        StringBuilder logMsg = new StringBuilder("BayesianNetworksCracker");
        for (int i = 0, j = baseElems.size(); i < j; i++) {
            if (baseElems.get(i).getStats().getN() < SAMPLE_NUM_LIMITS) {
                //数据不全放回
                continue;
            }

            //估计统计特征
            double meanEstmt = 0.0d;
            double sdEstmt = 0.0d;
            if (noise instanceof GaussMixtureNoise) {
                //混合模型，使用EM算法
                double[] resltEM = EMUtil.estimate(noise, estimateElems.get(i).getStats()
                    .getValues(), 30);
                meanEstmt = resltEM[0];
                sdEstmt = resltEM[1];
            } else {
                //扁平模型，使用统计
                meanEstmt = estimateElems.get(i).getStats().getMean();
                sdEstmt = estimateElems.get(i).getStats().getStandardDeviation();
            }

            String key = hashKyGen.key(baseElems.get(i).getTimeVal());
            String temperature = String.format("%.0f",
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper());
            String date = (new StringBuilder()).append(key).append(" (")
                .append(StringUtil.alignRight(temperature, 2)).append(")").append(" W：")
                .append(DateUtil.getDayOfWeek(baseElems.get(i).getTimeVal())).toString();
            String mean = (new StringBuilder())
                .append(String.format("%.2f", baseElems.get(i).getStats().getMean())).append(" (")
                .append(String.format("%.2f", meanEstmt)).append(")").toString();
            String sd = (new StringBuilder())
                .append(String.format("%.2f", baseElems.get(i).getStats().getStandardDeviation()))
                .append(" (").append(String.format("%.2f", sdEstmt)).append(")").toString();
            double cpVal = BayesianNetworkPRUtil.cp(1, WeatherCache.get(key).getHighTemper(),
                estimateElems.get(i).getStats().getMean());
            String cp = (new StringBuilder()).append(String.format("%.3f", cpVal)).toString();

            logMsg.append("\n T：").append(date).append(" M：")
                .append(StringUtil.alignRight(mean.toString(), 16)).append(" SD：")
                .append(StringUtil.alignRight(sd.toString(), 16)).append(" CP：")
                .append(StringUtil.alignRight(cp, 6));
        }
        LoggerUtil.info(logger, logMsg);
    }

    /** 
     * @see edu.tongji.crack.ExpectationSeqDayCracker#crackInnerNoise(edu.tongji.extend.crack.support.PrivacyCrackObject, edu.tongji.noise.Noise)
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public void crackInnerNoise(PrivacyCrackObject object, Noise noise, HashKeyCallBack hashKyGen) {
        //0. 汇总数据
        List<MeterReadingVO> content = object.getTarget();
        List<ELement> baseElems = tabulate(content, 0, content.size(), hashKyGen);

        //1. 计算条件概率
        List<BayesianEventVO> resultArr = new ArrayList<BayesianEventVO>();
        StringBuilder logMsg = new StringBuilder("BayesianNetworksCracker");
        for (int i = 0, j = baseElems.size(); i < j; i++) {

            //1. 估计均值
            double meanEstmt = 0.0d;
            if (noise instanceof GaussMixtureNoise) {
                //混合模型，使用EM算法
                double[] readings = baseElems.get(i).getStats().getValues();
                int capacity = readings.length;
                double[] samples = new double[capacity];
                for (int index = 0; index < capacity; index++) {
                    samples[index] = noise.perturb(readings[index]);
                }
                double[] resltEM = EMUtil.estimate(noise, samples, 30);
                meanEstmt = resltEM[0];
            } else {
                //扁平模型，使用统计
                //  E(P) = E(R) + E(N)
                double sumOfNoise = 0.0d;
                for (int index = 0, num = (int) baseElems.get(i).getStats().getN(); index < num; index++) {
                    sumOfNoise += noise.random();
                }
                meanEstmt = baseElems.get(i).getStats().getMean() + sumOfNoise
                            / baseElems.get(i).getStats().getN();
            }

            //2. 计算条件概率
            double cpVal = BayesianNetworkPRUtil.cp(1,
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper(), meanEstmt);

            resultArr.add(new BayesianEventVO(baseElems.get(i).getTimeVal(),
                (short) (cpVal > 0.5 ? 1 : 0)));

            //输出日志
            String temperature = String.format("%.0f",
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper());
            String date = (new StringBuilder())
                .append(
                    DateUtil.format(new Date(baseElems.get(i).getTimeVal()),
                        DateUtil.LONG_WEB_FORMAT_NO_SEC)).append(" (")
                .append(StringUtil.alignRight(temperature, 2)).append(")").append(" W：")
                .append(DateUtil.getDayOfWeek(baseElems.get(i).getTimeVal())).toString();
            String mean = (new StringBuilder()).append(
                String.format("%.2f", baseElems.get(i).getStats().getMean())).toString();
            String sd = (new StringBuilder()).append(
                String.format("%.2f", baseElems.get(i).getStats().getStandardDeviation()))
                .toString();
            String cp = (new StringBuilder()).append(String.format("%.3f", cpVal)).toString();
            logMsg.append("\n T：").append(date).append(" M：")
                .append(StringUtil.alignRight(mean.toString(), 16)).append(" SD：")
                .append(StringUtil.alignRight(sd.toString(), 16)).append(" CP：")
                .append(StringUtil.alignRight(cp, 6));
        }

        //载入结果缓存
        List<List<BayesianEventVO>> resultCache = (List<List<BayesianEventVO>>) object
            .get(PrivacyCrackObject.RESULT_CACHE);
        Collections.sort(resultArr);
        resultCache.add(resultArr);
        LoggerUtil.debug(logger, logMsg);
    }

    /** 
     * @see edu.tongji.extend.crack.ExpectationCracker#tabulate(java.util.List, int, int, edu.tongji.extend.crack.support.HashKeyCallBack)
     */
    @Override
    protected List<ELement> tabulate(List<MeterReadingVO> content, int start, int end,
                                     HashKeyCallBack hashKyGen) {

        //1. 规整数据,按天合并数据
        Map<String, List<MeterReadingVO>> cache = new HashMap<String, List<MeterReadingVO>>();
        for (int i = start; i < end; i++) {
            MeterReadingVO reading = content.get(i);
            String key = hashKyGen.key(reading.getTimeVal());

            List<MeterReadingVO> arr = cache.get(key);
            if (arr == null) {
                arr = new ArrayList<MeterReadingVO>();
                cache.put(key, arr);
            }

            arr.add(reading);
        }

        //2. 生成事件集合
        List<ELement> result = new ArrayList<ELement>();
        for (List<MeterReadingVO> arr : cache.values()) {

            if (arr.size() != 4 * 24) {
                continue;
            }

            //升序排序
            Collections.sort(arr);

            for (int i = 0, j = arr.size(); i < j - 1 * 4; i++) {
                //a. 获取时间撮
                long timeVal = arr.get(i).getTimeVal();

                //e. 获取功耗
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (int ele = i; ele < i + 4; ele++) {
                    stats.addValue(arr.get(ele).getReading());
                }

                result.add(new ELement(stats, timeVal));
            }

        }

        return result;
    }

}
