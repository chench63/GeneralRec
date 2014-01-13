/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.stopper;

import java.sql.Timestamp;

/**
 * 
 * @author chench
 * @version $Id: TimestampStopper.java, v 0.1 2013-9-18 上午11:33:20 chench Exp $
 */
public class TimestampStopper implements Stopper {

    /** 初始时间 */
    private String    scrachLine;

    /** 截至时间 */
    private String    deadLine;

    /** 间隔时间 */
    private long      stepDay;

    /** 当前种子 */
    private Timestamp seed;

    /** 截至时间 */
    private Timestamp deadLineOBJ;

    /** 
     * @see edu.tongji.stopper.Stopper#isStop()
     */
    @Override
    public boolean isStop() {
        return false;
    }

    /** 
     * @see edu.tongji.stopper.Stopper#genSeed()
     */
    @Override
    public Object genSeed() {
        if (seed == null) {
            seed = Timestamp.valueOf(scrachLine);
            return seed;
        }

        seed = new Timestamp(seed.getTime() + stepDay * 24 * 60 * 60 * 1000);

        return seed.after(deadLineOBJ) ? null : seed;
    }

    /** 
     * @see edu.tongji.stopper.Stopper#reset()
     */
    @Override
    public void reset() {
        seed = null;
    }

    /**
     * Getter method for property <tt>scrachLine</tt>.
     * 
     * @return property value of scrachLine
     */
    public String getScrachLine() {
        return scrachLine;
    }

    /**
     * Setter method for property <tt>scrachLine</tt>.
     * 
     * @param scrachLine value to be assigned to property scrachLine
     */
    public void setScrachLine(String scrachLine) {
        this.scrachLine = scrachLine;
    }

    /**
     * Getter method for property <tt>deadLine</tt>.
     * 
     * @return property value of deadLine
     */
    public String getDeadLine() {
        return deadLine;
    }

    /**
     * Setter method for property <tt>deadLine</tt>.
     * 
     * @param deadLine value to be assigned to property deadLine
     */
    public void setDeadLine(String deadLine) {
        deadLineOBJ = Timestamp.valueOf(deadLine);
        this.deadLine = deadLine;
    }

    /**
     * Getter method for property <tt>stepDay</tt>.
     * 
     * @return property value of stepDay
     */
    public long getStepDay() {
        return stepDay;
    }

    /**
     * Setter method for property <tt>stepDay</tt>.
     * 
     * @param stepDay value to be assigned to property stepDay
     */
    public void setStepDay(long stepDay) {
        this.stepDay = stepDay;
    }

    /**
     * Setter method for property <tt>seed</tt>.
     * 
     * @param seed value to be assigned to property seed
     */
    public void setSeed(Timestamp seed) {
        this.seed = seed;
    }

    /**
     * Getter method for property <tt>seed</tt>.
     * 
     * @return property value of seed
     */
    public Timestamp getSeed() {
        return seed;
    }

    /**
     * Getter method for property <tt>deadLineOBJ</tt>.
     * 
     * @return property value of deadLineOBJ
     */
    public Timestamp getDeadLineOBJ() {
        return deadLineOBJ;
    }

    /**
     * Setter method for property <tt>deadLineOBJ</tt>.
     * 
     * @param deadLineOBJ value to be assigned to property deadLineOBJ
     */
    public void setDeadLineOBJ(Timestamp deadLineOBJ) {
        this.deadLineOBJ = deadLineOBJ;
    }

}
