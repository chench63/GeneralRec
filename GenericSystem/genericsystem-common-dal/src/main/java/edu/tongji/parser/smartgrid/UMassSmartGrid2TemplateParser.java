/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.parser.smartgrid;

import edu.tongji.parser.Parser;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 
 * @author Hanke Chen
 * @version $Id: UMass2SmartGridTemplateParser.java, v 0.1 8 Apr 2014 17:58:00 chench Exp $
 */
public class UMassSmartGrid2TemplateParser implements Parser {

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
            String[] elements = context.split(SAPERATOR_EXPRESSION);
            long timeVal = Long.valueOf(elements[0]) * 1000;
            int reading = Double.valueOf(elements[1]).intValue();

            MeterReadingVO meter = new MeterReadingVO(reading, null, timeVal);
            return meter;
        } catch (Exception e) {
            ExceptionUtil.caught(e, "解析ParserTemplate错误，内容: " + template.getAsString());
        }
        return null;
    }

}
