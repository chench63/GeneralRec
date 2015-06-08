/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: RecommendationEngine.java, v 0.1 16 Sep 2013 21:51:35 chench Exp $
 */
public abstract class RcmdtnEngine implements Engine {

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
    protected abstract void loadDataSet();

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
    }

    /**
     * evaluate the prediction
     */
    protected void evaluate() {
        LoggerUtil.info(logger, "4. evaluate the prediction.");
    }

}
