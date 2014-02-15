/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chench
 * @version $Id: TestUMassSmartGridTemplateParser.java, v 0.1 2013-12-17 下午4:08:34 chench Exp $
 */
public class TestUMassSmartGridTemplateParser {

    /** logger */
    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    @Test
    public void test() {
        LoggerUtil.info(logger, new Timestamp(1341047995000L));
        LoggerUtil.info(logger, new Timestamp(1343206423000L));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1341047957000L);
        LoggerUtil.info(logger, cal.get(Calendar.HOUR_OF_DAY));

    }

}
