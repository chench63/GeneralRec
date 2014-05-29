/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.vo;

import java.util.Date;

import edu.tongji.util.DateUtil;

/**
 *  REDD 时间类 
 * 
 * @author chench
 * @version $Id: ReddEvent.java, v 0.1 2014-5-27 下午6:25:25 chench Exp $
 */
public final class BayesianEventVO implements Comparable<BayesianEventVO> {

    /** UTC时间*/
    private long  timeVal;

    /** 天气热，温度*/
    private float hot;

    /** 在家*/
    private short indoor;

    /** 空调工作情况*/
    private short ac;

    /** 电能功耗*/
    private float power;

    /**
     * 
     */
    public BayesianEventVO() {
    }

    /**
     * @param timeVal
     * @param ac
     */
    public BayesianEventVO(long timeVal, short ac) {
        super();
        this.timeVal = timeVal;
        this.ac = ac;
    }

    /**
     * @param timeVal
     * @param hot
     * @param indoor
     * @param ac
     * @param power
     */
    public BayesianEventVO(long timeVal, float hot, short indoor, short ac, float power) {
        super();
        this.timeVal = timeVal;
        this.hot = hot;
        this.indoor = indoor;
        this.ac = ac;
        this.power = power;
    }

    /** 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(BayesianEventVO o) {
        if (this.getTimeVal() == o.getTimeVal())
            return 0;
        return (this.getTimeVal() - o.getTimeVal() > 0) ? 1 : -1;
    }

    /**
     * Getter method for property <tt>ac</tt>.
     * 
     * @return property value of ac
     */
    public short getAc() {
        return ac;
    }

    /**
     * Setter method for property <tt>ac</tt>.
     * 
     * @param ac value to be assigned to property ac
     */
    public void setAc(short ac) {
        this.ac = ac;
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
     * Getter method for property <tt>hot</tt>.
     * 
     * @return property value of hot
     */
    public float getHot() {
        return hot;
    }

    /**
     * Setter method for property <tt>hot</tt>.
     * 
     * @param hot value to be assigned to property hot
     */
    public void setHot(float hot) {
        this.hot = hot;
    }

    /**
     * Getter method for property <tt>indoor</tt>.
     * 
     * @return property value of indoor
     */
    public short getIndoor() {
        return indoor;
    }

    /**
     * Setter method for property <tt>indoor</tt>.
     * 
     * @param indoor value to be assigned to property indoor
     */
    public void setIndoor(short indoor) {
        this.indoor = indoor;
    }

    /**
     * Getter method for property <tt>power</tt>.
     * 
     * @return property value of power
     */
    public float getPower() {
        return power;
    }

    /**
     * Setter method for property <tt>power</tt>.
     * 
     * @param power value to be assigned to property power
     */
    public void setPower(float power) {
        this.power = power;
    }

    /** 
     * @see java.lang.Object#toString()
     * 
     * [timeVal],[hot],[indoor],[ac],[power]
     */
    @Override
    public String toString() {
        return (new StringBuilder()).append(timeVal).append(',').append(hot).append(',')
            .append(indoor).append(',').append(ac).append(',').append(power).append(',')
            .append(DateUtil.format(new Date(timeVal), DateUtil.LONG_WEB_FORMAT_NO_SEC)).toString();
    }

}
