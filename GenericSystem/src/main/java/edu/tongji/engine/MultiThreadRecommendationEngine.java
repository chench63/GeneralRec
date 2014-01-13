/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 推荐系统引擎：多线程抽象类 
 * 
 * @author chench
 * @version $Id: RecommendationMultiThreadEngine.java, v 0.1 2013-10-15 上午9:50:14 chench Exp $
 */
public abstract class MultiThreadRecommendationEngine implements Engine, ApplicationContextAware {

    /** Spring 上下文环境 */
    protected ConfigurableApplicationContext applicationContext;

    /** 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}
