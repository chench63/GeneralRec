/**
 * Tongji Edu.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.analysis;

import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.data.BlockMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;
import prea.util.MatrixFileUtil;
import prea.util.MatrixInformationUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: PredictionAnalysis.java, v 0.1 2015-3-16 下午5:48:56 chench Exp $
 */
public class ClusteringAnalysis {

    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public final static String   rootDir     = "E:/MovieLens/ml-10M100K/1/";
    public final static String[] clusterDirs = { "Kmeanspp/KL_2_2/", "Kmeanspp/KL_3_3/",
            "Kmeanspp/KL_4_4/", "Kmeanspp/KL_5_5/", "Kmeanspp/KL_10_10/", "Kmeanspp/KL_20_20/" };

    /** The number of users. 943 6040 69878*/
    public final static int      userCount   = 6040;
    /** The number of items. 1682 3706 10677*/
    public final static int      itemCount   = 3706;
    /** Maximum value of rating, existing in the dataset. */
    public final static double   maxValue    = 5.0;
    /** Minimum value of rating, existing in the dataset. */
    public final static double   minValue    = 1.0;
    /** logger */
    private final static Logger  logger      = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        for (String clusterDir : clusterDirs) {
            String trainFile = rootDir + "trainingset";
            String settingFile = rootDir + clusterDir + "SETTING";
            String rowMappingFile = rootDir + clusterDir + "RM";
            String colMappingFile = rootDir + clusterDir + "CM";

            centroids(trainFile, settingFile, rowMappingFile, colMappingFile, clusterDir);
            //            averagedEntropy(trainFile, settingFile, rowMappingFile, colMappingFile, clusterDir);
            //            distribution(trainFile, settingFile, rowMappingFile, colMappingFile, clusterDir);
        }

    }

    public static void centroids(String sourceFile, String settingFile, String rowMappingFile,
                                 String colMappingFile, String clusterIndentity) {
        BlockMatrix blck = MatrixFileUtil.reads(sourceFile, settingFile, rowMappingFile,
            colMappingFile, userCount, itemCount, null);
        int K = blck.structure().length;
        int L = blck.structure()[0].length;

        //centroids of row clustering
        for (int rIndx = 0; rIndx < K; rIndx++) {
            int len = (int) (maxValue / minValue);
            int[] countTable = new int[len];
            int itemCount = 0;

            for (int cIndx = 0; cIndx < L; cIndx++) {
                SparseMatrix matrix = blck.getBlock(rIndx, cIndx);
                int M = matrix.length()[0];
                for (int u = 0; u < M; u++) {
                    SparseVector Ru = matrix.getRowRef(u);
                    int[] indexList = Ru.indexList();
                    if (indexList == null) {
                        continue;
                    }

                    for (int v : indexList) {
                        double val = Ru.getValue(v);
                        int pivot = Double.valueOf(val / minValue - 1).intValue();
                        countTable[pivot]++;
                        itemCount++;
                    }
                }
            }

            StringBuilder msg = new StringBuilder("rCluster : " + rIndx);
            for (int i = 0; i < len; i++) {
                msg.append('\t').append(countTable[i] * 1.0 / itemCount);
            }
            LoggerUtil.info(logger, msg.toString());
        }

        //centroids of column clustering
        LoggerUtil.info(logger, "====================================");
        for (int cIndx = 0; cIndx < L; cIndx++) {
            int len = (int) (maxValue / minValue);
            int[] countTable = new int[len];
            int itemCount = 0;

            for (int rIndx = 0; rIndx < K; rIndx++) {
                SparseMatrix matrix = blck.getBlock(rIndx, cIndx);
                int M = matrix.length()[0];
                for (int u = 0; u < M; u++) {
                    SparseVector Ru = matrix.getRowRef(u);
                    int[] indexList = Ru.indexList();
                    if (indexList == null) {
                        continue;
                    }

                    for (int v : indexList) {
                        double val = Ru.getValue(v);
                        int pivot = Double.valueOf(val / minValue - 1).intValue();
                        countTable[pivot]++;
                        itemCount++;
                    }
                }
            }

            StringBuilder msg = new StringBuilder("rCluster : " + cIndx);
            for (int i = 0; i < len; i++) {
                msg.append('\t').append(countTable[i] * 1.0 / itemCount);
            }
            LoggerUtil.info(logger, msg.toString());
        }
    }

    public static void distribution(String sourceFile, String settingFile, String rowMappingFile,
                                    String colMappingFile, String clusterIndentity) {
        BlockMatrix blck = MatrixFileUtil.reads(sourceFile, settingFile, rowMappingFile,
            colMappingFile, userCount, itemCount, null);
        List<SparseMatrix> matrices = blck.getList();

        StringBuilder result = new StringBuilder();
        for (SparseMatrix matrix : matrices) {
            double[] distrbtn = MatrixInformationUtil
                .ratingDistribution(matrix, maxValue, minValue);

            for (double prob : distrbtn) {
                result.append(String.format("%.2f", prob * 100)).append("%\t");
            }
            result.append('\n');
        }

        LoggerUtil.info(logger, clusterIndentity + "\tDistribution: \n" + result.toString());
    }

    public static void averagedEntropy(String sourceFile, String settingFile,
                                       String rowMappingFile, String colMappingFile,
                                       String clusterIndentity) {
        BlockMatrix blck = MatrixFileUtil.reads(sourceFile, settingFile, rowMappingFile,
            colMappingFile, userCount, itemCount, null);
        List<SparseMatrix> matrices = blck.getList();

        double averEntropy = 0.0d;
        double numElemnt = blck.itemCount();

        for (SparseMatrix matrix : matrices) {
            double[] distrbtn = MatrixInformationUtil
                .ratingDistribution(matrix, maxValue, minValue);

            double entropy = 0.0d;
            for (double prob : distrbtn) {
                entropy -= prob * Math.log(prob);
            }

            int itemCount = matrix.itemCount();
            averEntropy += entropy * itemCount / numElemnt;
        }

        LoggerUtil.info(logger, clusterIndentity + "\tAvgEntropy: " + averEntropy);
    }

}
