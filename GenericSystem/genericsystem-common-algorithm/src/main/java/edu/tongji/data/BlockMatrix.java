/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.data;

import java.io.Serializable;

/**
 * The integrated matrix for a compact matrix.
 * 
 * @author Hanke Chen
 * @version $Id: BlockMatrix.java, v 0.1 2014-10-15 上午8:46:44 chench Exp $
 */
public class BlockMatrix implements Serializable {
    private static final long serialVersionUID = -7038577151057697840L;

    /** the bounds of rows*/
    private int[]             rowBound;

    /** the bounds of columns*/
    //    private int[]             colBound;

    /** the structure of the block matrix*/
    private int[][]           coclusterStructure;

    /** The number of rows. */
    private int               M;

    /** The number of columns. */
    private int               N;

    /** the inner matrices*/
    private SparseMatrix[][]  rateMatrices;

    /**
     * Construction method.
     */
    public BlockMatrix() {

    }

    /**
     * Construction method.
     * e.g. the order of inner matrix[i, j] is rowBound[i] - rowBound[i-1] *  colBound[j] - colBound[j - 1]
     * 
     * @param rowBound          the bounds of rows
     * @param colBound          the bounds of cols
     */
    public BlockMatrix(int[] rowBound, int[][] coclusterStructure) {
        initialize(rowBound, coclusterStructure);
    }

    /**
     * initialize the block matrix
     * 
     * @param rowBound
     * @param coclusterStructure
     */
    public void initialize(int[] rowBound, int[][] coclusterStructure) {
        //primary parameters
        this.rowBound = rowBound;
        this.coclusterStructure = coclusterStructure;
        this.M = rowBound[rowBound.length - 1];
        this.N = coclusterStructure[0][coclusterStructure[0].length - 1];

        //matrix part
        int rowStrucCount = rowBound.length;
        int[] colStrucCount = new int[rowStrucCount];
        for (int i = 0; i < rowStrucCount; i++) {
            colStrucCount[i] = coclusterStructure[i].length;
        }

        //first row
        rateMatrices = new SparseMatrix[rowStrucCount][0];
        SparseMatrix[] firstRowMatrixes = new SparseMatrix[colStrucCount[0]];
        firstRowMatrixes[0] = new SparseMatrix(rowBound[0], coclusterStructure[0][0]);
        for (int j = 1; j < colStrucCount[0]; j++) {
            int rowCount = rowBound[0];
            int colCount = coclusterStructure[0][j] - coclusterStructure[0][j - 1];
            firstRowMatrixes[j] = new SparseMatrix(rowCount, colCount);
        }
        rateMatrices[0] = firstRowMatrixes;
        //other rowes
        for (int i = 1; i < rowStrucCount; i++) {
            SparseMatrix[] ithRowMatrixes = new SparseMatrix[colStrucCount[i]];

            int rowCount = rowBound[i] - rowBound[i - 1];
            ithRowMatrixes[0] = new SparseMatrix(rowCount, coclusterStructure[i][0]);
            for (int j = 1; j < colStrucCount[i]; j++) {
                int colCount = coclusterStructure[i][j] - coclusterStructure[i][j - 1];
                ithRowMatrixes[j] = new SparseMatrix(rowCount, colCount);
            }
            rateMatrices[i] = ithRowMatrixes;
        }
    }

    /**
     * set value at the given position
     * 
     * @param i         the row index in the integrated matrix
     * @param j         the col index in the integrated matrix
     * @param val       the value to set
     */
    public void setValue(int i, int j, double val) {
        int[] position = locate(i, j);
        int row = position[0];
        int col = position[1];
        int rowInner = position[2];
        int colInner = position[3];

        rateMatrices[row][col].setValue(rowInner, colInner, val);
    }

    /**
     * get value at the given position
     * 
     * @param i         the row index in the integrated matrix
     * @param j         the col index in the integrated matrix
     * @return          value to get
     */
    public double getValue(int i, int j) {
        int[] position = locate(i, j);
        int row = position[0];
        int col = position[1];
        int rowInner = position[2];
        int colInner = position[3];

        return rateMatrices[row][col].getValue(rowInner, colInner);
    }

    /**
     * locate the index of integrated matrix to the inner detail
     * index. 
     * 
     * @param i         the row index in the integrated matrix
     * @param j         the col index in the integrated matrix
     * @return          the detail position of the submatrix
     * index 0,1 contain the row and column index of the inner matrix,
     * index 2,3 contain the row and column index within the inner matrix. 
     */
    public int[] locate(int i, int j) {
        int[] position = new int[4];

        // row information
        if (i < rowBound[0]) {
            position[0] = 0;
            position[2] = i;
        } else {
            for (int indx = 1; indx < rowBound.length; indx++) {
                if (i < rowBound[indx]) {
                    position[0] = indx;
                    position[2] = i - rowBound[indx - 1];
                    break;
                }
            }
        }

        //column information
        int rowMatrix = position[0];
        if (j < coclusterStructure[rowMatrix][0]) {
            position[1] = 0;
            position[3] = j;
        } else {
            for (int indx = 1; indx < coclusterStructure[rowMatrix].length; indx++) {
                if (j < coclusterStructure[rowMatrix][indx]) {
                    position[1] = indx;
                    position[3] = j - coclusterStructure[rowMatrix][indx - 1];
                    break;
                }
            }
        }

        return position;

    }

    /**
     * transfer local index to global index
     * 
     * @param position  the local index to transform
     * @return  the global index, index 0 contains the row index, whereas index 1 is the column index.
     */
    public int[] global(int[] position) {

        int rowBase = (position[0] == 0) ? 0 : rowBound[position[0] - 1];
        int colBase = (position[1] == 0) ? 0 : coclusterStructure[position[0]][position[1] - 1];

        int[] result = new int[2];
        result[0] = rowBase + position[2];
        result[1] = colBase + position[3];
        return result;
    }

    /**
     * get the inner matrix at the given index
     *  
     * @param i         the row index in the integrated matrix
     * @param j         the col index in the integrated matrix
     * @return          the inner matrix of the given index
     */
    public SparseMatrix getBlock(int i, int j) {
        return rateMatrices[i][j];
    }

    /**
     * get the sparsity degree of the given matrix
     * 
     * @param i         the row index in the integrated matrix
     * @param j         the col index in the integrated matrix
     * @return          the sparsity degree of the given matrix
     */
    public double getSparsity(int i, int j) {
        SparseMatrix rateMatrix = rateMatrices[i][j];

        int[] length = rateMatrix.length();
        return rateMatrix.itemCount() * 1.0 / (length[0] * length[1]);
    }

    /**
     * get the sparsity degree of the integrated matrix
     * 
     * @return
     */
    public double getSparsity() {
        return itemCount() * 1.0 / (this.M * this.N);
    }

    /**
     * Actual number of items in the matrix.
     * 
     * @return The number of items in the matrix.
     */
    public int itemCount() {
        int itemCount = 0;
        for (int i = 0; i < rowBound.length; i++) {
            for (int j = 0; j < coclusterStructure[i].length; j++) {
                itemCount += rateMatrices[i][j].itemCount();
            }
        }

        return itemCount;
    }

    /*========================================
     * Properties
     *========================================*/
    /**
     * Capacity of this matrix.
     * 
     * @return An array containing the length of this matrix.
     * Index 0 contains row count, while index 1 contains column count.
     */
    public int[] length() {
        int[] lengthArray = new int[2];

        lengthArray[0] = this.M;
        lengthArray[1] = this.N;

        return lengthArray;
    }

    /**
     * return the structure of the block matrix
     * 
     * @return
     */
    public int[][] structure() {
        return this.coclusterStructure;
    }

}
