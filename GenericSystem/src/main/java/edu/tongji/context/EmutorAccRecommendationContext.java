/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.tongji.model.Rating;
import edu.tongji.stopper.Stopper;
import edu.tongji.stopper.TimestampStopper;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingHistoryVO;

/**
 * 
 * @author chenkh
 * @version $Id: EmutorAccRecommendationContext.java, v 0.1 2013-9-23 上午9:59:01 chenkh Exp $
 */
public class EmutorAccRecommendationContext extends AccRecommendationContext {

    /** stopper取个结束值*/
    private Stopper stopper;

    /** 
     * @see edu.tongji.context.ProcessorContext#clearContext()
     */
    @Override
    public void clearContext() {
        super.clearContext();
        stopper.reset();
    }

    /**
     * @see edu.tongji.context.AccRecommendationContext#checkAndFixData(java.util.List)
     */
    @Override
    protected void complementMissingRating(ContextEnvelope contextEnvelope) {

        List<Serializable> resultSet = contextEnvelope.getResultSet();
        Map<Timestamp, RatingHistoryVO> ratingComplementsSet = contextEnvelope
            .getRatingComplementsSet();
        Timestamp curSeed = ((TimestampStopper) getStopper()).getSeed();
        RatingHistoryVO ratingHistory = ratingComplementsSet.get(curSeed);

        //该天无[丢失用户]在线记录
        if (ratingHistory == null) {
            return;
        }

        List<Serializable> complementsSet = contextEnvelope.getComplementsSet();
        Rating rating = null;
        for (Iterator<Rating> iter = ratingHistory.iterator(); iter.hasNext();) {
            rating = iter.next();
            int indexOfRating = complementsSet.indexOf(rating);

            if (!resultSet.contains(rating) && indexOfRating != -1) {
                resultSet.add(complementsSet.get(indexOfRating));
                complementsSet.remove(indexOfRating);
            }
        }
    }

    /**
     * @see edu.tongji.context.AccRecommendationContext#checkDataSetAndShouldStop(edu.tongji.context.ContextEnvelope)
     */
    @Override
    protected boolean checkDataSetAndShouldStop(ContextEnvelope contextEnvelope) {
        int dataSetSize = 0;
        Rating rating = null;
        for (Serializable customer : contextEnvelope.getResultSet()) {
            rating = (Rating) customer;
            if (StringUtil.equalsIgnoreCase(rating.getMovieId(), itemI)
                && !userTableOfItemI.containsKey(rating.getUsrId())) {
                dataSetSize++;
            }

            if (StringUtil.equalsIgnoreCase(rating.getMovieId(), itemJ)
                && !userTableOfItemJ.containsKey(rating.getUsrId())) {
                userTableOfItemJ.put(rating.getUsrId(), rating.getRating());
                dataSetSize++;
            }

            //存在超过2个更新数，符合更新要求
            if (dataSetSize == 2) {
                return false;
            }
        }

        return true;
    }

    /**
     * Getter method for property <tt>stopper</tt>.
     * 
     * @return property value of stopper
     */
    public Stopper getStopper() {
        return stopper;
    }

    /**
     * Setter method for property <tt>stopper</tt>.
     * 
     * @param stopper value to be assigned to property stopper
     */
    public void setStopper(Stopper stopper) {
        this.stopper = stopper;
    }
}
