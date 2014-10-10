/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack.support;

import java.util.ArrayList;
import java.util.List;

import edu.tongji.util.DateUtil;

/**
 * 按小时输出内容
 * 
 * @author Hanke Chen
 * @version $Id: SeqHourHKCallBack.java, v 0.1 2014-5-27 上午9:14:22 chench Exp $
 */
public class SeqHourHKCallBack implements HashKeyCallBack {

    /** 
     * @see edu.tongji.extend.crack.support.HashKeyCallBack#key(java.lang.Object[])
     */
    @Override
    public String key(Object... object) {
        long timeVal = (Long) object[0];
        return (new StringBuilder()).append(DateUtil.getHourOfDay(timeVal)).toString();
    }

    /** 
     * @see edu.tongji.extend.crack.support.HashKeyCallBack#keyArr()
     */
    @Override
    public String[] keyArr() {
        List<String> keySet = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            keySet.add(String.valueOf(i));
        }
        return keySet.toArray(new String[24]);
    }

}
