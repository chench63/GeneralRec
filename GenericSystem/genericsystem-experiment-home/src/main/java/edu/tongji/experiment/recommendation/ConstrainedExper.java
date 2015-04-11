/**
 * Tongji Edu.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
    public final static String rootDir      = "E:/MovieLens/ml-10M100K/1/";
    public final static String clusterDir   = "Kmeanspp/KL_2_2/";

    /** The number of users. 943 6040 69878*/
    public final static int    userCount    = 69878;
    /** The number of items. 1682 3706 10677*/
    public final static int    itemCount    = 10677;
    /** Maximum value of rating, existing in the dataset. */
    public final static double maxValue     = 5.0;
    /** Minimum value of rating, existing in the dataset. */
    public final static double minValue     = 0.5;
    public final static int    featureCount = 20;
    public final static double lrate        = 0.001;
    public final static double regularized  = 0.02;
    public final static int    maxIteration = 100;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        RSVD();

        //        ItemConstrainedRSVD();

        //        BiConstrainedRSVD();

        //                for (int i = 1; i <= 3; i++) {
        //                    DynamicConstrainedRSVD(0.1 * i);
        //                }
        //        for (int i = 4; i <= 6; i++) {
        //            DynamicConstrainedRSVD(0.1 * i);
        //        }
        for (int i = 7; i <= 9; i++) {
            DynamicConstrainedRSVD(0.1 * i);
        }
        //        DynamicConstrainedRSVD(1.0);
    }

    public static void RSVD() {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        //build model
        RegularizedSVD recmmd = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
            featureCount, lrate, regularized, 0, maxIteration, false);
        recmmd.test = testMatrix;
        recmmd.buildModel(rateMatrix);

        //evaluation
        EvaluationMetrics metric = recmmd.evaluate(testMatrix);
        System.out.println(metric.printMultiLine());
        FileUtil.writeAsAppend("E://zRSVD", "fc: " + featureCount + "\tlr: " + lrate + "\tr: "
                                            + regularized + "\n" + metric.printOneLine() + "\n");
    }

    public static void UserConstrainedRSVD() {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ua = new int[itemCount];
        int[] dimnsn = readBiAssigmnt(ua, null);

        //build model
        UserConstraintRSVD recmmd = new UserConstraintRSVD(userCount, itemCount, maxValue,
            minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[0], ua, false);
        recmmd.test = testMatrix;
        recmmd.buildModel(rateMatrix);

        //evaluation
        EvaluationMetrics metric = recmmd.evaluate(testMatrix);
        System.out.println(metric.printMultiLine());
        FileUtil.writeAsAppend("E://zIC", "fc: " + featureCount + "\tlr: " + lrate + "\tr: "
                                          + regularized + "\n" + metric.printOneLine() + "\n");
    }

    public static void ItemConstrainedRSVD() {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ia = new int[userCount];
        int[] dimnsn = readBiAssigmnt(null, ia);

        //build model
        ItemConstraintRSVD recmmd = new ItemConstraintRSVD(userCount, itemCount, maxValue,
            minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[1], ia, false);
        recmmd.test = testMatrix;
        recmmd.buildModel(rateMatrix);

        //evaluation
        EvaluationMetrics metric = recmmd.evaluate(testMatrix);
        System.out.println(metric.printMultiLine());
        FileUtil.writeAsAppend("E://zIC", "fc: " + featureCount + "\tlr: " + lrate + "\tr: "
                                          + regularized + "\n" + metric.printOneLine() + "\n");
    }

    public static void BiConstrainedRSVD() {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ua = new int[itemCount];
        int[] ia = new int[userCount];
        int[] dimnsn = readBiAssigmnt(ua, ia);

        //build model
        FCRSVD recmmd = new FCRSVD(userCount, itemCount, maxValue, minValue, featureCount, lrate,
            regularized, 0, maxIteration, dimnsn[0], dimnsn[1], ia, ua, false);
        recmmd.test = testMatrix;
        recmmd.buildModel(rateMatrix);

        //evaluation
        EvaluationMetrics metric = recmmd.evaluate(testMatrix);
        System.out.println(metric.printMultiLine());
        FileUtil.writeAsAppend("E://zBC", "fc: " + featureCount + "\tlr: " + lrate + "\tr: "
                                          + regularized + "\tk: " + dimnsn[0] + "\tl: " + dimnsn[1]
                                          + "\n" + metric.printOneLine() + "\n");
    }

    public static void DynamicConstrainedRSVD(double balanced) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ua = new int[itemCount];
        int[] ia = new int[userCount];
        int[] dimnsn = readBiAssigmnt(ua, ia);

        //build model
        DynamicCRSVD recmmd = new DynamicCRSVD(userCount, itemCount, maxValue, minValue,
            featureCount, lrate, regularized, 0, maxIteration, dimnsn[0], dimnsn[1], ia, ua,
            balanced, false);
        recmmd.test = testMatrix;
        recmmd.buildModel(rateMatrix);

        //evaluation
        EvaluationMetrics metric = recmmd.evaluate(testMatrix);
        System.out.println(metric.printMultiLine());
        FileUtil.writeAsAppend("E://zBC", "fc: " + featureCount + "\tlr: " + lrate + "\tr: "
                                          + regularized + "\tk: " + dimnsn[0] + "\tl: " + dimnsn[1]
                                          + "\tb: " + balanced + "\n" + metric.printOneLine()
                                          + "\n");
    }

    public static int[] readBiAssigmnt(int[] ua, int[] ia) {
        String settingFile = rootDir + clusterDir + "SETTING";
        String rowMappingFile = rootDir + clusterDir + "RM";
        String colMappingFile = rootDir + clusterDir + "CM";

        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        BlockMatrix blockMatrix = MatrixFileUtil.readEmptyBlock(settingFile, rowMappingFile,
            colMappingFile, rowAssig, colAssig);
        int[] biSize = new int[2];

        if (ia != null) {
            int[] userClusterBounds = blockMatrix.rowBound();
            for (Entry<Integer, Integer> uIndex : rowAssig.entrySet()) {
                for (int i = 0; i < userClusterBounds.length; i++) {
                    if (uIndex.getValue() < userClusterBounds[i]) {
                        ia[uIndex.getKey()] = i;
                        break;
                    }
                }
            }
            biSize[0] = userClusterBounds.length;
        }

        if (ua != null) {
            int[] itemClusterBounds = blockMatrix.structure()[0];
            for (Entry<Integer, Integer> iIndex : colAssig.entrySet()) {
                for (int i = 0; i < itemClusterBounds.length; i++) {
                    if (iIndex.getValue() < itemClusterBounds[i]) {
                        ua[iIndex.getKey()] = i;
                        break;
                    }
                }
            }
            biSize[1] = itemClusterBounds.length;
        }

        return biSize;
    }
}
