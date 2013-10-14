/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.thread;

import edu.tongji.dao.RatingDAO;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixSimularityPerformanceDBReader.java, v 0.1 2013-10-12 下午2:51:30 chenkh Exp $
 */
public class NetflixSimularityPerformanceDBReader implements Runnable {

    /** DAO */
    private RatingDAO ratingDAO;

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        
        
        ratingDAO.select(null);
        
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
