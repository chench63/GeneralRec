/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import edu.tongji.model.Rating;
import edu.tongji.model.ValueOfItems;

/**
 * Map中自定义索引键工具类
 * 
 * @author chench
 * @version $Id: HashKeyUtil.java, v 0.1 2013-9-9 下午3:06:03 chench Exp $
 */
public final class HashKeyUtil {

    /**
     * 生成Hash值
     * 
     * @param rating
     * @return
     */
    public static String genKey(Rating rating) {
        StringBuilder stringBuilder = new StringBuilder(rating.getUsrId());
        return stringBuilder.append("_").append(rating.getMovieId()).toString();
    }

    /**
     * 生成Hash值
     * 
     * @param valueOfItem
     * @return
     */
    public static String genKey(ValueOfItems valueOfItem) {
        StringBuilder stringBuilder = new StringBuilder(valueOfItem.getItemI());
        return stringBuilder.append("_").append(valueOfItem.getItemJ()).toString();
    }

    /**
     * 生成Hash值
     * 
     * @param rating
     * @return
     */
    public static String genKey(int part1, int part2) {
        return part1 >= part2 ? part1 + "_" + part2 : part2 + "_" + part1;
    }
}
