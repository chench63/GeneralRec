/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import edu.tongji.extend.crack.support.PrivacyCrackObject;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.BayesianEventVO;
import edu.tongji.vo.MeterReadingVO;

/**
 * 准确度分析引擎
 * 
 * @author chench
 * @version $Id: AnalysisAccuracyEngine.java, v 0.1 18 May 2014 17:06:06 chench Exp $
 */
public class AnalysisAccuracyEngine extends SmartGridEngine {

    /** 重复实验次数*/
    private int                                               ROUND                         = 10000;

    /** 第一类错误统计器*/
    protected final static DescriptiveStatistics              FAULT_I_STAT                  = new DescriptiveStatistics();

    /** 第二类错误统计器*/
    protected final static DescriptiveStatistics              FAULT_II_STAT                 = new DescriptiveStatistics();

    /** 全局统计器*/
    protected final static DescriptiveStatistics              STAT                          = new DescriptiveStatistics();

    /** 电表读数缓存*/
    protected final static List<List<MeterReadingVO>>         CONTEXT_CACHE                 = new ArrayList<List<MeterReadingVO>>();

    /** 贝叶斯网络事件缓存，用于计算精确度*/
    protected final static List<List<BayesianEventVO>>        ACCURACY_CACHE                = new ArrayList<List<BayesianEventVO>>();

    /** 贝叶斯网络识别结果集合*/
    protected final static List<List<BayesianEventVO>>        BAYESIAN_NETWORK_RESULT_CACHE = new ArrayList<List<BayesianEventVO>>();

    /** 多维度统计缓存*/
    protected final static Map<String, DescriptiveStatistics> STAT_CACHE                    = new HashMap<String, DescriptiveStatistics>();

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#loadDataSet()
     */
    @Override
    protected void loadDataSet() {
        String dir = this.dataSource.getSourceEntity().get(TemplateType.REDD_SMART_GRID_TEMPLATE);
        File[] sources = FileUtil.parserFilesByPattern(dir);

        for (File fileHandler : sources) {
            //0. 读取文件
            String file = fileHandler.getPath();
            String[] lines = FileUtil.readLines(file);
            List<MeterReadingVO> source = new ArrayList<MeterReadingVO>();
            for (String line : lines) {
                if (StringUtil.isBlank(line)) {
                    continue;
                }

                source.add((MeterReadingVO) TemplateType.REDD_SMART_GRID_TEMPLATE
                    .parser(new ParserTemplate(line)));
            }

            //1. 整理数据集
            List<MeterReadingVO> target = new ArrayList<MeterReadingVO>();
            super.assembler.assemble(source, target);
            //2. 载入缓存
            CONTEXT_CACHE.add(target);
            //3. 输出日志
            LoggerUtil.info(logger, "Load file: " + file + " Size: " + target.size());
        }
    }

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#emulate()
     */
    @Override
    protected void emulate() {

        for (int round = 0; round < ROUND; round++) {
            BAYESIAN_NETWORK_RESULT_CACHE.clear();
            for (List<MeterReadingVO> context : CONTEXT_CACHE) {
                PrivacyCrackObject target = new PrivacyCrackObject(context);
                target.put(PrivacyCrackObject.MEAN_STAT, FAULT_I_STAT);
                target.put(PrivacyCrackObject.SD_STAT, FAULT_II_STAT);
                target.put(PrivacyCrackObject.STAT_CACHE, STAT_CACHE);
                target.put(PrivacyCrackObject.RESULT_CACHE, BAYESIAN_NETWORK_RESULT_CACHE);

                cracker.crackInnerNoise(target, noise, hashKyGen);
            }

            if (!BAYESIAN_NETWORK_RESULT_CACHE.isEmpty()) {
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
        if (BAYESIAN_NETWORK_RESULT_CACHE.isEmpty()) {
            //无数据返回
            return;
        }

        if (ACCURACY_CACHE.isEmpty()) {
            //初始化
            loadBayesianEventCache();
        }

        if (ACCURACY_CACHE.size() != BAYESIAN_NETWORK_RESULT_CACHE.size()) {
            LoggerUtil.warn(logger, "RESULT CACHE DIDNT CORRESPOND WITH ACCURACY CACHE!");
            return;
        }

        float faultTypeI = 0;
        float faultTypeII = 0;
        int testCaseNum = 0;

        for (int dataSetIndex = 0; dataSetIndex < BAYESIAN_NETWORK_RESULT_CACHE.size(); dataSetIndex++) {
            List<BayesianEventVO> dataSet = BAYESIAN_NETWORK_RESULT_CACHE.get(dataSetIndex);
            List<BayesianEventVO> standardSet = ACCURACY_CACHE.get(dataSetIndex);
            testCaseNum += dataSet.size();

            for (int index = 0; index < dataSet.size(); index++) {
                //检查是否匹配
                if (standardSet.get(index).getTimeVal() != dataSet.get(index).getTimeVal()) {
                    throw new RuntimeException(
                        "RESULT ELEMENT DIDNT CORRESPOND WITH ACCURACY ELEMENT! ARRAY: "
                                + dataSetIndex);
                }

                //计算第一类错误
                if (standardSet.get(index).getAc() == 0 && dataSet.get(index).getAc() == 1) {
                    faultTypeI++;
                    continue;
                }

                //计算第二类错误
                if (standardSet.get(index).getAc() == 1 && dataSet.get(index).getAc() == 0) {
                    faultTypeII++;
                }
            }
        }

        //计算准确度
        FAULT_I_STAT.addValue(faultTypeI / testCaseNum);
        FAULT_II_STAT.addValue(faultTypeII / testCaseNum);
        STAT.addValue(1 - (faultTypeI + faultTypeII) / testCaseNum);

        //输出日志
        LoggerUtil.info(
            logger,
            (new StringBuilder("I: ")).append(faultTypeI / testCaseNum).append(" II: ")
                .append(faultTypeII / testCaseNum).append(" A: ")
                .append(1 - (faultTypeI + faultTypeII) / testCaseNum));

    }

    /**
     * 初始化 贝叶斯网络事件缓存
     */
    protected void loadBayesianEventCache() {
        String dir = this.dataSource.getSourceEntity().get(TemplateType.BAYESIAN_EVENT_TEMPLATE);
        File[] sources = FileUtil.parserFilesByPattern(dir);

        for (File fileHandler : sources) {
            //0. 读取文件
            String file = fileHandler.getPath();
            String[] lines = FileUtil.readLines(file);

            //1. 解析内容
            List<BayesianEventVO> array = new ArrayList<BayesianEventVO>();
            for (String line : lines) {
                if (StringUtil.isBlank(line)) {
                    continue;
                }

                array.add((BayesianEventVO) TemplateType.BAYESIAN_EVENT_TEMPLATE
                    .parser(new ParserTemplate(line)));
            }

            //2. 按升序排序
            Collections.sort(array);

            //3. 加入缓存
            ACCURACY_CACHE.add(array);
        }
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
