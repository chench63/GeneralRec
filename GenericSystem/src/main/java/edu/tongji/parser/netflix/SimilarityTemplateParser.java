/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.parser.netflix;

import edu.tongji.parser.Parser;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.SimilarityVO;

/**
 * 相似度解析类
 * 
 * @author chench
 * @version $Id: SimilarityTemplateParser.java, v 0.1 23 Apr 2014 15:54:13 chench Exp $
 */
public class SimilarityTemplateParser implements Parser {

    /** 分隔符号*/
    public final static char    SEPERATOR            = ',';

    /** 分隔符正则表达式 */
    private final static String SAPERATOR_EXPRESSION = "\\,";

    /** 
     * [itemI],[itemJ],[similarity]
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
            String[] elements = context.split(SAPERATOR_EXPRESSION);
            int i = Integer.valueOf(elements[0]).intValue();
            int j = Integer.valueOf(elements[1]).intValue();
            float similarity = Float.valueOf(elements[2]).floatValue();
            return new SimilarityVO(i, j, similarity);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "解析ParserTemplate错误，内容: " + template.getAsString());
        }

        return null;
    }

}
