/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.tongji.log4j.LoggerDefineConstant;

/**
 * 
 * @author chench
 * @version $Id: TestGnuPlotUtil.java, v 0.1 2014-2-17 下午6:47:26 chench Exp $
 */
public class TestGnuPlotUtil {
    /** logger */
    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    @Test
    public void test() {
        String path = "C:\\Users\\ppiachen\\Desktop\\Backup\\Tex\\SmartMetering_Privacy\\Figure\\test";
        List<String> stream = new ArrayList<String>();
        stream.add("Month");
        stream.add("Beijing");
        stream.add("Shanghai");
        stream.add("1");
        stream.add("10.1");
        stream.add("12.3");
        stream.add("2");
        stream.add("4");
        stream.add("5.9");
        GnuplotUtil.genDataFile(stream, 3, path);
        LoggerUtil.debug(logger, TestGnuPlotUtil.class);
    }
}
