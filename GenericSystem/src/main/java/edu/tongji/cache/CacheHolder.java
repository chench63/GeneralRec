/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author chenkh
 * @version $Id: CacheHolder.java, v 0.1 2013-10-22 下午12:00:14 chenkh Exp $
 */
public final class CacheHolder {

    /** 属性集合*/
    private final Map<String, Object> properties = new HashMap<String, Object>();

    /** 固有属性 */
    public final static String        KEY        = "KEY";

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

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object property : properties.values()) {
            stringBuilder.append(property);
        }
        return stringBuilder.toString();
    }

}
