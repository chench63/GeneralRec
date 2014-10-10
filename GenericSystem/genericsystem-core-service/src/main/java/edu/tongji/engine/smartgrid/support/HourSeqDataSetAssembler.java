/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid.support;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.tongji.util.DateUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 按小時汇总合并数据
 * 
 * @author Hanke Chen
 * @version $Id: HourSeqDataSetAssembler.java, v 0.1 5 Apr 2014 12:25:40 chench Exp $
 */
public class HourSeqDataSetAssembler implements DataSetAssembler {

    /** 
     * @see edu.tongji.engine.smartgrid.support.DataSetAssembler#assemble(java.util.List, java.util.List)
     */
    @Override
    public void assemble(List<MeterReadingVO> source, List<MeterReadingVO> target) {

        //对上下文进行排序
        Collections.sort(source, new Comparator<MeterReadingVO>() {
            @Override
            public int compare(MeterReadingVO o1, MeterReadingVO o2) {
                return ((Long) (o1.getTimeVal() - o2.getTimeVal())).intValue();
            }
        });

        //处理数据集
        MeterReadingVO meterReading = null;
        for (MeterReadingVO meter : source) {

            if (meterReading == null) {
                //初始化
                meterReading = meter;
                continue;
            } else if (!DateUtil.sameDayAndHour(meterReading.getTimeVal(), meter.getTimeVal())) {

                //新的电表计时周期
                target.add(meterReading);
                meterReading = meter;

                continue;
            }
            //在同一计时周期，累计读数
            meterReading.setReading(meterReading.getReading() + meter.getReading());

        }
    }

}
