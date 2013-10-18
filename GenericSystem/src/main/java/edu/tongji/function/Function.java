/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.function;

import java.util.List;


/**
 * 函数计算类，模板参数为返回值的参数类型。
 * <p>
 *  <b>T</b>代表返回值类型，<b>J</b>代表输入参数的类型
 * </p>
 * @author chenkh
 * @version $Id: Function.java, v 0.1 2013-9-7 下午4:00:14 chenkh Exp $
 */
public interface Function {

    /**
     * 计算函数值
     * 
     * @param oper1
     * @param oper2
     * @return
     */
    public Number calculate(Number oper1, Number oper2);
    
    /**
     * 计算函数值
     * 
     * @param oper1
     * @param oper2
     * @return
     */
    public Number calculate(List<? extends Number> oper1, List<? extends Number> oper2);
    
    /**
     * 计算函数值
     * 
     * @param oper1
     * @param oper2
     * @return
     */
    public Number calculate(List<? extends Number> oper1, List<? extends Number> oper2, List<? extends Number> opers);
}
