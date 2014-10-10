/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation.thread;

import org.apache.log4j.Logger;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SingularOps;

import edu.tongji.cache.MatrixCache;
import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.engine.recommendation.SnglrValuDecmpsRcmdEngine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 矩阵奇异值分解线程
 * 
 * @author Hanke Chen
 * @version $Id: MatrixSnglrValDecmposr.java, v 0.1 2014-10-8 下午1:43:52 chench Exp $
 */
public class MatrixSnglrValDecmposr implements Runnable {

    /** 当前序列号 */
    private static int            pIndex = 0;

    /** 互斥锁 */
    private static final Object   mutex  = new Object();

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * 返回当前需要，进行SVD矩阵的index
     * 
     * @return submatrix index
     */
    protected static int task() {

        synchronized (mutex) {
            // 不存在子序列号
            if (pIndex >= ConfigurationConstant.ROW * ConfigurationConstant.COLUMN) {
                return -1;
            }

            // 更新当前序列号
            return pIndex++;
        }
    }

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        int index = -1;
        int rowStep = MatrixCache.getNumRows() / ConfigurationConstant.ROW;
        int colStep = MatrixCache.getNumCols() / ConfigurationConstant.COLUMN;

        while ((index = task()) != -1) {

            //0. 子矩阵索引
            int rowIndex = index / ConfigurationConstant.ROW;
            int colIndex = index / ConfigurationConstant.COLUMN;

            //1. 计算子矩阵的行区间
            int rowStart = rowIndex * rowStep;
            int rowEnd = ((rowEnd = rowStart + rowStep) > MatrixCache.getNumRows()) ? MatrixCache
                .getNumRows() : rowEnd;

            //2. 计算子矩阵的列区间
            int colStart = colIndex * colStep;
            int colEnd = ((colEnd = colStart + colStep) > MatrixCache.getNumCols()) ? MatrixCache
                .getNumCols() : colEnd;

            //3. 提取子矩阵
            LoggerUtil.info(logger, "Task: " + index + " extract submatrix.");
            DenseMatrix64F matrix = new DenseMatrix64F(rowEnd - rowStart, colEnd - colStart);
            for (int i = rowStart; i < rowEnd; i++) {
                for (int j = colStart; j < colEnd; j++) {
                    matrix.set(i - rowStart, j - colStart, MatrixCache.get(i, j));
                }
            }

            //4. SVD分解：     Matrix = UWV
            LoggerUtil.info(logger, "Task: " + index + " start SVD.");
            SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(
                matrix.numRows, matrix.numCols, true, true, true);

            if (!svd.decompose(matrix))
                throw new RuntimeException("Decomposition failed");

            DenseMatrix64F U = svd.getU(null, false);
            DenseMatrix64F W = svd.getW(null);
            DenseMatrix64F V = svd.getV(null, false);
            SingularOps.descendingOrder(U, false, W, V, false);

            //5. SVD分解：      取最大的K个奇异值
            LoggerUtil.info(logger, "Task: " + index + " keep k-max singular value.");
            int n = W.numCols > W.numRows ? W.numRows : W.numCols;
            for (int i = ConfigurationConstant.PARAM_K; i < n; i++) {
                W.set(i, i, 0.0);
            }
            if (logger.isDebugEnabled()) {
                for (int i = 0; i < ConfigurationConstant.PARAM_K; i++) {
                    LoggerUtil.debug(logger, W.get(i, i));
                }
            }

            //6. 更新目标矩阵
            LoggerUtil.info(logger, "Task: " + index + " replace block: [" + rowIndex + ", "
                                    + colIndex + "]");
            DenseMatrix64F temp = new DenseMatrix64F(U.getNumRows(), W.getNumCols());
            CommonOps.mult(U, W, temp);
            U = null;
            W = null;
            CommonOps.mult(temp, V, matrix);
            temp = null;
            V = null;
            SnglrValuDecmpsRcmdEngine.matricesEstimt.replaceBlock(rowIndex, colIndex, matrix);
        }

    }
}
