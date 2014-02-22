/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计工具工厂类
 * 
 * @author chench
 * @version $Id: StatisticianFactory.java, v 0.1 2014-2-22 下午12:33:12 chench Exp $
 */
public final class StatisticianFactory {

    /** bean仓库*/
    private Map<String, Statistician> reposity;

    /**
     * 获得bean实例
     * 
     * @param beanName
     * @return
     */
    public Statistician getBean(String beanName) {
        return reposity.get(beanName);
    }

    /**
     * Setter method for property <tt>reposity</tt>.
     * 
     * @param reposity value to be assigned to property reposity
     */
    public void setReposity(Map<String, Statistician> reposity) {
        this.reposity = new HashMap<String, Statistician>(reposity);
    }

}
