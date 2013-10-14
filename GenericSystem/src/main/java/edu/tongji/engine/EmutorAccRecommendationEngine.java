/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import java.io.Serializable;
import java.util.List;

import edu.tongji.util.StringUtil;

/**
 * 
 * @author chenkh
 * @version $Id: EmutorAccRecommendationEngine.java, v 0.1 2013-9-24 下午9:16:36 chenkh Exp $
 */
public class EmutorAccRecommendationEngine extends RecommendationEngine {

    private final static String MISS_RATING = "MISS_RATING";

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void excute() {

        //0. 获取[丢失用户]的虚拟在线记录
        contextEnvelope.setComplementsSet((List<Serializable>) dataSource.excute(MISS_RATING));
        contextEnvelope.setRatingComplementsSet(dataSource.excuteEx(StringUtil.EMPTY_STRING));

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
