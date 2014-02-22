/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

import java.util.List;

/**
 * 破解对象
 * 
 * @author chench
 * @version $Id: CrackObject.java, v 0.1 2014-2-20 上午9:47:57 chench Exp $
 */
public final class CrackObject {

    /** 数组对象对象*/
    @SuppressWarnings("rawtypes")
    private List target;

    /**
     * 构造函数
     */
    public CrackObject() {

    }

    /**
     * 构造函数
     * @param target
     */
    @SuppressWarnings("rawtypes")
    public CrackObject(List target) {
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
