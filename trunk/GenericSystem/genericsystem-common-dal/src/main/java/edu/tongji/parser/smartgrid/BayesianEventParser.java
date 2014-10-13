/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.parser.smartgrid;

import edu.tongji.parser.Parser;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.BayesianEventVO;

/**
 *  贝叶斯网络，概率事件解析器 
 * @author Hanke Chen
 * @version $Id: BayesianEventParser.java, v 0.1 2014-5-28 下午4:45:13 chench Exp $
 */
public class BayesianEventParser implements Parser {

    /** 分隔符正则表达式 */
    private final static String SAPERATOR_EXPRESSION = "\\,";

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
            //解析模板
            String[] elemnt = context.split(SAPERATOR_EXPRESSION);
            long timeVal = Long.valueOf(elemnt[0]).longValue();
            float hot = Float.valueOf(elemnt[1]).floatValue();
            short indoor = Short.valueOf(elemnt[2]).shortValue();
            short ac = Short.valueOf(elemnt[3]).shortValue();
            float power = Float.valueOf(elemnt[4]).floatValue();

            return new BayesianEventVO(timeVal, hot, indoor, ac, power);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "解析BayesianEventParser错误，内容: " + template.getAsString());
        }
        return null;

    }

    /** 
     * @see edu.tongji.parser.Parser#parse(java.lang.String)
     */
    @Override
    public Object parse(String template) {
        return null;
    }

}
