/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.cache.CacheHolder;
import edu.tongji.cache.GeneralCache;
import edu.tongji.configure.TestCaseConfigurationConstant;
import edu.tongji.dao.RatingDAO;
import edu.tongji.dao.ValueOfItemsDAO;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.model.ValueOfItems;
import edu.tongji.util.BeanUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.RandomUtil;

/**
 * 
 * @author chench
 * @version $Id: NetFlixSimularityDBReader.java, v 0.1 31 Oct 2013 19:42:33 chench Exp $
 */
public class NetFlixSimularityDBReader extends Thread {

    /** item间相似度相关的DAO */
    private ValueOfItemsDAO     valueOfItemsDAO;

    /** 投票信息的DAO */
    private RatingDAO           ratingDAO;

    /** logger */
    private final static Logger logger                 = Logger
                                                           .getLogger(LoggerDefineConstant.SERVICE_CACHE);
    /** 通过用户号查询评分集合 */
    private final static String EXCUTE_SELECT_BY_USERS = "select_by_usrs";

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        //加载相似度信息至缓存
        loadSimularityToCache();

        //加载测试集信息至缓存
        loadTestCaseToCache();
    }

    /**
     * 加载相似度信息至缓存
     */
    private void loadSimularityToCache() {
        //DB读取所需要的数据，加载至缓存
        List<ValueOfItems> resultSet = valueOfItemsDAO
            .selectByFunctionName(TestCaseConfigurationConstant.SIMILARITY_TYPE);

        //加载相似度入缓存
        List<CacheHolder> cacheHolders = new ArrayList<CacheHolder>();
        for (ValueOfItems valueOfItem : resultSet) {
            CacheHolder cacheHolder = BeanUtil.toBean(valueOfItem);
            cacheHolders.add(cacheHolder);
        }
        GeneralCache.put(cacheHolders);
        LoggerUtil.info(logger, "NetFlixSimularityDBReader 完成读取相似度，数据量：" + resultSet.size());
    }

    /**
     * 生成测试数据集，加载至缓存。
     * 加载顺序：
     *  1.随机生成测试机
     *  2.自定义文件加载
     *  3.自定义变量加载
     */
    private void loadTestCaseToCache() {
        //随机生成配置数量的测试集
        List<String> testSet = new ArrayList<String>();
        if (TestCaseConfigurationConstant.NEED_RANDOM_TESTCASE) {
            for (int i = 0; i < TestCaseConfigurationConstant.TESTCASE_SIZE; i++) {
                testSet.add(String.valueOf(RandomUtil.nextInt(
                    TestCaseConfigurationConstant.LEFT_SIDE,
                    TestCaseConfigurationConstant.RIGHT_SIDE)));
            }
        } else if (TestCaseConfigurationConstant.TEST_CASE_FILE != null) {
            String[] testCaseSet = FileUtil.readLines(TestCaseConfigurationConstant.TEST_CASE_FILE);
            for (String testCase : testCaseSet) {
                testSet.add(testCase.trim());
            }
        } else {
            String[] testCaseSet = TestCaseConfigurationConstant.TEST_CASE
                .split(TestCaseConfigurationConstant.SAPERATOR_EXPRESSION);
            for (String testCase : testCaseSet) {
                testSet.add(testCase.trim());
            }
        }

        //读取数据库相关评分数据
        List<Rating> resultSet = ratingDAO.select(EXCUTE_SELECT_BY_USERS, testSet);
        LoggerUtil.info(logger, "NetFlixSimularityDBReader 完成读取测试集，数据量: " + resultSet.size());

        //加载测试集至缓存
        List<CacheHolder> cacheHolders = new ArrayList<CacheHolder>();
        for (Rating rating : resultSet) {
            //填充{KEY, RATING, DISGUISED_VALUE}
            CacheHolder cacheHolder = BeanUtil.toBean(rating,
                TestCaseConfigurationConstant.IS_PERTURBATION);
            cacheHolders.add(cacheHolder);
        }
        GeneralCache.puts(cacheHolders);
        LoggerUtil.info(logger, "NetFlixSimularityDBReader 加载测试集缓存结束");

    }

    /**
     * Getter method for property <tt>valueOfItemsDAO</tt>.
     * 
     * @return property value of valueOfItemsDAO
     */
    public ValueOfItemsDAO getValueOfItemsDAO() {
        return valueOfItemsDAO;
    }

    /**
     * Setter method for property <tt>valueOfItemsDAO</tt>.
     * 
     * @param valueOfItemsDAO value to be assigned to property valueOfItemsDAO
     */
    public void setValueOfItemsDAO(ValueOfItemsDAO valueOfItemsDAO) {
        this.valueOfItemsDAO = valueOfItemsDAO;
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
