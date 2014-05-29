/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 破解对象
 * 
 * @author chench
 * @version $Id: CrackObject.java, v 0.1 2014-2-20 上午9:47:57 chench Exp $
 */
public final class PrivacyCrackObject {

    /** 数组对象对象*/
    @SuppressWarnings("rawtypes")
    private List                 target;

    /** 属性集合*/
    protected Map<Short, Object> properties;

    /** 均值统计器：键值*/
    public final static Short    MEAN_STAT    = 1;

    /** 方差统计器：键值*/
    public final static Short    SD_STAT      = 2;

    /** 多维度统计缓存：键值*/
    public final static Short    STAT_CACHE   = 3;

    /** 存放贝叶斯网络识别结果的数据结构*/
    public final static Short    RESULT_CACHE = 4;

    /**
     * 构造函数
     */
    public PrivacyCrackObject() {

    }

    /**
     * 添加属性
     * 
     * @param key
     * @param value
     */
    public void put(Short key, Object value) {
        if (properties == null) {
            properties = new HashMap<Short, Object>();
        }

        properties.put(key, value);
    }

    /**
     * 获得属性
     * 
     * @param key
     * @return
     */
    public Object get(Short key) {
        if (properties == null) {
            return null;
        }

        return properties.get(key);
    }

    /**
     * 构造函数
     * @param target
     */
    @SuppressWarnings("rawtypes")
    public PrivacyCrackObject(List target) {
        super();
        this.target = target;
    }

    /**
     * Getter method for property <tt>target</tt>.
     * 
     * @return property value of target
     */
    @SuppressWarnings("rawtypes")
    public List getTarget() {
        return target;
    }

    /**
     * Setter method for property <tt>target</tt>.
     * 
     * @param target value to be assigned to property target
     */
    @SuppressWarnings("rawtypes")
    public void setTarget(List target) {
        this.target = target;
    }

}
