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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import edu.tongji.engine.Initializable;
import edu.tongji.exception.DataSourceErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Movie;
import edu.tongji.model.Rating;
import edu.tongji.model.User;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.HashKeyUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 针对文本格式的数据的数据源
 * @author chench
 * @version $Id: DatDatasource.java, v 0.1 2013-9-6 下午4:01:29 chench Exp $
 */
public class MovielensDatasource implements DataSource, Initializable {

    /** 是否懒加载*/
    private boolean                   isLazy;

    /** 需要加载的文件  **/
    private Map<TemplateType, String> sourceEntity;

    /** 本地数据源缓存数据结构 <String, User> */
    private final Map<String, User>   userContexts   = new HashMap<String, User>();

    /** 本地数据源缓存数据结构<String, Movie> */
    private final Map<String, Movie>  movieContexts  = new HashMap<String, Movie>();

    /** 本地数据源缓存数据结构<String, Rating> */
    private final Map<String, Rating> ratingContexts = new HashMap<String, Rating>();

    private static final Logger       logger         = Logger
                                                         .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see edu.tongji.orm.DataSource#isLazy()
     */
    @Override
    public boolean isLazy() {
        return isLazy;
    }

    /**
     * 初始化加载数据
     */
    private void load() {
        StopWatch stopWatch = new StopWatch();

        for (Iterator<Entry<TemplateType, String>> iter = sourceEntity.entrySet().iterator(); iter
            .hasNext();) {
            Entry<TemplateType, String> entry = iter.next();
            TemplateType parserType = entry.getKey();
            File file = new File(entry.getValue());

            //性能计时器
            //-------------------------------------------------------
            LoggerUtil.info(logger, "开始加载数据文件: " + entry.getValue());
            stopWatch.start();
            //读取并解析数据
            if (!file.isFile() | !file.exists()) {
                LoggerUtil.warn(logger, "无法找到对应的加载文件: " + entry.getValue());
                stopWatch.stop();
                continue;
            }
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));

                String context = null;
                while ((context = reader.readLine()) != null) {
                    ParserTemplate template = new ParserTemplate();
                    template.setTemplate(context);

                    if (doFilter(parserType, template)) {
                        continue;
                    }
                    doParser(parserType, template);
                }
            } catch (FileNotFoundException e) {
                ExceptionUtil.caught(e, "无法找到对应的加载文件: " + entry.getValue());
            } catch (IOException e) {
                ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
            } finally {
                IOUtils.closeQuietly(reader);
            }
            //----------------------------------------------

            stopWatch.stop();
            LoggerUtil.info(logger,
                "结束加载数据文件: " + entry.getValue() + ",加载时间: " + stopWatch.getLastTaskTimeMillis());
        }

    }

    /**
     * 过滤数据集，防止内存溢出
     * 
     * @param parserType
     * @param template
     * @return
     */
    private boolean doFilter(TemplateType parserType, ParserTemplate template) {
        //此处余留，可能补充一些统计工作
        //...
        return parserType.isFiler(template);
    }

    /**
     * 解析数据
     * 
     * @param parserType
     * @param template
     */
    private void doParser(TemplateType parserType, ParserTemplate template) {
        switch (parserType) {
            case USER_TEMPLATE:
                User usr = (User) parserType.parser(template);
                userContexts.put(usr.getId(), usr);
                break;
            case MOVIE_TEMPLATE:
                Movie movie = (Movie) parserType.parser(template);
                movieContexts.put(movie.getId(), movie);
                break;
            case MOVIELENS_RATING_TEMPLATE:
                Rating rating = (Rating) parserType.parser(template);
                ratingContexts.put(HashKeyUtil.genKey(rating), rating);
                break;
            default:
                break;
        }
    }

    /** 
     * @see edu.tongji.orm.DataSource#reload()
     */
    @Override
    public void reload() {
        if (isLazy) {
            load();
            isLazy = false;
        }
    }

    /** 
     * @see edu.tongji.orm.DataSource#excute(java.lang.String)
     */
    @Override
    public List<? extends Serializable> excute(String expression) {

        try {
            SerializableBeanType beanType = DataSourceExpressionReader.readBeanType(expression);
            return doExcute(expression, beanType);
        } catch (Throwable e) {
            ExceptionUtil.caught(e, "解析结构表示式错误, Expression: " + expression);
        }

        return null;
    }

    /** 
     * @see edu.tongji.orm.DataSource#excuteEx(java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map excuteEx(String expression) {
        throw new OwnedException(DataSourceErrorCode.NOT_SUPPORT_EXCUTEEX);
    }

    /**
     * 根据expression中 Condition，在Map中，搜索符合Condition的结果集
     * 
     * @param expression
     * @param beanType
     * @return
     * @throws Throwable 
     */
    private List<? extends Serializable> doExcute(String expression, SerializableBeanType beanType)
                                                                                                   throws Throwable {
        List<? extends Serializable> resultSet = null;

        switch (beanType) {
            case RATING_BEAN:
                resultSet = DataSourceExpressionReader.read(expression, ratingContexts, beanType);
            default:
                break;
        }

        return resultSet;
    }

    /**
     * Getter method for property <tt>sourceEntity</tt>.
     * 
     * @return property value of sourceEntity
     */
    public Map<TemplateType, String> getSourceEntity() {
        return this.sourceEntity;
    }

    /**
     * Setter method for property <tt>sourceEntity</tt>.
     * 
     * @param sourceEntity value to be assigned to property sourceEntity
     */
    public void setSourceEntity(Map<TemplateType, String> sourceEntity) {
        this.sourceEntity = new EnumMap<TemplateType, String>(sourceEntity);
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
     * @see edu.tongji.engine.Initializable#initialize()
     */
    @Override
    public void initialize() {
        if (!isLazy) {
            load();
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

}