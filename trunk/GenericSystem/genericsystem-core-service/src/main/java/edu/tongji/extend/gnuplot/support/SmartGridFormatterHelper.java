/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.vo.MeterReadingVO;

/**
 * 智能电网格式化工具，汇总数据帮助类
 * 
 * @author Hanke Chen
 * @version $Id: SmartGridFormatterHelper.java, v 0.1 21 Apr 2014 15:31:03 chench Exp $
 */
public final class SmartGridFormatterHelper {

    /**
     * 汇总数据 <br/>
     * 
     * @param contents
     * @param rowSize
     * @return
     */
    public static Map<String, DescriptiveStatistics> tabulate(List<MeterReadingVO> contents,
                                                              int start, int end,
                                                              HashKeyCallBack hashKyGen) {
        Map<String, DescriptiveStatistics> repo = new HashMap<String, DescriptiveStatistics>();
        for (int i = start; i < end; i++) {
            MeterReadingVO reading = contents.get(i);
            //使用map整理数据
            String key = hashKyGen.key(reading.getTimeVal());

            DescriptiveStatistics stat = repo.get(key);
            if (stat == null) {
                stat = new DescriptiveStatistics();
                repo.put(key, stat);
            }
            stat.addValue(reading.getReading());
        }

        return repo;
    }

}
