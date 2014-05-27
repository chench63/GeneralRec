/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack.support;

import java.util.Date;

import edu.tongji.util.DateUtil;

/**
 * 
 * @author chench
 * @version $Id: SeqDayHKCallBack.java, v 0.1 2014-5-26 下午9:44:12 chench Exp $
 */
public class SeqDayHKCallBack implements HashKeyCallBack {

    /** 
     * @see edu.tongji.extend.crack.support.HashKeyCallBack#key(java.lang.Object[])
     */
    @Override
    public String key(Object... object) {
        long timeVal = (Long) object[0];

        return (new StringBuilder()).append(
            DateUtil.format(new Date(timeVal), DateUtil.SHORT_FORMAT)).toString();
    }

    /** 
     * @see edu.tongji.extend.crack.support.HashKeyCallBack#keyArr()
     */
    @Override
    public String[] keyArr() {
        return null;
    }

}
