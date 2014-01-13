/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.exception;

/**
 * 异常码
 * 
 * @author chench
 * @version $Id: ErrorCode.java, v 0.1 2013-9-7 下午4:03:01 chench Exp $
 */
public interface ErrorCode {
    /**
     * 获取异常类型
     * 
     * @return 异常类型枚举
     */
    public ExceptionType getType();
    
    /**
     * 获取网关错误代码
     * 
     * @return 
     */
    public String getCode();
    
    /**
     * 获取网关错误描述
     * 
     * @return 
     */
    public String getDescription();

}
