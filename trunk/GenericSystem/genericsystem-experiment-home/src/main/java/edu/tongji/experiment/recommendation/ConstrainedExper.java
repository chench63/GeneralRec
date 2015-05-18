/**
 * Tongji Edu.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.ml.matrix.DynamicCRSVD;
import edu.tongji.ml.matrix.FCRSVD;
import edu.tongji.ml.matrix.ItemConstraintRSVD;
import edu.tongji.ml.matrix.RegularizedSVD;
import edu.tongji.ml.matrix.UserConstraintRSVD;
import edu.tongji.util.FileUtil;
import prea.util.ClusteringInformationUtil;
import prea.util.EvaluationMetrics;
import prea.util.MatrixFileUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: ConstrainedExper.java, v 0.1 2015-3-31 上午9:40:57 chench Exp $
 */
public class ConstrainedExper {

    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]      rootDirs     = { "E:/MovieLens/zWarmStart/ml-10M100K/2/",
            "E:/MovieLens/zWarmStart/ml-10M100K/3/", "E:/MovieLens/zWarmStart/ml-10M100K/4/",
            "E:/MovieLens/zWarmStart/ml-10M100K/5/" };
    /** The number of users. 943 6040 69878  480189*/
    public final static int     userCount    = 69878;
    /** The number of items. 1682 3706 10677 17770*/
    public final static int     itemCount    = 10677;
    public final static double  maxValue     = 5.0;
    public final static double  minValue     = 0.5;
    public final static double  lrate        = 0.01;
    public final static double  regularized  = 0.001;
    public final static int     maxIteration = 100;
    public final static boolean showProgress = false;

    public final static String  resultDir    = "E:/MovieLens/zWarmStart/";

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
        for (int featureCount : featureCounts) {
            RegularizedSVD recmmd = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
                featureCount, lrate, regularized, 0, maxIteration, showProgress);
            recmmd.tMatrix = testMatrix;
            recmmd.buildModel(rateMatrix);

            //evaluation
            EvaluationMetrics metric = recmmd.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            FileUtil.writeAsAppend(
                resultDir + "zRSVD",
                "fc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized + "\n"
                        + metric.printOneLine() + "\n");
        }
    }

    public static void UserConstrainedRSVD(int[] featureCounts, String clusterDir, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ua = new int[userCount];
        int[] dimnsn = ClusteringInformationUtil.readBiAssigmnt(ua, null, clusterDir, rootDir);

        //build model
        for (int featureCount : featureCounts) {
            UserConstraintRSVD recmmd = new UserConstraintRSVD(userCount, itemCount, maxValue,
                minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[0], ua,
                showProgress);
            recmmd.tMatrix = testMatrix;
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
        int[] dimnsn = ClusteringInformationUtil.readBiAssigmnt(null, ia, clusterDir, rootDir);

        //build model
        for (int featureCount : featureCounts) {
            ItemConstraintRSVD recmmd = new ItemConstraintRSVD(userCount, itemCount, maxValue,
                minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[1], ia,
                showProgress);
            recmmd.tMatrix = testMatrix;
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
        int[] dimnsn = ClusteringInformationUtil.readBiAssigmnt(ua, ia, clusterDir, rootDir);

        //build model
        for (int featureCount : featureCounts) {
            FCRSVD recmmd = new FCRSVD(userCount, itemCount, maxValue, minValue, featureCount,
                lrate, regularized, 0, maxIteration, dimnsn[0], dimnsn[1], ua, ia, showProgress);
            recmmd.tMatrix = testMatrix;
            recmmd.buildModel(rateMatrix);

            //evaluation
            EvaluationMetrics metric = recmmd.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            FileUtil.writeAsAppend(resultDir + "zBC",
                "fc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized + "\tk: "
                        + dimnsn[0] + "\tl: " + dimnsn[1] + "\n" + metric.printOneLine() + "\n");
        }
    }

    public static void BiConstrainedRSVDSaving(int[] featureCounts, String clusterDir,
                                               String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        MatlabFasionSparseMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount,
            20 * 1000 * 1000, null);
        MatlabFasionSparseMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount,
            20 * 1000 * 1000, null);

        int[] ua = new int[userCount];
        int[] ia = new int[itemCount];
        int[] dimnsn = ClusteringInformationUtil.readBiAssigmnt(ua, ia, clusterDir, rootDir);

        //build model
        for (int featureCount : featureCounts) {
            FCRSVD recmmd = new FCRSVD(userCount, itemCount, maxValue, minValue, featureCount,
                lrate, regularized, 0, maxIteration, dimnsn[0], dimnsn[1], ua, ia, showProgress);
            recmmd.buildModel(rateMatrix, testMatrix);

            //evaluation
            double rmse = recmmd.evaluate(testMatrix);
            System.out.println(rmse);
        }
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
        int[] dimnsn = ClusteringInformationUtil.readBiAssigmnt(ua, ia, clusterDir, rootDir);

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

}
