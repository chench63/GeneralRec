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
 * Cosine相似度计算函数
 * 
 * @author chenkh
 * @version $Id: CosineSimularityFunction.java, v 0.1 2013-9-7 下午4:21:23 chenkh Exp $
 */
public class CosineSimularityFunction implements Function {

    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

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

        LoggerUtil.debug(logger, "CosineSimularityFunction 开始处理...\n");
        
        return FunctionHelper.dotProductValue(oper1, oper2).doubleValue()
               / Math.sqrt(FunctionHelper.sumOfSquareValue(oper1).doubleValue()
                           * FunctionHelper.sumOfSquareValue(oper2).doubleValue());

    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CosineSimularityFunction";
    }

    /** 
     * @see edu.tongji.function.Function#calculate(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public Number calculate(List<? extends Number> oper1, List<? extends Number> oper2,
                            List<? extends Number> opers) {
        throw new OwnedException(FunctionErrorCode.NOT_SUPPORT_LISTS);
    }

}
