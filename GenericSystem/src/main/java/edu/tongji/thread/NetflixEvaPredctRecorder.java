/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import edu.tongji.cache.CacheTask;
import edu.tongji.cache.GeneralCache;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.predictor.Predictor;
import edu.tongji.predictor.PredictorHolder;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;

/**
 * GeneralCache获得任务，并计算特定用户的所有评分，记录MAE.
 * 
 * @author chench
 * @version $Id: NetflixSimularityRecorder.java, v 0.1 31 Oct 2013 22:24:34 chench Exp $
 */
public class NetflixEvaPredctRecorder extends Thread {

    /** logger */
    private final static Logger                  logger = Logger
                                                            .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 预测器 */
    private Predictor                            predictor;

    /** 全局统计器*/
    protected static final DescriptiveStatistics STAT   = new DescriptiveStatistics();

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        CacheTask task = null;
        StatisticsInner statGlobal = new StatisticsInner();
        while ((task = GeneralCache.task()) != null) {
            //1. 读取文件
            File file = (File) task.get(CacheTask.FILE);
            String[] contents = FileUtil.readLines(file.getAbsolutePath());
            List<RatingVO> prsnHistry = new ArrayList<RatingVO>(contents.length);
            for (int i = 1; i < contents.length; i++) {
                ParserTemplate template = new ParserTemplate();
                template.setTemplate(contents[i]);

                prsnHistry.add((RatingVO) TemplateType.NETFLIX_RATINGVO_TEMPLATE.parser(template));
            }

            //2. 生产预测评分
            PredictorHolder prdctHlder = new PredictorHolder();
            prdctHlder.put(PredictorHolder.PERSON_RATING_HISTRY, prsnHistry);
            predictor.predict(prdctHlder);

            //3. 载入日志
            StatisticsInner statSingle = new StatisticsInner();
            for (RatingVO rating : prsnHistry) {
                //GeneralPredictor 预测值存于RatingCmp中
                statGlobal.addValue(Math.abs(rating.getRatingReal() - rating.getRatingCmp()));
                statSingle.addValue(Math.abs(rating.getRatingReal() - rating.getRatingCmp()));
            }
            LoggerUtil.info(logger, new StringBuilder("File: ").append(file.getName()).append('\t')
                .append(statSingle));
        }

        //4. 输出日志
        completeAndReport(statGlobal);

    }

    /**
     * 结束并报告线程计算结果
     * 
     * @param statGlobal
     */
    protected synchronized void completeAndReport(StatisticsInner statGlobal) {
        STAT.addValue(statGlobal.getAverage());

        if (STAT.getN() == ConfigurationConstant.THREAD_SIZE) {
            LoggerUtil.info(
                logger,
                (new StringBuilder(FileUtil.BREAK_LINE)).append("Attention!")
                    .append(FileUtil.BREAK_LINE).append("S: ").append(STAT.getSum())
                    .append(FileUtil.BREAK_LINE).append("N: ").append(STAT.getN()));
        }
    }

    /**
     * 简化统计内部类，节约内存
     * 
     * @author chench
     * @version $Id: NetflixEvaPredctRecorder.java, v 0.1 24 Apr 2014 19:48:23 chench Exp $
     */
    protected class StatisticsInner {

        /** 和*/
        private float sum = 0.0f;

        /** 累计个数*/
        private int   N   = 0;

        /**
         * 添加值
         * 
         * @param val
         */
        public void addValue(float val) {
            sum += val;
            N++;
        }

        /**
         * 返回均值
         * 
         * @return
         */
        public float getAverage() {
            if (N == 0) {
                return Float.NaN;
            }

            return sum / N;
        }

        /** 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return (new StringBuilder("Sum: "))
                .append(StringUtil.alignLeft(String.format("%.3f", sum), 12)).append(" N: ")
                .append(StringUtil.alignLeft(String.format("%d", N), 12)).append(" Avg: ")
                .append(this.getAverage()).toString();
        }
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
