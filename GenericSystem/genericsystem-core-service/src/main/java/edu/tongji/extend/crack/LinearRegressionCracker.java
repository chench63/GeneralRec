/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;

import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.noise.Noise;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 线性拟合破解器
 * 
 * @author chench
 * @version $Id: LinearExpectationCracker.java, v 0.1 2014-2-22 下午12:00:04 chench Exp $
 */
public class LinearRegressionCracker implements PrivacyCracker {

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_CORE);

    /** 
     * @see edu.tongji.extend.crack.PrivacyCracker#crack(edu.tongji.extend.crack.CrackObject, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void crack(CrackObject object, int blockSize, Noise noise, HashKeyCallBack hashKyGen) {
        //1.获得列数
        List<MeterReadingVO> content = object.getTarget();
        int columnSeq = content.size() / blockSize;

        //2.执行破解逻辑
        //根据期望的偏差，对各个数据进行修正
        for (int column = 1; column < columnSeq; column++) {

            //线性回归，计算截距intercept和斜率slope
            SimpleRegression regression = regression(content.subList(0, blockSize),
                content.subList(column * blockSize, (column + 1) * blockSize));

            //恢复加密数据
            for (int index = column * blockSize; index < (column + 1) * blockSize; index++) {
                //修正数值，List保存引用，所以不用set回List对象
                MeterReadingVO reading = content.get(index);
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
            DescriptiveStatistics stats = new DescriptiveStatistics();
            for (int i = 0; i < blockSize; i++) {
                stats.addValue(Math.abs(content.get(i).getReading()
                                        - content.get(i + blockSize).getReading()));
            }

            double mseValue = stats.getMean();
            double rmseValue = Math.sqrt(Math.pow(stats.getStandardDeviation(), 2.0)
                                         + Math.pow(mseValue, 2.0d));
            StringBuilder loggerMsg = (new StringBuilder()).append("Regression: Y = ")
                .append(String.format("%.4f", regression.getSlope())).append("X + ")
                .append(String.format("%.4f", regression.getIntercept()))
                .append("\tOverall: MSE: ").append(String.format("%.4f", mseValue))
                .append("\tRMSE: ").append(String.format("%.4f", rmseValue));
            LoggerUtil.debug(logger, loggerMsg.toString());
        }
    }

    /** 
     * @see edu.tongji.extend.crack.PrivacyCracker#crackInnerNoise(edu.tongji.extend.crack.CrackObject, edu.tongji.noise.Noise)
     */
    @Override
    public void crackInnerNoise(CrackObject object, Noise noise, HashKeyCallBack hashKyGen) {
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
