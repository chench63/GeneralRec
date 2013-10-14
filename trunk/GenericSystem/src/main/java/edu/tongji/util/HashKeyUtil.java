/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import edu.tongji.model.Rating;

/**
 * Map中自定义索引键工具类
 * 
 * @author chenkh
 * @version $Id: HashKeyUtil.java, v 0.1 2013-9-9 下午3:06:03 chenkh Exp $
 */
public final class HashKeyUtil {

    /**
     * 生成Hash值
     * 
     * @param rating
     * @return
     */
    public static String genKey(Rating rating) {
        return rating.getUsrId() + "_" + rating.getMovieId();
    }

}
