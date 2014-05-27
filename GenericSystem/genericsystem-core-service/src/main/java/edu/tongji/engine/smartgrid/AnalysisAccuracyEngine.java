/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import edu.tongji.extend.crack.CrackObject;
import edu.tongji.extend.crack.ExpectationCracker;
import edu.tongji.extend.crack.PrivacyCracker;
import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.noise.Noise;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 准确度分析引擎
 * 
 * @author chench
 * @version $Id: AnalysisAccuracyEngine.java, v 0.1 18 May 2014 17:06:06 chench Exp $
 */
public class AnalysisAccuracyEngine extends SmartGridEngine {

    /** 高斯噪声产生范围*/
    private Noise                                           noise         = null;

    /** 哈希函数*/
    private HashKeyCallBack                                 hashKyGen     = null;

    /** 隐私破解器*/
    private PrivacyCracker                                  cracker       = null;

    /** 重复实验次数*/
    private int                                             ROUND         = 10000;

    /** 第一类错误统计器*/
    public final static DescriptiveStatistics               FAULT_I_STAT  = new DescriptiveStatistics();

    /** 第二类错误统计器*/
    public final static DescriptiveStatistics               FAULT_II_STAT = new DescriptiveStatistics();

    /** 全局统计器*/
    public final static DescriptiveStatistics               STAT          = new DescriptiveStatistics();

    /** 缓存*/
    private final static List<List<MeterReadingVO>>         cache         = new ArrayList<List<MeterReadingVO>>();

    /** 多维度统计缓存*/
    private final static Map<String, DescriptiveStatistics> STAT_CACHE    = new HashMap<String, DescriptiveStatistics>();

    /** logger */
    protected final static Logger                           logger        = Logger
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
            SmartGridDataSource.meterContexts.clear();
            dataSource.reload();
            //1. 整理数据集
            super.assembleDataSet();
            //2. 载入缓存
            cache.add(new ArrayList<MeterReadingVO>(SmartGridDataSource.meterContexts));
            //3. 输出日志
            LoggerUtil.info(logger, "Load file: " + file + " Size: "
                                    + SmartGridDataSource.meterContexts.size());
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

        for (int round = 0; round < ROUND; round++) {
            ExpectationCracker.CP_RESULT.clear();
            for (List<MeterReadingVO> context : cache) {
                CrackObject target = new CrackObject(context);
                target.put(CrackObject.MEAN_STAT, FAULT_I_STAT);
                target.put(CrackObject.SD_STAT, FAULT_II_STAT);
                target.put(CrackObject.STAT_CACHE, STAT_CACHE);
                cracker.crackInnerNoise(target, noise, hashKyGen);
            }

            if (!ExpectationCracker.CP_RESULT.isEmpty()) {
                accuracy();
            }
        }

        LoggerUtil.info(
            logger,
            (new StringBuilder("Final: I: ")).append(String.format("%.6f", FAULT_I_STAT.getMean()))
                .append("(").append(FAULT_I_STAT.getStandardDeviation()).append(")")
                .append(" II: ").append(String.format("%.6f", FAULT_II_STAT.getMean())).append("(")
                .append(FAULT_II_STAT.getStandardDeviation()).append(")").append(" A: ")
                .append(String.format("%.6f", STAT.getMean())).append("(")
                .append(STAT.getStandardDeviation()).append(")"));

        if (!STAT_CACHE.isEmpty()) {
            StringBuilder logMsg = new StringBuilder();

            for (String key : hashKyGen.keyArr()) {
                DescriptiveStatistics stat = STAT_CACHE.get(key);

                if (stat == null) {
                    //为空，返回
                    continue;
                }
                logMsg.append(StringUtil.BREAK_LINE).append(key).append('\t')
                    .append(stat.getMean()).append('\t').append(stat.getStandardDeviation());
            }

            LoggerUtil.info(logger, logMsg);
        }

    }

    /**
     * 计算准确度;
     * 硬编码，风格很烂
     */
    protected void accuracy() {
        if (ExpectationCracker.CP_RESULT.isEmpty()) {
            //无数据返回
            return;
        }

        int faultTypeI = 0;
        int faultTypeII = 0;

        //计算第一类错误
        // 36 + 11 + 2 = 49
        for (int i = 9; i < 45; i++) {
            if (ExpectationCracker.CP_RESULT.get(i) > 0.5) {
                faultTypeI++;
            }
        }
        for (int i = 46; i < 57; i++) {
            if (ExpectationCracker.CP_RESULT.get(i) > 0.5) {
                faultTypeI++;
            }
        }
        for (int i = 71; i < 73; i++) {
            if (ExpectationCracker.CP_RESULT.get(i) > 0.5) {
                faultTypeI++;
            }
        }

        //计算第二类错误
        // 1 + 5 = 6
        if (ExpectationCracker.CP_RESULT.get(45) < 0.5) {
            faultTypeII++;
        }
        for (int i = 66; i < 71; i++) {
            if (ExpectationCracker.CP_RESULT.get(i) < 0.5) {
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

    /**
     * Setter method for property <tt>hashKyGen</tt>.
     * 
     * @param hashKyGen value to be assigned to property hashKyGen
     */
    public void setHashKyGen(HashKeyCallBack hashKyGen) {
        this.hashKyGen = hashKyGen;
    }

    /**
     * Setter method for property <tt>rOUND</tt>.
     * 
     * @param ROUND value to be assigned to property rOUND
     */
    public void setROUND(int rOUND) {
        ROUND = rOUND;
    }

}
