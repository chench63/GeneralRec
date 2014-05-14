/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ai.cluster;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.cache.WeatherCache;
import edu.tongji.util.DateUtil;
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
     * 引用规则, 识别标签 <br/>
     * 1. 温度 <br/>
     * 2. 当日的均值<br/>
     * 3. 当日的方差<br/><br/>
     * <b>规则</b>： <br/>
     * ruleThreshhold : 温度，均值，方差
     * 
     * @param samples   按日为序的样本集合
     * @return  -1: off, 0:Unknown, 1：On
     */
    public static int recognize(List<MeterReadingVO> samples) {

        //0. 整理数据按天汇总
        Map<String, DescriptiveStatistics> repsty = new HashMap<String, DescriptiveStatistics>();
        List<String> keySet = new ArrayList<String>();
        DescriptiveStatistics grossStat = new DescriptiveStatistics();
        for (MeterReadingVO sample : samples) {
            String key = DateUtil.format(new Date(sample.getTimeVal()), DateUtil.SHORT_FORMAT);
            DescriptiveStatistics stat = repsty.get(key);

            if (stat == null) {
                stat = new DescriptiveStatistics();
                repsty.put(key, stat);
                keySet.add(key);
            }
            stat.addValue(sample.getReading());
            grossStat.addValue(sample.getReading());
        }

        //1. 引用温度，筛选出高概率日期数据
        double tempThresh = ruleThreshhold.get(0);
        for (String key : keySet) {
            WeatherVO weather = WeatherCache.get(key);

            //低于阈值，则剔除
            if (weather.getHighTemper() < tempThresh) {
                repsty.remove(key);
            }
        }

        //数据不足，返回位置
        if (repsty.isEmpty()) {
            return 0;
        }

        //2. 执行规则
        //  1) 与图形无关部分
        //  Mean值大，说明存在 空调的可能性大
        //  Sigma值大，说明存在用户行为的波动大
        //  符合规律，X:气温, Y:在家, Z: 有空调, W：空调工作
        //      Pr[ W | X,Y,Z] 越大
        if (grossStat.getMean() >= ruleThreshhold.get(1)
            && grossStat.getStandardDeviation() >= ruleThreshhold.get(2)) {
            return 1;
        }
        for (DescriptiveStatistics stat : repsty.values()) {
            if (stat.getMean() >= ruleThreshhold.get(1)
                && stat.getStandardDeviation() >= ruleThreshhold.get(2)) {
                return 1;
            }
        }

        //  2) 恢复图形，寻找Peak值
        //        Map<String, DescriptiveStatistics> repo = SmartGridFormatterHelper.tabulateOrderQuarter(
        //            temple, Integer.MAX_VALUE);

        return -1;
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
