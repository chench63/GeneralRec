/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.extend.crack.support.PrivacyCrackObject;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 误差分析线程
 * 
 * @author chench
 * @version $Id: ErrorAcurcyCalcltor.java, v 0.1 2014-5-30 上午9:47:18 chench Exp $
 */
public class ErrorAcurcyCalcltor extends AcurcyCalcltor {

    /** 互斥锁 */
    private static final Object mutex = new Object();

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        while (!shouldStop()) {

            DescriptiveStatistics faultI = new DescriptiveStatistics();
            DescriptiveStatistics faultII = new DescriptiveStatistics();
            Map<String, DescriptiveStatistics> statCache = new HashMap<String, DescriptiveStatistics>();
            for (List<MeterReadingVO> context : CONTEXT_CACHE) {
                //1. 配置对象
                PrivacyCrackObject target = new PrivacyCrackObject(context);
                target.put(PrivacyCrackObject.MEAN_STAT, faultI);
                target.put(PrivacyCrackObject.SD_STAT, faultII);
                target.put(PrivacyCrackObject.STAT_CACHE, statCache);
                //2. 计算准确度
                cracker.crackInnerNoise(target, noise, hashKyGen);
            }

            accuracy(faultI, faultII, statCache);
        }
    }

    /**
     * 计算准确度;
     * 
     */
    protected void accuracy(DescriptiveStatistics faultI, DescriptiveStatistics faultII,
                            Map<String, DescriptiveStatistics> statCache) {

        synchronized (mutex) {
            for (double value : faultI.getValues()) {
                FAULT_I_STAT.addValue(value);
            }

            for (double value : faultII.getValues()) {
                FAULT_II_STAT.addValue(value);
            }

            for (String key : statCache.keySet()) {
                //1. 获取数据源
                DescriptiveStatistics source = statCache.get(key);

                //2. 获取目标数据位置
                DescriptiveStatistics target = STAT_CACHE.get(key);
                if (target == null) {
                    target = new DescriptiveStatistics();
                    STAT_CACHE.put(key, target);
                }

                //3. 添加数据
                for (double value : source.getValues()) {
                    target.addValue(value);
                }
            }
        }

        //输出日志
        LoggerUtil.info(logger, (new StringBuilder("I: ")).append(faultI.getMean()).append(" II: ")
            .append(faultII.getMean()));
    }

}
