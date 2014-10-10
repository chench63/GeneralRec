/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack;

import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import edu.tongji.cache.WeatherCache;
import edu.tongji.extend.crack.support.PrivacyCrackObject;
import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.noise.Noise;
import edu.tongji.util.DateUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 列均值破解器
 * 
 * @author Hanke Chen
 * @version $Id: StandardExpectationCracker.java, v 0.1 2014-2-20 上午9:51:28 chench Exp $
 */
public class StandardExpectationCracker extends ExpectationCracker {

    /** 
     * @see edu.tongji.extend.crack.PrivacyCracker#crack(edu.tongji.extend.crack.support.PrivacyCrackObject)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void crack(PrivacyCrackObject object, int blockSize, Noise noise, HashKeyCallBack hashKyGen) {
        //0. 汇总数据
        List<MeterReadingVO> content = object.getTarget();
        List<ELement> baseElems = tabulate(content, 0, blockSize, hashKyGen);
        List<ELement> estimateElems = tabulate(content, blockSize, 2 * blockSize, hashKyGen);

        //1. 日志输出
        StringBuilder logMsg = new StringBuilder("StandardExpectationCracker");
        for (int i = 0, j = baseElems.size(); i < j; i++) {
            String key = hashKyGen.key(baseElems.get(i).getTimeVal());

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
     * @see edu.tongji.extend.crack.PrivacyCracker#crackInnerNoise(edu.tongji.extend.crack.support.PrivacyCrackObject, edu.tongji.noise.Noise)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void crackInnerNoise(PrivacyCrackObject object, Noise noise, HashKeyCallBack hashKyGen) {
        //0. 汇总数据
        List<MeterReadingVO> content = object.getTarget();
        List<ELement> baseElems = tabulate(content, 0, content.size(), hashKyGen);

        //1. 输出均值
        Map<String, DescriptiveStatistics> cache = (Map<String, DescriptiveStatistics>) object
            .get(PrivacyCrackObject.STAT_CACHE);
        for (ELement element : baseElems) {
            String key = (hashKyGen == null) ? StringUtil.EMPTY_STRING : hashKyGen.key(element
                .getTimeVal());

            DescriptiveStatistics stat = cache.get(key);
            if (stat == null) {
                //初始值
                stat = element.getStats();
                cache.put(key, stat);
            }

            //循环输入
            for (double value : element.getStats().getValues()) {
                stat.addValue((noise == null) ? value : noise.perturb(value));
            }

        }
    }
}
