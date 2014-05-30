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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.engine.smartgrid.thread.AcurcyCalcltor;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.ExceptionUtil;
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

    /** 数据记录*/
    private List<AcurcyCalcltor>                              calculator;

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#loadDataSet()
     */
    @Override
    protected void loadDataSet() {

        //1. 加载电表读数
        this.loadMeteringReadingCache();

        //2. 加载 贝叶斯网络事件缓存
        this.loadBayesianEventCache();
    }

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#emulate()
     */
    @Override
    protected void emulate() {
        try {
            //配置线程上下文
            AcurcyCalcltor.FAULT_I_STAT = FAULT_I_STAT;
            AcurcyCalcltor.FAULT_II_STAT = FAULT_II_STAT;
            AcurcyCalcltor.STAT = STAT;
            AcurcyCalcltor.STAT_CACHE = STAT_CACHE;
            AcurcyCalcltor.CONTEXT_CACHE = CONTEXT_CACHE;
            AcurcyCalcltor.ACCURACY_CACHE = ACCURACY_CACHE;
            AcurcyCalcltor.ROUND = ROUND;

            //启动线程
            ExecutorService exec = Executors.newCachedThreadPool();
            for (AcurcyCalcltor runnable : calculator) {
                runnable.setNoise(noise);
                runnable.setCracker(cracker);
                runnable.setHashKyGen(hashKyGen);
                exec.execute(runnable);
            }
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);

            //输出日志
            LoggerUtil.info(
                logger,
                (new StringBuilder("Final: I: "))
                    .append(String.format("%.6f", FAULT_I_STAT.getMean())).append("(")
                    .append(FAULT_I_STAT.getStandardDeviation()).append(")").append(" II: ")
                    .append(String.format("%.6f", FAULT_II_STAT.getMean())).append("(")
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
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "ExecutorService await crush! ");
        }

    }

    /**
     * 加载电表读数
     */
    protected void loadMeteringReadingCache() {
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

    /**
     * Setter method for property <tt>calculator</tt>.
     * 
     * @param calculator value to be assigned to property calculator
     */
    public void setCalculator(List<AcurcyCalcltor> calculator) {
        this.calculator = calculator;
    }

}
