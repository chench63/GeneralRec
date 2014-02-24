/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.ArrayList;
import java.util.List;

import edu.tongji.util.DateUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 点状图格式化生成器
 * 
 * @author chench
 * @version $Id: PointgraphFormatter.java, v 0.1 2014-2-23 下午7:55:10 chench Exp $
 */
public class PointgraphFormatter implements FigureFormatter {

    protected final static char ELEMENT_SEPERATOR = '\t';

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#format(java.util.List, int)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<String> format(List context, int blockSize) {
        //智能电表处理逻辑
        if (!context.isEmpty() && context.get(0) instanceof MeterReadingVO) {

            //输出文本数据
            //格式：[用电量]
            List<String> stream = new ArrayList<String>();
            for (int row = blockSize, len = context.size(); row < len; row++) {
                MeterReadingVO reading = (MeterReadingVO) context.get(row);
                StringBuilder rowContext = (new StringBuilder())
                    .append(DateUtil.getHourOfDay(reading.getTimeVal())).append(ELEMENT_SEPERATOR)
                    .append(String.format("%.3f", reading.getReading()));
                stream.add(rowContext.toString());
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
        return false;
    }

}
