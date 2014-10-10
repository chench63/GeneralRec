/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.util.StopWatch;

import edu.tongji.encryption.EncryptionContext;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.OneTimePadUtil;
import edu.tongji.util.PaillierUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 功能测试引擎
 * 
 * @author Hanke Chen
 * @version $Id: AnalysisPerformanceEngine.java, v 0.1 2014-3-18 上午9:52:16 chench Exp $
 */
public class AnalysisPerformanceEngine extends SmartGridEngine {

    /** 密钥长度*/
    protected int    keyLens;

    /** 质数长度*/
    protected int    primeLens;

    /** 测试名称*/
    protected String testName;

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#emulate()
     */
    @Override
    protected void emulate() {

        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < 16; i++) {

            //1.初始化密钥配置
            keyLens += 128;
            OneTimePadUtil.newPrime(primeLens, keyLens);
            PaillierUtil.newInstance(keyLens, 64);

            //2.准备数据集
            List<BigInteger> chiperData = new ArrayList<BigInteger>();
            List<BigInteger> keySet = new ArrayList<BigInteger>();
            BigInteger monthChiper = null;
            if (StringUtil.equals(testName, "UCEA")) {
                prepareUCEAggScheme(chiperData, keySet);
            } else if (StringUtil.equals(testName, "HEA")) {
                prepareHEAggScheme(chiperData);
            } else if (StringUtil.equals(testName, "UCEAD")) {
                prepareUCEAggScheme(chiperData, keySet);
                monthChiper = testUCEAggScheme(chiperData);
            } else if (StringUtil.equals(testName, "HEAD")) {
                prepareHEAggScheme(chiperData);
                monthChiper = testHEAggScheme(chiperData);
            }

            //3.模拟meter加密
            DescriptiveStatistics stat = new DescriptiveStatistics();
            for (int repeat = 0; repeat < 1000; repeat++) {
                stopWatch.start();

                if (StringUtil.equals(testName, "UCEA")) {
                    testUCEAggScheme(chiperData);
                } else if (StringUtil.equals(testName, "UCE")) {
                    OneTimePadUtil.newPrime(primeLens, keyLens);
                    testUCEScheme();
                } else if (StringUtil.equals(testName, "HEA")) {
                    testHEAggScheme(chiperData);
                } else if (StringUtil.equals(testName, "HE")) {
                    testHEScheme();
                } else if (StringUtil.equals(testName, "HEAD")) {
                    testHEAggDec(monthChiper);
                } else if (StringUtil.equals(testName, "UCEAD")) {
                    testUCEAggDec(monthChiper, keySet);
                }

                stopWatch.stop();
                stat.addValue(stopWatch.getLastTaskTimeMillis() * 1000.0
                              / SmartGridDataSource.meterContexts.size());
            }

            //3.输出日志
            LoggerUtil.debug(
                logger,
                testName + " KL：" + String.format("%4d", keyLens) + " Total："
                        + SmartGridDataSource.meterContexts.size() + " Mean："
                        + String.format("%.3f", stat.getMean()) + " SD："
                        + String.format("%.3f", stat.getStandardDeviation()));
        }
    }

    /**
     * 测试整個Billing過程，两者加密
     * 
     * @param reading
     */
    protected void testUCEScheme() {
        for (MeterReadingVO reading : SmartGridDataSource.meterContexts) {
            //模拟meter加密
            BigInteger plainMeter = new BigInteger(String.format("%.0f", reading.getReading()));
            EncryptionContext providerContext = OneTimePadUtil.secretEnc(plainMeter);

            //模拟Customer加密
            BigInteger cipherMeter = (BigInteger) providerContext.get("CIPHERTEXT");
            OneTimePadUtil.sensitiveEnc(cipherMeter);
        }
    }

    /**
     * 测试整個Sentiment过程, 合并记录的效率, HE
     * 
     * @param chiperData
     */

    protected BigInteger testHEAggScheme(List<BigInteger> chiperData) {
        BigInteger cipherMonthly = null;
        for (BigInteger readingVal : chiperData) {
            if (cipherMonthly == null) {
                cipherMonthly = readingVal;
                continue;
            }

            cipherMonthly = PaillierUtil.add(cipherMonthly, readingVal);
        }
        return cipherMonthly;
    }

    /**
     * 测试整個Sentiment过程, 合并记录的效率, UCE
     * 
     * @param reading
     */
    protected BigInteger testUCEAggScheme(List<BigInteger> chiperData) {
        BigInteger cipherMonthly = BigInteger.ZERO;
        for (BigInteger readingVal : chiperData) {
            cipherMonthly = cipherMonthly.add(readingVal);
        }
        return cipherMonthly;
    }

    /**
     * 测试Homomorphic Encryption scheme 加密效率
     * 
     * @param reading
     */
    protected void testHEScheme() {
        for (MeterReadingVO reading : SmartGridDataSource.meterContexts) {
            BigInteger plainMeter = new BigInteger(String.format("%.0f", reading.getReading()));
            PaillierUtil.encryptions(plainMeter);
        }
    }

    /**
     * 测试整個Sentiment过程, 解密效率，HE
     * 
     * @param monthChiper
     */
    protected void testHEAggDec(BigInteger monthChiper) {
        PaillierUtil.decryptions(monthChiper);
    }

    /**
     * 测试整個Sentiment过程, 解密效率，UCES
     * 
     * @param monthChiper
     * @param keySet
     */
    protected void testUCEAggDec(BigInteger monthChiper, List<BigInteger> keySet) {
        BigInteger keySum = BigInteger.ZERO;
        for (BigInteger key : keySet) {
            keySum = keySum.add(key);
        }

        //解密
        EncryptionContext encryptionContext = new EncryptionContext(keySum);
        OneTimePadUtil.secretDec(monthChiper, encryptionContext);
    }

    /**
     * 为整個Sentiment过程准备数据, UCE
     * 
     * @param chiperData
     * @param keySet
     */
    protected void prepareUCEAggScheme(List<BigInteger> chiperData, List<BigInteger> keySet) {
        for (MeterReadingVO reading : SmartGridDataSource.meterContexts) {
            //模拟meter加密
            BigInteger plainMeter = new BigInteger(String.format("%.0f", reading.getReading()));
            EncryptionContext providerContext = OneTimePadUtil.secretEnc(plainMeter);
            BigInteger cipherMeter = (BigInteger) providerContext.get("CIPHERTEXT");

            //模拟Customer加密
            EncryptionContext customerContext = OneTimePadUtil.sensitiveEnc(cipherMeter);
            BigInteger cipherCustomer = (BigInteger) customerContext.get("CIPHERTEXT");

            //添加記錄
            chiperData.add(cipherCustomer);
            keySet.add(providerContext.getKeyValue());
            keySet.add(customerContext.getKeyValue());
        }
    }

    /**
     * 为整個Sentiment过程准备数据,HE
     * 
     * @param reading
     */
    protected void prepareHEAggScheme(List<BigInteger> chiperData) {
        for (MeterReadingVO reading : SmartGridDataSource.meterContexts) {
            //模拟加密
            BigInteger plainMeter = new BigInteger(String.format("%.0f", reading.getReading()));
            BigInteger ciphertext = PaillierUtil.encryptions(plainMeter);

            //添加記錄
            chiperData.add(ciphertext);
        }
    }

    /**
     * Getter method for property <tt>keyLens</tt>.
     * 
     * @return property value of keyLens
     */
    public int getKeyLens() {
        return keyLens;
    }

    /**
     * Setter method for property <tt>keyLens</tt>.
     * 
     * @param keyLens value to be assigned to property keyLens
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

    /**
     * Getter method for property <tt>testName</tt>.
     * 
     * @return property value of testName
     */
    public String getTestName() {
        return testName;
    }

    /**
     * Setter method for property <tt>testName</tt>.
     * 
     * @param testName value to be assigned to property testName
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }

}
