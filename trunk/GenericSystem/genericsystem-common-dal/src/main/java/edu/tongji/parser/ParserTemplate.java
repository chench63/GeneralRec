/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author chench
 * @version $Id: ParserTemplate.java, v 0.1 2013-9-6 下午4:19:59 chench Exp $
 */
public class ParserTemplate {

    /** 模板内容 **/
    private String                    template;

    private final Map<String, String> property = new HashMap<String, String>();

    /** 获取模板内容   **/
    public String getAsString() {
        return template;
    }

    /**
     * 构造函数
     */
    public ParserTemplate() {
        super();
    }

    /**
     * @param template
     */
    public ParserTemplate(String template) {
        super();
        this.template = template;
    }

    /**
     * 存储属性
     * 
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        property.put(key, value);
    }

    /**
     * 根据键获取属性
     * 
     * @param key
     * @return
     */
    public String get(String key) {
        return property.get(key);
    }

    /**
     * Setter method for property <tt>template</tt>.
     * 
     * @param template value to be assigned to property template
     */
    public void setTemplate(String template) {
        this.template = template;
    }

}
