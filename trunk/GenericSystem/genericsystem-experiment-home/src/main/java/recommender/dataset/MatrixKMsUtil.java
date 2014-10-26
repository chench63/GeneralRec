/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package recommender.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tongji.data.Cluster;
import edu.tongji.data.SparseMatrix;
import edu.tongji.ml.KMeansUtil;

/**
 * Cluster the matrix w.r.t the distance between the
 * user features or item features.
 * 
 * @author Hanke Chen
 * @version $Id: FeatureBasedKMsUtil.java, v 0.1 2014-10-16 下午2:29:53 chench Exp $
 */
public final class MatrixKMsUtil {

    /**
     * forbid construction method
     */
    private MatrixKMsUtil() {

    }

    /**
     * divide the rows of the input matrix into K clusters
     * 
     * @param matrix            the matrix contains the training data
     * @param K                 the number of clusters to divide
     * @param maxIteration      the maximum times to run the k-means algorithm
     * @param type              the type of distance
     * @param assign            the relationship map the old index to new index
     * @param bound             the bounds of each cluster
     */
    public static void divide(SparseMatrix matrix, int K, int maxIteration, int type,
                              Map<Integer, Integer> assign, int[] bound) {
        Cluster[] cols = KMeansUtil.divide(matrix, K, maxIteration, type);
        int newRow = 0;
        for (Cluster cluster : cols) {
            for (int row : cluster) {
                assign.put(row, newRow);
                newRow++;
            }
        }

        bound[0] = cols[0].getList().size();
        for (int i = 1; i < K; i++) {
            bound[i] = bound[i - 1] + cols[i].getList().size();
        }
    }

    /**
     * divide the rows of the input matrix into K clusters
     * 
     * @param rateMatrix        the matrix contains the training data
     * @param matrix            the matrix contains the data to cluster
     * @param K                 the number of clusters to divide
     * @param maxIteration      the maximum times to run the k-means algorithm
     * @param type              the type of distance
     * @param assign            the relationship map the old index to new index
     * @param bound             the bounds of each cluster
     */
    public static void divideAsDensity(SparseMatrix rateMatrix, SparseMatrix matrix, int K,
                                       int maxIteration, int type, Map<Integer, Integer> assign,
                                       int[] bound) {
        Cluster[] cols = KMeansUtil.divide(matrix, K, maxIteration, type);

        //move dense row in lower row
        preprocess(rateMatrix, cols);

        int newRow = 0;
        for (Cluster cluster : cols) {
            for (int row : cluster) {
                assign.put(row, newRow);
                newRow++;
            }
        }

        bound[0] = cols[0].getList().size();
        for (int i = 1; i < K; i++) {
            bound[i] = bound[i - 1] + cols[i].getList().size();
        }
    }

    protected void findAnchor(SparseMatrix testMatrix, int[] originators, int K, int count) {
        int[] sizes = new int[K];
        for (int i = 0; i < count; i++) {
            int size = testMatrix.getRow(i).itemCount();
            for (int k = 0; k < K; k++) {
                if (size > sizes[k]) {
                    for (int j = K - 1; j > k; j--) {
                        sizes[j] = sizes[j - 1];
                        originators[j] = originators[j - 1];
                    }

                    sizes[k] = size;
                    originators[k] = i;
                    break;
                }
            }
        }
    }

    /**
     * move dense row in lower row
     * 
     * @param matrix        the matrix contains data
     * @param cols          the clusters
     */
    protected static void preprocess(SparseMatrix matrix, Cluster[] cols) {
        for (Cluster cluster : cols) {
            Map<Integer, List<Integer>> globalCountToIndex = new HashMap<Integer, List<Integer>>();
            List<Integer> dists = new ArrayList<Integer>();

            //make relationship
            for (int row : cluster) {
                int size = matrix.getRowRef(row).itemCount();

                if (!globalCountToIndex.containsKey(size)) {
                    dists.add(size);
                }

                List<Integer> localIndex = globalCountToIndex.get(size);
                if (localIndex == null) {
                    localIndex = new ArrayList<Integer>();
                    globalCountToIndex.put(size, localIndex);
                }
                localIndex.add(row);
            }

            //sort
            cluster.clear();
            Collections.sort(dists);
            for (int i = dists.size() - 1; i >= 0; i--) {
                int size = dists.get(i);
                List<Integer> localIndex = globalCountToIndex.get(size);
                for (int row : localIndex) {
                    cluster.add(row);
                }
            }
        }

    }

}
