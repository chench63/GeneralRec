/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import edu.tongji.cache.DataStreamHolder;
import edu.tongji.model.Rating;
import edu.tongji.model.ValueOfItems;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author chenkh
 * @version $Id: BeanUtil.java, v 0.1 2013-10-8 下午2:59:49 chenkh Exp $
 */
public final class BeanUtil {

    /** Date解析规格*/
    private final static String DATE_PARSER_FORMAT      = "yyyy-MM-dd";

    /** Timestamp生成规格 */
    private final static String TIMESTAMP_CREATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 转化DataStreamHolder为ValueOfItems
     * 
     * @param task
     * @return
     */
    public static ValueOfItems toBean(DataStreamHolder task) {
        ValueOfItems bean = new ValueOfItems();
        bean.setItemI(String.valueOf(task.getItemI()));
        bean.setItemJ(String.valueOf(task.getItemJ()));
        bean.setValue(task.getNumMissingRating() * 1.0 / task.getNumToalRating());
        bean.setGMT_CREATE(Date.valueOf("2005-12-31"));
        bean.setFunctionName("MISSING_RATING");
        return bean;
    }

    /**
     * 转化string数字为Rating对象
     * 
     * @param elements
     * @return
     * @throws ParseException
     */
    public static Rating toBean(String[] elements) throws ParseException {
        Rating rating = new Rating();
        rating.setMovieId(Integer.valueOf(elements[0]));
        rating.setUsrId(elements[1]);
        rating.setRating(Integer.valueOf(elements[2]));
        rating.setTime(parserTimestamp(elements[3]));
        return rating;
    }

    /**
     * 转化Rating数字为RatingVO对象，日后使用反射扩展功能。
     * 
     * @param elements
     * @return
     * @throws ParseException
     */
    public static RatingVO toBean(Rating rating) {
        RatingVO ratingVO = new RatingVO();
        ratingVO.setMovieId(rating.getMovieId());
        ratingVO.setUsrId(rating.getUsrId());
        ratingVO.setRating(rating.getRating());
        ratingVO.setTime(rating.getTime());
        return ratingVO;
    }

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
