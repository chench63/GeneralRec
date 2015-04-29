/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import edu.tongji.engine.Engine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: RecommendationEngine.java, v 0.1 16 Sep 2013 21:51:35 chench Exp $
 */
public abstract class RcmdtnEngine implements Engine {

    /** thread to store the dataset in memory*/
    protected Thread              loader;

    /** 数据记录*/
    protected List<Runnable>      recorder;

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @Override
    public void excute() {
        //1. loading dataset
        loadDataSet();

        //2. preprocess data
        assembleDataSet();

        //3. buiding model
        excuteInner();

        //4. evaluate the prediction
        evaluate();
    }

    /**
     * loading dataset
     */
    protected void loadDataSet() {
        LoggerUtil.info(logger, "1. loading data set.");

        try {
            loader.start();
            loader.join();
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "NetflixSimularityPerformanceRecorder 线程等待join异常");
        }
    }

    /**
     * preprocess data
     */
    protected void assembleDataSet() {
        LoggerUtil.info(logger, "2. assembling data set.");
    }

    /**
     * buiding model
     */
    protected void excuteInner() {
        LoggerUtil.info(logger, "3. initializing working threads.");

        ExecutorService exec = Executors.newCachedThreadPool();
        for (Runnable runnable : recorder) {
            exec.execute(runnable);
        }
        exec.shutdown();
    }

    /**
     * evaluate the prediction
     */
    protected void evaluate() {
        LoggerUtil.info(logger, "4. evaluate the prediction.");
    }

    /**
     * Setter method for property <tt>loader</tt>.
     * 
     * @param loader value to be assigned to property loader
     */
    public void setLoader(Thread loader) {
        this.loader = loader;
    }

    /**
     * Setter method for property <tt>recorder</tt>.
     * 
     * @param recorder value to be assigned to property recorder
     */
    public void setRecorder(List<Runnable> recorder) {
        this.recorder = recorder;
    }

}