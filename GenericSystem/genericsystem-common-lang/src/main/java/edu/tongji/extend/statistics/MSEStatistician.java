/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.statistics;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * MSE统计工具
 * 
 * @author chench
 * @version $Id: MSEStatistician.java, v 0.1 2014-2-22 下午12:28:37 chench Exp $
 */
public class MSEStatistician implements Statistician {

    /** 
     * @see edu.tongji.extend.statistics.Statistician#calculate(java.util.List, java.util.List)
     */
    @Override
    public Number calculate(List<Statisticable> l1, List<Statisticable> l2) {

        //长度不一致，或者为空
        if (l1.isEmpty() | l2.isEmpty() | l1.size() > l2.size()) {
            return null;
        }

        //计算MSE的值
        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (int i = 0, j = l1.size(); i < j; i++) {
            double element = Math.abs(l1.get(i).getValue().doubleValue()
                                      - l2.get(i).getValue().doubleValue());
            stat.addValue(element);
        }
        return stat.getMean();
    }

}
