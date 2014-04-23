/**
 * Alipay.com Inc.
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
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.predictor.Predictor;
import edu.tongji.predictor.PredictorHolder;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author chench
 * @version $Id: NetflixSimularityRecorder.java, v 0.1 31 Oct 2013 22:24:34 chench Exp $
 */
public class NetflixEvaPredctRecorder extends Thread {

    /** logger */
    private final static Logger           logger = Logger
                                                     .getLogger(LoggerDefineConstant.SERVICE_NORMAL);
    /** 预测器 */
    private Predictor                     predictor;

    /** 计算MAE*/
    protected final DescriptiveStatistics stat   = new DescriptiveStatistics();

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        CacheTask task = null;
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
            for (RatingVO rating : prsnHistry) {
                stat.addValue(Math.abs(rating.getRatingReal() - rating.getRatingCmp()));
            }
        }

        //4. 载入日志
        LoggerUtil.info(logger, "HiHi");
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
