/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.tongji.log4j.LoggerDefineConstant;

/**
 * 
 * @author Hanke Chen
 * @version $Id: TestFileUtil.java, v 0.1 2014-2-16 下午10:41:27 chench Exp $
 */
public class TestFileUtil {
    /** logger */
    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    @Test
    public void testReader() {
        String path = "src/main/resources/dataset/testcase/smartgrid/2012-Jul.*.csv";
        String[] context = FileUtil.readLinesByPattern(path);
        LoggerUtil.info(logger, context.length);
    }

    @Test
    public void testWriter() {
        String path = "C:\\Users\\ppiachen\\Desktop\\Backup\\Tex\\SmartMetering_Privacy\\Figure\\test";
        String context = "TestFunction";
        FileUtil.write(path, context);
    }

}
