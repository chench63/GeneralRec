/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import org.apache.log4j.Logger;

import prea.util.EvaluationMetrics;
import prea.util.MatrixFileUtil;
import prea.util.MatrixInformationUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: FastProbabilisicRS.java, v 0.1 2014-12-30 上午10:54:53 chench Exp $
 */
public class FastProbabilisicRS {

    public final static String[] rootDirs       = { "E:/MovieLens/ml-10M100K/1/" };

    public final static int[]    dimensionality = { 69878, 10677 };

    public final static double[] ratingDomain   = { 5.0, 0.5 };

    private static final Logger  logger         = Logger
                                                    .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        String trainFile = rootDirs[0] + "trainingset";
        String testFile = rootDirs[0] + "testingset";
        SparseMatrix trainMatrix = MatrixFileUtil.read(trainFile, dimensionality[0],
            dimensionality[1], null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, dimensionality[0],
            dimensionality[1], null);
        LoggerUtil.info(logger, "1. loading dataset. Train: " + trainMatrix.itemCount() + " Test: "
                                + testMatrix.itemCount());

        SparseMatrix userFeature = new SparseMatrix(dimensionality[0], dimensionality[1]);
        SparseMatrix itemFeature = new SparseMatrix(dimensionality[0], dimensionality[1]);
        LoggerUtil.info(logger, "2. building models. ");
        build(trainMatrix, userFeature, itemFeature);
        LoggerUtil.info(logger, "3. evaluating models. ");
        evaluation(testMatrix, userFeature, itemFeature);

    }

    public static void build(SparseMatrix trainMatrix, SparseMatrix userFeature,
                             SparseMatrix itemFeature) {
        // common variables
        int featureCount = (int) (ratingDomain[0] / ratingDomain[1]);

        // userFeature
        int userCount = trainMatrix.length()[0];
        for (int i = 0; i < userCount; i++) {
            int[] featureDis = new int[featureCount];
            int total = featureCount;

            SparseVector Ri = trainMatrix.getRowRef(i);
            int[] itemList = Ri.indexList();

            if (itemList != null) {
                for (int j : itemList) {
                    int index = (int) (trainMatrix.getValue(i, j) / ratingDomain[1] - 1);
                    featureDis[index]++;
                    total++;
                }
            }

            for (int k = 0; k < featureCount; k++) {
                // Laplace Smooth
                userFeature.setValue(i, k, (featureDis[k] + 1) * 1.0 / total);
            }
        }

        //itemFeature
        int itemCount = trainMatrix.length()[1];
        for (int j = 0; j < itemCount; j++) {
            int[] featureDis = new int[featureCount];
            int total = featureCount;

            SparseVector Cj = trainMatrix.getColRef(j);
            int[] userList = Cj.indexList();

            if (userList != null) {
                for (int i : userList) {
                    int index = (int) (trainMatrix.getValue(i, j) / ratingDomain[1] - 1);
                    featureDis[index]++;
                    total++;
                }
            }

            for (int k = 0; k < featureCount; k++) {
                // Laplace Smooth
                userFeature.setValue(j, k, (featureDis[k] + 1) * 1.0 / total);
            }
        }
    }

    public static void evaluation(SparseRowMatrix testMatrix, SparseMatrix userFeature,
                                  SparseMatrix itemFeature) {
        // common variables
        int featureCount = (int) (ratingDomain[0] / ratingDomain[1]);

        SparseRowMatrix prediction = new SparseRowMatrix(dimensionality[0], dimensionality[1]);
        int userCount = testMatrix.length()[0];
        for (int i = 0; i < userCount; i++) {
            SparseVector Ri = testMatrix.getRowRef(i);
            int[] itemList = Ri.indexList();
            if (itemList == null) {
                continue;
            }

            for (int j : itemList) {

                // prediction
                int pivot = -1;
                double max = Double.MIN_VALUE;

                for (int k = 0; k < featureCount; k++) {
                    double prob = userFeature.getValue(i, k) * 0.15 + itemFeature.getValue(j, k)
                                  * 0.45;
                    if (prob > max) {
                        pivot = k;
                        max = prob;
                    }
                }

                double predicted = (pivot + 1) * ratingDomain[1];
                prediction.setValue(i, j, predicted);
            }
        }

        //Evaluation
        EvaluationMetrics metric = new EvaluationMetrics(testMatrix, prediction, ratingDomain[0],
            ratingDomain[1]);
        System.out.println(metric.printMultiLine());
        LoggerUtil.info(
            logger,
            "4. RMSE: " + metric.getRMSE()
                    + MatrixInformationUtil.RMSEAnalysis(testMatrix, prediction));
    }
}
