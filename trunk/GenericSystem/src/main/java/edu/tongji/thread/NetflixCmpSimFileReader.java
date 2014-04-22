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

import edu.tongji.cache.SimularityStreamCache;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.parser.NetflixRatingTemplateParser;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.FileUtil;
import edu.tongji.util.StringUtil;

/**
 * 文件中读取数据。
 * 
 * @author chench
 * @version $Id: NetflixCmpSimFileReader.java, v 0.1 2014-4-22 上午10:26:58 chench Exp $
 */
public class NetflixCmpSimFileReader extends Thread {

    /** 需要加载的文件  **/
    private Map<TemplateType, String> sourceEntity;

    /** logger */
    protected final static Logger     logger      = Logger
                                                      .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 文件后缀 */
    private static final String       FILE_SUFFIX = ".txt";

    /** 文件格式的填充字符 */
    private final static char         PAD_CHAR    = '0';

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        Entry<TemplateType, String> entry = sourceEntity.entrySet().iterator().next();
        for (int movieId = 1; movieId <= 17770; movieId++) {
            //1. 拼写文件名
            String fileName = (new StringBuilder(entry.getValue()))
                .append(StringUtil.alignRight(String.valueOf(movieId), 7, PAD_CHAR))
                .append(FILE_SUFFIX).toString();

            //2. 读取文件
            String[] contents = FileUtil.readLines(fileName);

            //3. 解析模板 
            List<Rating> singleItems = new ArrayList<Rating>(contents.length);
            for (int i = 1; i < contents.length; i++) {
                ParserTemplate template = new ParserTemplate();
                template.setTemplate(contents[i]);
                template.put(NetflixRatingTemplateParser.KEY_MOVIEID, String.valueOf(movieId));

                singleItems.add((Rating) TemplateType.NETFLIX_RATING_TEMPLATE.parser(template));
            }

            //4. 载入缓存
            SimularityStreamCache.fastPut(String.valueOf(movieId), singleItems);
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
