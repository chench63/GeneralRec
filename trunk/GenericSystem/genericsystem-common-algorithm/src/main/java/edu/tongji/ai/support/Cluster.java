/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ai.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 类簇
 * 
 * @author Hanke Chen
 * @version $Id: Cluster.java, v 0.1 10 Apr 2014 11:17:20 chench Exp $
 */
public final class Cluster {

    /** 点集，记录样本索引号，节省空间*/
    private final List<Integer> points = new ArrayList<Integer>();

    /** 点集的几何中心*/
    private Point               centroid;

    /**
     * 添加点到本类
     * 
     * @param pposition
     */
    public void put(int position) {
        points.add(position);
    }

    /**
     * 计算几何中心
     */
    public void centroid(List<Point> samples) {
        if (points.isEmpty()) {
            return;
        }

        //计算几何中心
        centroid = new Point();
        int dimension = samples.get(0).dimension();
        for (int axis = 0; axis < dimension; axis++) {

            //计算单维度上的均值
            double sum = 0.0d;
            for (Integer position : points) {
                sum += samples.get(position).dimensionValue(axis);
            }
            centroid.addAxis(sum / points.size());
        }
    }

    /**
     * 计算点到几何中心的距离
     * 
     * @param p
     * @return
     */
    public double distance(Point p) {
        return centroid.distance(p);
    }

    /**
     * 返回值集合
     * 
     * @return
     */
    public Integer[] values() {
        return (Integer[]) points.toArray();
    }
}
