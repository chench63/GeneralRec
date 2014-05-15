/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.cache.WeatherCache;
import edu.tongji.util.DateUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 
 * @author chench
 * @version $Id: ExpectationSeqDayCracker.java, v 0.1 15 Apr 2014 15:33:44 chench Exp $
 */
public class ExpectationSeqDayCracker extends ExpectationCracker {

    /** 
     * @see edu.tongji.crack.PrivacyCracker#crack(edu.tongji.crack.CrackObject, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void crack(CrackObject object, int blockSize) {
        //0. 汇总数据
        List<MeterReadingVO> content = object.getTarget();
        List<ELement> baseElems = tabulate(content, 0, blockSize, blockSize);
        List<ELement> estimateElems = tabulate(content, blockSize, content.size(), blockSize);

        //1. 日子输出
        StringBuilder logMsg = new StringBuilder("ExpectationSeqDayCracker");
        for (int i = 0, j = baseElems.size(); i < j; i++) {
            String key = DateUtil.format(new Date(baseElems.get(i).getTimeVal()),
                DateUtil.SHORT_FORMAT);
            String temperature = String.format("%.0f", WeatherCache.get(key).getHighTemper());
            String date = (new StringBuilder()).append(key).append(" (")
                .append(StringUtil.alignRight(temperature, 2)).append(")").append(" W：")
                .append(DateUtil.getDayOfWeek(baseElems.get(i).getTimeVal())).toString();
            String mean = (new StringBuilder())
                .append(String.format("%.2f", baseElems.get(i).getStats().getMean())).append(" (")
                .append(String.format("%.2f", estimateElems.get(i).getStats().getMean()))
                .append(")").toString();
            String sd = (new StringBuilder())
                .append(String.format("%.2f", baseElems.get(i).getStats().getStandardDeviation()))
                .append(" (")
                .append(
                    String.format("%.2f", estimateElems.get(i).getStats().getStandardDeviation()))
                .append(")").toString();

            logMsg.append("\n T：").append(date).append(" M：")
                .append(StringUtil.alignRight(mean.toString(), 16)).append(" SD：")
                .append(StringUtil.alignRight(sd.toString(), 16)).append(" S：")
                .append(String.format("%.2f", baseElems.get(i).getStats().getSum())).append(" (")
                .append(String.format("%.2f", estimateElems.get(i).getStats().getSum()))
                .append(")");
        }
        LoggerUtil.info(logger, logMsg);
    }

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

}
