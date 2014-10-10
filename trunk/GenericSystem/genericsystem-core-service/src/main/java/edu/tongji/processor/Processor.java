/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.processor;

import edu.tongji.context.ProcessorContext;

/**
 * [处理器}: 业务逻辑处理模板 
 * 
 * @author Hanke Chen
 * @version $Id: Processor.java, v 0.1 2013-9-6 下午9:12:58 chench Exp $
 */
public interface Processor {

    /**
     * 在给定上下文中，执行特定的业务逻辑.
     * 
     * @param processorContext  处理器上下文
     */
    public void process(ProcessorContext processorContext);
}
