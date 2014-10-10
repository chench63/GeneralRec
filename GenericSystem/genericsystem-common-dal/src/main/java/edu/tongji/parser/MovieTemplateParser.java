/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

import edu.tongji.model.Movie;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: MovieTemplateParser.java, v 0.1 2013-9-6 下午4:36:31 chench Exp $
 */
public class MovieTemplateParser implements Parser {
    /** 分隔符正则表达式 */
    private static String SAPERATOR_EXPRESSION = "\\::";

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

        Movie movie = new Movie();
        String[] elements = context.split(SAPERATOR_EXPRESSION);
        movie.setId(elements[0]);
        movie.setTitle(elements[1]);
        movie.setGenres(elements[2]);

        return movie;
    }

}
