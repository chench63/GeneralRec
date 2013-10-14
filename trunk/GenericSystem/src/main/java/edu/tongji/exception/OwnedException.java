/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.exception;

/**
 * 
 * @author chenkh
 * @version $Id: OwnedException.java, v 0.1 2013-9-7 下午4:17:01 chenkh Exp $
 */
public class OwnedException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 异常代码  */
    private final ErrorCode   resultCode;
    
    /**
     * 构造函数
     * @param resultCode
     */
    public OwnedException(ErrorCode resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     * @param resultCode
     * @param message
     */
    public OwnedException(ErrorCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     * @param message
     * @param e
     */
    public OwnedException(ErrorCode resultCode, Throwable e) {
        super(e);
        this.resultCode = resultCode;
    }
    
    /**
     * 构造函数
     * @param resultCode
     * @param message
     * @param e
     */
    public OwnedException(ErrorCode resultCode, String message, Throwable e) {
        super(message, e);
        this.resultCode = resultCode;
    }

    /**
     * 获取异常类型
     * @return
     */
    public ExceptionType getType() {
        return resultCode.getType();
    }

    /**
     * 获取异常代码
     * 
     * @return property value of resultCode
     */
    public ErrorCode getErrorCode() {
        return resultCode;
    }

    /** 
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        if (null == super.getMessage()) {
            return this.resultCode.getDescription();
        } else {
            return super.getMessage();
        }
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder retValue = new StringBuilder(" OwnedException[");
        if (resultCode != null) {
            retValue.append("type=").append(resultCode.getType()).append(',');
            retValue.append("code=").append(resultCode.getCode()).append(',');
            retValue.append("description=").append(resultCode.getDescription()).append(',');
        }
        retValue.append("extraMessage=").append(getMessage());
        retValue.append(']');
        return retValue.toString();
    }
}
