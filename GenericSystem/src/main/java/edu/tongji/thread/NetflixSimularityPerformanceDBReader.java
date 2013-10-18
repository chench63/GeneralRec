/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.cache.SimularityStreamCache;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.dao.RatingDAO;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixSimularityPerformanceDBReader.java, v 0.1 2013-10-12 下午2:51:30 chenkh Exp $
 */
public class NetflixSimularityPerformanceDBReader extends Thread {

    /** 查询评分的集合*/
    private final static String EXCUTE_SELECT_GENERAL_RATING = "excute_select_general_rating";

    /** DAO */
    private RatingDAO           ratingDAO;

    /** logger */
    private final static Logger logger                       = Logger
                                                                 .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        //Parameters：
        //  [movieStart, movieEnd), [scrachTimeLine, endTimeLine)
        List<String> param = new ArrayList<String>();
        param.add(String.valueOf(ConfigurationConstant.movieStart));
        param.add(String.valueOf(ConfigurationConstant.movieEnd));
        param.add(ConfigurationConstant.scrachTimeLine);
        param.add(ConfigurationConstant.endTimeLine);

        //DB读取所需要的数据，加载至缓存
        List<Rating> resultSet = ratingDAO.select(EXCUTE_SELECT_GENERAL_RATING, param);
        SimularityStreamCache.put(resultSet);
        LoggerUtil.debug(logger, "NetflixSimularityPerformanceDBReader 加载缓存结束");
        
    }

    /**
     * Getter method for property <tt>ratingDAO</tt>.
     * 
     * @return property value of ratingDAO
     */
    public RatingDAO getRatingDAO() {
        return ratingDAO;
    }

    /**
     * Setter method for property <tt>ratingDAO</tt>.
     * 
     * @param ratingDAO value to be assigned to property ratingDAO
     */
    public void setRatingDAO(RatingDAO ratingDAO) {
        this.ratingDAO = ratingDAO;
    }

}
