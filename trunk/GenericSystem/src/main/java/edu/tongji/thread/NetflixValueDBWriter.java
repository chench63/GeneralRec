/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import org.apache.log4j.Logger;

import edu.tongji.cache.DataStreamCache;
import edu.tongji.cache.DataStreamHolder;
import edu.tongji.dao.ValueOfItemsDAO;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.BeanUtil;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixValueDBWriter.java, v 0.1 2013-10-8 下午12:55:35 chenkh Exp $
 */
public class NetflixValueDBWriter implements Runnable {

    /** item间相似度相关的DAO */
    private ValueOfItemsDAO     valueOfItemsDAO;

    /** 碰撞避让步长 */
    private final static int    BACKOFF_STEP = 1000;

    /** logger */
    private final static Logger logger       = Logger
                                                 .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        boolean isStop = false;
        while (true) {
            //参与分配任务
            DataStreamHolder task = null;
            int numOfCollision = 1;
            while ((task = DataStreamCache.get()) == null) {
                if (numOfCollision >= 11) {
                    isStop = true;
                }

                try {
                    Thread.sleep(backoff(numOfCollision++));
                } catch (Exception e) {
                    ExceptionUtil.caught(e,
                        "NetflixValueDBWriter Sleep出错   DataStreamHolder itemI: " + task);
                    break;
                }
            }

            //超过等待2048+1024s，则认为任务结束，中止任务
            if (isStop) {
                return;
            }
            LoggerUtil.debug(logger, Thread.currentThread() + "  获得任务:" + task);
            valueOfItemsDAO.insert(BeanUtil.toBean(task));
        }
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
        } else if (numOfCollision >= 3) {
            return 1024 * 1000;
        } else {
            LoggerUtil.info(logger, "NetflixValueDBWriter: " + Thread.currentThread() + "  发生碰撞: "
                                    + numOfCollision);
        }

        return Double.valueOf(Math.pow(5, numOfCollision +2)).intValue() * BACKOFF_STEP;
    }

    /**
     * Setter method for property <tt>valueOfItemsDAO</tt>.
     * 
     * @param valueOfItemsDAO value to be assigned to property valueOfItemsDAO
     */
    public void setValueOfItemsDAO(ValueOfItemsDAO valueOfItemsDAO) {
        this.valueOfItemsDAO = valueOfItemsDAO;
    }
}
