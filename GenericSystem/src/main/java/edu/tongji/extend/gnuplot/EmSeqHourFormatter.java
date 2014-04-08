/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.extend.noise.Noise;
import edu.tongji.util.DateUtil;
import edu.tongji.util.EMUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * ExpectationMaximization估计均值 格式化生成器
 * 
 * 
 * @author chench
 * @version $Id: EmSeqHourFormatter.java, v 0.1 4 Apr 2014 10:51:46 chench Exp $
 */
public class EmSeqHourFormatter implements FigureFormatter {

    /** 原始电表数据列号*/
    protected final static int ORIGIN_POSITION = 0;

    /** 高斯混合噪声*/
    private Noise              noise;

    /** 最大迭代代数*/
    private int                maxIterations;

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#needRowNum()
     */
    @Override
    public boolean needRowNum() {
        throw new OwnedException(FunctionErrorCode.NOT_DEFINITION);
    }

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#format(java.util.List, int)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<String> format(List context, int rowSize) {
        throw new OwnedException(FunctionErrorCode.NOT_DEFINITION);
    }

    /** 
     * @see edu.tongji.extend.gnuplot.FigureFormatter#formatToArrs(java.util.List, int)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String[][] formatToArrs(List context, int rowSize) {
        //异常处理逻辑
        if (context == null || context.isEmpty()) {
            return null;
        }

        //智能电表处理逻辑
        if (context.get(0) instanceof MeterReadingVO) {
            //1. 使用Map汇总数据
            //  1) 汇总原始数据
            Map<String, DescriptiveStatistics> repoOri = new HashMap<String, DescriptiveStatistics>();
            for (int i = 0, j = rowSize; i < j; i++) {
                MeterReadingVO reading = (MeterReadingVO) context.get(i);
                //使用map整理数据，KEY = [HOUR]_[COLUMN_SEQ]
                String key = generateKey(reading, i, rowSize);

                DescriptiveStatistics stat = repoOri.get(key);
                if (stat == null) {
                    stat = new DescriptiveStatistics();
                }
                stat.addValue(reading.getReading());

                repoOri.put(key, stat);
            }

            //  2) 汇总高斯混合模型数据
            Map<String, List<Double>> repoGMM = new HashMap<String, List<Double>>();
            for (int i = rowSize, j = context.size(); i < j; i++) {
                MeterReadingVO reading = (MeterReadingVO) context.get(i);
                //使用map整理数据，KEY = [HOUR]_[COLUMN_SEQ]
                String key = generateKey(reading, i, rowSize);

                List<Double> samples = repoGMM.get(key);
                if (samples == null) {
                    samples = new ArrayList<Double>();
                }
                samples.add(reading.getReading());

                repoGMM.put(key, samples);
            }

            //2. 按规则输出至二维数组
            return doFormation(context, rowSize, repoOri, repoGMM);
        }

        return null;
    }

    /**
     * Map生成Key方法
     * 
     * @param reading   实体对象
     * @param index     序列号
     * @param rowSize   行数
     * @return
     */
    protected String generateKey(MeterReadingVO reading, int index, int rowSize) {
        return (new StringBuilder()).append(DateUtil.getHourOfDay(reading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR).append(index / rowSize).toString();
    }

    /**
     * 按规则输出至二维数组
     * 
     * @param context
     * @param rowSize
     * @param repo
     */
    @SuppressWarnings("rawtypes")
    protected String[][] doFormation(List context, int rowSize,
                                     Map<String, DescriptiveStatistics> repo,
                                     Map<String, List<Double>> repoGMM) {
        int columnSize = context.size() / rowSize;
        String[][] matrics = new String[HOUR_RANGE][columnSize];
        for (int row = 0; row < HOUR_RANGE; row++) {
            for (int column = 0; column < columnSize; column++) {
                String key = (new StringBuilder()).append(row)
                    .append(HashKeyUtil.ELEMENT_SEPERATOR).append(column).toString();

                if (column == ORIGIN_POSITION) {
                    DescriptiveStatistics stat = repo.get(key);
                    matrics[row][column] = String.format("%.2f", stat.getMean());
                } else {
                    List<Double> samples = repoGMM.get(key);
                    double[] samp = new double[samples.size()];
                    for (int i = 0; i < samples.size(); i++) {
                        samp[i] = samples.get(i);
                    }

                    matrics[row][column] = String.format("%.2f", EMUtil.estimate(noise, samp, 100));
                }
            }
        }
        return matrics;
    }

    /**
     * Getter method for property <tt>noise</tt>.
     * 
     * @return property value of noise
     */
    public Noise getNoise() {
        return noise;
    }

    /**
     * Setter method for property <tt>noise</tt>.
     * 
     * @param noise value to be assigned to property noise
     */
    public void setNoise(Noise noise) {
        this.noise = noise;
    }

    /**
     * Getter method for property <tt>maxIterations</tt>.
     * 
     * @return property value of maxIterations
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Setter method for property <tt>maxIterations</tt>.
     * 
     * @param maxIterations value to be assigned to property maxIterations
     */
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

}
