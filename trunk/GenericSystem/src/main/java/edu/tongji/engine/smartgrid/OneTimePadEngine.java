/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.math.BigInteger;
import java.util.List;

import org.springframework.util.StopWatch;

import edu.tongji.encryption.EncryptionContext;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.OneTimePadUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 智能电网，模拟处理过程,使用one-time pad加密机制
 * 
 * @author chench
 * @version $Id: SmartGridEngine.java, v 0.1 17 Dec 2013 20:08:27 chench Exp $
 */
public class OneTimePadEngine extends SmartGridEngine {

    /** 密钥长度*/
    private int keyLens;

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @SuppressWarnings("static-access")
    @Override
    public void excute() {

        //0. 载入数据集
        dataSource.reload();

        //1.模拟记录读数
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<MeterReadingVO> meterContexts = dataSource.meterContexts;
//        OneTimePadUtil.newPrime(keyLens);
        BigInteger cipherMonthly = BigInteger.ZERO;
        for (MeterReadingVO reading : meterContexts) {
            //模拟meter加密
            BigInteger plainMeter = new BigInteger(((Integer) reading.getReading()).toString());
            EncryptionContext providerContext = OneTimePadUtil.secretEnc(plainMeter);
            BigInteger cipherMeter = (BigInteger) providerContext.get("CIPHERTEXT");

            //模拟Customer加密
            EncryptionContext customerContext = OneTimePadUtil.sensitiveEnc(cipherMeter);
            BigInteger cipherCustomer = (BigInteger) customerContext.get("CIPHERTEXT");

            //模拟Provider存储密文
            cipherMonthly = cipherMonthly.add(cipherCustomer);
        }
        stopWatch.stop();

        //3.输出日志
        LoggerUtil.debug(
            logger,
            "共计算：" + meterContexts.size() + " 耗时："
                    + String.format("%2d", stopWatch.getLastTaskTimeMillis()) + " Prime："
                    + OneTimePadUtil.BIG_PRIME);
        if (logger.isDebugEnabled()) {
            runtimes[0] += stopWatch.getLastTaskTimeMillis();
            runtimes[1] += Math.pow(stopWatch.getLastTaskTimeMillis(), 2);
        }

    }

    /**
     * @return the keyLens
     */
    public int getKeyLens() {
        return keyLens;
    }

    /**
     * @param keyLens the keyLens to set
     */
    public void setKeyLens(int keyLens) {
        this.keyLens = keyLens;
    }

}
