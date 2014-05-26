/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

import java.sql.Date;
import java.util.List;

import edu.tongji.cache.WeatherCache;
import edu.tongji.crack.support.HashKeyCallBack;
import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.extend.noise.Noise;
import edu.tongji.util.DateUtil;
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
    public void crack(CrackObject object, int blockSize, HashKeyCallBack hashKyGen) {
        //0. 汇总数据
        List<MeterReadingVO> content = object.getTarget();
        List<ELement> baseElems = tabulate(content, 0, blockSize, blockSize);
        List<ELement> estimateElems = tabulate(content, blockSize, content.size(), blockSize);

        //1. 日志输出
        StringBuilder logMsg = new StringBuilder("ExpectationSeqDayCracker");
        for (int i = 0, j = baseElems.size(); i < j; i++) {
            String key = DateUtil.format(new Date(baseElems.get(i).getTimeVal()),
                DateUtil.SHORT_FORMAT);
            String temperature = String.format("%.0f",
                WeatherCache.get(baseElems.get(i).getTimeVal()).getHighTemper());
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

    /** 
     * @see edu.tongji.crack.PrivacyCracker#crackInnerNoise(edu.tongji.crack.CrackObject, edu.tongji.extend.noise.Noise)
     */
    @Override
    public void crackInnerNoise(CrackObject object, Noise noise, HashKeyCallBack hashKyGen) {
        throw new OwnedException(FunctionErrorCode.ILLEGAL_PARAMETER);
    }

}
