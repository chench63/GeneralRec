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
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.LoggerUtil;

/**
 * Using K-Means to construct Low-Rank Approximation
 * 
 * @author Hanke Chen
 * @version $Id: KMsLRA.java, v 0.1 2014-10-14 下午7:13:12 chench Exp $
 */
public class KMsLRA {

    //==========================
    //      Common variable
    //==========================
    /** file to store the original data, make sure the data is compact.*/
    public final static String  SOURCE_FILE      = "E:/MovieLens//ml-10M100K/3/trainingset";

    /** file to persist the new data */
    public final static String  ROW_MAPPING_FILE = "E:/MovieLens/ml-10M100K/3/KMeans/LeastAngle/RM";

    /** file to persist the new data */
    public final static String  COL_MAPPING_FILE = "E:/MovieLens/ml-10M100K/3/KMeans/LeastAngle/CM";

    /** file to persist the setting data */
    public final static String  SETTING_FILE     = "E:/MovieLens/ml-10M100K/3/KMeans/LeastAngle/SETTING";

    /** The parser to parse the dataset file  **/
    public final static Parser  parser           = new MovielensRatingTemplateParser();

    /** the number of rows*/
    public final static int     rowCount         = 69878;

    /** the number of columns*/
    public final static int     colCount         = 10677;

    /** the type of distance involved*/
    public final static int     DISTANCE_TYPE    = KMeansUtil.ANGLE_DISTANCE;

    /** the number of classes*/
    public final static int     K                = 2;

    /** the number of classes*/
    public final static int     L                = 2;

    /** the maximum number of iterations*/
    public final static int     maxIteration     = 10;

    /** logger */
    private final static Logger logger           = Logger
                                                     .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //read matrix
        LoggerUtil.info(logger, "1. read data.");
        SparseMatrix rateMatrix = MatrixFileUtil.read(SOURCE_FILE, rowCount, colCount, parser);

        //divide rows
        LoggerUtil.info(logger, "2.divide rows.");
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        int[] rowBound = new int[K];
        MatrixKMsUtil.divide(rateMatrix, K, maxIteration, DISTANCE_TYPE, rowAssig, rowBound);

        //divide columns
        LoggerUtil.info(logger, "3.divide columns.");
        rateMatrix.selfTranspose();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        int[] colBound = new int[L];
        MatrixKMsUtil.divide(rateMatrix, L, maxIteration, DISTANCE_TYPE, colAssig, colBound);

        //write cocluster structure
        LoggerUtil.info(logger, "4. write cocluster structure setting file.");
        MatrixFileUtil.writeStructureSetting(SETTING_FILE, ROW_MAPPING_FILE, COL_MAPPING_FILE, K,
            L, rowBound, colBound, rowAssig, colAssig);
    }
}
