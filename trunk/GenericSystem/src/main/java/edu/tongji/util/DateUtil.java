/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import java.util.Calendar;

/**
 * 日期类，太累啦，老了以后写
 * 
 * @author chench
 * @version $Id: DateUtil.java, v 0.1 16 Sep 2013 21:01:38 chench Exp $
 */
public final class DateUtil {

    /**
     * 判断两个时间，是否是当日当时
     * 
     * @param l1
     * @param l2
     * @return
     */
    public static boolean sameDayAndHour(long l1, long l2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(l1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(l2);

        return (cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY))
               && (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * 24小时制小时
     * 
     * @param l1
     * @return
     */
    public static int getHourOfDay(long l1) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(l1);
        return cal1.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 这一天是这一年的第几天
     * 
     * @param l1
     * @return
     */
    public static int getDayOfYear(long l1) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(l1);
        return cal1.get(Calendar.DAY_OF_YEAR);
    }
}
