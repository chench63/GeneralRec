/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.predictor;

import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.cache.GeneralCache;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;

/**
 * 常规评分推荐类
 * 
 * @author chench
 * @version $Id: GeneralPredictor.java, v 0.1 2013-10-31 下午4:30:10 chench Exp $
 */
public class GeneralPredictor implements Predictor {

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_CORE);

    /** 
     * @see edu.tongji.predictor.Predictor#predict(edu.tongji.predictor.PredictorHolder)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void predict(PredictorHolder predictHolder) {
        //1. 获得用户浏览记录
        List<RatingVO> prsnHistry = (List<RatingVO>) predictHolder
            .get(PredictorHolder.PERSON_RATING_HISTRY);

        //2. 复制ratingCmp
        float[] ratingCmp = new float[prsnHistry.size()];
        for (int i = 0, len = prsnHistry.size(); i < len; i++) {
            ratingCmp[i] = prsnHistry.get(i).getRatingCmp();
        }

        //3. 估计用户评分
        StringBuilder loggerMsg = new StringBuilder();
        for (int i = 0, len = prsnHistry.size(); i < len; i++) {
            RatingVO subject = prsnHistry.get(i);

            float sum = 0.0F;
            for (int j = 0; j < len; j++) {
                if (i == j) {
                    continue;
                }

                int x = i > j ? i : j;
                int y = i > j ? j : i;
                sum += GeneralCache.get(x, y).floatValue() * ratingCmp[j];
            }

            float predictVal = sum > 0 ? sum / (len - 1) : 0.0F;
            loggerMsg.append(StringUtil.alignLeft(String.valueOf(subject.getMovieId()), 7))
                .append("O：").append(String.format("%.2f", subject.getRatingReal())).append("  P：")
                .append(String.format("%.2f", predictVal)).append(FileUtil.BREAK_LINE);
            //ratingCmp存放预测值
            subject.setRatingCmp(predictVal);
        }

        //4.载入日志
        LoggerUtil.info(logger, loggerMsg);
    }
}
