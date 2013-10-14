/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

/**
 * 
 * @author chenkh
 * @version $Id: FastAccRecommendationContext.java, v 0.1 2013-9-24 下午9:13:55 chenkh Exp $
 */
public class FastAccRecommendationContext extends AccRecommendationContext {

    /**
     * @see edu.tongji.context.AccRecommendationContext#checkDataSetAndShouldStop(edu.tongji.context.ContextEnvelope)
     */
    @Override
    protected boolean checkDataSetAndShouldStop(ContextEnvelope contextEnvelope) {
        //数据集比较小的，跳过
        return contextEnvelope.getResultSet().size() < 2;
    }

}
