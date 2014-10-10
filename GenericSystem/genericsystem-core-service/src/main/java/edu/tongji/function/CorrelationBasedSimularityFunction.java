/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.function;

import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 皮尔森r相似度计算函数
 * 
 * @author Hanke Chen
 * @version $Id: CorrelationBasedSimularityFunction.java, v 0.1 2013-9-7 下午5:01:36 chench Exp $
 */
public class CorrelationBasedSimularityFunction implements Function {

    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see edu.tongji.function.Function#calculate(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public Number calculate(List<? extends Number> oper1, List<? extends Number> oper2,
                            List<? extends Number> opers) {
        throw new OwnedException(FunctionErrorCode.NOT_SUPPORT_LISTS);
    }
    
    /** 
     * @see edu.tongji.function.Function#calculate(java.lang.Number, java.lang.Number)
     */
    @Override
    public Number calculate(Number oper1, Number oper2) {
        throw new OwnedException(FunctionErrorCode.NOT_SUPPORT_OBJECT);
    }

    /** 
     * @see edu.tongji.function.Function#calculate(java.util.List, java.util.List)
     */
    @Override
    public Number calculate(List<? extends Number> oper1, List<? extends Number> oper2) {
        if (oper1.size() != oper2.size()) {
            throw new IllegalArgumentException("Operator must be not empty or has the same size.");
        }

        if (oper1.isEmpty() | oper2.isEmpty()) {
            throw new OwnedException(FunctionErrorCode.EMPTY_LIST);
        }

        LoggerUtil.debug(logger, "CorrelationBasedSimularityFunction 开始处理...");

        return numeratorValue(oper1, oper2) / denominatorValue(oper1, oper2);
    }

    /**
     * 计算分子部分 :   (  Every Ri - avg(Ri)  )(  Every Rj - avg(Rj)  )
     * 
     * @param oper1
     * @param oper2
     * @return
     */
    private double numeratorValue(List<? extends Number> oper1, List<? extends Number> oper2) {
        double avgOp1 = FunctionHelper.averageValue(oper1).doubleValue();
        double avgOp2 = FunctionHelper.averageValue(oper2).doubleValue();

        return FunctionHelper.adjuestedDotProductValue(oper1, avgOp1, oper2, avgOp2).doubleValue();
    }

    /**
     * 计算分母部分
     * 
     * @param oper1
     * @param oper2
     * @return
     */
    private double denominatorValue(List<? extends Number> oper1, List<? extends Number> oper2) {
        double sumOp1 = 0.0;
        double sumOp2 = 0.0;
        double sumOfListOp1 = FunctionHelper.sumValue(oper1).doubleValue();
        double sumOfListOp2 = FunctionHelper.sumValue(oper2).doubleValue();
        double countOfList = oper1.size();

        for (int i = 0, j = oper1.size(); i < j; i++) {
            sumOp1 += Math.pow((oper1.get(i).doubleValue() - sumOfListOp1 / countOfList), 2);
            sumOp2 += Math.pow((oper2.get(i).doubleValue() - sumOfListOp2 / countOfList), 2);
        }

        return Math.sqrt(sumOp1 * sumOp2);
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CorrelationBasedSimularityFunction";
    }



}
