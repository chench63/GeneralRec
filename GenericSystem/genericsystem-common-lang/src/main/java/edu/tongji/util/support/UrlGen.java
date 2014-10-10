/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util.support;

import java.io.InputStream;
import java.util.Iterator;

/**
 * 网页爬虫所用URL生成器
 * 
 * @author Hanke Chen
 * @version $Id: UrlIterator.java, v 0.1 2014-5-26 上午10:28:04 chench Exp $
 */
public interface UrlGen {

    /**
     * 返回URL迭代器
     * 
     * @return
     */
    public Iterator<String> iterator();

    /**
     * 规整HTML文件流
     * 
     * @param inputStream
     * @return
     */
    public StringBuilder regulate(InputStream inputStream);

}
