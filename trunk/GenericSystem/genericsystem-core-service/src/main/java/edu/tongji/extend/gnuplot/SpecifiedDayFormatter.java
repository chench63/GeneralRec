/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.util.DateUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 
 * @author chench
 * @version $Id: SpecifiedDayFormatter.java, v 0.1 21 Apr 2014 12:03:35 chench Exp $
 */
public class SpecifiedDayFormatter extends AbstractSeqTimeFormatter {

    /** 指定日期*/
    private String specifiedDay;

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#formatToArrs(java.util.List, int)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String[][] formatToArrs(List context, int rowSize, HashKeyCallBack hashKyGen) {
        //异常处理逻辑
        if (context == null || context.isEmpty()) {
            return null;
        }

        //智能电表处理逻辑
        if (context.get(0) instanceof MeterReadingVO) {
            //1. 使用Map汇总数据
            //  1) 汇总原始数据
            Map<String, DescriptiveStatistics> repo = new HashMap<String, DescriptiveStatistics>();
            for (int i = 0, j = context.size(); i < j; i++) {
                MeterReadingVO reading = (MeterReadingVO) context.get(i);

                //非指定日期，跳过
                if (!isSpecifiedDay(reading)) {
                    continue;
                }

                //使用map整理数据，KEY = [HOUR]_[COLUMN_SEQ]
                String key = generateKey(reading, i, rowSize);

                DescriptiveStatistics stat = repo.get(key);
                if (stat == null) {
                    stat = new DescriptiveStatistics();
                    repo.put(key, stat);
                }
                stat.addValue(reading.getReading());

            }

            //2. 按规则输出至二维数组
            return doFormation(context.size() / rowSize, rowSize, repo);
        }

        return null;
    }

    /**
     * 判断是否为制定日期
     * 
     * @param reading
     * @return
     */
    protected boolean isSpecifiedDay(MeterReadingVO reading) {
        String dateStr = DateUtil.format(new Date(reading.getTimeVal()), DateUtil.SHORT_FORMAT);
        return StringUtil.equals(dateStr, this.specifiedDay);
    }

    /** 
     * Map生成Key方法 KEY = [HOUR]_[Quarter]_[COLUMN_SEQ]
     * 
     * @see edu.tongji.extend.gnuplot.StatisticsSeqTimeFormatter#generateKey(edu.tongji.vo.MeterReadingVO, int, int)
     */
    protected String generateKey(MeterReadingVO reading, int index, int rowSize) {
        return (new StringBuilder()).append(DateUtil.getHourOfDay(reading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR)
            .append(DateUtil.getMinOfHour(reading.getTimeVal()) / 15)
            .append(HashKeyUtil.ELEMENT_SEPERATOR).append(index / rowSize).toString();
    }

    /** 
     * @see edu.tongji.extend.gnuplot.StatisticsSeqTimeFormatter#doFormation(java.util.List, int, java.util.Map)
     */
    protected String[][] doFormation(int columnSize, int rowSize,
                                     Map<String, DescriptiveStatistics> repo) {
        String[][] matrics = new String[4 * 24][columnSize];
        for (int row = 0; row < 24; row++) {
            for (int quarter = 0; quarter < 4; quarter++) {
                for (int column = 0; column < columnSize; column++) {
                    String key = (new StringBuilder()).append(row)
                        .append(HashKeyUtil.ELEMENT_SEPERATOR).append(quarter)
                        .append(HashKeyUtil.ELEMENT_SEPERATOR).append(column).toString();
                    DescriptiveStatistics stat = repo.get(key);

                    //判断输出为均值还是标准差
                    if (stat == null) {
                        matrics[row * 4 + quarter][column] = String.valueOf(Double.NaN);

                        //记入日志
                        LoggerUtil.warn(logger,
                            (new StringBuilder("Key: ")).append(key).append(" Has no value."));
                    } else if (mean) {
                        matrics[row * 4 + quarter][column] = String.format("%.2f", stat.getMean());
                    } else {
                        matrics[row * 4 + quarter][column] = String.format("%.2f",
                            stat.getStandardDeviation());
                    }
                }
            }
        }
        return matrics;
    }

    /**
     * Getter method for property <tt>specifiedDay</tt>.
     * 
     * @return property value of specifiedDay
     */
    public String getSpecifiedDay() {
        return specifiedDay;
    }

    /**
     * Setter method for property <tt>specifiedDay</tt>.
     * 
     * @param specifiedDay value to be assigned to property specifiedDay
     */
    public void setSpecifiedDay(String specifiedDay) {
        this.specifiedDay = specifiedDay;
    }

}
