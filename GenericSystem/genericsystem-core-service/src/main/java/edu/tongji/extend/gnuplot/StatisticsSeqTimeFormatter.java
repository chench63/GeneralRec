/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.extend.gnuplot.support.SmartGridFormatterHelper;
import edu.tongji.vo.MeterReadingVO;

/**
 * 均值vs小时，格式化生成器<br/>
 * 数据按小时汇总并计算均值.
 * 
 * @author Hanke Chen
 * @version $Id: HistographFormatter.java, v 0.1 2014-2-18 下午2:05:52 chench Exp $
 */
public class StatisticsSeqTimeFormatter extends AbstractSeqTimeFormatter {

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#formatToArrs(java.util.List, int)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public String[][] formatToArrs(List context, int rowSize, HashKeyCallBack hashKyGen) {
        //异常处理逻辑
        if (context == null || context.isEmpty()) {
            return null;
        }

        //智能电表处理逻辑
        if (context.get(0) instanceof MeterReadingVO) {

            //1. 使用Map汇总数据
            Map<String, DescriptiveStatistics> reading = SmartGridFormatterHelper.tabulate(context,
                0, rowSize, hashKyGen);
            Map<String, DescriptiveStatistics> perturbation = SmartGridFormatterHelper.tabulate(
                context, rowSize, rowSize * 2, hashKyGen);

            //2. 按规则输出至二维数组
            return doFormation(reading, perturbation, hashKyGen);
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
    protected String[][] doFormation(Map<String, DescriptiveStatistics> reading,
                                     Map<String, DescriptiveStatistics> perturbation,
                                     HashKeyCallBack hashKyGen) {

        String[] keySet = hashKyGen.keyArr();
        int rowNum = keySet.length;
        String[][] matrics = new String[rowNum][2];
        for (int row = 0; row < rowNum; row++) {
            String key = keySet[row];
            DescriptiveStatistics readingStat = reading.get(key);
            DescriptiveStatistics perturbationStat = perturbation.get(key);

            //判断输出为均值还是标准差
            if (mean) {
                matrics[row][0] = String.format("%.2f", readingStat.getMean());
                matrics[row][1] = String.format("%.2f", perturbationStat.getMean());
            } else {
                matrics[row][0] = String.format("%.2f", readingStat.getStandardDeviation());
                matrics[row][1] = String.format("%.2f", perturbationStat.getStandardDeviation());
            }
        }

        return matrics;
    }

}
