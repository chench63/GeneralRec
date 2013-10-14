/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

/**
 * 处理器上下文环境
 * 
 * @author chenkh
 * @version $Id: ProcessorContext.java, v 0.1 2013-9-6 下午9:12:49 chenkh Exp $
 */
public interface ProcessorContext {

    /**
     * 将外部采集信息，转化为内部处理器适用的上下文。
     * 
     * @param contextEnvelope
     */
    public void switchToProcessorContext(ContextEnvelope contextEnvelope);

    /**
     * 清空环境，为了在重复迭代环境下使用
     */
    public void clearContext();

}
