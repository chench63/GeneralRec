/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.predictor;

import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.cache.CacheHolder;
import edu.tongji.cache.GeneralCache;
import edu.tongji.configure.TestCaseConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: GeneralPredictor.java, v 0.1 2013-10-31 下午4:30:10 chenkh Exp $
 */
public class NegativeDeniedPredictor implements Predictor {

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see edu.tongji.predictor.Predictor#predict(edu.tongji.predictor.PredictorHolder)
     */
    @Override
    public void predict(PredictorHolder predictHolder) {
        //PredictorHolder:
        //{KEY, @PREDICT_ITEM, @PREDICT_VALUE}
        //key为usrId
        String key = String.valueOf(predictHolder.get(PredictorHolder.KEY));
        //获得被估计用户，所有历史评分数据
        List<CacheHolder> ratings = GeneralCache.gets(key);
        //获得被估计用户，需要估计的itemId
        int predictItemId = (int) predictHolder.get("PREDICT_ITEM");

        //估计itemd的评分
        boolean canPredict = false;
        double sumOfValue = 0.0;
        double sumOfSim = 0.0;
        double originalValue = 0.0;
        for (CacheHolder cacheHolder : ratings) {
            Rating rating = (Rating) cacheHolder.get("RATING");
            if (rating.getMovieId() == predictItemId) {
                //记录分析数据使用
                originalValue = rating.getRating();
            }

            //获取[predictItemId]与[该评分对应的item]相似度
            //{@SIM}
            CacheHolder similarity = GeneralCache.get(HashKeyUtil.genKey(predictItemId,
                rating.getMovieId()));
            if (similarity == null
                || (rating.getMovieId() >= TestCaseConfigurationConstant.SIMILARITY_RIGHT_SIDE)) {
                //相似度不存在,返回
                continue;
            }

            if (!canPredict) {
                //存在有个item的历史数据在训练数据集内
                //标记为可以估计
                canPredict = true;
            }
            Double sim = (Double) similarity.get("SIM");
            if (sim < 0) {
                //拒绝负值的相似度
                continue;
            } else if (TestCaseConfigurationConstant.IS_PERTURBATION) {
                sumOfValue += ((Double) cacheHolder.get("DISGUISED_VALUE")) * (sim);
            } else {
                sumOfValue += rating.getRating() * (sim);
            }
            sumOfSim += sim;
        }

        //添加估计值
        if (canPredict) {
            double predictValue = sumOfValue / sumOfSim;

            if (predictValue < 0) {
                predictValue = 0;
            } else if (predictValue > 5) {
                predictValue = 5.0;
            }

            predictHolder.put("PREDICT_VALUE", predictValue);

            if (logger.isDebugEnabled()) {

            }
            LoggerUtil.debug(logger, "产生估计...原始值：" + originalValue + "   估计值: " + predictValue);
        }
    }
}
