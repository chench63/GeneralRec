/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.tongji.cache.SimilarityStreamCache;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.noise.Noise;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.FileUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;

/**
 * 文件中读取数据。
 * 
 * @author Hanke Chen
 * @version $Id: NetflixCmpSimFileReader.java, v 0.1 2014-4-22 上午10:26:58 chench Exp $
 */
public class NetflixCmpSimFileReader extends Thread {

    /** 需要加载的文件  **/
    private Map<TemplateType, String> sourceEntity;

    /** 噪声*/
    private Noise                     noise;

    /** 任务开始位置*/
    private int                       taskStart = 1;

    /** 任务结束位置*/
    private int                       taskEnd   = ConfigurationConstant.TASK_SIZE;

    /** logger */
    protected final static Logger     logger    = Logger
                                                    .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        Entry<TemplateType, String> entry = sourceEntity.entrySet().iterator().next();
        for (int movieId = taskStart; movieId <= taskEnd; movieId++) {
            //1. 拼写文件名
            String fileName = (new StringBuilder(entry.getValue()))
                .append(StringUtil.alignRight(String.valueOf(movieId), 7, FileUtil.ZERO_PAD_CHAR))
                .append(FileUtil.TXT_FILE_SUFFIX).toString();

            //2. 读取文件
            String[] contents = FileUtil.readLines(fileName);

            //3. 解析模板 
            List<RatingVO> singleItems = new ArrayList<RatingVO>(contents.length);
            for (int i = 0; i < contents.length; i++) {
                ParserTemplate template = new ParserTemplate();

                //兼容处理
                if (ConfigurationConstant.PARSER_RATINGVO) {
                    //解析RatingVO文件
                    template.setTemplate(contents[i]);
                } else {
                    //解析Rating文件，在抬头加上movieId
                    template.setTemplate((new StringBuilder()).append(movieId)
                        .append(Rating.ELEMENT_SEPERATOR).append(contents[i]).toString());
                    //template.put(NetflixRatingTemplateParser.KEY_MOVIEID, String.valueOf(movieId));
                }

                RatingVO rating = (RatingVO) entry.getKey().parser(template);
                if (rating == null) {
                    //异常处理
                    continue;
                }

                if (ConfigurationConstant.IS_PERTURBATION) {
                    //RP Algorithm
                    rating.setRatingCmp((float) noise.perturb(rating.getRatingReal()));
                }
                singleItems.add(rating);
            }

            //4. 载入缓存
            SimilarityStreamCache.put(movieId, singleItems);
        }
    }

    /**
     * Setter method for property <tt>taskStart</tt>.
     * 
     * @param taskStart value to be assigned to property taskStart
     */
    public void setTaskStart(int taskStart) {
        this.taskStart = taskStart;
    }

    /**
     * Setter method for property <tt>taskEnd</tt>.
     * 
     * @param taskEnd value to be assigned to property taskEnd
     */
    public void setTaskEnd(int taskEnd) {
        this.taskEnd = taskEnd;
    }

    /**
     * Getter method for property <tt>noise</tt>.
     * 
     * @return property value of noise
     */
    public Noise getNoise() {
        return noise;
    }

    /**
     * Setter method for property <tt>noise</tt>.
     * 
     * @param noise value to be assigned to property noise
     */
    public void setNoise(Noise noise) {
        this.noise = noise;
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
