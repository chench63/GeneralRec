/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import edu.tongji.crack.CrackObject;
import edu.tongji.crack.ExpectationSeqDayCracker;
import edu.tongji.crack.PrivacyCracker;
import edu.tongji.extend.noise.Noise;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 准确度分析引擎
 * 
 * @author chench
 * @version $Id: AnalysisAccuracyEngine.java, v 0.1 18 May 2014 17:06:06 chench Exp $
 */
public class AnalysisAccuracyEngine extends SmartGridEngine {

    /** 高斯噪声产生范围*/
    private Noise                                   noise;

    /** 隐私破解器*/
    private PrivacyCracker                          cracker;

    /** 第一类错误统计器*/
    private final static DescriptiveStatistics      FAULT_I_STAT  = new DescriptiveStatistics();

    /** 第二类错误统计器*/
    private final static DescriptiveStatistics      FAULT_II_STAT = new DescriptiveStatistics();

    /** 第二类错误统计器*/
    private final static DescriptiveStatistics      STAT          = new DescriptiveStatistics();

    /** 缓存*/
    private final static List<List<MeterReadingVO>> cache         = new ArrayList<List<MeterReadingVO>>();

    /** logger */
    protected final static Logger                   logger        = Logger
                                                                      .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#loadDataSet()
     */
    @Override
    protected void loadDataSet() {
        String dir = this.dataSource.getSourceEntity().get(TemplateType.REDD_SMART_GRID_TEMPLATE);
        for (int i = 1; i <= 6; i++) {
            //0. 读取文件
            String file = (new StringBuilder(dir)).append("H").append(i).append("_.*").toString();
            dataSource.getSourceEntity().put(TemplateType.REDD_SMART_GRID_TEMPLATE, file);
            dataSource.reload();
            //1. 整理数据集
            super.assembleDataSet();
            //2. 载入缓存
            cache.add(new ArrayList<MeterReadingVO>(SmartGridDataSource.meterContexts));
        }
        SmartGridDataSource.meterContexts.clear();
    }

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#assembleDataSet()
     */
    @Override
    protected void assembleDataSet() {

    }

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#emulate()
     */
    @Override
    protected void emulate() {

        for (int round = 0; round < 1000; round++) {
            ExpectationSeqDayCracker.CP_RESULT.clear();
            for (List<MeterReadingVO> context : cache) {
                cracker.crackInnerNoise(new CrackObject(context), noise);
            }
            accuracy();
        }

        LoggerUtil.info(
            logger,
            (new StringBuilder("Final: I: ")).append(String.format("%.6f", FAULT_I_STAT.getMean()))
                .append(" II: ").append(String.format("%.6f", FAULT_II_STAT.getMean()))
                .append(" A: ").append(String.format("%.6f", STAT.getMean())));

    }

    /**
     * 计算准确度;
     * 硬编码，风格很烂
     */
    protected void accuracy() {
        int faultTypeI = 0;
        int faultTypeII = 0;

        //计算第一类错误
        // 36 + 11 + 2 = 49
        for (int i = 9; i < 45; i++) {
            if (ExpectationSeqDayCracker.CP_RESULT.get(i) > 0.5) {
                faultTypeI++;
            }
        }
        for (int i = 46; i < 57; i++) {
            if (ExpectationSeqDayCracker.CP_RESULT.get(i) > 0.5) {
                faultTypeI++;
            }
        }
        for (int i = 71; i < 73; i++) {
            if (ExpectationSeqDayCracker.CP_RESULT.get(i) > 0.5) {
                faultTypeI++;
            }
        }

        //计算第二类错误
        // 1 + 5 = 6
        if (ExpectationSeqDayCracker.CP_RESULT.get(45) < 0.5) {
            faultTypeII++;
        }
        for (int i = 66; i < 71; i++) {
            if (ExpectationSeqDayCracker.CP_RESULT.get(i) < 0.5) {
                faultTypeII++;
            }
        }

        //计算准确度
        FAULT_I_STAT.addValue(faultTypeI / 49.0);
        FAULT_II_STAT.addValue(faultTypeII / 6.0);
        STAT.addValue(1 - (faultTypeI + faultTypeII) / 55.0);

        //输出日志
        LoggerUtil.info(
            logger,
            (new StringBuilder("I: ")).append(faultTypeI / 49.0).append(" II: ")
                .append(faultTypeII / 6.0).append(" A: ")
                .append(1 - (faultTypeI + faultTypeII) / 55.0));

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
     * Getter method for property <tt>cracker</tt>.
     * 
     * @return property value of cracker
     */
    public PrivacyCracker getCracker() {
        return cracker;
    }

    /**
     * Setter method for property <tt>cracker</tt>.
     * 
     * @param cracker value to be assigned to property cracker
     */
    public void setCracker(PrivacyCracker cracker) {
        this.cracker = cracker;
    }

}
