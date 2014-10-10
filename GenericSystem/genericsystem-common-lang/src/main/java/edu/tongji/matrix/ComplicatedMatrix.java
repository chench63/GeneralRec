/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.matrix;

import org.ejml.data.DenseMatrix64F;

/**
 * 符合矩阵
 * 
 * @author Hanke Chen
 * @version $Id: ComplicatedMatrix.java, v 0.1 2014-10-8 下午8:26:21 chench Exp $
 */
public class ComplicatedMatrix {

    /** 目标子矩阵*/
    private DenseMatrix64F[][] matrices;

    /** 子矩阵行数*/
    private int                blockRow;

    /** 子矩阵列数*/
    private int                blockCol;

    /**
     * 构造函数
     * 
     * @param blockRow 子矩阵行数
     * @param blockCol 子矩阵列数
     */
    public ComplicatedMatrix(int blockRow, int blockCol) {
        matrices = new DenseMatrix64F[blockRow][blockCol];
        this.blockRow = blockRow;
        this.blockCol = blockCol;
    }

    /**
     * 
     * 
     * @param row
     * @param col
     * @return
     */
    public double get(int row, int col) {

        //1. 确定所在子矩阵块的行索引号
        int blockRowIndx = 0;
        for (int i = 0; i < blockRow; i++) {
            //在此模块中
            if (row < matrices[i][0].numRows) {
                blockRowIndx = i;
                break;
            }

            //不在模块中
            row -= matrices[i][0].numRows;
        }

        //2. 确定所在子矩阵块的列索引号
        int blockColIndx = 0;
        for (int j = 0; j < blockCol; j++) {
            //在此模块中
            if (col < matrices[blockRowIndx][j].numCols) {
                blockColIndx = j;
                break;
            }

            //不在模块中
            col -= matrices[blockRowIndx][j].numCols;
        }

        return matrices[blockRowIndx][blockColIndx].get(row, col);
    }

    /**
     * 替换子矩阵模块
     * 
     * @param blockRowIndx
     * @param blockColIndx
     * @param target
     */
    public void replaceBlock(int blockRowIndx, int blockColIndx, DenseMatrix64F target) {
        matrices[blockRowIndx][blockColIndx] = target;
    }

}
