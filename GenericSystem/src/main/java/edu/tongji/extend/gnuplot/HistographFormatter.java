/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tongji.util.DateUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 直方图格式化生成器
 * 
 * @author chench
 * @version $Id: HistographFormatter.java, v 0.1 2014-2-18 下午2:05:52 chench Exp $
 */
public class HistographFormatter implements FigureFormatter {

    protected final static char ELEMENT_SEPERATOR = '_';

    protected final static int  HOUR_RANGE        = 24;

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#format(java.util.List)
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    public List<String> format(List context, int blockSize) {
        //智能电表处理逻辑
        if (!context.isEmpty() && context.get(0) instanceof MeterReadingVO) {

            //对原始数据格式无要求，
            //使用map整理数据，KEY = [HOUR]_[COLUMN_SEQ]
            Map<String, Double> columns = new HashMap<String, Double>();
            for (int i = 0; i < context.size(); i++) {
                MeterReadingVO reading = (MeterReadingVO) context.get(i);
                String key = (new StringBuilder())
                    .append(DateUtil.getHourOfDay(reading.getTimeVal())).append(ELEMENT_SEPERATOR)
                    .append(i / blockSize).toString();

                Double readingValue = columns.get(key);
                if (readingValue == null) {
                    readingValue = 0.0;
                }
                columns.put(key, readingValue + reading.getReading());

            }

            //输出文本数据
            //格式：[行数]   [列1]    [列2]    [列3]
            List<String> stream = new ArrayList<String>();
            for (int row = 0; row < HOUR_RANGE; row++)
                for (int column = 0; column < (context.size() / blockSize); column++) {
                    String keyInside = (new StringBuilder()).append(row).append(ELEMENT_SEPERATOR)
                        .append(column).toString();
                    double reading = columns.get(keyInside);
                    stream.add(String.valueOf((int) reading));
                }
            return stream;
        }

        return null;
    }

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#needRowNum()
     */
    @Override
    public boolean needRowNum() {
        return true;
    }

}
