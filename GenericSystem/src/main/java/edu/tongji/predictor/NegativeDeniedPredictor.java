/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.predictor;

import java.text.DecimalFormat;
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
    private final static Logger logger      = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** logger */
    private final static Logger log         = Logger.getLogger(LoggerDefineConstant.SERVICE_CORE);

    /* 统计估计值大于原始值数量*/
    private static int          countLagger = 0;

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
        //        int countOfNeighbour = 0;
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

            if (!logger.isDebugEnabled()) {
                //系统处于非Debug状态，直接返回
                return;
            }
            DecimalFormat decimalFormat = null;
            Boolean isBigger = false;
            decimalFormat = new DecimalFormat("#.000");
            if (predictValue > originalValue) {
                countLagger++;
                isBigger = true;

                int itemsBelongtoUser = ratings.size();
                StringBuilder logMessage = new StringBuilder(countLagger);
                logMessage.append("\t").append(" U: ").append(String.format("%7s", key))
                    .append(" I: ").append(String.format("%5d", predictItemId)).append(" P: ")
                    .append(String.format("%4d", itemsBelongtoUser));
                LoggerUtil.debug(log, countLagger + " " + logMessage);
            }
            LoggerUtil.debug(
                logger,
                "O：" + decimalFormat.format(originalValue) + " P: "
                        + decimalFormat.format(predictValue) + " U: " + String.format("%7s", key)
                        + (isBigger ? (" *") : ""));
        }
    }
}
