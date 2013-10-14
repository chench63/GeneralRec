/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import java.io.Serializable;
import java.util.List;

import edu.tongji.model.Rating;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author chenkh
 * @version $Id: FastAccRecommendationEngine.java, v 0.1 2013-9-24 下午9:18:10 chenkh Exp $
 */
public class FastAccRecommendationEngine extends RecommendationEngine {

    private final static String MISS_RATING = "MISS_RATING";

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void excute() {

        //0. 获取[丢失用户]的虚拟在线记录
        List<Serializable> complementsSet = (List<Serializable>) dataSource.excute(MISS_RATING);

        //1. 获取更新用户集合
        List<Serializable> resultSet = (List<Serializable>) dataSource
            .excute(StringUtil.EMPTY_STRING);

        //2. 简单粗暴的修剪数据集合
        removeMissingRating(complementsSet, resultSet);

        //3. 从数据库，获取测试数据集, 存储于ContextEnvelope
        contextEnvelope.setResultSet(resultSet);

        //4. 转化测试数据集为内部抽象数据集
        processorContext.switchToProcessorContext(contextEnvelope);

        //5. 调用处理器，处理数据
        processor.process(processorContext);
    }

    /**
     * 简单粗暴的修剪数据集合，删除[丢失用户]的评价。
     * 
     * @param complementsSet
     * @param resultSet
     */
    private void removeMissingRating(List<Serializable> complementsSet, List<Serializable> resultSet) {
        Rating rating = null;
        for (Serializable customer : complementsSet) {
            rating = (Rating) customer;
            int indexOfRating = resultSet.indexOf(rating);

            //usrId && movieId相等表示为[丢失用户]的评价，删除之
            if ((indexOfRating != -1)
                && rating.getMovieId() == ((Rating) resultSet.get(indexOfRating)).getMovieId()) {
                resultSet.remove(indexOfRating);
            }
        }

    }

}
