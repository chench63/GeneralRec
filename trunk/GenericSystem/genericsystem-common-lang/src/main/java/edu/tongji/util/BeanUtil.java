/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 
 * @author Hanke Chen
 * @version $Id: BeanUtil.java, v 0.1 2013-10-8 下午2:59:49 chench Exp $
 */
public final class BeanUtil {

    /** Date解析规格*/
    private final static String DATE_PARSER_FORMAT      = "yyyy-MM-dd";

    /** Timestamp生成规格 */
    private final static String TIMESTAMP_CREATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     *  转化日期格式，日后抽取为DateUtil 
     * 
     * @param times
     * @return
     * @throws ParseException
     */
    public static Timestamp parserTimestamp(String times) throws ParseException {
        SimpleDateFormat parserFormat = new SimpleDateFormat(DATE_PARSER_FORMAT);
        java.util.Date date = parserFormat.parse(times);

        SimpleDateFormat createFormat = new SimpleDateFormat(TIMESTAMP_CREATE_FORMAT);
        String timeString = createFormat.format(date);

        return Timestamp.valueOf(timeString);
    }
}
