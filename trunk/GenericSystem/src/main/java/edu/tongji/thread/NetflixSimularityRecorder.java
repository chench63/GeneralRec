/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.cache.CacheHolder;
import edu.tongji.cache.CacheTask;
import edu.tongji.cache.GeneralCache;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.predictor.Predictor;
import edu.tongji.predictor.PredictorHolder;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chench
 * @version $Id: NetflixSimularityRecorder.java, v 0.1 31 Oct 2013 22:24:34 chench Exp $
 */
public class NetflixSimularityRecorder extends Thread {

    /** logger */
    private final static Logger logger         = Logger
                                                   .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 预测器 */
    private Predictor           predictor;

    /** AE 平均绝对值误差*/
    private static double       absolute_error = 0.0;

    private static double       square_error   = 0.0;

    private static double       positve_error  = 0.0;

    /** AE 估计样本的个数*/
    private static double       nCount         = 0.0;

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        LoggerUtil.debug(logger, "Recorder join.");

        CacheTask task = null;
        double partOfAE = 0.0;
        double partOfSE = 0.0;
        double partOfnCount = 0.0;
        double partOfPE = 0.0;
        while ((task = GeneralCache.task()) != null) {
            //=============================
            //性能测试开始
            //=============================
            PredictorHolder predictorHolder = new PredictorHolder();
            String key = String.valueOf(task.i);
            predictorHolder.put(PredictorHolder.KEY, key);

            List<CacheHolder> ratings = GeneralCache.gets(key);
            for (CacheHolder cacheHolder : ratings) {
                Rating rating = (Rating) cacheHolder.get("RATING");
                int movieId = rating.getMovieId();
                if (movieId < ConfigurationConstant.movieStart
                    || movieId >= ConfigurationConstant.movieEnd) {
                    //所示id不在训练集范围内，返回
                    continue;
                }

                //预测该用户的评分
                predictorHolder.put("PREDICT_ITEM", movieId);
                predictor.predict(predictorHolder);
                //计算估计值与真实值的绝对值
                Double predictValue = (Double) predictorHolder.get("PREDICT_VALUE");

                if (predictValue != null) {
                    //预测估计器，产生估计值，记录入内.
                    partOfAE += Math.abs(predictValue - rating.getRating());
                    partOfSE += Math.pow(predictValue - rating.getRating(), 2.0);
                    partOfPE += (predictValue > rating.getRating()) ? predictValue
                                                                      - rating.getRating() : 0;
                    partOfnCount += 1;
                }
            }
            //=============================
            //性能测试结束
            //=============================
        }

        update(partOfAE, partOfSE, partOfPE, partOfnCount);
    }

    /**
     * 更新累计值AE
     * 
     * @param addition
     */
    private static synchronized void update(double partOfAE, double partOfSE, double partOfPE,
                                            double additionOfCount) {
        absolute_error += partOfAE;
        square_error += partOfSE;
        positve_error += partOfPE;
        nCount += additionOfCount;
        DecimalFormat decimalFormat = new DecimalFormat("#.00000");
        LoggerUtil.info(
            logger,
            "AE: " + decimalFormat.format(absolute_error) + " PE： "
                    + decimalFormat.format(positve_error) + " Ic： " + (int) nCount + " MAE: "
                    + decimalFormat.format(absolute_error / nCount) + " RMSE: "
                    + decimalFormat.format(Math.sqrt(square_error / nCount)));
    }

    /**
     * Getter method for property <tt>predictor</tt>.
     * 
     * @return property value of predictor
     */
    public Predictor getPredictor() {
        return predictor;
    }

    /**
     * Setter method for property <tt>predictor</tt>.
     * 
     * @param predictor value to be assigned to property predictor
     */
    public void setPredictor(Predictor predictor) {
        this.predictor = predictor;
    }

}
