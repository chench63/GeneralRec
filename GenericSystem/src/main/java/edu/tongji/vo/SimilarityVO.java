/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.vo;

import edu.tongji.parser.netflix.SimilarityTemplateParser;

/**
 * 相似度实体类
 * 
 * @author chench
 * @version $Id: SimilarityVO.java, v 0.1 23 Apr 2014 15:56:34 chench Exp $
 */
public final class SimilarityVO {

    private int   itemI;

    private int   itemJ;

    private float similarity;

    /**
     * 
     */
    public SimilarityVO() {
        super();
    }

    /**
     * @param itemI
     * @param itemJ
     * @param similarity
     */
    public SimilarityVO(int itemI, int itemJ, float similarity) {
        super();
        this.itemI = itemI;
        this.itemJ = itemJ;
        this.similarity = similarity;
    }

    /** 
     * [itemI],[itemJ],[similarity]
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (new StringBuilder()).append(this.itemI).append(SimilarityTemplateParser.SEPERATOR)
            .append(this.itemI).append(SimilarityTemplateParser.SEPERATOR).append(this.similarity)
            .toString();
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
     * Getter method for property <tt>similarity</tt>.
     * 
     * @return property value of similarity
     */
    public float getSimilarity() {
        return similarity;
    }

    /**
     * Setter method for property <tt>similarity</tt>.
     * 
     * @param similarity value to be assigned to property similarity
     */
    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

}
