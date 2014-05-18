/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.extend.noise.Noise;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 线性拟合破解器
 * 
 * @author chench
 * @version $Id: LinearExpectationCracker.java, v 0.1 2014-2-22 下午12:00:04 chench Exp $
 */
public class LinearExpectationCracker extends ExpectationCracker {

    /** 
     * @see edu.tongji.crack.PrivacyCracker#crack(edu.tongji.crack.CrackObject, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void crack(CrackObject object, int blockSize) {
        //1.获得列数
        int columnSeq = object.getTarget().size() / blockSize;

        //2.计算各列的均值（期望）
        //For each column：
        //  行总值 / 行数量
        double[] expectations = new double[columnSeq];
        double sum = 0.0;
        for (int index = 0, len = object.getTarget().size(); index < len; index++) {
            //完成一列和的计算，计算均值
            if ((index % blockSize == 0) && index != 0) {
                expectations[index / blockSize - 1] = sum / blockSize;
                sum = 0.0;
            }

            //执行单列数据求和
            MeterReadingVO reading = (MeterReadingVO) object.getTarget().get(index);
            sum += reading.getReading();

            //最后一列特殊处理
            if (index == (len - 1)) {
                expectations[columnSeq - 1] = sum / blockSize;
            }
        }

        //3.执行破解部分逻辑
        //根据期望的偏差，对各个数据进行修正
        for (int column = 1; column < columnSeq; column++) {

            //线性回归，计算截距intercept和斜率slope
            SimpleRegression regression = regression(object.getTarget().subList(0, blockSize),
                object.getTarget().subList(column * blockSize, (column + 1) * blockSize));

            //恢复加密数据
            for (int index = column * blockSize; index < (column + 1) * blockSize; index++) {
                //修正数值，List保存引用，所以不用set回List对象
                MeterReadingVO reading = (MeterReadingVO) object.getTarget().get(index);
                double readingValue = regression.predict(reading.getReading());
                readingValue = readingValue < 0 ? 0.0 : readingValue;

                //记入日志
                StringBuilder loggerMsg = null;
                if (logger.isDebugEnabled()) {
                    MeterReadingVO originalReading = (MeterReadingVO) object.getTarget().get(
                        index % blockSize);
                    String origStr = String.format("%.0f", originalReading.getReading());
                    String reportStr = String.format("%.3f", reading.getReading());
                    String crackStr = String.format("%.3f", readingValue);

                    loggerMsg = new StringBuilder("Crack ColumnSeq：").append(column).append("  O：")
                        .append(StringUtil.alignRight(origStr, 5)).append("\tR：")
                        .append(StringUtil.alignRight(reportStr, 8)).append("\tF：")
                        .append(StringUtil.alignRight(crackStr, 8));
                    LoggerUtil.debug(logger, loggerMsg.toString());
                }

                reading.setReading(readingValue);
            }

            //统计处理
            double mseValue = (Double) statisticianFactory.getBean("mse").calculate(
                object.getTarget().subList(0, blockSize),
                object.getTarget().subList(blockSize * column, blockSize * (column + 1)));
            double rmseValue = (Double) statisticianFactory.getBean("rmse").calculate(
                object.getTarget().subList(0, blockSize),
                object.getTarget().subList(blockSize * column, blockSize * (column + 1)));
            StringBuilder loggerMsg = (new StringBuilder()).append("Regression: Y = ")
                .append(String.format("%.4f", regression.getSlope())).append("X + ")
                .append(String.format("%.4f", regression.getIntercept()))
                .append("\tOverall: MSE: ").append(String.format("%.4f", mseValue))
                .append("\tRMSE: ").append(String.format("%.4f", rmseValue));
            LoggerUtil.debug(logger, loggerMsg.toString());
        }
    }

    /** 
     * @see edu.tongji.crack.PrivacyCracker#crackInnerNoise(edu.tongji.crack.CrackObject, edu.tongji.extend.noise.Noise)
     */
    @Override
    public void crackInnerNoise(CrackObject object, Noise noise) {
        throw new OwnedException(FunctionErrorCode.ILLEGAL_PARAMETER);
    }

    /**
     * 
     * 
     * @param ySet
     * @param xSet
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected SimpleRegression regression(List ySet, List xSet) {

        SimpleRegression regression = new SimpleRegression();

        for (int i = 0; i < ySet.size(); i++) {
            MeterReadingVO xEle = (MeterReadingVO) xSet.get(i);
            MeterReadingVO yEle = (MeterReadingVO) ySet.get(i);
            regression.addData(xEle.getReading(), yEle.getReading());
        }

        return regression;
    }

}
