/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.tongji.parser.Parser;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.WeatherTemplateParser;
import edu.tongji.util.DateUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.vo.WeatherVO;

/**
 * WeatherVO为元素，提供天气查询
 * 
 * @author chench
 * @version $Id: WeatherCache.java, v 0.1 18 Apr 2014 16:32:59 chench Exp $
 */
public final class WeatherCache {

    /** file 地址*/
    private static String                       source;

    /** 缓存*/
    private final static Map<String, WeatherVO> reposity = new HashMap<String, WeatherVO>();

    /**
     * 获得天气信息
     * 
     * @param key   yyyyMMdd
     * @return
     */
    public static WeatherVO get(String key) {
        if (reposity.isEmpty()) {
            //初始化
            initialize();
        }

        return reposity.get(key);
    }

    /**
     * 获得天气信息
     * 
     * @param l1
     * @return
     */
    public static WeatherVO get(long l1) {
        if (reposity.isEmpty()) {
            //初始化
            initialize();
        }

        return reposity.get(DateUtil.format(new Date(l1), DateUtil.SHORT_FORMAT));
    }

    /**
     * 初始化缓存
     */
    protected static void initialize() {
        String[] content = FileUtil.readLinesByPattern(source);
        for (String tempStr : content) {
            ParserTemplate template = new ParserTemplate();
            template.setTemplate(tempStr);

            Parser parser = new WeatherTemplateParser();
            WeatherVO weather = (WeatherVO) parser.parser(template);

            //装入缓存
            reposity.put(DateUtil.format(weather.getDay(), DateUtil.SHORT_FORMAT), weather);
        }
    }

    /**
     * Setter method for property <tt>source</tt>.
     * 
     * @param source value to be assigned to property source
     */
    public void setSource(String source) {
        WeatherCache.source = source;
    }

}
