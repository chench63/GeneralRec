/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import edu.tongji.crack.support.HashKeyCallBack;
import edu.tongji.extend.statistics.StatisticianFactory;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.DateUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 期望累破解器抽象类
 * 
 * @author chench
 * @version $Id: ExpectationCracker.java, v 0.1 2014-2-22 下午12:39:58 chench Exp $
 */
public abstract class ExpectationCracker implements PrivacyCracker {

    /** 最低样本阈值*/
    public int                       SAMPLE_NUM_LIMITS = 84;

    /** 结果集*/
    public final static List<Double> CP_RESULT         = new ArrayList<Double>();

    /** logger */
    protected final static Logger    logger            = Logger
                                                           .getLogger(LoggerDefineConstant.SERVICE_CORE);

    /** 统计器*/
    protected StatisticianFactory    statisticianFactory;

    /**
     * 汇总数据
     * 
     * @param content
     * @param start
     * @param end
     * @param blockSize
     * @return
     */
    protected List<ELement> tabulate(List<MeterReadingVO> content, int start, int end, int blockSize) {
        Map<String, ELement> repo = new HashMap<String, ELement>();
        for (int i = start; i < end; i++) {
            MeterReadingVO reading = content.get(i);
            //使用map整理数据
            String key = generateKey(reading, i, blockSize);

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
     * @param start
     * @param end
     * @param blockSize
     * @return
     */
    protected List<ELement> tabulate(List<MeterReadingVO> content, int start, int end,
                                     int blockSize, HashKeyCallBack hashKyGen) {
        Map<String, ELement> repo = new HashMap<String, ELement>();
        for (int i = start; i < end; i++) {
            MeterReadingVO reading = content.get(i);
            //使用map整理数据
            String key = hashKyGen.key(reading.getTimeVal(), i, blockSize);

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
     * Map生成Key方法 KEY = [Day]_[COLUMN_SEQ]
     * 
     * @param reading   实体对象
     * @param index     序列号
     * @param rowSize   行数
     * @return
     */
    protected String generateKey(MeterReadingVO reading, int index, int rowSize) {
        return (new StringBuilder()).append(DateUtil.getDayOfYear(reading.getTimeVal()))
            .append(HashKeyUtil.ELEMENT_SEPERATOR).append(index / rowSize).toString();
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

    /**
     * Getter method for property <tt>statisticianFactory</tt>.
     * 
     * @return property value of statisticianFactory
     */
    public StatisticianFactory getStatisticianFactory() {
        return statisticianFactory;
    }

    /**
     * Setter method for property <tt>statisticianFactory</tt>.
     * 
     * @param statisticianFactory value to be assigned to property statisticianFactory
     */
    public void setStatisticianFactory(StatisticianFactory statisticianFactory) {
        this.statisticianFactory = statisticianFactory;
    }

}
