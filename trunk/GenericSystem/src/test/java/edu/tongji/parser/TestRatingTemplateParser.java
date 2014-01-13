/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chench
 * @version $Id: TestRatingTemplateParser.java, v 0.1 2013-9-6 下午4:44:29 chench Exp $
 */
public class TestRatingTemplateParser {

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

}
