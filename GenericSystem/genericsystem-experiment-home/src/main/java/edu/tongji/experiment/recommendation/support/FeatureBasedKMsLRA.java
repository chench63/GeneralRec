/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import prea.util.MatrixFileUtil;
import recommender.dataset.MatrixKMsUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.ml.KMeansUtil;
import edu.tongji.ml.matrix.WeigtedRSVD;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: FeatureBasedKMsLRA.java, v 0.1 2014-10-15 下午4:53:41 chench Exp $
 */
public class FeatureBasedKMsLRA {

    //==========================
    //      Common variable
    //==========================
    /** file to store the original data, make sure the data is compact.*/
    protected final static String SOURCE_FILE      = "E:/MovieLens/ml-10M100K/trainingset";

    /** file to persist the new data */
    public final static String    ROW_MAPPING_FILE = "E:/MovieLens/ml-10M100K/RM";

    /** file to persist the new data */
    public final static String    COL_MAPPING_FILE = "E:/MovieLens/ml-10M100K/CM";

    /** file to persist the setting data */
    public final static String    SETTING_FILE     = "E:/MovieLens/ml-10M100K/SETTING";

    /** The parser to parse the dataset file  **/
    public final static Parser    parser           = new MovielensRatingTemplateParser();

    /** the number of rows*/
    public final static int       userCount        = 69878;

    /** the number of columns*/
    public final static int       itemCount        = 10677;

    //==========================
    //      K-means variable
    //==========================

    /** the type of distance involved*/
    public final static int       DISTANCE_TYPE    = KMeansUtil.ANGLE_DISTANCE;

    /** the number of classes*/
    public final static int       K                = 2;

    /** the number of classes*/
    public final static int       L                = 2;

    /** the maximum number of iterations*/
    public final static int       maxIter          = 10;

    /** logger */
    private final static Logger   logger           = Logger
                                                       .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //read data, compute user and item features
        LoggerUtil.info(logger, "1. read data, compute user and item features");
        SparseMatrix rateMatrix = MatrixFileUtil.read(SOURCE_FILE, userCount, itemCount, parser);

        double maxValue = 5;
        double minValue = 0.5;
        int featureCount = 20;
        double learningRate = 0.005;
        double regularization = 0.1;
        int maxIteration = 100;
        WeigtedRSVD recommender = new WeigtedRSVD(userCount, itemCount, maxValue, minValue,
            featureCount, learningRate, regularization, 0, maxIteration, 1.45f, 0.5f);
        recommender.buildModel(rateMatrix);

        //divide rows
        LoggerUtil.info(logger, "2. divide rows");
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        int[] rowBound = new int[K];
        MatrixKMsUtil.divide(recommender.getU(), K, maxIter, DISTANCE_TYPE, rowAssig, rowBound);
        //        MatrixKMsUtil.divideAsDensity(rateMatrix, recommender.getU(), K_Row, maxIter,
        //            DISTANCE_TYPE, rowAssig, rowBound);

        //divide cols
        LoggerUtil.info(logger, "3. divide cols");
        recommender.getV().selfTranspose();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        int[] colBound = new int[L];
        MatrixKMsUtil.divide(recommender.getV(), L, maxIter, DISTANCE_TYPE, colAssig, colBound);
        //        MatrixKMsUtil.divideAsDensity(rateMatrix, recommender.getV(), K_Col, maxIter,
        //            DISTANCE_TYPE, colAssig, colBound);

        //write cocluster structure
        MatrixFileUtil.writeStructureSetting(SETTING_FILE, ROW_MAPPING_FILE, COL_MAPPING_FILE, K,
            L, rowBound, colBound, rowAssig, colAssig);

    }
}
