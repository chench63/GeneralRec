/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package recommender.dataset;

import java.util.Map;

import edu.tongji.data.Cluster;
import edu.tongji.data.SparseMatrix;
import edu.tongji.ml.CoclusterUtil;

/**
 * Matrix Cocluster utility
 * 
 * @author Hanke Chen
 * @version $Id: MatrixCoclusterUtil.java, v 0.1 2014-10-28 上午11:19:49 chench Exp $
 */
public final class MatrixCoclusterUtil {

    /**
     * forbid construction method
     */
    private MatrixCoclusterUtil() {

    }

    /**
     * coclustering the matrix
     * 
     * @param matrix                 the matrix contains the data
     * @param K                      the number of row clusters
     * @param L                      the number of column clusters
     * @param maxIteration           the maximum iteration to divide
     * @param constraint             the co-clustering base
     * @param divergence             the bragman divergence involved
     * @param rowAssign              the relationship map the old index to new index w.r.t rows
     * @param rowBound               the bounds of each row clusters
     * @param colAssign              the relationship map the old index to new index w.r.t columns
     * @param coclusterStructure     the structure of the entire coclusters
     */
    //    public static void coclustering(SparseMatrix matrix, final int K, final int L,
    //                                    final int maxIteration, final int constraint,
    //                                    final int divergence, Map<Integer, Integer> rowAssign,
    //                                    int[] rowBound, Map<Integer, Integer> colAssign,
    //                                    int[][] coclusterStructure) {
    //        //Coclustering
    //        Cluster[][] result = CoclusterUtil.divide(matrix, K, L, maxIteration, constraint,
    //            divergence);
    //
    //        //establish the mapping between new and old row index of matrix
    //        Cluster[] rowClusters = result[0];
    //        int newRow = 0;
    //        for (Cluster local : rowClusters) {
    //            for (int oldRow : local) {
    //                rowAssign.put(oldRow, newRow);
    //                newRow++;
    //            }
    //        }
    //        rowBound[0] = rowClusters[0].getList().size();
    //        for (int k = 1; k < K; k++) {
    //            rowBound[k] = rowBound[k - 1] + rowClusters[k].getList().size();
    //        }
    //
    //        //establish the mapping between new and old row index of matrix
    //        Cluster[] colClusters = result[1];
    //        int newCol = 0;
    //        for (Cluster local : colClusters) {
    //            for (int oldCol : local) {
    //                colAssign.put(oldCol, newCol);
    //                newCol++;
    //            }
    //        }
    //        int[] colBound = new int[L];
    //        colBound[0] = colClusters[0].getList().size();
    //        for (int l = 1; l < L; l++) {
    //            colBound[l] = colBound[l - 1] + colClusters[l].getList().size();
    //        }
    //
    //        //update the entire structure of colusters, in the order of row clusters.
    //        for (int k = 0; k < K; k++) {
    //            coclusterStructure[k] = colBound;
    //        }
    //    }

    /**
     * coclustering the matrix
     * 
     * @param matrix                 the matrix contains the data
     * @param K                      the number of row clusters
     * @param L                      the number of column clusters
     * @param maxIteration           the maximum iteration to divide
     * @param constraint             the co-clustering base
     * @param divergence             the bragman divergence involved
     * @param rowAssign              the relationship map the old index to new index w.r.t rows
     * @param rowBound               the bounds of each row clusters
     * @param colAssign              the relationship map the old index to new index w.r.t columns
     * @param coclusterStructure     the structure of the entire coclusters
     */
    public static void coclusteringWithConjugateAssumption(SparseMatrix matrix, final int K,
                                                           final int L, final int maxIteration,
                                                           final int constraint,
                                                           final int divergence,
                                                           Map<Integer, Integer> rowAssign,
                                                           int[] rowBound,
                                                           Map<Integer, Integer> colAssign,
                                                           int[][] coclusterStructure) {
        //Coclustering
        Cluster[][] result = CoclusterUtil.divideWithConjugateAssumption(matrix, K, L,
            maxIteration, constraint, divergence);

        //establish the mapping between new and old row index of matrix
        Cluster[] rowClusters = result[0];
        int newRow = 0;
        for (Cluster local : rowClusters) {
            for (int oldRow : local) {
                rowAssign.put(oldRow, newRow);
                newRow++;
            }
        }
        rowBound[0] = rowClusters[0].getList().size();
        for (int k = 1; k < K; k++) {
            rowBound[k] = rowBound[k - 1] + rowClusters[k].getList().size();
        }

        //establish the mapping between new and old row index of matrix
        Cluster[] colClusters = result[1];
        int newCol = 0;
        for (Cluster local : colClusters) {
            for (int oldCol : local) {
                colAssign.put(oldCol, newCol);
                newCol++;
            }
        }
        int[] colBound = new int[L];
        colBound[0] = colClusters[0].getList().size();
        for (int l = 1; l < L; l++) {
            colBound[l] = colBound[l - 1] + colClusters[l].getList().size();
        }

        //update the entire structure of colusters, in the order of row clusters.
        for (int k = 0; k < K; k++) {
            coclusterStructure[k] = colBound;
        }
    }
}
