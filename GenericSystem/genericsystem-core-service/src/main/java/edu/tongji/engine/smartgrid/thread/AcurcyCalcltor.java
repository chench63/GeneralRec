/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid.thread;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import edu.tongji.extend.crack.PrivacyCracker;
import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.noise.Noise;
import edu.tongji.vo.BayesianEventVO;
import edu.tongji.vo.MeterReadingVO;

/**
 * 准确度分析所用线程抽象类
 * 
 * @author Hanke Chen
 * @version $Id: AcurcyCalcltor.java, v 0.1 2014-5-30 上午9:56:49 chench Exp $
 */
public abstract class AcurcyCalcltor extends Thread {

    /** 第一类错误统计器*/
    public static DescriptiveStatistics              FAULT_I_STAT   = null;

    /** 第二类错误统计器*/
    public static DescriptiveStatistics              FAULT_II_STAT  = null;

    /** 全局统计器*/
    public static DescriptiveStatistics              STAT           = null;

    /** 多维度统计缓存*/
    public static Map<String, DescriptiveStatistics> STAT_CACHE     = null;

    /** 电表读数缓存*/
    public static List<List<MeterReadingVO>>         CONTEXT_CACHE  = null;

    /** 贝叶斯网络事件缓存，用于计算精确度*/
    public static List<List<BayesianEventVO>>        ACCURACY_CACHE = null;

    /** 重复实验次数*/
    public static int                                ROUND          = 10000;

    /** 高斯噪声产生范围*/
    protected Noise                                  noise          = null;

    /** 哈希函数*/
    protected HashKeyCallBack                        hashKyGen      = null;

    /** 隐私破解器*/
    protected PrivacyCracker                         cracker        = null;

    /** 互斥量*/
    private final static Object                      mutex          = new Object();

    /** logger */
    protected final static Logger                    logger         = Logger
                                                                        .getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * 是否停止任务
     * 
     * @return
     */
    protected final boolean shouldStop() {

        synchronized (mutex) {
            if (ROUND < 0) {
                return true;
            }

            ROUND--;
        }

        return false;
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
     * Setter method for property <tt>hashKyGen</tt>.
     * 
     * @param hashKyGen value to be assigned to property hashKyGen
     */
    public void setHashKyGen(HashKeyCallBack hashKyGen) {
        this.hashKyGen = hashKyGen;
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
     * Setter method for property <tt>rOUND</tt>.
     * 
     * @param ROUND value to be assigned to property rOUND
     */
    public static void setROUND(int rOUND) {
        ROUND = rOUND;
    }

}
