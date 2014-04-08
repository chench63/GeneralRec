/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.util.DateUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 
 * @author chench
 * @version $Id: MeanSeqQuarterFormatter.java, v 0.1 5 Apr 2014 15:17:59 chench Exp $
 */
public class MeanSeqQuarterFormatter extends MeanSeqHourFormatter {

    /** 
     * Map生成Key方法 KEY = [HOUR]_[Quarter]_[COLUMN_SEQ]
     * 
     * @see edu.tongji.extend.gnuplot.MeanSeqHourFormatter#generateKey(edu.tongji.vo.MeterReadingVO, int, int)
     */
    @Override
    protected String generateKey(MeterReadingVO reading, int index, int rowSize) {
        return (new StringBuilder()).append(DateUtil.getHourOfDay(reading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR)
            .append(DateUtil.getMinOfHour(reading.getTimeVal()) / 15)
            .append(HashKeyUtil.ELEMENT_SEPERATOR).append(index / rowSize).toString();
    }

    /** 
     * @see edu.tongji.extend.gnuplot.MeanSeqHourFormatter#doFormation(java.util.List, int, java.util.Map)
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected String[][] doFormation(List context, int rowSize,
                                     Map<String, DescriptiveStatistics> repo) {
        int columnSize = context.size() / rowSize;
        String[][] matrics = new String[HOUR_RANGE * QUARTER_RANGE][columnSize];
        for (int row = 0; row < HOUR_RANGE; row++) {
            for (int quarter = 0; quarter < QUARTER_RANGE; quarter++) {
                for (int column = 0; column < columnSize; column++) {
                    String key = (new StringBuilder()).append(row)
                        .append(HashKeyUtil.ELEMENT_SEPERATOR).append(quarter)
                        .append(HashKeyUtil.ELEMENT_SEPERATOR).append(column).toString();
                    DescriptiveStatistics stat = repo.get(key);
                    matrics[row * QUARTER_RANGE + quarter][column] = String.format("%.2f",
                        stat.getMean());
                }
            }
        }
        return matrics;
    }
}
