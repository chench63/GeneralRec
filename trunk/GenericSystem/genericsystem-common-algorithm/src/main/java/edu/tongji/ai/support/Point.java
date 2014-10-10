/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ai.support;

import java.util.ArrayList;
import java.util.List;

import edu.tongji.exception.FunctionErrorCode;
import edu.tongji.exception.OwnedException;

/**
 * 多维空间点
 * 
 * @author Hanke Chen
 * @version $Id: Point.java, v 0.1 10 Apr 2014 10:35:11 chench Exp $
 */
public final class Point {
    /** 多维坐标值*/
    private final List<Double> coordinate = new ArrayList<Double>();

    /**
     * 按顺序依次插入坐标值
     * 
     * @param val
     */
    public void addAxis(double val) {
        coordinate.add(val);
    }

    /**
     * 插入坐标点
     * 
     * @param point
     */
    public void addPoint(double[] point) {
        coordinate.clear();
        for (double val : point) {
            coordinate.add(val);
        }
    }

    /**
    * 插入坐标点
    * 
    * @param point
    */
    public void addPoint(List<Double> point) {
        coordinate.clear();
        coordinate.addAll(point);
    }

    /**
     * 返回空间点的维度
     * 
     * @return
     */
    public int dimension() {
        return this.coordinate.size();
    }

    /**
     * 返回第axis维上的值
     * 
     * @param axis
     * @return
     */
    public double dimensionValue(int axis) {
        return this.coordinate.get(axis);
    }

    /**
     * 返回各维度点值
     * 
     * @return
     */
    public Double[] valueSet() {
        return (Double[]) this.coordinate.toArray();
    }

    /**
     * 计算点与点之间的距离
     * 
     * @param point
     * @return
     */
    public double distance(Point point) {
        if (this.dimension() != this.dimension()) {
            throw new OwnedException(FunctionErrorCode.ILLEGAL_PARAMETER);
        }

        Double[] values = point.valueSet();
        double numDimention = this.dimension();
        double distance = 0.0d;
        for (int i = 0; i < numDimention; i++) {
            distance += Math.pow(coordinate.get(i) - values[i], 2.0d);
        }

        return Math.sqrt(distance);
    }

}
