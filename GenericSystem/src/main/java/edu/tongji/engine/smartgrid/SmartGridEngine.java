/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import edu.tongji.encryption.EncryptionContext;
import edu.tongji.engine.Engine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.OneTimePadUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 智能电网，模拟处理过程
 * 
 * @author chench
 * @version $Id: SmartGridEngine.java, v 0.1 17 Dec 2013 20:08:27 chench Exp $
 */
public class SmartGridEngine implements Engine {

    /** 数据源*/
    protected SmartGridDataSource dataSource;

    /** logger */
    private final static Logger   logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

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
        LoggerUtil.info(logger,
            "共计算：" + meterContexts.size() + " 耗时：" + stopWatch.getLastTaskTimeMillis());
    }

    /**
     * Getter method for property <tt>dataSource</tt>.
     * 
     * @return property value of dataSource
     */
    public SmartGridDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Setter method for property <tt>dataSource</tt>.
     * 
     * @param dataSource value to be assigned to property dataSource
     */
    public void setDataSource(SmartGridDataSource dataSource) {
        this.dataSource = dataSource;
    }

}
