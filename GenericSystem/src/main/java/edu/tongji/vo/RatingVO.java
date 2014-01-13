/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.vo;

import edu.tongji.model.Rating;

/**
 * Rating的扩展值类
 * 
 * @author chench
 * @version $Id: RatingVO.java, v 0.1 2013-10-30 下午8:27:30 chench Exp $
 */
public class RatingVO extends Rating {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 上下文，Map太耗内存，内存不够，这种用double变量代替*/
    //    private final Map<String, Object> properties       = new HashMap<String, Object>();
    private Float            value;

    /**
     * 新增usrId和rating键值对
     * 
     * @param usrId
     * @param rating
     */
    public void put(String usrId, Object object) {
        //        properties.put(usrId, object);
        value = ((Double) object).floatValue();
    }

    /**
     * 根据usrId获取rating对象
     * 
     * @param usrId
     * @return
     */
    public Object get(String usrId) {
        return value;
    }

}
