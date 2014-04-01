/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

/**
 * Gnuplot数据文件模板
 * 
 * @author chench
 * @version $Id: AssembleTemplate.java, v 0.1 1 Apr 2014 17:41:39 chench Exp $
 */
public class AssembleTemplate {

    /** 主模板，基础配置，必填 */
    private String mainTemplate;

    /**
     * Getter method for property <tt>mainTemplate</tt>.
     * 
     * @return property value of mainTemplate
     */
    public String getMainTemplate() {
        return mainTemplate;
    }

    /**
     * Setter method for property <tt>mainTemplate</tt>.
     * 
     * @param mainTemplate value to be assigned to property mainTemplate
     */
    public void setMainTemplate(String mainTemplate) {
        this.mainTemplate = mainTemplate;
    }

}
