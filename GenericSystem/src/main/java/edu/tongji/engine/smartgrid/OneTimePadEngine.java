/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.math.BigInteger;
import org.springframework.util.StopWatch;
import edu.tongji.encryption.EncryptionContext;
import edu.tongji.orm.SmartGridDataSource;
//import edu.tongji.util.LoggerUtil;
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
    protected int keyLens;

    /** 质数长度*/
    protected int primeLens;

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#emulate()
     */
    @Override
    protected void emulate() {
        //1.模拟记录读数
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        OneTimePadUtil.newPrime(primeLens, keyLens);
        BigInteger cipherMonthly = BigInteger.ZERO;

        for (MeterReadingVO reading : SmartGridDataSource.meterContexts) {
            //模拟meter加密
            BigInteger plainMeter = new BigInteger(String.format("%.0f", reading.getReading()));
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
        //        LoggerUtil.debug(logger,
        //            "KL：" + keyLens + " Total：" + SmartGridDataSource.meterContexts.size() + " Tick："
        //                    + String.format("%2d", stopWatch.getLastTaskTimeMillis()));
        //                                 + " Prime：" + OneTimePadUtil.BIG_PRIME);
        if (logger.isDebugEnabled()) {
            SmartGridEngine.STAT.addValue(stopWatch.getLastTaskTimeMillis());
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

    /**
     * Getter method for property <tt>primeLens</tt>.
     * 
     * @return property value of primeLens
     */
    public int getPrimeLens() {
        return primeLens;
    }

    /**
     * Setter method for property <tt>primeLens</tt>.
     * 
     * @param primeLens value to be assigned to property primeLens
     */
    public void setPrimeLens(int primeLens) {
        this.primeLens = primeLens;
    }

}
