/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.parser.smartgrid;

import java.util.Date;

import edu.tongji.parser.Parser;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.util.DateUtil;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.WeatherVO;

/**
 * 天气模板解析类
 * 
 * @author Hanke Chen
 * @version $Id: WeatherTemplateParser.java, v 0.1 18 Apr 2014 15:13:03 chench Exp $
 */
public class WeatherTemplateParser implements Parser {

    /** 日期索引*/
    private static final int    DAY_INDEX            = 0;

    /** 最高温度索引*/
    private static final int    HIGHT_INDEX          = 1;

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

            //23元素，按天计的天气信息
            Date date = elements.length == 23 ? DateUtil.parse(elements[DAY_INDEX],
                DateUtil.WEB_FORMAT) : DateUtil.parse(elements[elements.length - 1],
                DateUtil.LONG_WEB_FORMAT);

            double highTemper = Double.valueOf(elements[HIGHT_INDEX]).doubleValue();

            WeatherVO weather = new WeatherVO();
            weather.setDay(date);
            weather.setHighTemper(highTemper);
            return weather;
        } catch (Exception e) {
            ExceptionUtil.caught(e, "解析ParserTemplate错误，内容: " + template.getAsString());
        }
        return null;
    }

}
