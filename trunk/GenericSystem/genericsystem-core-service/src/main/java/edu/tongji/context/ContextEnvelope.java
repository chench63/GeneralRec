/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Movie;
import edu.tongji.model.Rating;
import edu.tongji.model.User;
import edu.tongji.orm.DataSource;
import edu.tongji.orm.SerializableBeanType;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingHistoryVO;

/**
 * 上下文信息抽象类,非线程安全，多线程情况下谨慎使用。
 * <p>
 *   外部采集的信息
 * </p>
 * @author Hanke Chen
 * @version $Id: ProcessorContextHolder.java, v 0.1 2013-9-7 下午7:28:26 chench Exp $
 */
public class ContextEnvelope {

    /** 本地数据源缓存数据结构 <String, User> */
    private final Map<String, User>           userContexts   = new HashMap<String, User>();

    /** 本地数据源缓存数据结构<String, Movie> */
    private final Map<String, Movie>          movieContexts  = new HashMap<String, Movie>();

    /** 本地数据源缓存数据结构<String, Rating> */
    private final Map<String, Rating>         ratingContexts = new HashMap<String, Rating>();

    /** [丢失用户]在线时间记录结果集<Timestamp, RatingHistoryVO> */
    private Map<Timestamp, RatingHistoryVO>   ratingComplementsSet;

    /** [丢失用户]相关的评分记录的结果集 */
    private List<Serializable>                complementsSet;

    /** DataSource执行excute后的结果集 */
    private List<Serializable>                resultSet;

    /** 数据采集规_结构化表达式  **/
    private Map<SerializableBeanType, String> samplingExpr;

    /** 优化多线程情况下，内存使用量 */
    private boolean                           isChanged      = true;

    /** logger */
    private static final Logger               logger         = Logger
                                                                 .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 按照配置规则，从数据源采集数据。
     * 
     * @param dataSource
     */
    public void sampling(DataSource dataSource) {
        LoggerUtil.debug(logger, "开始从数据源中采集数据..");
        if (!isChanged) {
            return;
        }

        for (SerializableBeanType key : samplingExpr.keySet()) {
            LoggerUtil.info(logger, "执行结构化表达式: " + samplingExpr.get(key));
            switch (key) {
                case RATING_BEAN:
                    doSamplingForRatingObject(dataSource.excute(samplingExpr.get(key)));
                    break;
                default:
                    break;
            }
        }
        LoggerUtil.debug(logger, "从数据源中采集数据结束");
    }

    /**
     * 针对Rating对象数据转换，将List转化为Map结构
     * 
     * @param resultOfDataSource
     */
    private void doSamplingForRatingObject(List<? extends Serializable> resultOfDataSource) {
        if (!(resultOfDataSource.get(0) instanceof Rating)) {
            throw new IllegalArgumentException("Arg must be instance of List<Rating>");
        }

        for (Serializable rating : resultOfDataSource) {
            ratingContexts.put(HashKeyUtil.genKey((Rating) rating), (Rating) rating);
        }
    }

    /**
     * Getter method for property <tt>userContexts</tt>.
     * 
     * @return property value of userContexts
     */
    public Map<String, User> getUserContexts() {
        return userContexts;
    }

    /**
     * Getter method for property <tt>movieContexts</tt>.
     * 
     * @return property value of movieContexts
     */
    public Map<String, Movie> getMovieContexts() {
        return movieContexts;
    }

    /**
     * Getter method for property <tt>ratingContexts</tt>.
     * 
     * @return property value of ratingContexts
     */
    public Map<String, Rating> getRatingContexts() {
        return ratingContexts;
    }

    /**
     * Getter method for property <tt>samplingExpr</tt>.
     * 
     * @return property value of samplingExpr
     */
    public Map<SerializableBeanType, String> getSamplingExpr() {
        return samplingExpr;
    }

    /**
     * Setter method for property <tt>samplingExpr</tt>.
     * 
     * @param samplingExpr value to be assigned to property samplingExpr
     */
    public void setSamplingExpr(Map<SerializableBeanType, String> samplingExpr) {
        this.samplingExpr = new EnumMap<SerializableBeanType, String>(samplingExpr);
    }

    /**
     * Getter method for property <tt>isChanged</tt>.
     * 
     * @return property value of isChanged
     */
    public boolean isChanged() {
        return isChanged;
    }

    /**
     * Setter method for property <tt>isChanged</tt>.
     * 
     * @param isChanged value to be assigned to property isChanged
     */
    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    /**
     * Getter method for property <tt>resultSet</tt>.
     * 
     * @return property value of resultSet
     */
    public List<Serializable> getResultSet() {
        return resultSet;
    }

    /**
     * Setter method for property <tt>resultSet</tt>.
     * 
     * @param resultSet value to be assigned to property resultSet
     */
    public void setResultSet(List<Serializable> resultSet) {
        this.resultSet = resultSet;
    }

    /**
     * Getter method for property <tt>ratingComplementsSet</tt>.
     * 
     * @return property value of ratingComplementsSet
     */
    public Map<Timestamp, RatingHistoryVO> getRatingComplementsSet() {
        return ratingComplementsSet;
    }

    /**
     * Setter method for property <tt>ratingComplementsSet</tt>.
     * 
     * @param ratingComplementsSet value to be assigned to property ratingComplementsSet
     */
    public void setRatingComplementsSet(Map<Timestamp, RatingHistoryVO> ratingComplementsSet) {
        this.ratingComplementsSet = ratingComplementsSet;
    }

    /**
     * Getter method for property <tt>complementsSet</tt>.
     * 
     * @return property value of complementsSet
     */
    public List<Serializable> getComplementsSet() {
        return complementsSet;
    }

    /**
     * Setter method for property <tt>complementsSet</tt>.
     * 
     * @param complementsSet value to be assigned to property complementsSet
     */
    public void setComplementsSet(List<Serializable> complementsSet) {
        this.complementsSet = complementsSet;
    }

}
