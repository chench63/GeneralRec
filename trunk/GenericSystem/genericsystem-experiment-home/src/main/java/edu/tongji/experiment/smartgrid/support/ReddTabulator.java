/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid.support;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.DateUtil;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 
 * @author chench
 * @version $Id: ReddTabulator.java, v 0.1 2014-5-19 下午3:52:54 chench Exp $
 */
public final class ReddTabulator {

    /** 文件路径*/
    protected final static String            SOURCE_FILE    = "E:/H3_C.*";

    /** 文件路径*/
    protected final static String            TARGET_FILE    = "E:/H3_C101";

    /** logger */
    private final static Logger              logger         = Logger
                                                                .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /** 数据源*/
    private final static SmartGridDataSource dataSource     = new SmartGridDataSource();

    /** 下界*/
    private static long                      low_bound      = 0L;

    /** 上界*/
    private static long                      upper_bound    = 0L;

    /** 指定日期*/
    private final static String              specified_data = null;

    /** 修正波士顿时差 UTC-5*/
    //    private static long                      UTC_OFFSET     = 5 * 60 * 60 * 1000;
    private static long                      UTC_OFFSET     = 0;

    static {

        try {
            //修正波士顿时差 UTC-5
            low_bound = DateUtil.parse("20110423", DateUtil.SHORT_FORMAT).getTime();
            upper_bound = DateUtil.parse("20110429", DateUtil.SHORT_FORMAT).getTime();
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Parse Error.");
        }

    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //1. 读取数据
        Map<TemplateType, String> sourceEntity = new HashMap<TemplateType, String>();
        sourceEntity.put(TemplateType.REDD_SMART_GRID_TEMPLATE, SOURCE_FILE);
        dataSource.setSourceEntity(sourceEntity);
        dataSource.reload();
        LoggerUtil.info(logger, "1. Loading data set completes.");

        //2. 过滤出目标数据
        StringBuilder content = new StringBuilder();
        for (MeterReadingVO reading : SmartGridDataSource.meterContexts) {
            long timeVal = reading.getTimeVal() - UTC_OFFSET;

            if (StringUtil.isNotBlank(specified_data)
                && StringUtil.equals(specified_data,
                    DateUtil.format(new Date(timeVal), DateUtil.SHORT_FORMAT))) {
                reading.setTimeVal(timeVal);
                content.append(reading).append(FileUtil.BREAK_LINE);
            } else if (timeVal >= low_bound && timeVal <= upper_bound) {
                reading.setTimeVal(timeVal);
                content.append(reading).append(FileUtil.BREAK_LINE);
            }
        }
        LoggerUtil.info(logger, "2. Filtering target context completes.");

        //3. 写入目标文件
        FileUtil.write(TARGET_FILE, content.toString());
        LoggerUtil.info(logger, "3. File Gens.");
    }
}
