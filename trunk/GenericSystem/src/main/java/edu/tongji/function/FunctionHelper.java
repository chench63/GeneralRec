/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.function;

import java.util.List;

/**
 * 函数计算帮助类
 * 
 * @author chenkh
 * @version $Id: FunctionHelper.java, v 0.1 2013-9-7 下午5:19:25 chenkh Exp $
 */
public final class FunctionHelper {

    /**
     * 禁用构造函数
     */
    private FunctionHelper() {

    }

    /**
     * 计算向量点积
     * <p>
     *   vector1 = (1, 2, 3)<br/>
     *   vector2 = (5, 6, 7)<br/>
     *   <b>dotProductValue</b> = (1*5 + 2*6 + 3*7)
     * </p>
     * @param oper1
     * @param oper2
     * @return
     */
    public static Number dotProductValue(List<? extends Number> oper1, List<? extends Number> oper2) {
        return adjuestedDotProductValue(oper1, 0.0, oper2, 0.0);
    }

    /**
     * 计算修正型向量点积
     * <p>
     *   vector1 = (1, 2) 修正值 a<br/>
     *   vector2 = (5, 6) 修正值 b<br/>
     *   <b>adjuestedDotProductValue</b> = (  (1-)*(5-b) + (2-a)*(6-b)  )
     * </p>
     * @param oper1
     * @param adjustedOper1
     * @param oper2
     * @param adjustedOper2
     * @return
     */
    public static Number adjuestedDotProductValue(List<? extends Number> oper1,
                                                  Number adjustedOper1,
                                                  List<? extends Number> oper2, Number adjustedOper2) {
        double dot = 0.0;
        for (int i = 0, j = oper1.size(); i < j; i++) {
            dot += (oper1.get(i).doubleValue() - adjustedOper1.doubleValue())
                   * (oper2.get(i).doubleValue() - adjustedOper2.doubleValue());
        }

        return dot;
    }

    /**
     * 计算向量模
     * <p>
     *  vector: v;
     *  return |v|
     * </p>
     * 
     * @param oper
     * @return
     */
    public static Number modelValue(List<? extends Number> oper) {
        double model = 0.0;
        for (int i = 0, j = oper.size(); i < j; i++) {
            model += oper.get(i).doubleValue() * oper.get(i).doubleValue();
        }
        return Math.sqrt(model);
    }

    /**
     * 计算修正向量模的平方,用于修正double精度不够的误差
     * <p>
     *  vector: v;
     *  Double: r;
     *  return |v - r|*|v - r|
     * </p>
     * 
     * @param oper
     * @param adjustedOper1
     * @return
     */
    public static Number adjustSumOfSquareValue(List<? extends Number> oper, Number adjustedOper1) {
        double model = 0.0;
        for (int i = 0, j = oper.size(); i < j; i++) {
            model += Math.pow(oper.get(i).doubleValue() - adjustedOper1.doubleValue(), 2);
        }

        return model;
    }

    /**
     * 计算向量模的平方,用于修正double精度不够的误差
     * <p>
     *  vector: v;
     *  return |v|*|v|
     * </p>
     * 
     * @param oper
     * @return
     */
    public static Number sumOfSquareValue(List<? extends Number> oper) {
        double model = 0.0;
        for (int i = 0, j = oper.size(); i < j; i++) {
            model += oper.get(i).doubleValue() * oper.get(i).doubleValue();
        }

        return model;
    }

    /**
    * 计算平均值
    * 
    * @param oper
    * @return
    */
    public static Number averageValue(List<? extends Number> oper) {
        double avg = 0.0;
        for (int i = 0, j = oper.size(); i < j; i++) {
            avg += oper.get(i).doubleValue();
        }
        return avg / oper.size();
    }

    /**
     * 计算元素和
     * 
     * @param oper
     * @return
     */
    public static Number sumValue(List<? extends Number> oper) {
        double sum = 0.0;
        for (int i = 0, j = oper.size(); i < j; i++) {
            sum += oper.get(i).doubleValue();
        }
        return sum;
    }

}
