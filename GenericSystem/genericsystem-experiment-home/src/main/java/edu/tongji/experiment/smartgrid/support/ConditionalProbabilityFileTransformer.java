/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.cache.WeatherCache;
import edu.tongji.engine.smartgrid.support.WattQuarterSeqDataSetAssembler;
import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.extend.crack.support.SeqDayHKCallBack;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.MeterReadingVO;
import edu.tongji.vo.BayesianEventVO;

/**
 * 读取REDD数据，转化为计算条件概率的Event文件
 * 
 * @author chench
 * @version $Id: ConditionalProbabilityFileTransformer.java, v 0.1 2014-5-27 下午6:10:49 chench Exp $
 */
public final class ConditionalProbabilityFileTransformer {

    /** 原始文件*/
    private final static String SOURCE             = "E:/H6_MIX";

    /** 目标文件*/
    private final static String TARGET             = "E:/H6_EVNT";

    /** 间隔小时*/
    private final static int    STEP_HOUR          = 1;

    /** 天气缓存加载文件*/
    private final static String WEATHER_CACHE_FILE = "src/main/resources/dataset/cache/weather/Hour_Boston_2011_.*";

    /** logger */
    private final static Logger logger             = Logger
                                                       .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        LoggerUtil.info(logger, "0. Program: ConditionalProbabilityFileTransformer ");

        //1. 读取并解析数据
        String[] lines = FileUtil.readLinesByPattern(SOURCE);
        List<MeterReadingVO> source = new ArrayList<MeterReadingVO>();
        for (String line : lines) {
            ParserTemplate template = new ParserTemplate();
            template.setTemplate(line);

            // 解析
            MeterReadingVO meter = (MeterReadingVO) TemplateType.REDD_SMART_GRID_TEMPLATE
                .parser(template);
            source.add(meter);
        }
        LoggerUtil.info(logger, "1. finish loading data from source: " + SOURCE);

        //2. 合并数据，模拟为15分钟一组
        List<MeterReadingVO> context = new ArrayList<MeterReadingVO>();
        WattQuarterSeqDataSetAssembler assembler = new WattQuarterSeqDataSetAssembler();
        assembler.assemble(source, context);
        LoggerUtil.info(logger, "2. finish regulating data. ");

        //3. 规整数据,按天合并数据
        Map<String, List<MeterReadingVO>> cache = new HashMap<String, List<MeterReadingVO>>();
        tabulate(cache, context, new SeqDayHKCallBack());
        LoggerUtil.info(logger, "3. finish merging data order by day. ");

        //4. 加载天气缓存
        WeatherCache.setSource(WEATHER_CACHE_FILE);
        LoggerUtil.info(logger, "4. finish configuring weather cache file. ");

        //5. 转化为REDD事件
        List<BayesianEventVO> fileContent = new ArrayList<BayesianEventVO>();
        for (List<MeterReadingVO> arr : cache.values()) {
            if (arr.size() != 4 * 24) {
                continue;
            }

            //升序排序
            Collections.sort(arr);

            for (int i = 0, j = arr.size(); i < j - STEP_HOUR * 4; i++) {
                //a. 获取时间撮
                long timeVal = arr.get(i).getTimeVal();

                //b. 获得温度
                float hot = (float) WeatherCache.get(timeVal).getHighTemper();

                //c. 在家
                short indoor = 1;

                //d. 空调开启
                short ac = 0;

                //e. 计算功耗
                float power = 0.0f;
                for (int ele = i; ele < i + 4; ele++) {
                    power += arr.get(ele).getReading();
                }
                power /= 4.0f;

                fileContent.add(new BayesianEventVO(timeVal, hot, indoor, ac, power));
            }
        }
        LoggerUtil.info(logger, "5. finish transforming bean to ReddEvent. ");

        //6. 序列化至文件
        StringBuilder content = new StringBuilder();
        for (BayesianEventVO event : fileContent) {
            content.append(event.toString()).append(FileUtil.BREAK_LINE);
        }
        FileUtil.write(TARGET, content.toString());
        LoggerUtil.info(logger, "6. Program Completes. ");
    }

    /**
     * 规整数据
     * 
     * @param cache
     * @param context
     */
    private static void tabulate(Map<String, List<MeterReadingVO>> cache,
                                 List<MeterReadingVO> context, HashKeyCallBack hashKyGen) {

        for (MeterReadingVO reading : context) {
            String key = hashKyGen.key(reading.getTimeVal());

            List<MeterReadingVO> arr = cache.get(key);
            if (arr == null) {
                arr = new ArrayList<MeterReadingVO>();
                cache.put(key, arr);
            }

            arr.add(reading);
        }

    }
}
