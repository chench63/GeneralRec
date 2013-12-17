/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.encryption;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * 加密协议所需密钥
 * 
 * @author chenkh
 * @version $Id: Key.java, v 0.1 2013-12-17 上午11:14:44 chenkh Exp $
 */
public class EncryptionContext {

    /** properties*/
    Map<String, Object> properties;

    /** one-time pad 密钥值*/
    private BigInteger  keyValue;

    /**
     * @param keyValue
     */
    public EncryptionContext(BigInteger keyValue) {
        super();
        this.keyValue = keyValue;
    }

    /**
     * 装载属性
     * 
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        if (properties == null) {
            //慢加载
            properties = new HashMap<String, Object>();
        }

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
     * Getter method for property <tt>keyValue</tt>.
     * 
     * @return property value of keyValue
     */
    public BigInteger getKeyValue() {
        return keyValue;
    }

    /**
     * Setter method for property <tt>keyValue</tt>.
     * 
     * @param keyValue value to be assigned to property keyValue
     */
    public void setKeyValue(BigInteger keyValue) {
        this.keyValue = keyValue;
    }

}
