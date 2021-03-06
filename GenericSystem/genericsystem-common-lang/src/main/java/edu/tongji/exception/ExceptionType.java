/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.exception;

/**
 * 
 * @author Hanke Chen
 * @version $Id: ExceptionType.java, v 0.1 2013-9-7 下午4:02:46 chench Exp $
 */
public enum ExceptionType {
    
    /** 函数计算错误*/
    FUNCTION_ERROR,
    
    /** 结构化表达式错误*/
    EXPR_ERROR,
    
    /** 数据源错误 */
    DATASOURCE_ERROR;
    
    
    /**
     * 枚举名称
     * 
     * @return
     */
    public String getCode() {
        return this.name();
    }
    
}
