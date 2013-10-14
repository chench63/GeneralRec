/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

/**
 * 
 * @author chenkh
 * @version $Id: DataStreamHolder.java, v 0.1 2013-10-8 下午12:03:52 chenkh Exp $
 */
public class DataStreamHolder {

    /** itemI号 */
    private int itemI;

    /** itemJ号*/
    private int itemJ;

    /** rating总数*/
    private int numToalRating;

    /** 丢失rating总数*/
    private int numMissingRating;

    /**
     * @param itemI
     * @param itemJ
     * @param numToalRating
     * @param numMissingRating
     */
    public DataStreamHolder(int itemI, int itemJ, int numToalRating, int numMissingRating) {
        this.itemI = itemI;
        this.itemJ = itemJ;
        this.numToalRating = numToalRating;
        this.numMissingRating = numMissingRating;
    }

    /**
     * Getter method for property <tt>itemI</tt>.
     * 
     * @return property value of itemI
     */
    public int getItemI() {
        return itemI;
    }

    /**
     * Setter method for property <tt>itemI</tt>.
     * 
     * @param itemI value to be assigned to property itemI
     */
    public void setItemI(int itemI) {
        this.itemI = itemI;
    }

    /**
     * Getter method for property <tt>itemJ</tt>.
     * 
     * @return property value of itemJ
     */
    public int getItemJ() {
        return itemJ;
    }

    /**
     * Setter method for property <tt>itemJ</tt>.
     * 
     * @param itemJ value to be assigned to property itemJ
     */
    public void setItemJ(int itemJ) {
        this.itemJ = itemJ;
    }

    /**
     * Getter method for property <tt>numToalRating</tt>.
     * 
     * @return property value of numToalRating
     */
    public int getNumToalRating() {
        return numToalRating;
    }

    /**
     * Setter method for property <tt>numToalRating</tt>.
     * 
     * @param numToalRating value to be assigned to property numToalRating
     */
    public void setNumToalRating(int numToalRating) {
        this.numToalRating = numToalRating;
    }

    /**
     * Getter method for property <tt>numMissingRating</tt>.
     * 
     * @return property value of numMissingRating
     */
    public int getNumMissingRating() {
        return numMissingRating;
    }

    /**
     * Setter method for property <tt>numMissingRating</tt>.
     * 
     * @param numMissingRating value to be assigned to property numMissingRating
     */
    public void setNumMissingRating(int numMissingRating) {
        this.numMissingRating = numMissingRating;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (new StringBuilder("ItemI : ")).append(this.itemI).append(" ItemJ: ").append(itemJ)
            .append("  numToalRating:").append(this.numToalRating).append(" numMissingRating: ")
            .append(this.numMissingRating).toString();
    }
}
