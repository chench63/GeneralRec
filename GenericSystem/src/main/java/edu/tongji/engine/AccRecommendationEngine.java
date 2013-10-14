/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import java.io.Serializable;
import java.util.List;

import edu.tongji.util.StringUtil;

/**
 * multiple-part secure protocol结合后，使用类似copy-on-write方式，累计修正相似度误差值。
 * 
 * @author chench
 * @version $Id: MultipartSecureRecommendationEngine.java, v 0.1 16 Sep 2013 21:46:27 chench Exp $
 */
public class AccRecommendationEngine extends RecommendationEngine {

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void excute() {

        //1. 模拟更新过程
        for (List<Serializable> resultSet = (List<Serializable>) dataSource
            .excute(StringUtil.EMPTY_STRING); resultSet != null; resultSet = (List<Serializable>) dataSource
            .excute(StringUtil.EMPTY_STRING)) {

            //0. 从数据库，获取测试数据集, 存储于ContextEnvelope
            contextEnvelope.setResultSet(resultSet);

            //1. 转化测试数据集为内部抽象数据集
            processorContext.switchToProcessorContext(contextEnvelope);

            //3. 调用处理器，处理数据
            processor.process(processorContext);
        }

    }
}
