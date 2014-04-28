/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.extend.gnuplot.support.SmartGridFormatterHelper;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 均值vs小时，格式化生成器<br/>
 * 数据按小时汇总并计算均值.
 * 
 * @author chench
 * @version $Id: HistographFormatter.java, v 0.1 2014-2-18 下午2:05:52 chench Exp $
 */
public class MeanSeqHourFormatter extends AbstractSeqTimeFormatter {

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#format(java.util.List)
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    public List<String> format(List context, int rowSize) {
        throw new OwnedException(FunctionErrorCode.NOT_DEFINITION);
    }

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#formatToArrs(java.util.List, int)
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
            Map<String, DescriptiveStatistics> repo = SmartGridFormatterHelper.tabulateOrderHour(
                context, rowSize);

            //2. 按规则输出至二维数组
            return doFormation(context.size() / rowSize, rowSize, repo);
        }

        return null;
    }

    /**
     * 按规则输出至二维数组
     * 
     * @param context
     * @param rowSize
     * @param repo
     */
    protected String[][] doFormation(int columnSize, int rowSize,
                                     Map<String, DescriptiveStatistics> repo) {
        String[][] matrics = new String[HOUR_RANGE][columnSize];
        for (int row = 0; row < HOUR_RANGE; row++) {
            for (int column = 0; column < columnSize; column++) {
                String key = (new StringBuilder()).append(row)
                    .append(HashKeyUtil.ELEMENT_SEPERATOR).append(column).toString();
                DescriptiveStatistics stat = repo.get(key);

                //判断输出为均值还是标准差
                if (mean) {
                    matrics[row][column] = String.format("%.2f", stat.getMean());
                } else {
                    matrics[row][column] = String.format("%.2f", stat.getStandardDeviation());
                }
            }
        }
        return matrics;
    }

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#needRowNum()
     */
    @Override
    public boolean needRowNum() {
        return true;
    }

}