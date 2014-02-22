/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 比较均值差，破解对象
 * 
 * @author chench
 * @version $Id: StandardExpectationCracker.java, v 0.1 2014-2-20 上午9:51:28 chench Exp $
 */
public class StandardExpectationCracker extends ExpectationCracker {

    /** 
     * @see edu.tongji.crack.PrivacyCracker#crack(edu.tongji.crack.CrackObject)
     */
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
            double diffValue = expectations[0] - expectations[column];
            for (int index = column * blockSize; index < (column + 1) * blockSize; index++) {
                //修正数值，List保存引用，所以不用set回List对象
                MeterReadingVO reading = (MeterReadingVO) object.getTarget().get(index);
                double readingValue = reading.getReading();
                //                readingValue = (readingValue > 0) ? (readingValue + diffValue) : 0;
                readingValue = readingValue + diffValue;

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
        }
    }
}
