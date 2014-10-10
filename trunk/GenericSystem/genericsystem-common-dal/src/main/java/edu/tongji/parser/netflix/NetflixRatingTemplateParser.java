/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser.netflix;

import edu.tongji.model.Rating;
import edu.tongji.parser.Parser;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: NetflixRatingTemplateParser.java, v 0.1 2013-9-16 下午2:21:49 chench Exp $
 */
public class NetflixRatingTemplateParser implements Parser {

    /** 分隔符正则表达式 */
    private final static String SAPERATOR_EXPRESSION = "\\,";

    /** movieId键 */
    public final static String  KEY_MOVIEID          = "movieId";

    /** 
     * [userId],[rating],[date]
     * 
     * @see edu.tongji.parser.Parser#parser(edu.tongji.parser.ParserTemplate)
     */
    @Override
    public Object parser(ParserTemplate template) {
        //获取模板内容
        String context = template.getAsString();
        if (StringUtil.isEmpty(context)) {
            return null;
        }

        try {
            String movieId = template.get(KEY_MOVIEID);
            String[] elements = context.split(SAPERATOR_EXPRESSION);
            return new Rating(Integer.valueOf(elements[0]), Integer.valueOf(movieId),
                Integer.valueOf(elements[1]), null);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "解析ParserTemplate错误，内容: " + template.getAsString());
        }

        return null;
    }
}
