/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

import java.sql.Timestamp;

import edu.tongji.model.Rating;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author chench
 * @version $Id: RatingTemplateParser.java, v 0.1 2013-9-6 下午4:39:29 chench Exp $
 */
public class MovielensRatingTemplateParser implements Parser {

    /** 分隔符正则表达式 */
    private static String SAPERATOR_EXPRESSION = "\\::";

    /** 转化为毫秒所需权重 */
    private static long   SEC_TO_MILSEC        = 1000;

    /** 
     * @see edu.tongji.parser.Parser#parser(edu.tongji.parser.ParserTemplate)
     */
    @Override
    public Object parser(ParserTemplate template) {
        //获取模板内容
        String context = template.getAsString();
        if (StringUtil.isEmpty(context)) {
            return null;
        }

        Rating rating = new Rating();
        String[] elements = context.split(SAPERATOR_EXPRESSION);
        rating.setUsrId(Integer.valueOf(elements[0]));
        rating.setMovieId(Integer.valueOf(elements[1]));
        rating.setRating(Integer.valueOf(elements[2]));
        rating.setTime(new Timestamp(Long.valueOf(elements[3]) * SEC_TO_MILSEC));

        return rating;
    }

}
