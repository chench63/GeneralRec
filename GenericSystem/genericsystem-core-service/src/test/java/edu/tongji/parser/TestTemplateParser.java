/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.parser.smartgrid.WeatherTemplateParser;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.WeatherVO;

/**
 * 
 * @author chench
 * @version $Id: TestRatingTemplateParser.java, v 0.1 2013-9-6 下午4:44:29 chench Exp $
 */
public class TestTemplateParser {

    /** logger*/
    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    @Test
    public void test() {
        String context = "2::1090::2::978298580";
        ParserTemplate template = new ParserTemplate();
        template.setTemplate(context);

        Parser parser = new MovielensRatingTemplateParser();
        Rating rating = (Rating) parser.parser(template);
        LoggerUtil.info(logger, rating, "=\t" + System.currentTimeMillis());
    }

    @Test
    public void testDataFormat() throws ParseException {
        String timestamp = "2005-09-06";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateString = format.parse(timestamp);
        String timeString = format.format(dateString);
        LoggerUtil.info(logger, timeString);
    }

    @Test
    public void testWeatherTemplateParser() {
        String context = "2011-6-1,28,23,18,20,17,14,90,76,62,1021,1014,1008,16,13,2,48,23,61,14.22,6,中雨-冰雹-雷暴,211";
        ParserTemplate template = new ParserTemplate();
        template.setTemplate(context);

        Parser parser = new WeatherTemplateParser();
        WeatherVO weather = (WeatherVO) parser.parser(template);
        LoggerUtil.info(logger, weather);
    }

    @Test
    public void testSmartGrid() {
        LoggerUtil.info(logger, new Timestamp(1341047995000L));
        LoggerUtil.info(logger, new Timestamp(1343206423000L));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1341047957000L);
        LoggerUtil.info(logger, cal.get(Calendar.HOUR_OF_DAY));

    }

}
