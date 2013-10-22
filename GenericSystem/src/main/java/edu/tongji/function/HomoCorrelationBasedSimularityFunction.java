/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.function;

import java.math.BigInteger;
import java.util.List;

import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.util.PaillierUtil;

/**
 * 同态加密-皮尔森r相似度计算函数
 * 
 * @author chenkh
 * @version $Id: HomoCorrelationBasedSimularityFunction.java, v 0.1 2013-10-18 上午10:26:48 chenkh Exp $
 */
public class HomoCorrelationBasedSimularityFunction implements Function {

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
        throw new OwnedException(FunctionErrorCode.NOT_SUPPORT_LIST);
    }

    /** 
     * @see edu.tongji.function.Function#calculate(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public Number calculate(List<? extends Number> oper1, List<? extends Number> oper2,
                            List<? extends Number> opers) {

        BigInteger numerator = sum(oper1);
        BigInteger denominatorOfI = sum(oper2);
        BigInteger denominatorOfJ = sum(opers);

        //下算式只为估计运行效率
        return numerator.doubleValue()
               / Math.sqrt(denominatorOfI.doubleValue() * denominatorOfJ.doubleValue());
    }

    /**
     * 同态求和
     * 
     * @param oper
     * @return
     */
    private BigInteger sum(List<? extends Number> oper) {
        BigInteger sum = BigInteger.ZERO;
        for (int i = 0, j = oper.size(); i < j; i++) {
            if (i == 0) {
                sum = (BigInteger) oper.get(i);
            }

            sum = PaillierUtil.add(sum, (BigInteger) oper.get(i));
        }
        return sum;
    }

}
