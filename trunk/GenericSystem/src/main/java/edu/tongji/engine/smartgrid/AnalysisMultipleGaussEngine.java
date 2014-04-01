/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.velocity.VelocityContext;

import edu.tongji.crack.CrackObject;
import edu.tongji.crack.PrivacyCracker;
import edu.tongji.extend.gnuplot.AssembleTemplate;
import edu.tongji.extend.gnuplot.support.GenericMessage;
import edu.tongji.extend.gnuplot.support.VelocityContextHelper;
import edu.tongji.extend.noise.Noise;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.util.DateUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.VelocityUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 针对智能电网的分析引擎
 * 
 * @author chench
 * @version $Id: AnalysisEngine.java, v 0.1 2014-2-14 下午12:42:00 chench Exp $
 */
public class AnalysisMultipleGaussEngine extends SmartGridEngine {

    /** 高斯噪声产生范围*/
    private Noise[]               noises;

    /** 主部和高斯噪声对应的比重系数*/
    private double[]              weightDomain = { 1.0 };

    /** 数据文件存储绝对地址 */
    private String                absolutePath;

    /** 隐私破解器*/
    private PrivacyCracker        cracker;

    /** 数据集组合模板*/
    private AssembleTemplate      assembleTemplate;

    /**Velocity上下文模板助手*/
    private VelocityContextHelper velocityContextHelper;

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#assembleDataSet()
     */
    @Override
    protected void assembleDataSet() {
        //物理数据集为老数据集，
        //逻辑数据集保持稳健
        if (!dataSource.isFresh() && keepSteady) {
            return;
        }

        //交替上下文
        List<MeterReadingVO> context = new ArrayList<MeterReadingVO>();
        context.addAll(SmartGridDataSource.meterContexts);
        SmartGridDataSource.meterContexts.clear();

        //对上下文进行排序
        Collections.sort(context, new Comparator<MeterReadingVO>() {
            @Override
            public int compare(MeterReadingVO o1, MeterReadingVO o2) {
                return ((Long) (o1.getTimeVal() - o2.getTimeVal())).intValue();
            }
        });

        //处理数据集
        MeterReadingVO meterReading = null;
        for (MeterReadingVO meter : context) {

            if (meterReading == null) {
                //初始化
                meterReading = meter;
                continue;
            } else if (!DateUtil.sameDayAndHour(meterReading.getTimeVal(), meter.getTimeVal())) {
                LoggerUtil.debug(logger, "O：" + meterReading.getReading() + "   Date："
                                         + new Timestamp(meterReading.getTimeVal()));

                //新的电表计时周期
                SmartGridDataSource.meterContexts.add(meterReading);
                meterReading = meter;

                continue;
            }
            //在同一计时周期，累计读数
            meterReading.setReading(meterReading.getReading() + meter.getReading());

        }
    }

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#emulate()
     */
    @Override
    protected void emulate() {

        //1.在原始数据后，添加隐私保护的数据
        int rowSize = SmartGridDataSource.meterContexts.size();
        for (int index = 0; index < rowSize; index++) {
            MeterReadingVO reading = SmartGridDataSource.meterContexts.get(index);
            double reads = reading.getReading() * weightDomain[0];
            for (int i = 0; i < noises.length; i++) {
                reads += weightDomain[i + 1] * noises[i].random();
            }
            LoggerUtil.debug(logger, "O：" + reading.getReading() + " R：" + reads);

            SmartGridDataSource.meterContexts.add(new MeterReadingVO(reads, null, reading
                .getTimeVal()));
        }

        //2.Crack Privacy Scheme
        cracker.crack(new CrackObject(SmartGridDataSource.meterContexts), rowSize);

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
        String[][] matrics = new String[rowSize][columnSize];
        int capcity = SmartGridDataSource.meterContexts.size();
        for (int i = 0; i < capcity; i++) {
            int row = i % rowSize;
            int column = i / rowSize;
            matrics[row][column] = String.format("%.2f", SmartGridDataSource.meterContexts.get(i)
                .getReading());
        }

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
     * Getter method for property <tt>noises</tt>.
     * 
     * @return property value of noises
     */
    public Noise[] getNoises() {
        return noises;
    }

    /**
     * Setter method for property <tt>noises</tt>.
     * 
     * @param noises value to be assigned to property noises
     */
    public void setNoises(Noise[] noises) {
        this.noises = noises;
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

}
