/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.parser.netflix;

import edu.tongji.parser.Parser;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;

/**
 * RatingVO解析模板，向上兼容Rating模板
 * 
 * @author Hanke Chen
 * @version $Id: NetflixRatingVOTemplateParser.java, v 0.1 23 Apr 2014 13:21:00
 *          chench Exp $
 */
public class NetflixRatingVOTemplateParser implements Parser {

    /** 分隔符正则表达式 */
    private final static String SAPERATOR_EXPRESSION = "\\,";

    /**
     * [movieId],[userId],[ratingReal],,[ratingCmp]
     * 
     * @see edu.tongji.parser.Parser#parser(edu.tongji.parser.ParserTemplate)
     */
    @Override
    public Object parser(ParserTemplate template) {
        // 获取模板内容
        String context = template.getAsString();
        if (StringUtil.isEmpty(context) | (context.indexOf(":") != -1)) {
            return null;
        }

        try {
            String[] elements = context.split(SAPERATOR_EXPRESSION);
            int movieId = Integer.valueOf(elements[0]).intValue();
            int userId = Integer.valueOf(elements[1]).intValue();
            Float ratingReal = Float.valueOf(elements[2]);
            Float ratingCmp = (elements.length == 5 && StringUtil.isNotBlank(elements[4])) ? Float
                .valueOf(elements[4]) : ratingReal;
            return new RatingVO(userId, movieId, ratingCmp, ratingReal);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "解析ParserTemplate错误，内容: " + template.getAsString());
        }

        return null;
    }

}
