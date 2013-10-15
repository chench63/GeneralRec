/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.cache.SimularityStreamCache;
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

    /** 开始movieId号*/
    private int                 movieStart;

    /** 结束movieId号*/
    private int                 movieEnd;

    /** 开始时间轴*/
    private String              scrachTimeLine;

    /** 截止时间轴*/
    private String              endTimeLine;

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
        param.add(String.valueOf(this.movieStart));
        param.add(String.valueOf(this.movieEnd));
        param.add(scrachTimeLine);
        param.add(endTimeLine);

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

    /**
     * Getter method for property <tt>movieStart</tt>.
     * 
     * @return property value of movieStart
     */
    public int getMovieStart() {
        return movieStart;
    }

    /**
     * Setter method for property <tt>movieStart</tt>.
     * 
     * @param movieStart value to be assigned to property movieStart
     */
    public void setMovieStart(int movieStart) {
        this.movieStart = movieStart;
    }

    /**
     * Getter method for property <tt>movieEnd</tt>.
     * 
     * @return property value of movieEnd
     */
    public int getMovieEnd() {
        return movieEnd;
    }

    /**
     * Setter method for property <tt>movieEnd</tt>.
     * 
     * @param movieEnd value to be assigned to property movieEnd
     */
    public void setMovieEnd(int movieEnd) {
        this.movieEnd = movieEnd;
    }

    /**
     * Getter method for property <tt>scrachTimeLine</tt>.
     * 
     * @return property value of scrachTimeLine
     */
    public String getScrachTimeLine() {
        return scrachTimeLine;
    }

    /**
     * Setter method for property <tt>scrachTimeLine</tt>.
     * 
     * @param scrachTimeLine value to be assigned to property scrachTimeLine
     */
    public void setScrachTimeLine(String scrachTimeLine) {
        this.scrachTimeLine = scrachTimeLine;
    }

    /**
     * Getter method for property <tt>endTimeLine</tt>.
     * 
     * @return property value of endTimeLine
     */
    public String getEndTimeLine() {
        return endTimeLine;
    }

    /**
     * Setter method for property <tt>endTimeLine</tt>.
     * 
     * @param endTimeLine value to be assigned to property endTimeLine
     */
    public void setEndTimeLine(String endTimeLine) {
        this.endTimeLine = endTimeLine;
    }

}
