/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.tongji.dao.RatingDAO;
import edu.tongji.dao.ValueOfItemsDAO;
import edu.tongji.thread.NetflixRatingDBReader;
import edu.tongji.thread.NetflixValueDBWriter;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author chenkh
 * @version $Id: MissingRateEngine.java, v 0.1 2013-10-8 下午3:30:45 chenkh Exp $
 */
public class MissingRateEngine extends RecommendationMultiThreadEngine {

    /** Reader数量 */
    private int numOfReader;

    /** Writer数量 */
    private int numOfWriter;

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @Override
    public void excute() {

        ExecutorService exec = Executors.newFixedThreadPool(numOfReader + numOfWriter);
        try {
            for (int i = 0; i < numOfWriter; i++) {
                exec.execute(initReader());
                exec.execute(initWriter());
            }

            for (int i = 0; i < numOfReader - numOfWriter; i++) {
                exec.execute(initReader());
            }

            exec.shutdown();
        } catch (Exception e) {
            ExceptionUtil.caught(e, "主流程发生错误");
        }

    }

    /**
     * SpringContext文中，获取多例的RatingDAO。
     * 
     * @return
     */
    private Runnable initReader() {
        NetflixRatingDBReader reader = new NetflixRatingDBReader();
        reader.setRatingDAO((RatingDAO) applicationContext.getBean("ratingDAO"));
        return reader;
    }

    /**
     * SpringContext文中，获取多例的ValueOfItemsDAO。
     * 
     * @return
     */
    private Runnable initWriter() {
        NetflixValueDBWriter writer = new NetflixValueDBWriter();
        writer.setValueOfItemsDAO((ValueOfItemsDAO) applicationContext
            .getBean("valueOfItemsDAOImpl"));
        return writer;
    }

    /**
     * Getter method for property <tt>numOfReader</tt>.
     * 
     * @return property value of numOfReader
     */
    public int getNumOfReader() {
        return numOfReader;
    }

    /**
     * Setter method for property <tt>numOfReader</tt>.
     * 
     * @param numOfReader value to be assigned to property numOfReader
     */
    public void setNumOfReader(int numOfReader) {
        this.numOfReader = numOfReader;
    }

    /**
     * Getter method for property <tt>numOfWriter</tt>.
     * 
     * @return property value of numOfWriter
     */
    public int getNumOfWriter() {
        return numOfWriter;
    }

    /**
     * Setter method for property <tt>numOfWriter</tt>.
     * 
     * @param numOfWriter value to be assigned to property numOfWriter
     */
    public void setNumOfWriter(int numOfWriter) {
        this.numOfWriter = numOfWriter;
    }

}
