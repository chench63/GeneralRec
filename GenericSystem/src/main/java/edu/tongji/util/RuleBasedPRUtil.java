/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.cache.WeatherCache;
import edu.tongji.extend.gnuplot.support.SmartGridFormatterHelper;
import edu.tongji.vo.MeterReadingVO;
import edu.tongji.vo.WeatherVO;

/**
 * 基于规则的模式识别工具
 * 
 * @author chench
 * @version $Id: RuleBasedPRUtil.java, v 0.1 21 Apr 2014 14:59:42 chench Exp $
 */
public final class RuleBasedPRUtil {

    /** 规则阈值*/
    private final static List<Double> ruleThreshhold = new ArrayList<Double>();

    /**
     * 引用规则, 识别标签
     * 
     * @param samples
     * @return  -1: off, 0:Unknown, 1：On
     */
    public int recognize(List<MeterReadingVO> samples) {

        //引用温度，筛选出高概率日期数据
        double tempThresh = ruleThreshhold.get(0);
        List<MeterReadingVO> temple = new ArrayList<MeterReadingVO>();
        for (MeterReadingVO reading : samples) {
            WeatherVO weather = WeatherCache.get(reading.getTimeVal());

            //高于阈值，则加入
            if (weather.getHighTemper() > tempThresh) {
                temple.add(reading);
            }
        }

        //数据不足，返回位置
        if (temple.isEmpty()) {
            return 0;
        }

        //执行规则
        //1. 与图形无关部分
        DescriptiveStatistics grossStat = new DescriptiveStatistics();
        for (MeterReadingVO reading : temple) {
            grossStat.addValue(reading.getReading());
        }

        //2. 恢复图形，寻找Peak值
        Map<String, DescriptiveStatistics> repo = SmartGridFormatterHelper.tabulateOrderQuarter(
            temple, Integer.MAX_VALUE);

        //Mean值大，说明存在 空调的可能性大
        //Sigma值大，说明存在用户行为的波动大
        //符合规律，X:气温, Y:在家, Z: 有空调, W：空调工作
        // Pr[ W | X,Y,Z] 越大
        if (grossStat.getMean() >= ruleThreshhold.get(1)
            && grossStat.getStandardDeviation() >= ruleThreshhold.get(2)) {
            return 1;
        }
        return 0;
    }

    /**
     * Setter method for property <tt>ruleThreshhold</tt>.
     * 
     * @param ruleThreshhold value to be assigned to property ruleThreshhold
     */
    public static void setRuleThreshhold(List<Double> ruleThreshhold) {
        RuleBasedPRUtil.ruleThreshhold.clear();
        RuleBasedPRUtil.ruleThreshhold.addAll(ruleThreshhold);
    }

}
