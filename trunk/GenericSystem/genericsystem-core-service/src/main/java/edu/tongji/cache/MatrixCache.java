/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 *  {@code DenseMatrix64F}线程安全版本
 * 
 * 
 * @author Hanke Chen
 * @version $Id: MatrixCache.java, v 0.1 2014-10-7 下午6:47:30 chench Exp $
 */
public final class MatrixCache {

    /** MovieLens 1M*/
    protected static DenseMatrix64F    matrix         = null;

    /** 读写锁 */
    private static final ReadWriteLock lock           = new ReentrantReadWriteLock();

    /** 默认评分*/
    private static final double        DEFAULT_RATING = 3.0;

    /**
     * Set value in Matrix
     * 一般：
     * 数据集索引，行列都-1；如果直接代表矩阵的行列号，不需要修正
     * 
     * 
     * @param row       矩阵行号：0开始
     * @param column    矩阵列号：0开始
     * @param val
     */
    public static void set(int row, int col, double val) {

        // 初始化评分矩阵
        if (matrix == null) {
            matrix = new DenseMatrix64F(6040, 3952);
            CommonOps.fill(matrix, DEFAULT_RATING);
        }

        // 读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            matrix.set(row, col, val);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 使用数据集索引，行列都-1；
     * 
     * @param row
     * @param col
     * @param val
     */
    public static void setByDatstIndex(int row, int col, double val) {
        set(row - 1, col - 1, val);
    }

    /**
     * Get value w.r.t matrix index 
     * 
     * @param row
     * @param col
     * @return
     */
    public static double get(int row, int col) {
        return matrix.get(row, col);
    }

    /**
     * 使用数据集索引，行列都-1；
     * 
     * @param row
     * @param col
     * @return
     */
    public static double getByDatstIndex(int row, int col) {
        return get(row - 1, col - 1);
    }

    /**
     * 返回列数
     * 
     * @return
     */
    public static int getNumCols() {
        return matrix.getNumCols();
    }

    /**
     * 返回行数
     * 
     * @return
     */
    public static int getNumRows() {
        return matrix.getNumRows();
    }
}
