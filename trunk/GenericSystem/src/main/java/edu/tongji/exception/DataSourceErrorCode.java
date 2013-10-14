/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.exception;

/**
 * 
 * @author chenkh
 * @version $Id: DataSourceErrorCode.java, v 0.1 2013-9-16 下午2:28:59 chenkh Exp $
 */
public enum DataSourceErrorCode implements ErrorCode {
    /** 提供文件的文件格式不合法  */
    FILE_FORMAT_INCORRECT("提供文件的文件格式不合法", "DATASOURCE01001"),

    /** 不支持excuteEx方法  */
    NOT_SUPPORT_EXCUTEEX("不支持excuteEx方法", "DATASOURCE02001");

    /** 异常描述 */
    private final String description;

    /** 异常编码 */
    private final String shortCode;

    private DataSourceErrorCode(String description, String shortCode) {
        this.description = description;
        this.shortCode = shortCode;
    }

    /** 
     * @see edu.tongji.exception.ErrorCode#getType()
     */
    @Override
    public ExceptionType getType() {
        return ExceptionType.DATASOURCE_ERROR;
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
