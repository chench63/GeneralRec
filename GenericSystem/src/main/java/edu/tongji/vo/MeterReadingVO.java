/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.vo;

import java.sql.Timestamp;

import edu.tongji.extend.statistics.Statisticable;

/**
 * 电表读数VO类
 * 
 * @author chench
 * @version $Id: MeterReading.java, v 0.1 2013-12-17 下午3:54:54 chench Exp $
 */
public class MeterReadingVO implements Statisticable {

    /** 电表读数*/
    private double    reading;

    /** 读取时间 */
    private Timestamp time;

    /** 时间计数 */
    private long      timeVal;

    /**
     * 构造函数
     * 
     * @param reading
     * @param time
     * @param timeVal
     */
    public MeterReadingVO(double reading, Timestamp time, long timeVal) {
        super();
        this.reading = reading;
        this.time = time;
        this.timeVal = timeVal;
    }

    /**
     * Getter method for property <tt>reading</tt>.
     * 
     * @return property value of reading
     */
    public double getReading() {
        return reading;
    }

    /**
     * Setter method for property <tt>reading</tt>.
     * 
     * @param reading value to be assigned to property reading
     */
    public void setReading(double reading) {
        this.reading = reading;
    }

    /**
     * Getter method for property <tt>time</tt>.
     * 
     * @return property value of time
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * Setter method for property <tt>time</tt>.
     * 
     * @param time value to be assigned to property time
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * Getter method for property <tt>timeVal</tt>.
     * 
     * @return property value of timeVal
     */
    public long getTimeVal() {
        return timeVal;
    }

    /**
     * Setter method for property <tt>timeVal</tt>.
     * 
     * @param timeVal value to be assigned to property timeVal
     */
    public void setTimeVal(long timeVal) {
        this.timeVal = timeVal;
    }

    /** 
     * @see edu.tongji.extend.statistics.Statisticable#getValue()
     */
    @Override
    public Number getValue() {
        return this.reading;
    }

}
