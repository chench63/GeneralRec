/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.orm.DataSource;
import edu.tongji.orm.MovielensDatasource;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: TestEngine.java, v 0.1 2013-9-7 下午9:41:24 chenkh Exp $
 */
public class TestEngine {

    /** 数据源*/
    private final DataSource    dataSource = new MovielensDatasource();

    private static final Logger logger     = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    private void mock() {
        // 设置实时加载，在Engine业务目标中跳过加载过程
        ((MovielensDatasource) dataSource).setLazy(false);
        Map<String, Rating> ratingContexts = ((MovielensDatasource) dataSource).getRatingContexts();

        Rating rating = null;
        Random randow = new Random(System.currentTimeMillis());
        for (int i = 0; i < 10; i++) {
            rating = new Rating();
            rating.setMovieId(101);
            rating.setUsrId(String.valueOf(i));
            rating.setRating(randow.nextInt(5));
            ratingContexts.put(genKey(rating), rating);

            rating = new Rating();
            rating.setMovieId(102);
            rating.setUsrId(String.valueOf(i));
            rating.setRating(randow.nextInt(5));
            ratingContexts.put(genKey(rating), rating);
        }

    }

    //    @Test
    public void test() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "META-INF/spring/application-context-movielens.xml");

            Engine engine = (Engine) ctx.getBean("RecommendationEngine");
            mock();
            ((FileBasedRecommendationEngine) engine).setDataSource(dataSource);
            engine.excute();

        } catch (Exception e) {
            ExceptionUtil.caught(e, "edu.tongji.engine.TestEngine 测试用例发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }

    }

    @Test
    public void testCaseII() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext("META-INF/spring/application-context.xml");
            LoggerUtil.info(logger, "Starts");
            Engine engine = (Engine) ctx.getBean("RecommendationEngine");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, "edu.tongji.engine.TestEngine 测试用例发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }

    }

    /**
     * 生成Hash值
     * 
     * @param rating
     * @return
     */
    private String genKey(Rating rating) {
        return rating.getUsrId() + "_" + rating.getMovieId();
    }
}
