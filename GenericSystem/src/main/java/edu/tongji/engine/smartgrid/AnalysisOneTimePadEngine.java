/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.math.BigInteger;

import org.springframework.util.StopWatch;

import edu.tongji.encryption.EncryptionContext;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.OneTimePadUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 
 * @author chench
 * @version $Id: AnalysisOneTimePadEngine.java, v 0.1 2014-3-10 上午9:39:22 chench Exp $
 */
public class AnalysisOneTimePadEngine extends OneTimePadEngine {

    /** 
     * @see edu.tongji.engine.smartgrid.OneTimePadEngine#emulate()
     */
    @Override
    protected void emulate() {
        //1.模拟记录读数
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //2.模拟收集区域数据
        OneTimePadUtil.newPrime(primeLens, keyLens);
        BigInteger cipherMonthly = BigInteger.ZERO;
        Double consumationInDomain = 0.0;
        for (MeterReadingVO reading : SmartGridDataSource.meterContexts) {
            //模拟meter加密
            consumationInDomain += reading.getReading();
            BigInteger plainMeter = new BigInteger(String.format("%.0f", reading.getReading()));
            EncryptionContext providerContext = OneTimePadUtil.sensitiveEnc(plainMeter);
            BigInteger cipherMeter = (BigInteger) providerContext.get("CIPHERTEXT");

            //模拟Customer加密
            EncryptionContext customerContext = OneTimePadUtil.secretEnc(cipherMeter);
            BigInteger cipherCustomer = (BigInteger) customerContext.get("CIPHERTEXT");

            //模拟Provider存储密文
            cipherMonthly = cipherMonthly.add(cipherCustomer);
        }
        stopWatch.stop();

        //3.模拟估计区域总用电量
        double consumationEsti = estimate(cipherMonthly);

        //4.输出日志
        LoggerUtil.debug(logger, "共计算：" + SmartGridDataSource.meterContexts.size() + " Original："
                                 + String.format("%.2f", consumationInDomain) + " Estimation："
                                 + String.format("%.2f", consumationEsti));
        if (logger.isDebugEnabled()) {
            SmartGridEngine.STAT.addValue(stopWatch.getLastTaskTimeMillis());
        }
    }

    /**
     * 估计sigma k_i，使用期望代替
     * 
     * @param cipherMonthly
     * @return
     */
    protected double estimate(BigInteger cipherMonthly) {
        //1. 估计密钥  
        //sigma K_i 的期望 =  m * ( 2^n -1 )
        StringBuilder binaryStr = new StringBuilder();

        //      1.1. 2^n -1 即n个1
        for (int i = 0; i < OneTimePadUtil.KEY_LENGTH; i++) {
            binaryStr.append(1);
        }
        //      1.2. 乘以虚拟用户数m
        BigInteger keyEstimatation = (new BigInteger(binaryStr.toString(), 2))
            .multiply(new BigInteger(String.valueOf(-1 * SmartGridDataSource.meterContexts.size())));

        //2. 恢复至smart meter层秘文
        cipherMonthly = cipherMonthly.add(keyEstimatation);

        //3. 取模操作
        cipherMonthly = cipherMonthly.mod(OneTimePadUtil.MODULUS_BASE);

        //4. 计算初始数据
        return cipherMonthly.divide(OneTimePadUtil.BIG_PRIME).doubleValue();
    }

}
