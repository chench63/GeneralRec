/**
 * Tongji Edu.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.tongji.data.BlockMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.ml.matrix.DynamicCRSVD;
import edu.tongji.ml.matrix.FCRSVD;
import edu.tongji.ml.matrix.ItemConstraintRSVD;
import edu.tongji.ml.matrix.RegularizedSVD;
import edu.tongji.ml.matrix.UserConstraintRSVD;
import edu.tongji.util.FileUtil;
import prea.util.EvaluationMetrics;
import prea.util.MatrixFileUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: ConstrainedExper.java, v 0.1 2015-3-31 上午9:40:57 chench Exp $
 */
public class ConstrainedExper {

    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]      rootDirs     = { "E:/MovieLens/ml-10M100K/fetch50/2/",
            "E:/MovieLens/ml-10M100K/fetch50/3/", "E:/MovieLens/ml-10M100K/fetch50/1/" };
    /** The number of users. 943 6040 69878*/
    public final static int     userCount    = 69878;
    /** The number of items. 1682 3706 10677*/
    public final static int     itemCount    = 10677;
    public final static double  maxValue     = 5.0;
    public final static double  minValue     = 0.5;
    public final static double  lrate        = 0.001;
    public final static double  regularized  = 0.06;
    public final static int     maxIteration = 200;
    public final static boolean showProgress = false;

    public final static String  resultDir    = "E:/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        rankExp();
    }

    /*========================================
     * Experiments
     *========================================*/
    public static void rankExp() {
        String clusterDir = "Kmeanspp/KL_2_2/";

        int[] featureCounts = { 20 };
        for (String rootDir : rootDirs) {
            //            RSVD(featureCounts, rootDir);
            //            UserConstrainedRSVD(featureCounts, clusterDir, rootDir);
            //            ItemConstrainedRSVD(featureCounts, clusterDir, rootDir);
            BiConstrainedRSVD(featureCounts, clusterDir, rootDir);
        }
    }

    public static void clusteringExp() {
        String[] clusterDirs = { "Kmeanspp/KL_2_2/" };
        int[] featureCounts = { 20 };

        for (String rootDir : rootDirs) {
            for (String clusterDir : clusterDirs) {
                BiConstrainedRSVD(featureCounts, clusterDir, rootDir);
            }
        }
    }

    /*========================================
     * Algorithms
     *========================================*/

    public static void RSVD(int[] featureCounts, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        //build model
        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (int featureCount : featureCounts) {
            RegularizedSVD recmmd = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
                featureCount, lrate, regularized, 0, maxIteration, showProgress);
            recmmd.test = testMatrix;
            recmmd.buildModel(rateMatrix);

            //evaluation
            EvaluationMetrics metric = recmmd.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            FileUtil.writeAsAppend(
                resultDir + "zRSVD",
                "fc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized + "\n"
                        + metric.printOneLine() + "\n");
            stat.addValue(metric.getRMSE());
        }
        FileUtil.writeAsAppend(
            resultDir + "zRSVD",
            "Mean: " + stat.getMean() + "\tSD:"
                    + String.format("%.6f", stat.getStandardDeviation()) + "\n");
    }

    public static void UserConstrainedRSVD(int[] featureCounts, String clusterDir, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ua = new int[userCount];
        int[] dimnsn = readBiAssigmnt(ua, null, clusterDir, rootDir);

        //build model
        for (int featureCount : featureCounts) {
            UserConstraintRSVD recmmd = new UserConstraintRSVD(userCount, itemCount, maxValue,
                minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[0], ua,
                showProgress);
            recmmd.test = testMatrix;
            recmmd.buildModel(rateMatrix);

            //evaluation
            EvaluationMetrics metric = recmmd.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            FileUtil.writeAsAppend(
                resultDir + "zUC",
                "fc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized + "\n"
                        + metric.printOneLine() + "\n");
        }
    }

    public static void ItemConstrainedRSVD(int[] featureCounts, String clusterDir, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ia = new int[itemCount];
        int[] dimnsn = readBiAssigmnt(null, ia, clusterDir, rootDir);

        //build model
        for (int featureCount : featureCounts) {
            ItemConstraintRSVD recmmd = new ItemConstraintRSVD(userCount, itemCount, maxValue,
                minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[1], ia,
                showProgress);
            recmmd.test = testMatrix;
            recmmd.buildModel(rateMatrix);

            //evaluation
            EvaluationMetrics metric = recmmd.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            FileUtil.writeAsAppend(
                resultDir + "zIC",
                "fc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized + "\n"
                        + metric.printOneLine() + "\n");
        }
    }

    public static void BiConstrainedRSVD(int[] featureCounts, String clusterDir, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ua = new int[userCount];
        int[] ia = new int[itemCount];
        int[] dimnsn = readBiAssigmnt(ua, ia, clusterDir, rootDir);

        //build model
        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (int featureCount : featureCounts) {
            FCRSVD recmmd = new FCRSVD(userCount, itemCount, maxValue, minValue, featureCount,
                lrate, regularized, 0, maxIteration, dimnsn[0], dimnsn[1], ua, ia, showProgress);
            recmmd.test = testMatrix;
            recmmd.buildModel(rateMatrix);

            //evaluation
            EvaluationMetrics metric = recmmd.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            FileUtil.writeAsAppend(resultDir + "zBC",
                "fc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized + "\tk: "
                        + dimnsn[0] + "\tl: " + dimnsn[1] + "\n" + metric.printOneLine() + "\n");
            stat.addValue(metric.getRMSE());
        }
        FileUtil.writeAsAppend(
            resultDir + "zBC",
            "Mean: " + stat.getMean() + "\tSD:"
                    + String.format("%.6f", stat.getStandardDeviation()) + "\n");
    }

    public static void DynamicConstrainedRSVD(int[] featureCounts, String clusterDir,
                                              double balanced, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ia = new int[itemCount];
        int[] ua = new int[userCount];
        int[] dimnsn = readBiAssigmnt(ua, ia, clusterDir, rootDir);

        //build model
        for (int featureCount : featureCounts) {
            DynamicCRSVD recmmd = new DynamicCRSVD(userCount, itemCount, maxValue, minValue,
                featureCount, lrate, regularized, 0, maxIteration, dimnsn[0], dimnsn[1], ua, ia,
                balanced, showProgress);
            recmmd.test = testMatrix;
            recmmd.buildModel(rateMatrix);

            //evaluation
            EvaluationMetrics metric = recmmd.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            FileUtil.writeAsAppend(resultDir + "zDC", "fc: " + featureCount + "\tlr: " + lrate
                                                      + "\tr: " + regularized + "\tk: " + dimnsn[0]
                                                      + "\tl: " + dimnsn[1] + "\tb: " + balanced
                                                      + "\n" + metric.printOneLine() + "\n");
        }
    }

    public static int[] readBiAssigmnt(int[] ua, int[] ia, String clusterDir, String rootDir) {
        String settingFile = rootDir + clusterDir + "SETTING";
        String rowMappingFile = rootDir + clusterDir + "RM";
        String colMappingFile = rootDir + clusterDir + "CM";

        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        BlockMatrix blockMatrix = MatrixFileUtil.readEmptyBlock(settingFile, rowMappingFile,
            colMappingFile, rowAssig, colAssig);
        int[] biSize = new int[2];

        if (ua != null) {
            int[] userClusterBounds = blockMatrix.rowBound();
            for (Entry<Integer, Integer> uIndex : rowAssig.entrySet()) {
                for (int i = 0; i < userClusterBounds.length; i++) {
                    if (uIndex.getValue() < userClusterBounds[i]) {
                        ua[uIndex.getKey()] = i;
                        break;
                    }
                }
            }
            biSize[0] = userClusterBounds.length;
        }

        if (ia != null) {
            int[] itemClusterBounds = blockMatrix.structure()[0];
            for (Entry<Integer, Integer> iIndex : colAssig.entrySet()) {
                for (int i = 0; i < itemClusterBounds.length; i++) {
                    if (iIndex.getValue() < itemClusterBounds[i]) {
                        ia[iIndex.getKey()] = i;
                        break;
                    }
                }
            }
            biSize[1] = itemClusterBounds.length;
        }

        return biSize;
    }
}
