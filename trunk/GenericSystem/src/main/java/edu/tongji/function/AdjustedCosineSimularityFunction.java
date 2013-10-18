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
 * 修正Cosine相似度计算函数
 * <p>
 *  <b>用户评分平均值</b>置于List的尾部
 * </p>
 * @author chenkh
 * @version $Id: AdjustedCosineSimularityFunction.java, v 0.1 2013-9-7 下午5:17:57 chenkh Exp $
 */
public class AdjustedCosineSimularityFunction implements Function {

    /** logger */
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

        LoggerUtil.debug(logger, "AdjustedCosineSimularityFunction 开始处理...\n");
        return numeratorValue(oper1, oper2) / denominatorValue(oper1, oper2);

    }

    /**
     * 计算分子部分 :   (  Every Ri - avg(User's Rating)  )(  Every Rj - avg(User's Rating)  )
     * 
     * @param oper1
     * @param oper2
     * @return
     */
    private double numeratorValue(List<? extends Number> oper1, List<? extends Number> oper2) {
        int indexOfAvgUserRaing = oper1.size() - 1;
        double avgOfUsersRaing1 = oper1.get(indexOfAvgUserRaing).doubleValue();
        double avgOfUsersRaing2 = oper2.get(indexOfAvgUserRaing).doubleValue();

        return FunctionHelper.adjuestedDotProductValue(oper1.subList(0, indexOfAvgUserRaing),
            avgOfUsersRaing1, oper2.subList(0, indexOfAvgUserRaing), avgOfUsersRaing2)
            .doubleValue();
    }

    /**
     * 计算分母部分
     * 
     * @param oper1
     * @param oper2
     * @return
     */
    private double denominatorValue(List<? extends Number> oper1, List<? extends Number> oper2) {
        int indexOfAvgUserRaing = oper1.size() - 1;
        double sumOp1 = 0.0;
        double sumOp2 = 0.0;
        double avgOfUsersRaing1 = oper1.get(indexOfAvgUserRaing).doubleValue();
        double avgOfUsersRaing2 = oper2.get(indexOfAvgUserRaing).doubleValue();

        for (int i = 0, j = oper1.size(); i < j; i++) {
            sumOp1 += Math.pow((oper1.get(i).doubleValue() - avgOfUsersRaing1), 2);
            sumOp2 += Math.pow((oper2.get(i).doubleValue() - avgOfUsersRaing2), 2);
        }

        return Math.sqrt(sumOp1 * sumOp2);
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AdjustedCosineSimularityFunction";
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
