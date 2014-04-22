/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.extend.gnuplot.support.SmartGridFormatterHelper;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 均值格式器，按刻分
 * 
 * @author chench
 * @version $Id: MeanSeqQuarterFormatter.java, v 0.1 5 Apr 2014 15:17:59 chench Exp $
 */
public class MeanSeqQuarterFormatter extends MeanSeqHourFormatter {

    /**
     * @see edu.tongji.extend.gnuplot.MeanSeqHourFormatter#formatToArrs(java.util.List, int)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public String[][] formatToArrs(List context, int rowSize) {
        //异常处理逻辑
        if (context == null || context.isEmpty()) {
            return null;
        }

        //智能电表处理逻辑
        if (context.get(0) instanceof MeterReadingVO) {

            //1. 使用Map汇总数据
            Map<String, DescriptiveStatistics> repo = SmartGridFormatterHelper
                .tabulateOrderQuarter(context, rowSize);

            //2. 按规则输出至二维数组
            return doFormation(context.size() / rowSize, rowSize, repo);
        }

        return null;
    }

    /** 
     * @see edu.tongji.extend.gnuplot.MeanSeqHourFormatter#doFormation(java.util.List, int, java.util.Map)
     */
    @Override
    protected String[][] doFormation(int columnSize, int rowSize,
                                     Map<String, DescriptiveStatistics> repo) {
        String[][] matrics = new String[HOUR_RANGE * QUARTER_RANGE][columnSize];
        for (int row = 0; row < HOUR_RANGE; row++) {
            for (int quarter = 0; quarter < QUARTER_RANGE; quarter++) {
                for (int column = 0; column < columnSize; column++) {
                    String key = (new StringBuilder()).append(row)
                        .append(HashKeyUtil.ELEMENT_SEPERATOR).append(quarter)
                        .append(HashKeyUtil.ELEMENT_SEPERATOR).append(column).toString();
                    DescriptiveStatistics stat = repo.get(key);

                    //判断输出为均值还是标准差
                    if (mean) {
                        matrics[row * QUARTER_RANGE + quarter][column] = String.format("%.2f",
                            stat.getMean());
                    } else {
                        matrics[row * QUARTER_RANGE + quarter][column] = String.format("%.2f",
                            stat.getStandardDeviation());
                    }
                }
            }
        }
        return matrics;
    }
}
