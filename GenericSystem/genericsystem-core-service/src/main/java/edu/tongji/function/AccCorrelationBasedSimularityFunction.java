/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.function;

import java.util.List;

import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;

/**
 * 累计pearson系数计算函数。
 *  估计算法运行效率时，计算pearson的累加时间值
 * 
 * @author Hanke Chen
 * @version $Id: AccCorrelationBasedSimularityFunction.java, v 0.1 17 Sep 2013 12:41:56 chench Exp $
 */
public class AccCorrelationBasedSimularityFunction implements Function {

    /** 
     * @see edu.tongji.function.Function#calculate(java.lang.Number, java.lang.Number)
     */
    @Override
    public Number calculate(Number oper1, Number oper2) {
        return oper1.doubleValue() - oper2.doubleValue();
    }

    /** 
     * @see edu.tongji.function.Function#calculate(java.util.List, java.util.List)
     */
    @Override
    public Number calculate(List<? extends Number> oper1, List<? extends Number> oper2) {
        throw new OwnedException(FunctionErrorCode.NOT_SUPPORT_LIST);
    }

    /** 
     * @see edu.tongji.function.Function#calculate(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public Number calculate(List<? extends Number> oper1, List<? extends Number> oper2,
                            List<? extends Number> opers) {
        double numerator = FunctionHelper.sumValue(oper1).doubleValue();
        double denominatorOfI = FunctionHelper.sumValue(oper2).doubleValue();
        double denominatorOfJ = FunctionHelper.sumValue(opers).doubleValue();

        return numerator / Math.sqrt(denominatorOfI * denominatorOfJ);
    }

}
