/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.util.HashKeyUtil;

/**
 * 按15分钟分，取最大值
 * 
 * @author chench
 * @version $Id: MaxSeqQuarterFormatter.java, v 0.1 15 Apr 2014 12:42:05 chench Exp $
 */
public class MaxSeqQuarterFormatter extends MeanSeqQuarterFormatter {

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

                    matrics[row * QUARTER_RANGE + quarter][column] = String.format("%.2f",
                        stat.getMax());
                }
            }
        }
        return matrics;
    }

}
