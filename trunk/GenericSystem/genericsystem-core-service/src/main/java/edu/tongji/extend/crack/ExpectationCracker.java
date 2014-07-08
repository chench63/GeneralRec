/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.vo.MeterReadingVO;

/**
 * 期望累破解器抽象类
 * 
 * @author chench
 * @version $Id: ExpectationCracker.java, v 0.1 2014-2-22 下午12:39:58 chench Exp $
 */
public abstract class ExpectationCracker implements PrivacyCracker {

    /** 最低样本阈值*/
    public int                    SAMPLE_NUM_LIMITS = 84;

    /** logger */
    protected final static Logger logger            = Logger
                                                        .getLogger(LoggerDefineConstant.SERVICE_CORE);

    /**
     * 汇总数据
     * 
     * @param content
     * @param start
     * @param end
     * @return
     */
    protected List<ELement> tabulate(List<MeterReadingVO> content, int start, int end,
                                     HashKeyCallBack hashKyGen) {
        Map<String, ELement> repo = new HashMap<String, ELement>();
        for (int i = start; i < end; i++) {
            MeterReadingVO reading = content.get(i);
            //使用map整理数据
            String key = hashKyGen.key(reading.getTimeVal());

            ELement element = repo.get(key);
            if (element == null) {
                element = new ELement(new DescriptiveStatistics(), reading.getTimeVal());
            }
            element.getStats().addValue(reading.getReading());

            repo.put(key, element);
        }

        //对结果集合，按时间排序
        List<ELement> result = new ArrayList<ELement>();
        Collections.synchronizedList(result).addAll(repo.values());
        Collections.sort(result);
        return result;
    }

    /**
     * 汇总数据
     * 
     * @param content
     * @param hashKyGen
     * @return
     */
    protected List<ELement> tabulateWithOneDay(List<MeterReadingVO> content,
                                               HashKeyCallBack hashKyGen) {
        //0. 参数初始化
        int start = 0;
        int end = content.size();

        //1. 规整数据,按天合并数据
        Map<String, List<MeterReadingVO>> cache = new HashMap<String, List<MeterReadingVO>>();
        for (int i = start; i < end; i++) {
            MeterReadingVO reading = content.get(i);
            String key = hashKyGen.key(reading.getTimeVal());

            List<MeterReadingVO> arr = cache.get(key);
            if (arr == null) {
                arr = new ArrayList<MeterReadingVO>();
                cache.put(key, arr);
            }

            arr.add(reading);
        }

        //2. 生成事件集合
        List<ELement> result = new ArrayList<ELement>();
        for (List<MeterReadingVO> arr : cache.values()) {

            if (arr.size() != 4 * 24) {
                continue;
            }

            //升序排序
            Collections.sort(arr);

            for (int i = 0, j = arr.size(); i < j - 1 * 4; i++) {
                //a. 获取时间撮
                long timeVal = arr.get(i).getTimeVal();

                //e. 获取功耗
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (int ele = i; ele < i + 4; ele++) {
                    stats.addValue(arr.get(ele).getReading());
                }

                result.add(new ELement(stats, timeVal));
            }

        }

        return result;
    }

    /**
     * 内部类，数据承载类
     * 
     * @author chench
     * @version $Id: ExpectationSeqDayCracker.java, v 0.1 15 Apr 2014 17:29:37 chench Exp $
     */
    class ELement implements Comparable<ELement> {
        /** 统计类*/
        private DescriptiveStatistics stats;

        /** 时间撮*/
        private long                  timeVal;

        /**
         * @param stats
         * @param timeVal
         */
        public ELement(DescriptiveStatistics stats, long timeVal) {
            this.stats = stats;
            this.timeVal = timeVal;
        }

        /**
         * Getter method for property <tt>stats</tt>.
         * 
         * @return property value of stats
         */
        public DescriptiveStatistics getStats() {
            return stats;
        }

        /**
         * Setter method for property <tt>stats</tt>.
         * 
         * @param stats value to be assigned to property stats
         */
        public void setStats(DescriptiveStatistics stats) {
            this.stats = stats;
        }

        /**
         * Getter method for property <tt>timeVal</tt>.
         * 
         * @return property value of timeVal
         */
        public long getTimeVal() {
            return timeVal;
        }

        /**
         * Setter method for property <tt>timeVal</tt>.
         * 
         * @param timeVal value to be assigned to property timeVal
         */
        public void setTimeVal(long timeVal) {
            this.timeVal = timeVal;
        }

        /** 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(ELement o) {
            if (this.getTimeVal() == o.getTimeVal())
                return 0;
            return (this.getTimeVal() - o.getTimeVal() > 0) ? 1 : -1;
        }

    }

}
