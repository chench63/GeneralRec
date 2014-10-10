/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.predictor;

import java.util.HashMap;
import java.util.Map;

/**
 * 预测类，数据mode载体
 * 
 * @author Hanke Chen
 * @version $Id: PredictorHolder.java, v 0.1 31 Oct 2013 18:50:38 chench Exp $
 */
public class PredictorHolder {

    /** 属性集合*/
    private final Map<String, Object> properties           = new HashMap<String, Object>(2);

    /** 单用户所有浏览记录*/
    public final static String        PERSON_RATING_HISTRY = "PERSON_RATING_HISTRY";

    /**
     * 添加属性
     * 
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * 获取属性
     * 
     * @param key
     * @return
     */
    public Object get(String key) {
        return properties.get(key);
    }

}
