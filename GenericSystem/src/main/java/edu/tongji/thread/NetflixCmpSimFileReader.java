/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.tongji.cache.SimilarityStreamCache;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.extend.noise.Noise;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.parser.netflix.NetflixRatingTemplateParser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;

/**
 * 文件中读取数据。
 * 
 * @author chench
 * @version $Id: NetflixCmpSimFileReader.java, v 0.1 2014-4-22 上午10:26:58 chench Exp $
 */
public class NetflixCmpSimFileReader extends Thread {

    /** 需要加载的文件  **/
    private Map<TemplateType, String> sourceEntity;

    /** 噪声*/
    private Noise                     noise;

    /** logger */
    protected final static Logger     logger = Logger
                                                 .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        Entry<TemplateType, String> entry = sourceEntity.entrySet().iterator().next();
        for (int movieId = 1; movieId <= ConfigurationConstant.TASK_SIZE; movieId++) {
            //1. 拼写文件名
            String fileName = (new StringBuilder(entry.getValue()))
                .append(StringUtil.alignRight(String.valueOf(movieId), 7, FileUtil.ZERO_PAD_CHAR))
                .append(FileUtil.TXT_FILE_SUFFIX).toString();

            //2. 读取文件
            String[] contents = FileUtil.readLines(fileName);

            //3. 解析模板 
            List<RatingVO> singleItems = new ArrayList<RatingVO>(contents.length);
            for (int i = 1; i < contents.length; i++) {
                ParserTemplate template = new ParserTemplate();
                template.setTemplate((new StringBuilder()).append(movieId)
                    .append(Rating.ELEMENT_SEPERATOR).append(contents[i]).toString());
                template.put(NetflixRatingTemplateParser.KEY_MOVIEID, String.valueOf(movieId));

                singleItems.add((RatingVO) entry.getKey().parser(template));
            }

            //4. 载入缓存
            if (ConfigurationConstant.IS_PERTURBATION) {
                SimilarityStreamCache.putAndDisguise(movieId, singleItems, noise);
            } else {
                SimilarityStreamCache.put(movieId, singleItems);
            }
        }
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
