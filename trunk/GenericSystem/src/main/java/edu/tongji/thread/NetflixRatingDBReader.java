/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.cache.DataStreamCache;
import edu.tongji.cache.DataStreamHolder;
import edu.tongji.cache.DataStreamTask;
import edu.tongji.dao.RatingDAO;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixRatingDBLoader.java, v 0.1 2013-10-8 下午12:55:08 chenkh Exp $
 */
public class NetflixRatingDBReader implements Runnable {

    /** 投票信息的DAO */
    private RatingDAO                    ratingDAO;

    /** 碰撞避让步长 */
    private final static int             BACKOFF_STEP = 1000;

    /** 结果集 */
    private final List<DataStreamHolder> resultSet    = new ArrayList<DataStreamHolder>();

    /** logger */
    private final static Logger          logger       = Logger
                                                          .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        DataStreamTask task = DataStreamCache.task();

        do {
            LoggerUtil.info(logger, "NetflixRatingDBReader 获得任务: " + task);
            resultSet.clear();
            int i = task.i;
            int jStart = task.jStart;
            int jEnd = task.jEnd;
            int countTotal = 0;
            int countMissting = 0;
            for (int j = jStart; j < jEnd; j++) {
                countTotal = ratingDAO.countTotalItems(i, j);
                countMissting = ratingDAO.countMissingItems(i, j);

                resultSet.add(new DataStreamHolder(i, j, countTotal, countMissting));
            }

            //按CSMACD协议，采用配置避免算法
            boolean isLoad = false;
            int numOfCollision = 0;
            while (!isLoad) {
                isLoad = DataStreamCache.put(resultSet);
                try {
                    Thread.sleep(backoff(numOfCollision++));
                } catch (InterruptedException e) {
                    ExceptionUtil.caught(e, "NetflixRatingDBReader Sleep出错, DataStreamTask： i: "
                                            + i + " jStart: " + jStart + " jEnd:" + jEnd);
                    break;
                }
            }

            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        } while ((task = DataStreamCache.task()) != null);

    }

    /**
     * 碰撞躲避时间算法：
     *  参考CSMA协议。
     * 
     * @param numOfCollision
     * @return
     */
    private long backoff(int numOfCollision) {
        if (numOfCollision == 0) {
            return 0;
        } else if (numOfCollision == 2) {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        } else if (numOfCollision >= 10) {
            return 1024 * 1000;
        } else {
            LoggerUtil.info(logger, "NetflixRatingDBReader: " + Thread.currentThread() + "  发生碰撞: "
                                    + numOfCollision);
        }

        return Double.valueOf(Math.pow(2, numOfCollision)).intValue() * BACKOFF_STEP;
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
