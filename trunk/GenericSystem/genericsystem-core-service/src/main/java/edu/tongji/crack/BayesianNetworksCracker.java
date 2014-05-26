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
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 基于贝叶斯网络的破解器
 * 
 * @author chench
 * @version $Id: BayesianNetworksCracker.java, v 0.1 16 May 2014 09:46:27 chench Exp $
 */
public class BayesianNetworksCracker extends ExpectationCracker {

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

        //1. 日志输出
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
            String mean = (new StringBuilder())
                .append(String.format("%.2f", baseElems.get(i).getStats().getMean())).append(" (")
                .append(String.format("%.2f", estimateElems.get(i).getStats().getMean()))
                .append(")").toString();
            String sd = (new StringBuilder())
                .append(String.format("%.2f", baseElems.get(i).getStats().getStandardDeviation()))
                .append(" (")
                .append(
                    String.format("%.2f", estimateElems.get(i).getStats().getStandardDeviation()))
                .append(")").toString();
            double cpVal = BayesianNetworkPRUtil.cp(1,
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper(),
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

            //计算误差均值
            //  E(P) = E(R) + E(N)
            double sumOfNoise = 0.0d;
            for (int index = 0; index < baseElems.get(i).getStats().getN(); index++) {
                sumOfNoise += noise.random();
            }

            //计算条件概率
            String key = DateUtil.format(new Date(baseElems.get(i).getTimeVal()),
                DateUtil.SHORT_FORMAT);
            double cpVal = BayesianNetworkPRUtil.cp(1,
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper(), baseElems.get(i)
                    .getStats().getMean()
                                                                                 + sumOfNoise
                                                                                 / baseElems.get(i)
                                                                                     .getStats()
                                                                                     .getN());
            CP_RESULT.add(cpVal);

            //输出日志
            String temperature = String.format("%.0f",
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper());
            String date = (new StringBuilder()).append(key).append(" (")
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
        LoggerUtil.debug(logger, logMsg);
    }
}
