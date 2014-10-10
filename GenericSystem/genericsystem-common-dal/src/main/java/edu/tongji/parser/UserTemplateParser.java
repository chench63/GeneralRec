/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

import edu.tongji.model.User;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: UserTemplateParser.java, v 0.1 2013-9-6 下午4:22:44 chench Exp $
 */
public class UserTemplateParser implements Parser {
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

        User usr = new User();
        String[] elements = context.split(SAPERATOR_EXPRESSION);
        usr.setId(elements[0]);
        usr.setGender(elements[1]);
        usr.setAge(elements[2]);
        usr.setOccupation(elements[3]);
        usr.setZipCode(elements[4]);

        return usr;
    }

}
