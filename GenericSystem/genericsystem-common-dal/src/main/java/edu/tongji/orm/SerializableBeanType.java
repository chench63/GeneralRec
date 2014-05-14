/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.orm;

import edu.tongji.model.Rating;

/**
 * DataSource结构化表示式，表名相关的Enum类;
 * <p>
 *      命名规则：<br/>
 *      <b>[类名]_BEAN</b>
 * </p>
 * @author chench
 * @version $Id: SerialableBeanType.java, v 0.1 2013-9-9 下午2:44:57 chench Exp $
 */
@SuppressWarnings("rawtypes")
public enum SerializableBeanType {
    /** Rating对象的Class信息 */
    RATING_BEAN(Rating.class, "Rating");

    /** Class */
    private Class claz;
    
    /** ClassName */
    private String clazName;

    private SerializableBeanType(Class claz, String clazName) {
        this.claz = claz;
        this.clazName = clazName;
    }

    /**
     * Getter method for property <tt>claz</tt>.
     * 
     * @return property value of claz
     */

    public Class getClaz() {
        return claz;
    }

    /**
     * Getter method for property <tt>clazName</tt>.
     * 
     * @return property value of clazName
     */
    public String getClazName() {
        return clazName;
    }

}
