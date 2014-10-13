/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.matrix;

/**
 * A integrated matrix, which consists of a set of block.
 * 
 * @author Hanke Chen
 * @version $Id: ComplicatedMatrix.java, v 0.1 2014-10-8 下午8:26:21 chench Exp $
 */
public class ComplicatedMatrix {

    /** matrixes*/
    private SparseMatrix[][] matrices;

    /** the boundaries of row, where the lower bound is inclusive and upper bound is exclusive*/
    private int[]            boundRow;

    /** the boundaries of column, where the lower bound is inclusive and upper bound is exclusive*/
    private int[]            boundCol;

    /** the number of matrix in the row perspective*/
    private int              rowCount;

    /** the number of matrix in the column perspective*/
    private int              colCount;

    /**
     * Construction function
     * 
     * @param blockRow 子矩阵行数
     * @param blockCol 子矩阵列数
     */
    public ComplicatedMatrix(int[] boundRow, int[] boundCol) {
        this.boundRow = boundRow;
        this.boundCol = boundCol;

        rowCount = boundRow.length;
        colCount = boundCol.length;
        matrices = new SparseMatrix[rowCount][colCount];
        for (int i = 0; i < rowCount; i++)
            for (int j = 0; j < colCount; j++) {
                int rowCountInner = (i == 0) ? boundRow[0] - 1 : boundRow[i] - boundRow[i - 1];
                int colCountInner = (j == 0) ? boundCol[0] - 1 : boundCol[j] - boundCol[j - 1];
                matrices[i][j] = new SparseMatrix(rowCountInner + 1, colCountInner + 1);
            }

    }

    /**
     * set value at the given index
     * 
     * @param i     the row of the given index
     * @param j     the column of the given index
     * @param value
     */
    public void setValue(int i, int j, double value) {
        int[] position = get(i, j);
        int row = position[0];
        int col = position[1];
        int rowInner = position[2];
        int colInner = position[3];

        matrices[row][col].setValue(rowInner, colInner, value);
    }

    /**
     * the corresponding pisition of the given index
     * [0],[1] the row and column index of the block,
     * [2],[3] the row and column index of the element in the block.
     * 
     * @param row   the row index
     * @param col   the column index
     * @return
     */
    public int[] get(int row, int col) {
        int[] position = new int[4];

        int rowBlock = -1;
        for (int i = 0; i < boundRow.length; i++) {
            if (row < boundRow[i]) {
                rowBlock = i;
                break;
            }
        }
        int rowInner = (rowBlock == 0) ? row : row - boundRow[rowBlock - 1] + 1;

        int colBlock = -1;
        for (int i = 0; i < boundCol.length; i++) {
            if (col < boundCol[i]) {
                colBlock = i;
                break;
            }
        }
        int colInner = (colBlock == 0) ? col : col - boundCol[colBlock - 1] + 1;

        position[0] = rowBlock;
        position[1] = colBlock;
        position[2] = rowInner;
        position[3] = colInner;
        return position;
    }

    /**
     * get the block given the index
     * 
     * @param i the row index of the block to get
     * @param j the colunm index of the block to get
     * @return
     */
    public SparseMatrix getBlock(int i, int j) {
        return matrices[i][j];
    }

    /**
     * Getter method for property <tt>rowCount</tt>.
     * 
     * @return property value of rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Getter method for property <tt>colCount</tt>.
     * 
     * @return property value of colCount
     */
    public int getColCount() {
        return colCount;
    }

}
