/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

/**
 * 解析模板
 * 
 * @author chenkh
 * @version $Id: TemplateParserType.java, v 0.1 2013-9-6 下午6:31:41 chenkh Exp $
 */
public enum TemplateType implements Parser, Filter {

    /** 用户解析处理模板 */
    USER_TEMPLATE(new UserTemplateParser(), new DefaultTemplateFilter()),
    /** 电影解析处理模板 */
    MOVIE_TEMPLATE(new MovieTemplateParser(), new DefaultTemplateFilter()),
    /** 评分解析处理模板 */
    MOVIELENS_RATING_TEMPLATE(new MovielensRatingTemplateParser(), new DefaultTemplateFilter()),
    /** NetFlix评分解析处理模板 */
    NETFLIX_RATING_TEMPLATE(new NetflixRatingTemplateParser(), new DefaultTemplateFilter()),
    /** UMASS电表读数处理模板*/
    UMASS_SMART_GRID_TEMPLATE(new UMassSmartGridTemplateParser(), new DefaultTemplateFilter());

    /** 模板解析类 */
    private final Parser parser;
    /** 模板过滤类 */
    private final Filter filter;

    private TemplateType(Parser parser, Filter filter) {
        this.parser = parser;
        this.filter = filter;
    }

    /** 
     * @see edu.tongji.parser.Parser#parser(edu.tongji.parser.ParserTemplate)
     */
    @Override
    public Object parser(ParserTemplate template) {
        return this.parser.parser(template);
    }

    /** 
     * @see edu.tongji.parser.Filter#isFiler(edu.tongji.parser.ParserTemplate)
     */
    @Override
    public boolean isFiler(ParserTemplate template) {
        return this.filter.isFiler(template);
    }

}
