/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.util.LoggerUtil;

/**
 * ProccessorContext，注意非线程安全。
 * 
 * @author Hanke Chen
 * @version $Id: DefaultProcessorContext.java, v 0.1 2013-9-7 下午7:40:12 chench Exp $
 */
public class RecommendationContext implements ProcessorContext {

    /** logger */
    private static final Logger logger              = Logger
                                                        .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 本地数据源缓存数据结构<String, Rating> */
    private Map<String, Rating> ratingContexts;

    /** 最大用户id*/
    private final int           countOfUsers;

    /** 最大电影id*/
    private final int           countOfMovies;

    /** itemI编号*/
    private String              itemI;

    /** itemI编号*/
    private String              itemJ;

    /** itemI所有用户的评分向量*/
    private final List<Number> ratingsValusOfItemI = new ArrayList<Number>();

    /** itemJ所有用户的评分向量*/
    private final List<Number> ratingsValusOfItemJ = new ArrayList<Number>();

    /**
     * 构造函数
     * @param countOfUsers
     * @param countOfMovies
     */
    public RecommendationContext(int countOfUsers, int countOfMovies) {
        this.countOfUsers = countOfUsers;
        this.countOfMovies = countOfMovies;
    }

    /** 
     * @see edu.tongji.context.ProcessorContext#clearContext()
     */
    @Override
    public void clearContext() {
    }

    /** 
     * @see edu.tongji.context.ProcessorContext#switchToProcessorContext(edu.tongji.context.ContextEnvelope)
     */
    @Override
    public void switchToProcessorContext(ContextEnvelope contextEnvelope) {
        //清空数据集，防止多次执行，数据集交叉
        ratingsValusOfItemI.clear();
        ratingsValusOfItemJ.clear();

        ratingContexts = contextEnvelope.getRatingContexts();
        LoggerUtil.debug(logger, "RecommendationContext开始处理数据...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //------------------------------------------------
        //性能测试: 
        doSwitchToProcessorContext();
        //测试结束
        //--------------------------------------------
        stopWatch.stop();
        LoggerUtil.debug(logger, "处理数据结束.  耗时: " + stopWatch.getLastTaskTimeMillis());
    }

    private void doSwitchToProcessorContext() {
        List<Rating> ratingsOfItemI = getRatingsOfSpecificItem(itemI);
        List<Rating> ratingsOfItemJ = getRatingsOfSpecificItem(itemJ);

        ProcessorContextHelper.forgeRatingValues(ratingsOfItemI, ratingsOfItemJ,
            ratingsValusOfItemI, ratingsValusOfItemJ);
    }

    /**
     * 获取给定id的Item，所有相关用户的评分向量
     * 
     * @param itemId
     * @return
     */
    public List<Rating> getRatingsOfSpecificItem(String itemId) {
        List<Rating> ratings = new LinkedList<Rating>();
        Set<String> keySet = ratingContexts.keySet();

        String key = null;
        for (int usrId = 1; usrId <= countOfUsers; usrId++) {
            key = genKey(usrId, itemId);

            if (keySet.contains(key)) {
                ratings.add(ratingContexts.get(key));
            }
        }

        return ratings;
    }

    /**
     * 获取给定id的用户，所有相关item的评分向量
     * 
     * @param usrId
     * @return
     */
    public List<Rating> getRatingsOfSpecificUser(String usrId) {
        List<Rating> ratings = new LinkedList<Rating>();
        Set<String> keySet = ratingContexts.keySet();

        String key = null;
        for (int movieId = 0; movieId <= countOfMovies; movieId++) {
            key = genKey(usrId, movieId);

            if (keySet.contains(key)) {
                ratings.add(ratingContexts.get(key));
            }
        }

        return ratings;
    }

    private String genKey(Object key, Object keyPart) {
        return (new StringBuffer()).append(key).append(("_")).append(keyPart).toString();
    }

    /**
     * Setter method for property <tt>itemI</tt>.
     * 
     * @param itemI value to be assigned to property itemI
     */
    public void setItemI(String itemI) {
        this.itemI = itemI;
    }

    /**
     * Setter method for property <tt>itemJ</tt>.
     * 
     * @param itemJ value to be assigned to property itemJ
     */
    public void setItemJ(String itemJ) {
        this.itemJ = itemJ;
    }

    /**
     * Getter method for property <tt>itemI</tt>.
     * 
     * @return property value of itemI
     */
    public String getItemI() {
        return itemI;
    }

    /**
     * Getter method for property <tt>itemJ</tt>.
     * 
     * @return property value of itemJ
     */
    public String getItemJ() {
        return itemJ;
    }

    /**
     * Getter method for property <tt>ratingsValusOfItemI</tt>.
     * 
     * @return property value of ratingsValusOfItemI
     */
    public List<Number> getRatingsValusOfItemI() {
        return ratingsValusOfItemI;
    }

    /**
     * Getter method for property <tt>ratingsValusOfItemJ</tt>.
     * 
     * @return property value of ratingsValusOfItemJ
     */
    public List<Number> getRatingsValusOfItemJ() {
        return ratingsValusOfItemJ;
    }

    /**
     * Getter method for property <tt>countOfUsers</tt>.
     * 
     * @return property value of countOfUsers
     */
    public int getCountOfUsers() {
        return countOfUsers;
    }

    /**
     * Getter method for property <tt>countOfMovies</tt>.
     * 
     * @return property value of countOfMovies
     */
    public int getCountOfMovies() {
        return countOfMovies;
    }

}
