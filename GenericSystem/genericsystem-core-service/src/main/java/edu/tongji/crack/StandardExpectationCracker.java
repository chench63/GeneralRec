/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 列均值破解器
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

        //[数组]声明与定义,
        //此时数组成员 DescriptiveStatistics[0]与DescriptiveStatistics[1]
        //默认初始化为null
        DescriptiveStatistics[] stats = new DescriptiveStatistics[columnSeq];

        //[数组成员]定义
        for (int i = 0; i < columnSeq; i++) {
            stats[i] = new DescriptiveStatistics();
        }

        for (int index = 0, len = object.getTarget().size(); index < len; index++) {
            //执行单列数据求和
            MeterReadingVO reading = (MeterReadingVO) object.getTarget().get(index);
            //For each column：
            //  行总值 / 行数量
            stats[index / blockSize].addValue(reading.getReading());
        }

        //3.记入日志
        StringBuilder loggerMsg = new StringBuilder("StandardExpectationCracker：");
        for (int column = 0; column < columnSeq; column++) {
            loggerMsg.append("\nColumnSeq：").append(column).append("  Mean：")
                .append(String.format("%.4f", stats[column].getMean())).append("  SD：")
                .append(String.format("%.4f", stats[column].getStandardDeviation()));
        }
        LoggerUtil.info(logger, loggerMsg.toString());

    }
}
