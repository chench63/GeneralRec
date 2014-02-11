/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.math.BigInteger;
import org.springframework.util.StopWatch;
import edu.tongji.orm.SmartGridDataSource;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.PaillierUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * 智能电网，模拟处理过程,使用同态加密机制
 * 
 * @author chench
 * @version $Id: HomomorphicEngine.java, v 0.1 2013-12-18 上午11:03:34 chench Exp
 *          $
 */
public class HomomorphicEngine extends SmartGridEngine {

    /**
     * @see edu.tongji.engine.Engine#excute()
     */
    @Override
    public void excute() {

        // 0. 载入数据集
        dataSource.reload();

        // 1.模拟记录读数
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        PaillierUtil.newInstance();
        BigInteger cipherMonthly = null;
        for (MeterReadingVO reading : SmartGridDataSource.meterContexts) {
            // 模拟Customer加密
            BigInteger plaintext = new BigInteger(((Integer) reading.getReading()).toString());
            BigInteger ciphertext = PaillierUtil.encryptions(plaintext);

            // 模拟Provider存储密文
            if (cipherMonthly == null) {
                cipherMonthly = ciphertext;
            } else {
                PaillierUtil.add(cipherMonthly, ciphertext);
            }
        }
        stopWatch.stop();

        // 3.输出日志
        LoggerUtil.debug(logger, "共计算：" + SmartGridDataSource.meterContexts.size() + " 耗时："
                                 + String.format("%2d", stopWatch.getLastTaskTimeMillis()));
        if (logger.isDebugEnabled()) {
            runtimes[0] += stopWatch.getLastTaskTimeMillis();
        }

    }

}
