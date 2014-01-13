/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.predictor;

/**
 * 
 * @author chench
 * @version $Id: Predictor.java, v 0.1 2013-10-31 下午4:21:26 chench Exp $
 */
public interface Predictor {

    /**
     * 产生推荐结果
     */
    public void predict(PredictorHolder predictHolder);

}
