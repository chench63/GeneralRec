/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.Parser;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.smartgrid.WeatherTemplateParser;
import edu.tongji.util.DateUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
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

    /** logger */
    protected final static Logger               logger   = Logger
                                                             .getLogger(LoggerDefineConstant.SERVICE_CACHE);

    /**
     * 获得天气信息，非线程安全
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
     * 获得天气信息，非线程安全
     * 
     * @param l1
     * @return
     */
    public static WeatherVO get(long l1) {
        if (reposity.isEmpty()) {
            //初始化
            initialize();
        }

        WeatherVO weatherVO = null;
        return (weatherVO = reposity.get(DateUtil.format(new Date(l1 - DateUtil.getMinOfHour(l1)
                                                                  * 60 * 1000),
            DateUtil.LONG_WEB_FORMAT_NO_SEC))) == null ? reposity.get(DateUtil.format(new Date(l1),
            DateUtil.SHORT_FORMAT)) : weatherVO;
    }

    /**
     * 初始化缓存
     */
    public static void initialize() {
        String[] content = FileUtil.readLinesByPattern(source);
        float[] T = new float[4];
        for (String tempStr : content) {
            ParserTemplate template = new ParserTemplate();
            template.setTemplate(tempStr);

            Parser parser = new WeatherTemplateParser();
            WeatherVO weather = (WeatherVO) parser.parser(template);

            //装入缓存
            reposity.put(DateUtil.format(weather.getDay(), DateUtil.SHORT_FORMAT), weather);
            reposity.put(DateUtil.format(
                new Date(weather.getDay().getTime()
                         - DateUtil.getMinOfHour(weather.getDay().getTime()) * 60 * 1000),
                DateUtil.LONG_WEB_FORMAT_NO_SEC), weather);

            //日志部分信息
            int t = 0;
            double temperature = weather.getHighTemper();
            if (temperature >= 16.0d) {
                t = temperature >= 28.0d ? 3 : ((Double) ((temperature - 16.0d) / 6 + 1))
                    .intValue();
            }
            T[t] += 1;
        }

        //输出日志
        LoggerUtil.info(
            logger,
            (new StringBuilder()).append(FileUtil.BREAK_LINE).append("T[0]：")
                .append(T[0] / reposity.size()).append(FileUtil.BREAK_LINE).append("T[1]：")
                .append(T[1] / reposity.size()).append(FileUtil.BREAK_LINE).append("T[2]：")
                .append(T[2] / reposity.size()).append(FileUtil.BREAK_LINE).append("T[3]：")
                .append(T[3] / reposity.size()).append(FileUtil.BREAK_LINE));
    }

    /**
     * Setter method for property <tt>source</tt>.
     * 
     * @param source value to be assigned to property source
     */
    public static void setSource(String source) {
        WeatherCache.source = source;
    }

}
