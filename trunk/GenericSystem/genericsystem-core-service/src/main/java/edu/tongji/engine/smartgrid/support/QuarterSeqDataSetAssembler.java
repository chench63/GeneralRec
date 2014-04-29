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

import edu.tongji.util.DateUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 按刻汇总合并数据
 * 
 * @author chench
 * @version $Id: QuarterSeqDataSetAssembler.java, v 0.1 5 Apr 2014 12:30:50 chench Exp $
 */
public class QuarterSeqDataSetAssembler implements DataSetAssembler {

    /** 
     * @see edu.tongji.engine.smartgrid.support.DataSetAssembler#assemble(java.util.List, java.util.List)
     */
    @Override
    public void assemble(List<MeterReadingVO> source, List<MeterReadingVO> target) {

        //处理数据集，默认15分钟为一组汇总数据
        Map<String, MeterReadingVO> repo = new HashMap<String, MeterReadingVO>();
        MeterReadingVO meterReading = null;
        for (MeterReadingVO meter : source) {
            String key = HashKeyUtil.genKeySeqQuarter(meter);
            meterReading = repo.get(key);

            //Key对映值为null，插入值
            if (meterReading == null) {
                meter.setTimeVal(meter.getTimeVal() - 1000 * 60
                                 * (DateUtil.getMinOfHour(meter.getTimeVal()) % 15));
                repo.put(key, meter);
                continue;
            }

            //更新读数
            //按引用传递，无需重复插入到Map
            double newReadingValue = meterReading.getReading() + meter.getReading();
            meterReading.setReading(newReadingValue);
        }

        //复制结果至上下文
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
