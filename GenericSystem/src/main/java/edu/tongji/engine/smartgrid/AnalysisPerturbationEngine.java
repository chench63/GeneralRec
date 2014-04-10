/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.io.IOException;
import java.sql.Date;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.velocity.VelocityContext;

import edu.tongji.crack.CrackObject;
import edu.tongji.crack.PrivacyCracker;
import edu.tongji.extend.gnuplot.AssembleTemplate;
import edu.tongji.extend.gnuplot.FigureFormatter;
import edu.tongji.extend.gnuplot.support.GenericMessage;
import edu.tongji.extend.gnuplot.support.VelocityContextHelper;
import edu.tongji.extend.noise.Noise;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.util.DateUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.util.VelocityUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 针对 扰动型隐私保护算法的分析引擎
 * 
 * @author chench
 * @version $Id: AnalysisEngine.java, v 0.1 2014-2-14 下午12:42:00 chench Exp $
 */
public class AnalysisPerturbationEngine extends SmartGridEngine {

    /** 高斯噪声产生范围*/
    private Noise                 noise;

    /** 数据文件存储绝对地址 */
    private String                absolutePath;

    /** 格式器*/
    private FigureFormatter       formatter;

    /** 隐私破解器*/
    private PrivacyCracker        cracker;

    /** 数据集组合模板*/
    private AssembleTemplate      assembleTemplate;

    /**Velocity上下文模板助手*/
    private VelocityContextHelper velocityContextHelper;

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#emulate()
     */
    @Override
    protected void emulate() {

        //0.计算均值与方差
        DescriptiveStatistics statInner = new DescriptiveStatistics();
        for (MeterReadingVO smartMeter : SmartGridDataSource.meterContexts) {
            statInner.addValue(smartMeter.getReading());
        }
        LoggerUtil
            .debug(logger, "Meter Reading Mean：" + String.format("%.3f", statInner.getMean())
                           + " SD：" + String.format("%.3f", statInner.getStandardDeviation()));

        //1.在原始数据后，添加隐私保护的数据
        int rowSize = SmartGridDataSource.meterContexts.size();
        for (int index = 0; index < rowSize; index++) {
            MeterReadingVO reading = SmartGridDataSource.meterContexts.get(index);
            double reads = noise.perturb(reading.getReading());
            LoggerUtil.debug(
                logger,
                (new StringBuilder())
                    .append("Tick：")
                    .append(
                        DateUtil.format(new Date(reading.getTimeVal()), DateUtil.LONG_FORMAT)
                            .substring(4, 12)).append("\tO：")
                    .append(StringUtil.alignLeft(String.format("%.2f", reading.getReading()), 8))
                    .append(" R：").append(StringUtil.alignLeft(String.format("%.2f", reads), 8)));

            SmartGridDataSource.meterContexts.add(new MeterReadingVO(reads, null, reading
                .getTimeVal()));
        }

        //2.破解还原数据
        if (cracker != null) {
            cracker.crack(new CrackObject(SmartGridDataSource.meterContexts), rowSize);
        }

        //3.Velocity 渲染测试数据文件
        String content = rendContent(rowSize, SmartGridDataSource.meterContexts.size() / rowSize);
        String fileAbsolutePath = (new StringBuilder(absolutePath)).append(
            DateUtil.formatCurrent(DateUtil.LONG_FORMAT)).toString();
        FileUtil.write(fileAbsolutePath, content);
    }

    /**
     * 格式化数据
     * 
     * @param matrics
     * @param blockSize
     */
    protected String rendContent(int rowSize, int columnSize) {
        //格式化数据
        String[][] matrics = formatter.formatToArrs(SmartGridDataSource.meterContexts, rowSize);

        try {
            //1. 填充VelocityContext，准备基础数据
            VelocityContext context = velocityContextHelper.fillContext(assembleTemplate,
                new GenericMessage(matrics));

            //2. 渲染报文正文内容
            String content = VelocityUtil.evaluate(context, assembleTemplate.getMainTemplate());

            return content;
        } catch (IOException e) {
            edu.tongji.util.ExceptionUtil.caught(e, "");
        }

        return null;
    }

    /**
     * Getter method for property <tt>absolutePath</tt>.
     * 
     * @return property value of absolutePath
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Setter method for property <tt>absolutePath</tt>.
     * 
     * @param absolutePath value to be assigned to property absolutePath
     */
    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
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
     * Getter method for property <tt>assembleTemplate</tt>.
     * 
     * @return property value of assembleTemplate
     */
    public AssembleTemplate getAssembleTemplate() {
        return assembleTemplate;
    }

    /**
     * Setter method for property <tt>assembleTemplate</tt>.
     * 
     * @param assembleTemplate value to be assigned to property assembleTemplate
     */
    public void setAssembleTemplate(AssembleTemplate assembleTemplate) {
        String mainTemplate = FileUtil.readLinesAsStream(assembleTemplate.getMainTemplate());
        assembleTemplate.setMainTemplate(mainTemplate);
        this.assembleTemplate = assembleTemplate;
    }

    /**
     * Getter method for property <tt>velocityContextHelper</tt>.
     * 
     * @return property value of velocityContextHelper
     */
    public VelocityContextHelper getVelocityContextHelper() {
        return velocityContextHelper;
    }

    /**
     * Setter method for property <tt>velocityContextHelper</tt>.
     * 
     * @param velocityContextHelper value to be assigned to property velocityContextHelper
     */
    public void setVelocityContextHelper(VelocityContextHelper velocityContextHelper) {
        this.velocityContextHelper = velocityContextHelper;
    }

    /**
     * Getter method for property <tt>formatter</tt>.
     * 
     * @return property value of formatter
     */
    public FigureFormatter getFormatter() {
        return formatter;
    }

    /**
     * Setter method for property <tt>formatter</tt>.
     * 
     * @param formatter value to be assigned to property formatter
     */
    public void setFormatter(FigureFormatter formatter) {
        this.formatter = formatter;
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

}
