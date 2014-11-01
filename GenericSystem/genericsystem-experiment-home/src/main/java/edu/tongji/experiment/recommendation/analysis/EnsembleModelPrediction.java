/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.analysis;

import prea.util.MatrixFileUtil;
import prea.util.SimpleEvaluationMetrics;
import edu.tongji.data.SparseMatrix;
import edu.tongji.ml.matrix.WeigtedRSVD;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;

/**
 * 
 * @author Hanke Chen
 * @version $Id: MixturePrediction.java, v 0.1 2014-10-31 下午3:06:22 chench Exp $
 */
public class EnsembleModelPrediction {

    /** file with setting data*/
    public static String    settingFile        = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/SETTING";

    /** file with row mapping data*/
    public static String    rowMappingFile     = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/RM";

    /** file with column mapping data*/
    public static String    colMappingFile     = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/CM";

    /** file with training data*/
    public static String    globalTrainingFile = "E:/MovieLens/ml-10M100K/3/trainingset";

    /** file with testing data*/
    public static String    localTrainingFile  = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/trainingset_0_0";

    /** file with training data*/
    public static String    globalPrediction   = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/global_0_0";

    /** file with testing data*/
    public static String    localPrediction    = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/local_0_0";

    /** file with testing data*/
    public static String    testFile           = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/testingset_0_0";

    /** the number of rows*/
    public final static int rowCount           = 69878;

    /** the number of columns*/
    public final static int colCount           = 10677;

    /** the content parser w.r.t certain dataset*/
    public static Parser    parser             = new MovielensRatingTemplateParser();

    /** the row index of the target block*/
    public static int       iBlock             = 0;

    /** the column index of the target block*/
    public static int       jBlock             = 0;

    public static double    maxValue           = 5;

    public static double    minValue           = 0.5;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //global
        SparseMatrix globalRateMatrix = MatrixFileUtil.read(globalTrainingFile, rowCount, colCount,
            parser);
        SparseMatrix globalModel = MatrixFileUtil
            .read(globalPrediction, rowCount, colCount, parser);
        WeigtedRSVD global = new WeigtedRSVD(rowCount, colCount, maxValue, minValue, 0, 0, 0, 0, 0);
        global.getItemWeights(globalRateMatrix);
        global.getUserWeights(globalRateMatrix);

        //local
        SparseMatrix localRateMatrix = MatrixFileUtil.toGlobalIndex(localTrainingFile, settingFile,
            rowMappingFile, colMappingFile, rowCount, colCount, parser, iBlock, jBlock);
        SparseMatrix localModel = MatrixFileUtil.toGlobalIndex(localPrediction, settingFile,
            rowMappingFile, colMappingFile, rowCount, colCount, parser, iBlock, jBlock);
        WeigtedRSVD local = new WeigtedRSVD(rowCount, colCount, maxValue, minValue, 0, 0, 0, 0, 0);
        local.getItemWeights(globalRateMatrix);
        local.getUserWeights(globalRateMatrix);

        SparseMatrix prediction = new SparseMatrix(rowCount, colCount);
        for (int u = 0; u < localRateMatrix.length()[0]; u++) {
            int[] indexList = localRateMatrix.getRowRef(u).indexList();
            if (indexList == null) {
                continue;
            }

            for (int v : indexList) {
                double gEst = globalModel.getValue(u, v) < minValue ? minValue : globalModel
                    .getValue(u, v);
                double gW = global.getWeight(u, v, Double.valueOf(gEst / 0.5).intValue() - 1);
                double lEst = localModel.getValue(u, v) < minValue ? minValue : localModel
                    .getValue(u, v);
                double lW = local.getWeight(u, v, Double.valueOf(lEst / 0.5).intValue() - 1);
                double sumW = gW + lW;
                double predicted = (gW * gEst + lEst * lW) / sumW;

                prediction.setValue(u, v, predicted);
            }
        }

        //testing
        SparseMatrix testMatrix = MatrixFileUtil.toGlobalIndex(testFile, settingFile,
            rowMappingFile, colMappingFile, rowCount, colCount, parser, iBlock, jBlock);
        SimpleEvaluationMetrics metric = new SimpleEvaluationMetrics(testMatrix, prediction,
            maxValue, minValue);
        System.out.println(metric.printOneLine());

    }
}
