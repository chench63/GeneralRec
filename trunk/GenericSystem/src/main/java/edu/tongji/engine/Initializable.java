/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

/**
 * 可初始化的BEAN接口定义<br>
 * 采用Spring的init-method实现系统初始化<br>
 * 所有需要系统启动时自动初始化的BEAN都要实现这个接口<br>
 * 
 * @author chenkh
 * @version $Id: Initializable.java, v 0.1 2013-9-9 上午8:53:45 chenkh Exp $
 */
public interface Initializable {
    /**
     * 初始化执行的逻辑
     */
    public void initialize();
}