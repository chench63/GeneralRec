/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.orm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import edu.tongji.dao.RatingDAO;
import edu.tongji.exception.DataSourceErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.stopper.Stopper;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingHistoryVO;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixDataSource.java, v 0.1 2013-9-16 下午1:50:34 chenkh Exp $
 */
public class NetflixDataSource implements DataSource {

    /** 是否懒加载*/
    private boolean                   isLazy;

    /** 需要加载的文件  **/
    private Map<TemplateType, String> sourceEntity;

    /** 本地数据源缓存数据结构<String, Rating> */
    private final Map<String, Rating> ratingContexts                  = new HashMap<String, Rating>();

    /** movieId 分隔符 */
    private static final String       MOVIEID_SEPERATOR               = "\\:";

    /** 文件后缀 */
    private static final String       FILE_SUFFIX                     = ".txt";

    /** 文件总数 */
    private int                       countOfMovieFiles;

    /**  加载文件的初始索引 */
    private int                       indexOfMoviesFront;

    /** 文件格式的填充字符 */
    private final static char         PAD_CHAR                        = '0';

    /** DAO */
    private RatingDAO                 ratingDAO;

    /** Stopper*/
    private Stopper                   stopper;

    /** 时间记录点*/
    private Timestamp                 epicZone;

    /** 优化sql查询 */
    private String                    itemI;

    /** 优化sql查询 */
    private String                    itemJ;

    /** logger */
    private static final Logger       logger                          = Logger
                                                                          .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 查询补足评分记录 */
    private final static String       EXCUTE_SELECT_COMPLEMENT_RATING = "excute_select_complement_rating";

    /** 查询将会丢失评分的集合*/
    private final static String       EXCUTE_SELECT_MISSING_RATING    = "excute_select_missing_rating";

    /** 
     * @see edu.tongji.orm.DataSource#isLazy()
     */
    @Override
    public boolean isLazy() {
        return isLazy;
    }

    /** 
     * @see edu.tongji.orm.DataSource#reload()
     */
    @Override
    public void reload() {
        if (isLazy) {
            clearLastEnv();
            load();
            isLazy = false;
        }
    }

    /**
     * 清理环境，以免reload以后仍存在之前环境的信息
     */
    private void clearLastEnv() {
        ratingContexts.clear();
    }

    /**
     * 加载数据文件
     */
    private void load() {
        StopWatch stopWatch = new StopWatch();

        Entry<TemplateType, String> entry = sourceEntity.entrySet().iterator().next();
        StringBuilder fileName = null;

        for (int fileSuffix = indexOfMoviesFront; fileSuffix <= countOfMovieFiles; fileSuffix++) {
            TemplateType parserType = entry.getKey();
            fileName = new StringBuilder(entry.getValue());
            fileName.append(StringUtil.alignRight(String.valueOf(fileSuffix), 7, PAD_CHAR)).append(
                FILE_SUFFIX);
            File file = new File(fileName.toString());

            //性能计时器
            //-------------------------------------------------------
            LoggerUtil.info(logger, "开始加载数据文件: " + fileName.toString());
            stopWatch.start();
            //读取并解析数据
            if (!file.isFile() | !file.exists()) {
                LoggerUtil.warn(logger, "无法找到对应的加载文件: " + fileName.toString());
                stopWatch.stop();
                continue;
            }
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String context = null;
                String movieId = parserMovieId(reader.readLine());

                while ((context = reader.readLine()) != null) {
                    ParserTemplate template = new ParserTemplate();
                    template.setTemplate(context);
                    template.put("movieId", movieId);

                    doParser(parserType, template);
                }

            } catch (FileNotFoundException e) {
                ExceptionUtil.caught(e, "无法找到对应的加载文件: " + fileName.toString());
            } catch (IOException e) {
                ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
            } catch (OwnedException e) {
                ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
            } finally {
                IOUtils.closeQuietly(reader);
            }
            //----------------------------------------------

            stopWatch.stop();
            LoggerUtil.info(logger,
                "结束加载数据文件: " + fileName.toString() + ",加载时间: " + stopWatch.getLastTaskTimeMillis());
        }
    }

    /**
     * 解析movieId
     * 
     * @param firstLine
     * @return
     */
    private String parserMovieId(String firstLine) {
        if (StringUtil.isEmpty(firstLine)) {
            throw new OwnedException(DataSourceErrorCode.FILE_FORMAT_INCORRECT);
        }

        String[] elements = firstLine.split(MOVIEID_SEPERATOR);
        return elements[0];
    }

    /**
     * 解析数据
     * 
     * @param parserType
     * @param template
     */
    private void doParser(TemplateType parserType, ParserTemplate template) {
        switch (parserType) {
            case NETFLIX_RATING_TEMPLATE:
                Rating rating = (Rating) parserType.parser(template);
                ratingContexts.put(HashKeyUtil.genKey(rating), rating);
                break;
            default:
                break;
        }
    }

    /** 
     * @see edu.tongji.orm.DataSource#excute(java.lang.String)
     */
    @Override
    public List<? extends Serializable> excute(String expression) {
        List<? extends Serializable> resultSet = null;

        if (StringUtil.equalsIgnoreCase(expression, "MISS_RATING")) {
            resultSet = doMissingRatingExcute();
        } else {
            resultSet = doDefaultExcute();
        }

        return resultSet;
    }

    /** 
     * @see edu.tongji.orm.DataSource#excuteEx(java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map excuteEx(String expression) {
        Map resultSet = null;

        if (StringUtil.isEmpty(expression)) {
            resultSet = doLoadingMissingRatingComplementExcute();
        }

        return resultSet;
    }

    /**
     * 按时钟定义，从数据库中捞取数据
     * 
     * @return
     */
    private List<? extends Serializable> doDefaultExcute() {
        //初始值
        if (epicZone == null) {
            epicZone = Timestamp.valueOf("1970-01-01 00:00:00");
        }

        //获取结束值，为null，说明停止
        Timestamp endZone = (Timestamp) stopper.genSeed();
        if (endZone == null) {
            return null;
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //=============================================
        //功能测试部分
        //=============================================
        Map<String, String> map = new HashMap<String, String>();
        map.put("itemI", itemI);
        map.put("itemJ", itemJ);
        map.put("first", epicZone.toString());
        map.put("second", endZone.toString());

        //构建下次循环信息，查询数据库
        epicZone = endZone;
        List<? extends Serializable> resultSet = ratingDAO.select(map);
        stopWatch.stop();
        LoggerUtil.info(logger,
            "从数据库载入数据结束，共捞取: " + resultSet.size() + " 耗时: " + stopWatch.getLastTaskTimeMillis());
        //==============================================
        //功能测试结束
        //=============================================

        return resultSet;
    }

    /**
     * 从数据库读取，丢失的用户评分记录
     * 
     * @return
     */
    private List<? extends Serializable> doMissingRatingExcute() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //=============================================
        //功能测试部分
        //=============================================
        //载入将会丢失的用户评分记录
        List<? extends Serializable> resultSet = null;
        List<String> param = new ArrayList<String>();
        param.add(itemI);
        param.add(itemJ);
        resultSet = ratingDAO.select(EXCUTE_SELECT_MISSING_RATING, param);
        //==============================================
        //功能测试结束
        //=============================================
        stopWatch.stop();
        LoggerUtil.info(logger,
            "从数据库捞取[丢失用户]， 用户数量:" + resultSet.size() + " 耗时: " + stopWatch.getLastTaskTimeMillis());

        return resultSet;
    }

    /**
     * 从数据库读取，丢失的用户所有虚拟在线记录
     * 
     * @return
     */
    private Map<Timestamp, RatingHistoryVO> doLoadingMissingRatingComplementExcute() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //=============================================
        //功能测试部分
        //=============================================
        //载入将会丢失的用户评分记录
        List<? extends Serializable> resultSet = null;
        List<String> param = new ArrayList<String>();
        param.add(itemI);
        param.add(itemJ);
        resultSet = ratingDAO.select(EXCUTE_SELECT_MISSING_RATING, param);
        LoggerUtil.info(logger, "从数据库捞取[丢失用户]， 用户数量: " + resultSet.size());

        //载入[丢失用户]的所有虚拟在线记录，以备在后续时间中补充修正
        param.clear();
        Rating rating = null;
        for (Serializable customer : resultSet) {
            rating = (Rating) customer;
            param.add(rating.getUsrId());
        }
        resultSet = ratingDAO.select(EXCUTE_SELECT_COMPLEMENT_RATING, param);
        LoggerUtil.info(logger, "从数据库捞取[丢失用户]的访问记录， 记录数量: " + resultSet.size());

        //组装[丢失用户]所有虚拟在线记录
        Map<Timestamp, RatingHistoryVO> ratingComplementsContexts = new HashMap<Timestamp, RatingHistoryVO>();
        rating = null;
        RatingHistoryVO ratingHistory = null;
        for (Serializable customer : resultSet) {
            rating = (Rating) customer;

            ratingHistory = ratingComplementsContexts.get(rating.getTime());
            if (ratingHistory == null) {
                ratingHistory = new RatingHistoryVO();
            }
            ratingHistory.put(rating.getUsrId(), rating);
            ratingComplementsContexts.put(rating.getTime(), ratingHistory);
        }
        stopWatch.stop();
        LoggerUtil
            .info(
                logger,
                "从数据库载入补偿用户访问记录，共捞取: " + resultSet.size() + " 耗时: "
                        + stopWatch.getLastTaskTimeMillis());
        //==============================================
        //功能测试结束
        //=============================================

        return ratingComplementsContexts;
    }

    /**
     * Getter method for property <tt>sourceEntity</tt>.
     * 
     * @return property value of sourceEntity
     */
    public Map<TemplateType, String> getSourceEntity() {
        return sourceEntity;
    }

    /**
     * Setter method for property <tt>sourceEntity</tt>.
     * 
     * @param sourceEntity value to be assigned to property sourceEntity
     */
    public void setSourceEntity(Map<TemplateType, String> sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    /**
     * Setter method for property <tt>isLazy</tt>.
     * 
     * @param isLazy value to be assigned to property isLazy
     */
    public void setLazy(boolean isLazy) {
        this.isLazy = isLazy;
    }

    /**
     * Getter method for property <tt>countOfMovieFiles</tt>.
     * 
     * @return property value of countOfMovieFiles
     */
    public int getCountOfMovieFiles() {
        return countOfMovieFiles;
    }

    /**
     * Setter method for property <tt>countOfMovieFiles</tt>.
     * 
     * @param countOfMovieFiles value to be assigned to property countOfMovieFiles
     */
    public void setCountOfMovieFiles(int countOfMovieFiles) {
        this.countOfMovieFiles = countOfMovieFiles;
    }

    /**
     * Getter method for property <tt>indexOfMoviesFront</tt>.
     * 
     * @return property value of indexOfMoviesFront
     */
    public int getIndexOfMoviesFront() {
        return indexOfMoviesFront;
    }

    /**
     * Setter method for property <tt>indexOfMoviesFront</tt>.
     * 
     * @param indexOfMoviesFront value to be assigned to property indexOfMoviesFront
     */
    public void setIndexOfMoviesFront(int indexOfMoviesFront) {
        this.indexOfMoviesFront = indexOfMoviesFront;
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
     * Getter method for property <tt>stopper</tt>.
     * 
     * @return property value of stopper
     */
    public Stopper getStopper() {
        return stopper;
    }

    /**
     * Setter method for property <tt>stopper</tt>.
     * 
     * @param stopper value to be assigned to property stopper
     */
    public void setStopper(Stopper stopper) {
        this.stopper = stopper;
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
     * Setter method for property <tt>itemI</tt>.
     * 
     * @param itemI value to be assigned to property itemI
     */
    public void setItemI(String itemI) {
        this.itemI = itemI;
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
     * Setter method for property <tt>itemJ</tt>.
     * 
     * @param itemJ value to be assigned to property itemJ
     */
    public void setItemJ(String itemJ) {
        this.itemJ = itemJ;
    }

    /**
     * Getter method for property <tt>epicZone</tt>.
     * 
     * @return property value of epicZone
     */
    public Timestamp getEpicZone() {
        return epicZone;
    }

    /**
     * Setter method for property <tt>epicZone</tt>.
     * 
     * @param epicZone value to be assigned to property epicZone
     */
    public void setEpicZone(Timestamp epicZone) {
        this.epicZone = epicZone;
    }
    
}
