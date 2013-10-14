/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

import java.text.ParseException;
import edu.tongji.util.BeanUtil;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixRatingTemplateParser.java, v 0.1 2013-9-16 下午2:21:49 chenkh Exp $
 */
public class NetflixRatingTemplateParser implements Parser {

    /** 分隔符字符值 */
    private final static String SAPERATOR            = ",";

    /** 分隔符正则表达式 */
    private final static String SAPERATOR_EXPRESSION = "\\,";

    /** movieId键 */
    private final static String KEY_MOVIEID          = "movieId";

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

        try {
            String movieId = template.get(KEY_MOVIEID);
            context = movieId + SAPERATOR + context;
            String[] elements = context.split(SAPERATOR_EXPRESSION);
            return BeanUtil.toBean(elements);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "解析ParserTemplate错误，内容: " + template.getAsString());
        }

        return null;
    }

}
