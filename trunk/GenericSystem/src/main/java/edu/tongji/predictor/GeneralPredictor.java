/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.predictor;

import java.util.List;

import edu.tongji.cache.CacheHolder;
import edu.tongji.cache.GeneralCache;
import edu.tongji.model.Rating;
import edu.tongji.util.HashKeyUtil;

/**
 * 
 * @author chenkh
 * @version $Id: GeneralPredictor.java, v 0.1 2013-10-31 下午4:30:10 chenkh Exp $
 */
public class GeneralPredictor implements Predictor {

    /** 
     * @see edu.tongji.predictor.Predictor#predict(edu.tongji.predictor.PredictorHolder)
     */
    @Override
    public void predict(PredictorHolder predictHolder) {
        //获得用户评分数据
        //PredictorHolder:
        //{KEY, @PREDICT_ITEM, @PREDICT_VALUE}
        //key为usrId
        String key = String.valueOf(predictHolder.get(PredictorHolder.KEY));
        List<CacheHolder> ratings = GeneralCache.gets(key);

        int predictItemId = (int) predictHolder.get("PREDICT_ITEM");
        double predictValue = 0.0;
        for (CacheHolder cacheHolder : ratings) {
            Rating rating = (Rating) cacheHolder.get("RATING");

            //获取[predictItemId]与[该评分对应的item]相似度
            //{@SIM}
            CacheHolder similarity = GeneralCache.get(HashKeyUtil.genKey(predictItemId,
                rating.getMovieId()));
            Object sim = similarity.get("SIM");
            if (sim == null || !(sim instanceof Double)) {
                //相似度不存在,返回
                continue;
            }
            predictValue += rating.getRating() * ((Double) sim);
        }

        //添加估计值
        predictHolder.put("PREDICT_VALUE", predictValue);
    }
}
