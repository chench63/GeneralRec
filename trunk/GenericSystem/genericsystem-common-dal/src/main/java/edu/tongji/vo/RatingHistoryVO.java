/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.vo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.tongji.model.Rating;

/**
 * 
 * @author Hanke Chen
 * @version $Id: RatingHistoryVO.java, v 0.1 2013-9-23 下午4:46:19 chench Exp $
 */
public class RatingHistoryVO {

    /** rating实体上下文 */
    private final Map<String, Rating> ratingEntities = new HashMap<String, Rating>();

    /**
     * 新增usrId和rating键值对
     * 
     * @param usrId
     * @param rating
     */
    public void put(String usrId, Rating rating) {
        ratingEntities.put(usrId, rating);
    }

    /**
     * 根据usrId获取rating对象
     * 
     * @param usrId
     * @return
     */
    public Rating get(String usrId) {
        return ratingEntities.get(usrId);
    }

    public Iterator<Rating> iterator() {
        return ratingEntities.values().iterator();
    }
}
