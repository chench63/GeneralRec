/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import edu.tongji.cache.MatrixCache;
import edu.tongji.engine.recommendation.SnglrValuDecmpsRcmdEngine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.FileUtil;

/**
 * 文件中读取数据
 * 
 * @author chench
 * @version $Id: MovieLensMatrixReader.java, v 0.1 2014-10-7 下午7:38:27 chench Exp $
 */
public final class MovieLensMatrixReader extends Thread {

    /** 需要加载的文件  **/
    private Map<TemplateType, String> sourceEntity;

    /** logger */
    protected final static Logger     logger = Logger
                                                 .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        //1. 获取文件名
        Entry<TemplateType, String> entry = sourceEntity.entrySet().iterator().next();
        String fileName = entry.getValue();

        //2. 读取文件
        String[] contents = FileUtil.readLines(fileName);

        //3. 解析模板
        Map<Integer, DescriptiveStatistics> avgRating = new HashMap<Integer, DescriptiveStatistics>();
        for (String content : contents) {
            ParserTemplate template = new ParserTemplate(content);
            Rating rating = (Rating) entry.getKey().parser(template);
            if (rating == null) {
                //异常处理
                continue;
            }

            //3a. 计算用户评分均值
            DescriptiveStatistics stat = avgRating.get(rating.getUsrId());
            if (stat == null) {
                stat = new DescriptiveStatistics();
                avgRating.put(rating.getUsrId(), stat);
            }
            stat.addValue(rating.getRating());

            //4. 载入缓存
            MatrixCache.setByDatstIndex(rating.getUsrId(), rating.getMovieId(), rating.getRating());
        }

        //5. 导入用户平均分
        DescriptiveStatistics stat = null;
        for (Integer key : avgRating.keySet()) {
            stat = avgRating.get(key);
            SnglrValuDecmpsRcmdEngine.avgUsrRating.put(key, stat.getMean());
        }

    }

    /**
     * Setter method for property <tt>sourceEntity</tt>.
     * 
     * @param sourceEntity value to be assigned to property sourceEntity
     */
    public void setSourceEntity(Map<TemplateType, String> sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

}
