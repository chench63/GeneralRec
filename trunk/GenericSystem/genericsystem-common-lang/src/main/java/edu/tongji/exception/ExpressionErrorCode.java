/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.exception;

/**
 * DataSource表示式相关错误代码
 * 
 * @author chench
 * @version $Id: ExpressionErrorCode.java, v 0.1 2013-9-9 下午3:38:10 chench Exp $
 */
public enum ExpressionErrorCode implements ErrorCode {

    NOT_EXIST_FROM("DataSource表示式不存在FROM词素", "EXPR01001"),
    
    NOT_INSTANCEOF_COMPARABLE("DataSource表达式的右值不是Compare的实例","EXPR05001");

    /** 异常描述 */
    private final String description;

    /** 异常编码 */
    private final String shortCode;

    private ExpressionErrorCode(String description, String shortCode) {
        this.description = description;
        this.shortCode = shortCode;
    }

    /** 
     * @see edu.tongji.exception.ErrorCode#getType()
     */
    @Override
    public ExceptionType getType() {
        return ExceptionType.EXPR_ERROR;
    }

    /** 
     * @see edu.tongji.exception.ErrorCode#getCode()
     */
    @Override
    public String getCode() {
        return shortCode;
    }

    /** 
     * @see edu.tongji.exception.ErrorCode#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }
}
