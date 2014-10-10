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
import edu.tongji.noise.Noise;
import edu.tongji.util.EMUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * ExpectationMaximization估计均值 格式化生成器
 * 
 * 
 * @author Hanke Chen
 * @version $Id: EmSeqHourFormatter.java, v 0.1 4 Apr 2014 10:51:46 chench Exp $
 */
public class EmSeqTimeFormatter extends AbstractSeqTimeFormatter {

    /** 原始电表数据列号*/
    protected final static int ORIGIN_POSITION = 0;

    /** 高斯混合噪声*/
    protected Noise            noise;

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
            double[] resultEmstmt = EMUtil.estimate(noise, perturbationStat.getValues(), 30);

            //判断输出为均值还是标准差
            if (mean) {
                matrics[row][0] = String.format("%.2f", readingStat.getMean());
                matrics[row][1] = String.format("%.2f", resultEmstmt[0]);
            } else {
                matrics[row][0] = String.format("%.2f", readingStat.getStandardDeviation());
                matrics[row][1] = String.format("%.2f", resultEmstmt[1]);
            }

        }
        return matrics;
    }

    /**
     * Setter method for property <tt>noise</tt>.
     * 
     * @param noise value to be assigned to property noise
     */
    public void setNoise(Noise noise) {
        this.noise = noise;
    }

}
