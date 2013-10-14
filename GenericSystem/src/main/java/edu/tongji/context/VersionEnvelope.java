/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

/**
 * 
 * @author chench
 * @version $Id: VersionEnvelope.java, v 0.1 17 Sep 2013 12:57:19 chench Exp $
 */
public class VersionEnvelope {
    /** 投票分值总值 */
    private double sumOfRating;

    /** 投票平均值 */
    private double avgOfRating;

    /** 已投票人数 */
    private int    countOfUsers;

    /**
     * Getter method for property <tt>sumOfRating</tt>.
     * 
     * @return property value of sumOfRating
     */
    public double getSumOfRating() {
        return sumOfRating;
    }

    /**
     * Setter method for property <tt>sumOfRating</tt>.
     * 
     * @param sumOfRating value to be assigned to property sumOfRating
     */
    public void setSumOfRating(double sumOfRating) {
        this.sumOfRating = sumOfRating;
    }

    /**
     * Getter method for property <tt>avgOfRating</tt>.
     * 
     * @return property value of avgOfRating
     */
    public double getAvgOfRating() {
        return avgOfRating;
    }

    /**
     * Setter method for property <tt>avgOfRating</tt>.
     * 
     * @param avgOfRating value to be assigned to property avgOfRating
     */
    public void setAvgOfRating(double avgOfRating) {
        this.avgOfRating = avgOfRating;
    }

    /**
     * Getter method for property <tt>countOfUsers</tt>.
     * 
     * @return property value of countOfUsers
     */
    public int getCountOfUsers() {
        return countOfUsers;
    }

    /**
     * Setter method for property <tt>countOfUsers</tt>.
     * 
     * @param countOfUsers value to be assigned to property countOfUsers
     */
    public void setCountOfUsers(int countOfUsers) {
        this.countOfUsers = countOfUsers;
    }

}
