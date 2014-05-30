/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid.thread;

import java.util.ArrayList;
import java.util.List;

import edu.tongji.extend.crack.support.PrivacyCrackObject;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.BayesianEventVO;
import edu.tongji.vo.MeterReadingVO;

/**
 * 准确度分析计算器线程
 * 
 * @author chench
 * @version $Id: AnalysisAccuracyCalculator.java, v 0.1 2014-5-30 上午8:55:45 chench Exp $
 */
public class BayesianAcurcyCalcltor extends AcurcyCalcltor {

    /** 互斥锁 */
    private static final Object mutex = new Object();

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        while (!shouldStop()) {

            List<List<BayesianEventVO>> resultSet = new ArrayList<List<BayesianEventVO>>();
            for (List<MeterReadingVO> context : CONTEXT_CACHE) {
                //1. 配置对象
                PrivacyCrackObject target = new PrivacyCrackObject(context);
                target.put(PrivacyCrackObject.RESULT_CACHE, resultSet);

                //2. 计算准确度
                cracker.crackInnerNoise(target, noise, hashKyGen);
            }

            accuracy(resultSet);
        }
    }

    /**
     * 计算准确度;
     * 
     */
    protected void accuracy(List<List<BayesianEventVO>> resultSet) {
        if (resultSet.isEmpty()) {
            //无数据返回
            return;
        }

        if (ACCURACY_CACHE.size() != resultSet.size()) {
            LoggerUtil.warn(logger, "RESULT CACHE DIDNT CORRESPOND WITH ACCURACY CACHE!");
            return;
        }

        float faultTypeI = 0;
        float faultTypeII = 0;
        int testCaseNum = 0;

        for (int dataSetIndex = 0; dataSetIndex < resultSet.size(); dataSetIndex++) {
            List<BayesianEventVO> dataSet = resultSet.get(dataSetIndex);
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
        synchronized (mutex) {
            FAULT_I_STAT.addValue(faultTypeI / testCaseNum);
            FAULT_II_STAT.addValue(faultTypeII / testCaseNum);
            STAT.addValue(1 - (faultTypeI + faultTypeII) / testCaseNum);
        }

        //输出日志
        LoggerUtil.info(
            logger,
            (new StringBuilder("I: ")).append(faultTypeI / testCaseNum).append(" II: ")
                .append(faultTypeII / testCaseNum).append(" A: ")
                .append(1 - (faultTypeI + faultTypeII) / testCaseNum));

    }
}
