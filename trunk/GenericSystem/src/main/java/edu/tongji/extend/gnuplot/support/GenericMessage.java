/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot.support;

/**
 * 标准的Velocity模板通用参数的数据结构
 * 
 * @author chench
 * @version $Id: SuperMessage.java, v 0.1 1 Apr 2014 19:19:04 chench Exp $
 */
public class GenericMessage {

    /** 二維表格数据 */
    private String[][] matrics;

    /**
     * construction
     */
    public GenericMessage() {

    }

    /**
     * @param matrics
     */
    public GenericMessage(String[][] matrics) {
        super();
        this.matrics = matrics;
    }

    /**
     * Getter method for property <tt>matrics</tt>.
     * 
     * @return property value of matrics
     */
    public String[][] getMatrics() {
        return matrics;
    }

    /**
     * Setter method for property <tt>matrics</tt>.
     * 
     * @param matrics value to be assigned to property matrics
     */
    public void setMatrics(String[][] matrics) {
        this.matrics = matrics;
    }

}
