/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import edu.tongji.model.Rating;
import edu.tongji.model.ValueOfItems;
import edu.tongji.vo.MeterReadingVO;

/**
 * Map中自定义索引键工具类
 * 
 * @author chench
 * @version $Id: HashKeyUtil.java, v 0.1 2013-9-9 下午3:06:03 chench Exp $
 */
public final class HashKeyUtil {

    /** key分隔符号*/
    public final static char ELEMENT_SEPERATOR = '_';

    /** key分隔符号*/
    public final static char ELEMENT_DOT       = '.';

    /**
     * 生成Hash值
     * 
     * @param rating
     * @return
     */
    public static String genKey(Rating rating) {
        return (new StringBuilder()).append(rating.getUsrId()).append(ELEMENT_SEPERATOR)
            .append(rating.getMovieId()).toString();
    }

    /**
     * 生成Hash值
     * [itemI]_[itemJ]
     * 
     * @param valueOfItem
     * @return
     */
    public static String genKey(ValueOfItems valueOfItem) {
        return (new StringBuilder(valueOfItem.getItemI())).append(ELEMENT_SEPERATOR)
            .append(valueOfItem.getItemJ()).toString();
    }

    /**
     * Map中Key值算法，[Day]_[Hour] <br/>
     * e.g: 02011017 -> 32_10
     * 
     * @param meterReading
     * @return
     */
    public static String genKeySeqHour(MeterReadingVO meterReading) {
        return (new StringBuilder()).append(DateUtil.getDayOfYear(meterReading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR)
            .append(DateUtil.getHourOfDay(meterReading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR)
            .append(DateUtil.getMinOfHour(meterReading.getTimeVal()) / 15).toString();
    }

    /**
     * Map中Key值算法，[Day]_[Hour]_[Quarter] <br/>
     * e.g: 02011017 -> 32_10_1
     * 
     * @param meterReading
     * @return
     */
    public static String genKeySeqQuarter(MeterReadingVO meterReading) {
        return (new StringBuilder()).append(DateUtil.getDayOfYear(meterReading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR)
            .append(DateUtil.getHourOfDay(meterReading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR)
            .append(DateUtil.getMinOfHour(meterReading.getTimeVal()) / 15).toString();
    }

    /**
     * 生成Hash值
     * 
     * @param rating
     * @return
     */
    public static String genKey(int part1, int part2) {
        return part1 >= part2 ? part1 + "_" + part2 : part2 + "_" + part1;
    }
}
