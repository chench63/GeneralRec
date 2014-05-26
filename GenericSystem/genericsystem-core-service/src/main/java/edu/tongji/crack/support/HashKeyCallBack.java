/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack.support;

/**
 * 哈希函数，键生成接口
 * 
 * @author chench
 * @version $Id: HashKeyGen.java, v 0.1 2014-5-21 下午4:15:06 chench Exp $
 */
public interface HashKeyCallBack {

    /**
     * 生成键函数
     * 
     * @param object
     * @return
     */
    public String key(Object... object);

    /**
     * 返回有序的键全集
     * 
     * @return
     */
    public String[] keyArr();
}
