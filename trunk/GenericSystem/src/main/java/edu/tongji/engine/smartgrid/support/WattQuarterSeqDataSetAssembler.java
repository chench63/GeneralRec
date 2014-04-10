/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid.support;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.util.DateUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 
 * @author chench
 * @version $Id: WattQuarterSeqDataSetAssembler.java, v 0.1 8 Apr 2014 21:25:56 chench Exp $
 */
public class WattQuarterSeqDataSetAssembler implements DataSetAssembler {

    /** 
     * @see edu.tongji.engine.smartgrid.support.DataSetAssembler#assemble(java.util.List, java.util.List)
     */
    @Override
    public void assemble(List<MeterReadingVO> source, List<MeterReadingVO> target) {

        //处理数据集，默认15分钟为一组汇总数据
        Map<String, MeterReadingVO> repo = new HashMap<String, MeterReadingVO>();
        Map<String, DescriptiveStatistics> meanRepo = new HashMap<String, DescriptiveStatistics>();
        MeterReadingVO meterReading = null;
        DescriptiveStatistics stat = null;
        for (MeterReadingVO meter : source) {
            String key = HashKeyUtil.genKeySeqQuarter(meter);
            meterReading = repo.get(key);
            stat = meanRepo.get(key);

            //Key对映值为null，插入值
            if (meterReading == null && stat == null) {
                meter.setTimeVal(meter.getTimeVal() - 1000 * 60
                                 * (DateUtil.getMinOfHour(meter.getTimeVal()) % 15));
                stat = new DescriptiveStatistics();
                repo.put(key, meter);
                meanRepo.put(key, stat);
            }

            //累计功率
            stat.addValue(meter.getReading());
        }

        //复制结果至上下文
        for (String key : repo.keySet()) {
            meterReading = repo.get(key);
            stat = meanRepo.get(key);
            meterReading.setReading(stat.getMean());
        }
        Collections.synchronizedList(target).addAll(repo.values());

        //对上下文进行排序
        Collections.sort(target, new Comparator<MeterReadingVO>() {
            @Override
            public int compare(MeterReadingVO o1, MeterReadingVO o2) {
                return o1.compareTo(o2);
            }

        });
    }

}
