/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.util.DateUtil;
import edu.tongji.util.EMUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 
 * @author chench
 * @version $Id: MeanEmSeqHourFormatter.java, v 0.1 9 Apr 2014 18:21:08 chench Exp $
 */
public class EmSeqQuarterFormatter extends EmSeqHourFormatter {

    /** 
     * @see edu.tongji.extend.gnuplot.EmSeqHourFormatter#generateKey(edu.tongji.vo.MeterReadingVO, int, int)
     */
    @Override
    protected String generateKey(MeterReadingVO reading, int index, int rowSize) {
        return (new StringBuilder()).append(DateUtil.getHourOfDay(reading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR)
            .append(DateUtil.getMinOfHour(reading.getTimeVal()) / 15)
            .append(HashKeyUtil.ELEMENT_SEPERATOR).append(index / rowSize).toString();
    }

    /** 
     * @see edu.tongji.extend.gnuplot.EmSeqHourFormatter#doFormation(java.util.List, int, java.util.Map, java.util.Map)
     */
    @Override
    protected String[][] doFormation(int columnSize, int rowSize,
                                     Map<String, DescriptiveStatistics> repo,
                                     Map<String, List<Double>> repoGMM) {
        String[][] matrics = new String[HOUR_RANGE * QUARTER_RANGE][columnSize];

        for (int row = 0; row < HOUR_RANGE; row++) {
            for (int quarter = 0; quarter < QUARTER_RANGE; quarter++) {
                for (int column = 0; column < columnSize; column++) {
                    String key = (new StringBuilder()).append(row)
                        .append(HashKeyUtil.ELEMENT_SEPERATOR).append(quarter)
                        .append(HashKeyUtil.ELEMENT_SEPERATOR).append(column).toString();

                    if (column == ORIGIN_POSITION) {
                        DescriptiveStatistics stat = repo.get(key);

                        //判断输出为均值还是标准差
                        if (mean) {
                            matrics[row * QUARTER_RANGE + quarter][column] = String.format("%.2f",
                                stat.getMean());
                        } else {
                            matrics[row * QUARTER_RANGE + quarter][column] = String.format("%.2f",
                                stat.getStandardDeviation());
                        }
                    } else {
                        List<Double> samples = repoGMM.get(key);
                        double[] samp = new double[samples.size()];
                        for (int i = 0; i < samples.size(); i++) {
                            samp[i] = samples.get(i);
                        }

                        //判断输出为均值还是标准差
                        if (mean) {
                            matrics[row * QUARTER_RANGE + quarter][column] = String.format("%.2f",
                                EMUtil.estimate(noise, samp, 100)[0]);
                        } else {
                            matrics[row * QUARTER_RANGE + quarter][column] = String.format("%.2f",
                                EMUtil.estimate(noise, samp, 100)[1]);
                        }
                    }
                }
            }
        }
        return matrics;
    }

}
