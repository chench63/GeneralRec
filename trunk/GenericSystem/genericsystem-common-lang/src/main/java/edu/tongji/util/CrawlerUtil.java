/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.support.UrlGen;

/**
 * 网页爬虫工具
 * 
 * @author chench
 * @version $Id: CrawlerUtil.java, v 0.1 2014-5-26 上午10:04:47 chench Exp $
 */
public final class CrawlerUtil {

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 禁用构造函数
     */
    private CrawlerUtil() {

    }

    /**
     * 爬虫程序
     * 
     * @param urlGen
     */
    public static void crawl(UrlGen urlGen) {
        try {
            StringBuilder content = new StringBuilder();
            for (Iterator<String> iter = urlGen.iterator(); iter.hasNext();) {
                //1. 获取地址
                URL url = new URL(iter.next());

                //2. 创建链接
                HttpURLConnection cnnctn = (HttpURLConnection) url.openConnection();

                //3. 读取HTML
                content.append(urlGen.regulate(cnnctn.getInputStream()));

                //4. 输出日志
                cnnctn.disconnect();
                LoggerUtil.info(logger, "Load: " + url);
            }

            //4. 序列化至本地
            FileUtil.write("E:\\1.text", content.toString());
        } catch (IOException e) {
            ExceptionUtil.caught(e, "CrawlerUtil Crashed");
        }
    }
}
