/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.DBGenerator;

import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import edu.tongji.dao.MovieDAO;
import edu.tongji.dao.RatingDAO;
import edu.tongji.dao.UserDAO;
import edu.tongji.engine.FileBasedRecommendationEngine;
import edu.tongji.model.Movie;
import edu.tongji.model.Rating;
import edu.tongji.model.User;
import edu.tongji.orm.MovielensDatasource;
import edu.tongji.util.ExceptionUtil;

/**
 * 将movieLens文本数据，导入数据库，
 * 信息划分为【Rating】【User】【Movie】
 * 
 * @author chenkh
 * @version $Id: TestGenerator.java, v 0.1 2013-9-10 上午11:27:11 chenkh Exp $
 */
public class MovielensRatingDBGenerator {

    public void generateRating() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext("META-INF/spring/application-context.xml");

            FileBasedRecommendationEngine engine = (FileBasedRecommendationEngine) ctx
                .getBean("RecommendationEngine");
            RatingDAO ratingDao = (RatingDAO) ctx.getBean("ratingDAO");
            MovielensDatasource dataSource = (MovielensDatasource) engine.getDataSource();

            dataSource.setLazy(true);
            dataSource.reload();
            Map<String, Rating> ratingsContext = dataSource.getRatingContexts();
            for (Rating rating : ratingsContext.values()) {
                ratingDao.insert(rating);
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, MovielensRatingDBGenerator.class + "发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    public void generateUser() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext("META-INF/spring/application-context.xml");

            FileBasedRecommendationEngine engine = (FileBasedRecommendationEngine) ctx
                .getBean("RecommendationEngine");
            UserDAO userDao = (UserDAO) ctx.getBean("userDAO");
            MovielensDatasource dataSource = (MovielensDatasource) engine.getDataSource();

            dataSource.setLazy(true);
            dataSource.reload();
            Map<String, User> usersContext = dataSource.getUserContexts();
            for (User usr : usersContext.values()) {
                userDao.insert(usr);
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, MovielensRatingDBGenerator.class + "发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    public void generateMovie() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext("META-INF/spring/application-context.xml");

            FileBasedRecommendationEngine engine = (FileBasedRecommendationEngine) ctx
                .getBean("RecommendationEngine");
            MovieDAO movieDao = (MovieDAO) ctx.getBean("movieDAO");
            MovielensDatasource dataSource = (MovielensDatasource) engine.getDataSource();

            dataSource.setLazy(true);
            dataSource.reload();
            Map<String, Movie> moviesContext = dataSource.getMovieContexts();
            for (Movie movie : moviesContext.values()) {
                movieDao.insert(movie);
            }

        } catch (Exception e) {
            ExceptionUtil.caught(e, MovielensRatingDBGenerator.class + "发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
