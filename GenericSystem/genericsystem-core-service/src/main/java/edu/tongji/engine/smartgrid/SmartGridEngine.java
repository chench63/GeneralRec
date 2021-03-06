/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import edu.tongji.engine.Engine;
import edu.tongji.engine.smartgrid.support.DataSetAssembler;
import edu.tongji.engine.smartgrid.support.QuarterSeqDataSetAssembler;
import edu.tongji.extend.crack.PrivacyCracker;
import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.noise.Noise;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 智能电网抽象类
 * 
 * @author Hanke Chen
 * @version $Id: SmartGridEngine.java, v 0.1 2013-12-18 上午11:05:23 chench Exp $
 */
public abstract class SmartGridEngine implements Engine {

    /** 数据源*/
    protected SmartGridDataSource             dataSource;

    /** 数据汇总合并器, 默认按刻钟计算*/
    protected DataSetAssembler                assembler = new QuarterSeqDataSetAssembler();

    /** 测试需要，统计平均运行时间*/
    public final static DescriptiveStatistics STAT      = new DescriptiveStatistics();

    /** 高斯噪声产生范围*/
    protected Noise                           noise     = null;

    /** 哈希函数*/
    protected HashKeyCallBack                 hashKyGen = null;

    /** 隐私破解器*/
    protected PrivacyCracker                  cracker   = null;

    /** logger */
    protected final static Logger             logger    = Logger
                                                            .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @Override
    public void excute() {

        //0. 载入数据集
        loadDataSet();

        //1. 整理数据集
        assembleDataSet();

        //2. 模拟记录读数
        emulate();
    }

    /**
     * 载入数据集
     */
    protected void loadDataSet() {
        SmartGridDataSource.meterContexts.clear();
        dataSource.reload();
        LoggerUtil.info(logger, "0. finish loading data set.");
    }

    /**
     * 组装数据集
     */
    protected void assembleDataSet() {

        //交替上下文
        List<MeterReadingVO> context = new ArrayList<MeterReadingVO>();
        context.addAll(SmartGridDataSource.meterContexts);
        SmartGridDataSource.meterContexts.clear();

        assembler.assemble(context, SmartGridDataSource.meterContexts);
        LoggerUtil.info(logger, "1. finish assembling data set.");
    }

    /**
     * 模拟记录读数
     */
    protected void emulate() {

    }

    /**
     * Getter method for property <tt>dataSource</tt>.
     * 
     * @return property value of dataSource
     */
    public SmartGridDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Setter method for property <tt>dataSource</tt>.
     * 
     * @param dataSource value to be assigned to property dataSource
     */
    public void setDataSource(SmartGridDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Getter method for property <tt>assembler</tt>.
     * 
     * @return property value of assembler
     */
    public DataSetAssembler getAssembler() {
        return assembler;
    }

    /**
     * Setter method for property <tt>assembler</tt>.
     * 
     * @param assembler value to be assigned to property assembler
     */
    public void setAssembler(DataSetAssembler assembler) {
        this.assembler = assembler;
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
     * Getter method for property <tt>hashKyGen</tt>.
     * 
     * @return property value of hashKyGen
     */
    public HashKeyCallBack getHashKyGen() {
        return hashKyGen;
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
