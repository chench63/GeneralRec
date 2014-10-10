/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.vo;

import java.util.Date;

/**
 * 天气数据VO类
 * 
 * @author Hanke Chen
 * @version $Id: WeatherVO.java, v 0.1 18 Apr 2014 14:46:54 chench Exp $
 */
public final class WeatherVO {

    /** 日期*/
    private Date   day;

    /** 最高温度*/
    private double highTemper;

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (new StringBuilder()).append(" Date: ").append(this.day).append(" HT: ")
            .append(this.highTemper).toString();
    }

    /**
     * Getter method for property <tt>day</tt>.
     * 
     * @return property value of day
     */
    public Date getDay() {
        return day;
    }

    /**
     * Setter method for property <tt>day</tt>.
     * 
     * @param day value to be assigned to property day
     */
    public void setDay(Date day) {
        this.day = day;
    }

    /**
     * Getter method for property <tt>highTemper</tt>.
     * 
     * @return property value of highTemper
     */
    public double getHighTemper() {
        return highTemper;
    }

    /**
     * Setter method for property <tt>highTemper</tt>.
     * 
     * @param highTemper value to be assigned to property highTemper
     */
    public void setHighTemper(double highTemper) {
        this.highTemper = highTemper;
    }

}
