/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack.support;

import java.util.ArrayList;
import java.util.List;

import edu.tongji.util.DateUtil;
import edu.tongji.util.HashKeyUtil;

/**
 * 按时刻输出内容
 * 
 * @author Hanke Chen
 * @version $Id: SeqQuarterHKCallBack.java, v 0.1 2014-5-21 下午6:33:10 chench Exp $
 */
public class SeqQuarterHKCallBack implements HashKeyCallBack {

    /** 
     * @see edu.tongji.extend.crack.support.HashKeyCallBack#key(java.lang.Object[])
     */
    @Override
    public String key(Object... object) {
        long timeVal = (Long) object[0];

        return (new StringBuilder()).append(DateUtil.getHourOfDay(timeVal))
            .append(HashKeyUtil.ELEMENT_DOT).append(DateUtil.getMinOfHour(timeVal) / 15 * 25)
            .toString();
    }

    /** 
     * @see edu.tongji.extend.crack.support.HashKeyCallBack#keyArr()
     */
    @Override
    public String[] keyArr() {
        List<String> keyArr = new ArrayList<String>();

        for (int hour = 0; hour < 24; hour++) {
            for (int quater = 0; quater < 4; quater++) {
                keyArr.add((new StringBuilder()).append(hour).append(HashKeyUtil.ELEMENT_DOT)
                    .append(quater * 25).toString());
            }
        }

        return keyArr.toArray(new String[keyArr.size()]);
    }

}
