/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.cache.CacheTask;
import edu.tongji.cache.GeneralCache;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.SimilarityVO;

/**
 * 从文件中读取相似度信息，<br/>
 * 按RE规则，读取用户集
 * 
 * @author chench
 * @version $Id: NetFlixSimularityDBReader.java, v 0.1 31 Oct 2013 19:42:33 chench Exp $
 */
public class NetflixEvaPredctFileReader extends Thread {

    /** 需要加载的文件  **/
    private static Map<TemplateType, String> sourceEntity;

    /** logger */
    private final static Logger              logger = Logger
                                                        .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        //加载相似度信息至缓存
        if (!ConfigurationConstant.ENABLE_ECONOMICAL_CACHE) {
            loadSimularityToCache();
        }

        //加载评分测试用户级
        loadUserToCahche();
    }

    /**
     * 加载相似度信息至缓存
     */
    protected void loadSimularityToCache() {

        for (int movieId = 2; movieId <= ConfigurationConstant.TASK_SIZE; movieId++) {

            //1. 读取特定Item的相似度
            List<SimilarityVO> content = new ArrayList<SimilarityVO>();
            String fileName = loadSimilarityOutter(movieId, content);

            //2. 加载相似度入缓存
            for (SimilarityVO sim : content) {
                //由插入数据保证，x>y
                GeneralCache.put(sim.getItemI(), sim.getItemJ(), sim.getSimilarity());
            }

            //3. 输出日志
            LoggerUtil.info(logger, "File: " + fileName);
        }

    }

    /**
     * 读取特定Item的相似度
     * 
     * @param movieId
     * @return
     */
    public static String loadSimilarityOutter(int movieId, List<SimilarityVO> content) {
        //1. 拼写文件名
        String fileName = (new StringBuilder(sourceEntity.get(TemplateType.SIMILARITY_TEMPLATE)))
            .append(StringUtil.alignRight(String.valueOf(movieId), 7, FileUtil.ZERO_PAD_CHAR))
            .append(FileUtil.TXT_FILE_SUFFIX).toString();

        //2. 读取文件
        String[] contents = FileUtil.readLines(fileName);

        //3. 解析模板 
        for (int i = 0; i < contents.length; i++) {
            ParserTemplate template = new ParserTemplate();
            template.setTemplate(contents[i]);

            content.add((SimilarityVO) TemplateType.SIMILARITY_TEMPLATE.parser(template));
        }
        ((ArrayList<SimilarityVO>) content).ensureCapacity(contents.length);

        return fileName;
    }

    /**
     * 加载评分测试用户级
     */
    private void loadUserToCahche() {
        //获得所有目标文件
        File[] files = FileUtil.parserFilesByPattern(sourceEntity
            .get(TemplateType.NETFLIX_RATINGVO_TEMPLATE));

        //载入任务
        for (File file : files) {
            CacheTask task = new CacheTask();
            task.put(CacheTask.FILE, file);

            //载入缓存
            GeneralCache.store(task);
        }
    }

    /**
     * Setter method for property <tt>sourceEntity</tt>.
     * 
     * @param sourceEntity value to be assigned to property sourceEntity
     */
    public void setSourceEntity(Map<TemplateType, String> sourceEntity) {
        NetflixEvaPredctFileReader.sourceEntity = sourceEntity;
    }

}
