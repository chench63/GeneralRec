/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.util.DateUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 智能电网格式化工具，汇总数据帮助类
 * 
 * @author chench
 * @version $Id: SmartGridFormatterHelper.java, v 0.1 21 Apr 2014 15:31:03 chench Exp $
 */
public final class SmartGridFormatterHelper {

    /**
     * 按小时汇总数据 <br/>
     * 
     * @param contents
     * @param rowSize
     * @return
     */
    public static Map<String, DescriptiveStatistics> tabulateOrderHour(List<MeterReadingVO> contents,
                                                                       int rowSize) {
        return tabulate(contents, rowSize, 1);
    }

    /**
     * 按刻汇总数据 <br/>
     * 
     * @param contents
     * @param rowSize
     * @return
     */
    public static Map<String, DescriptiveStatistics> tabulateOrderQuarter(List<MeterReadingVO> contents,
                                                                          int rowSize) {
        return tabulate(contents, rowSize, 2);
    }

    /**
     * 汇总数据 <br/>
     * Mode 1 : 按小时汇总， 2 : 按刻汇总 
     * 
     * @param contents  上下文
     * @param rowSize   行数
     * @param mode      模式
     * @return
     */
    protected static Map<String, DescriptiveStatistics> tabulate(List<MeterReadingVO> contents,
                                                                 int rowSize, int mode) {
        Map<String, DescriptiveStatistics> repo = new HashMap<String, DescriptiveStatistics>();
        for (int i = 0, j = contents.size(); i < j; i++) {
            MeterReadingVO reading = contents.get(i);
            //使用map整理数据
            String key = null;

            if (mode == 1) {
                key = keyOrderHour(reading, i, rowSize);
            } else if (mode == 2) {
                key = keyOrderQuarter(reading, i, rowSize);
            } else {
                break;
            }

            DescriptiveStatistics stat = repo.get(key);
            if (stat == null) {
                stat = new DescriptiveStatistics();
                repo.put(key, stat);
            }
            stat.addValue(reading.getReading());

        }
        return repo;
    }

    /**
     * Map生成Key方法 KEY = [HOUR]_[COLUMN_SEQ]
     * 
     * @param reading   实体对象
     * @param index     序列号
     * @param rowSize   行数
     * @return
     */
    protected static String keyOrderHour(MeterReadingVO reading, int index, int rowSize) {
        return (new StringBuilder()).append(DateUtil.getHourOfDay(reading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR).append(index / rowSize).toString();
    }

    /**
     * Map生成Key方法 KEY = [HOUR]_[Quarter]_[COLUMN_SEQ]
     * 
     * @param reading
     * @param index
     * @param rowSize
     * @return
     */
    protected static String keyOrderQuarter(MeterReadingVO reading, int index, int rowSize) {
        return (new StringBuilder()).append(DateUtil.getHourOfDay(reading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR)
            .append(DateUtil.getMinOfHour(reading.getTimeVal()) / 15)
            .append(HashKeyUtil.ELEMENT_SEPERATOR).append(index / rowSize).toString();
    }
}
