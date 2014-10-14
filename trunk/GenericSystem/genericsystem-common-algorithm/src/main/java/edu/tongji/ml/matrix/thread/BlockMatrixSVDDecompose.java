/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ml.matrix.thread;

import edu.tongji.matrix.ComplicatedMatrix;
import edu.tongji.matrix.SparseMatrix;
import edu.tongji.ml.matrix.MatrixFactorizationRecommender;

/**
 * RegularizedSVD thread
 * 
 * @author Hanke Chen
 * @version $Id: BlockMatrixSVDDecompose.java, v 0.1 2014-10-13 下午4:19:27 chench Exp $
 */
public class BlockMatrixSVDDecompose extends Thread {

    /**  the matrix with the training data*/
    public static ComplicatedMatrix                  rateBlockes;

    /**  the corresponding recommender*/
    public static MatrixFactorizationRecommender[][] recommender;

    /** mutex object*/
    public final static Object                       mutex   = new Object();

    /** current working matrix*/
    private static int                               currRow = 0;
    /** current working matrix*/
    private static int                               currCol = 0;

    public static int[] task() {

        synchronized (mutex) {
            SparseMatrix matrix = rateBlockes.getBlock(currRow, currCol);
            if (matrix == null) {
                return null;
            }

            int[] result = new int[2];
            result[0] = currRow;
            result[1] = currCol;

            currCol++;
            if (currCol == rateBlockes.getColCount()) {
                currCol = 0;
                currRow++;
            }

            return result;
        }
    }

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        int[] position = null;
        while ((position = task()) != null) {
            int i = position[0];
            int j = position[1];

            SparseMatrix rateMatrix = rateBlockes.getBlock(i, j);
            recommender[i][j].buildModel(rateMatrix);
        }

    }

}
