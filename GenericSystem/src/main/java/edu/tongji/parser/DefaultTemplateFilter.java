/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

/**
 * 默认数据过滤类
 * 
 * @author chench
 * @version $Id: DefaultTemplateFilter.java, v 0.1 2013-9-6 下午7:28:52 chench Exp $
 */
public class DefaultTemplateFilter implements Filter {

    /** 
     * @see edu.tongji.parser.Filter#isFiler(edu.tongji.parser.ParserTemplate)
     */
    @Override
    public boolean isFiler(ParserTemplate template) {
        return false;
    }

}
