/**
 * Tongji.edu Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import org.springframework.util.StopWatch;

import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.RandomUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 混合高斯模型引擎
 * 
 * @author chench
 * @version $Id: MultipleGaussEngine.java, v 0.1 2014-1-20 上午10:16:36 chench Exp $
 */
public class MultipleGaussEngine extends SmartGridEngine {

    /** 高斯噪声的数量*/
    private int      gauseNum;

    /** 高斯噪声产生范围*/
    private double[] gauseDomain;

    /** 主部和高斯噪声对应的比重系数*/
    private double[] weightDomain;

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @Override
    public void excute() {
        //0. 载入数据集
        dataSource.reload();

        //1.模拟记录读数
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (MeterReadingVO reading : SmartGridDataSource.meterContexts) {
            double reads = reading.getReading();
            for (int i = 0; i < gauseDomain.length; i++) {
                reads += weightDomain[i] * RandomUtil.nextGaussian(gauseDomain[i]);
            }
            LoggerUtil.debug(logger, "O：" + reading.getReading() + " R" + reads);
        }
    }

    /**
     * Getter method for property <tt>gauseNum</tt>.
     * 
     * @return property value of gauseNum
     */
    public int getGauseNum() {
        return gauseNum;
    }

    /**
     * Setter method for property <tt>gauseNum</tt>.
     * 
     * @param gauseNum value to be assigned to property gauseNum
     */
    public void setGauseNum(int gauseNum) {
        this.gauseNum = gauseNum;
    }

    /**
     * Getter method for property <tt>gauseDomain</tt>.
     * 
     * @return property value of gauseDomain
     */
    public double[] getGauseDomain() {
        return gauseDomain;
    }

    /**
     * Setter method for property <tt>gauseDomain</tt>.
     * 
     * @param gauseDomain value to be assigned to property gauseDomain
     */
    public void setGauseDomain(double[] gauseDomain) {
        this.gauseDomain = gauseDomain;
    }

    /**
     * Getter method for property <tt>weightDomain</tt>.
     * 
     * @return property value of weightDomain
     */
    public double[] getWeightDomain() {
        return weightDomain;
    }

    /**
     * Setter method for property <tt>weightDomain</tt>.
     * 
     * @param weightDomain value to be assigned to property weightDomain
     */
    public void setWeightDomain(double[] weightDomain) {
        this.weightDomain = weightDomain;
    }

}
