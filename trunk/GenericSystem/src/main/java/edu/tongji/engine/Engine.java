/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

/**
 * 处理引擎核心类
 * <p>
 *      多线程环境和单线程环境，采用不同的编码结构。
 * </p>
 * 
 * @author chenkh
 * @version $Id: Engine.java, v 0.1 2013-9-7 下午7:22:53 chenkh Exp $
 */
public interface Engine {

    /**
     * 执行处理模板
     */
    public void excute();
}
