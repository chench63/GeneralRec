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
import edu.tongji.configure.TestCaseConfigurationConstant;
import edu.tongji.dao.RatingDAO;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chench
 * @version $Id: NetflixSimularityPerformanceDBReader.java, v 0.1 2013-10-12 下午2:51:30 chench Exp $
 */
public class NetflixCmpSimDBReader extends Thread {

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

        int movieStart = ConfigurationConstant.movieStart;
        int movieEnd = 177;
        final int step = 177;
        while (movieEnd <= ConfigurationConstant.movieEnd) {
            List<String> param = new ArrayList<String>();
            param.add(String.valueOf(movieStart));
            param.add(String.valueOf(movieEnd));
            param.add(ConfigurationConstant.scrachTimeLine);
            param.add(ConfigurationConstant.endTimeLine);

            //DB读取所需要的数据，加载至缓存
            List<Rating> resultSet = ratingDAO.select(EXCUTE_SELECT_GENERAL_RATING, param);

            if (TestCaseConfigurationConstant.IS_PERTURBATION) {
                SimularityStreamCache.putAndDisguise(resultSet, false);
            } else {
                SimularityStreamCache.put(resultSet);
            }

            //更新任务参数
            movieStart = movieEnd;
            movieEnd = movieEnd + step;
        }
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
