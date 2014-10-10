/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ai.cluster;

import java.util.ArrayList;
import java.util.List;

import edu.tongji.ai.support.Cluster;
import edu.tongji.ai.support.Point;
import edu.tongji.util.RandomUtil;

/**
 * K-means 工具类，聚类算法
 * 
 * @author Hanke Chen
 * @version $Id: KMeansUtil.java, v 0.1 10 Apr 2014 10:32:28 chench Exp $
 */
public final class KMeansUtil {

    /** CLusters*/
    private final static List<Cluster> clusters = new ArrayList<Cluster>();

    /**
     * 禁用构造函数
     */
    private KMeansUtil() {

    }

    /**
     * 聚类
     * 
     * @param samples
     * @param k
     * @return
     */
    public static List<Cluster> clustering(List<Point> samples, int k) {
        clusters.clear();

        //0. 初始化类簇
        initializeClusters(samples, k);

        boolean isChanged = true;
        do {
            //1. 计算几何中心
            centroidStep(samples);

            //.2 重新聚合
            repartionStep(samples, k, isChanged);

        } while (isChanged);

        return clusters;
    }

    /**
     * 初始化类簇
     * 
     * @param samples
     * @param k
     */
    public static void initializeClusters(List<Point> samples, int k) {

        //初始化类簇 
        for (int i = 0; i < k; i++) {
            clusters.add(new Cluster());
        }

        //随机聚合样本
        int numSample = samples.size();
        for (int i = 0; i < numSample; i++) {
            clusters.get(RandomUtil.nextInt(0, k)).put(i);
        }

    }

    /**
     * 计算几何中心
     */
    public static void centroidStep(List<Point> samples) {
        for (Cluster cluster : clusters) {
            cluster.centroid(samples);
        }
    }

    /**
     * 重新聚合
     */
    public static void repartionStep(List<Point> samples, int k, boolean isChanged) {

        //为判别变化情况，建立原始聚类镜像
        int[] positionMirror = new int[samples.size()];
        for (int i = 0; i < k; i++) {
            for (Integer pos : clusters.get(i).values()) {
                positionMirror[pos] = i;
            }
        }

        //重新聚合
        int numSample = samples.size();
        for (int i = 0; i < numSample; i++) {
            Point sample = samples.get(i);

            //计算样本到各个几何中心的最小距离
            int index = 0;
            double min = Double.MAX_VALUE;
            for (int clusterId = 0; clusterId < k; clusterId++) {
                double distance = clusters.get(clusterId).distance(sample);

                if (min > distance) {
                    index = clusterId;
                    min = distance;
                }
            }

            //将原本分类至最小距离的几何中心类簇中
            clusters.get(index).put(i);

            //未改变且位置发生改变，设置标志位为改变
            if (!isChanged & positionMirror[i] != index) {
                isChanged = true;
            }
        }
    }

}
