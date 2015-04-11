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
import edu.tongji.ml.KMeansPlusPlusUtil;
import edu.tongji.util.FileUtil;
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
    /** file to store the original data and cocluster directory, make sure the data is compact.*/
    public final static String[] ROOTDIRS      = { "E:/MovieLens/ml-10M100K/1/"
                                               //                                                   ,"E:/MovieLens/ml-1m/2/", "E:/MovieLens/ml-10M100K/1/" 
                                               };
    /** the number of rows 69878 6040*/
    public final static int      rowCount      = 69878;
    /** the number of columns 10677 3706*/
    public final static int      colCount      = 10677;
    /** the type of distance involved*/
    public final static int[]    DIVERGENCE    = { KMeansPlusPlusUtil.KL_DISTANCE,
                                               //            KMeansPlusPlusUtil.SQUARE_EUCLIDEAN_DISTANCE, KMeansPlusPlusUtil.SINE_DISTANCE 
                                               };
    /** the nickname of distance type involved*/
    public final static String[] DIR           = { "KL"
                                               //"EU", "SI" 
                                               };
    /** the number of  row_column classes*/
    public final static String[] DIMEN_SETTING = { "5_5" };
    /** the maximum number of iterations*/
    public final static int      maxIteration  = 70;
    /** Maximum value of rating, existing in the dataset. */
    public final static double   maxValue      = 5.0;
    /** Minimum value of rating, existing in the dataset. */
    public final static double   minValue      = 0.5;

    /** logger */
    private final static Logger  logger        = Logger
                                                   .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        distriDataSpase();
    }

    public static void normDataSpace() {
        // coclustering
        for (String rootDir : ROOTDIRS) {
            // load dataset
            String sourceFile = rootDir + "trainingset";

            String targetCoclusterRoot = rootDir + "Kmeanspp/";
            SparseMatrix rateMatrix = MatrixFileUtil.read(sourceFile, rowCount, colCount, null);
            //                      String targetCoclusterRoot = rootDir + "KmeansppLimited/";
            //                        SparseMatrix rateMatrix = MatrixFileUtil.read(sourceFile, rowCount, colCount, parser,
            //                            1.0f);
            LoggerUtil.info(logger, (new StringBuilder("0. load dataset: ")).append(sourceFile));

            for (int diverIndx = 0; diverIndx < DIVERGENCE.length; diverIndx++) {
                for (String dimsn : DIMEN_SETTING) {
                    String[] dimenVal = dimsn.split("\\_");
                    int k = Integer.valueOf(dimenVal[0]);
                    int l = Integer.valueOf(dimenVal[1]);
                    String settingFile = (new StringBuilder(targetCoclusterRoot))
                        .append(DIR[diverIndx]).append('_').append(k).append('_').append(l)
                        .append(FileUtil.UNION_DIR_SEPERATOR).append("SETTING").toString();
                    String rowMappingFile = (new StringBuilder(targetCoclusterRoot))
                        .append(DIR[diverIndx]).append('_').append(k).append('_').append(l)
                        .append(FileUtil.UNION_DIR_SEPERATOR).append("RM").toString();
                    String colMappingFile = (new StringBuilder(targetCoclusterRoot))
                        .append(DIR[diverIndx]).append('_').append(k).append('_').append(l)
                        .append(FileUtil.UNION_DIR_SEPERATOR).append("CM").toString();

                    LoggerUtil.info(logger,
                        (new StringBuilder("1. start to cocluster. ")).append(DIR[diverIndx])
                            .append('_').append(k).append('_').append(l));
                    doCocluster(rateMatrix, settingFile, rowMappingFile, colMappingFile,
                        DIVERGENCE[diverIndx], k, l);
                }
            }
        }
    }

    public static void distriDataSpase() {
        // coclustering
        for (String rootDir : ROOTDIRS) {
            // load dataset
            String sourceFile = rootDir + "trainingset";

            String targetCoclusterRoot = rootDir + "Kmeanspp/";
            SparseMatrix[] distriInfoMatrix = MatrixFileUtil.readDistriInfo(sourceFile, rowCount,
                colCount, maxValue, minValue, null);
            LoggerUtil.info(logger, (new StringBuilder("0. load dataset: ")).append(sourceFile));

            for (int diverIndx = 0; diverIndx < DIVERGENCE.length; diverIndx++) {
                for (String dimsn : DIMEN_SETTING) {
                    String[] dimenVal = dimsn.split("\\_");
                    int k = Integer.valueOf(dimenVal[0]);
                    int l = Integer.valueOf(dimenVal[1]);

                    String settingFile = (new StringBuilder(targetCoclusterRoot))
                        .append(DIR[diverIndx]).append('_').append(k).append('_').append(l)
                        .append(FileUtil.UNION_DIR_SEPERATOR).append("SETTING").toString();
                    String rowMappingFile = (new StringBuilder(targetCoclusterRoot))
                        .append(DIR[diverIndx]).append('_').append(k).append('_').append(l)
                        .append(FileUtil.UNION_DIR_SEPERATOR).append("RM").toString();
                    String colMappingFile = (new StringBuilder(targetCoclusterRoot))
                        .append(DIR[diverIndx]).append('_').append(k).append('_').append(l)
                        .append(FileUtil.UNION_DIR_SEPERATOR).append("CM").toString();

                    LoggerUtil.info(logger,
                        (new StringBuilder("1. start to cocluster. ")).append(DIR[diverIndx])
                            .append('_').append(k).append('_').append(l));
                    doCocluster(distriInfoMatrix, settingFile, rowMappingFile, colMappingFile,
                        DIVERGENCE[diverIndx], k, l);
                }
            }
        }
    }

    /**
     * 
     * 
     * @param rateMatrix
     * @param settingFile
     * @param rowMappingFile
     * @param colMappingFile
     * @param diverType
     * @param K
     * @param L
     */
    public static void doCocluster(SparseMatrix rateMatrix, String settingFile,
                                   String rowMappingFile, String colMappingFile, int diverType,
                                   int K, int L) {
        //divide rows
        LoggerUtil.info(logger, "2.divide rows.");
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>(); //Key : Val, OldIndx : NewIndx
        int[] rowBound = new int[K];
        MatrixKMsUtil.divide(rateMatrix, K, maxIteration, diverType, rowAssig, rowBound);

        //divide columns
        LoggerUtil.info(logger, "3.divide columns.");
        rateMatrix.selfTranspose();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        int[] colBound = new int[L];
        MatrixKMsUtil.divide(rateMatrix, L, maxIteration, diverType, colAssig, colBound);

        //write cocluster structure
        LoggerUtil.info(logger, "4. write cocluster structure setting file.");
        rateMatrix.selfTranspose();
        MatrixFileUtil.writeStructureSetting(settingFile, rowMappingFile, colMappingFile, K, L,
            rowBound, colBound, rowAssig, colAssig);
    }

    public static void doCocluster(SparseMatrix[] distriInfoMatrix, String settingFile,
                                   String rowMappingFile, String colMappingFile, int diverType,
                                   int K, int L) {
        //divide rows
        LoggerUtil.info(logger, "2.divide rows.");
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>(); //Key : Val, OldIndx : NewIndx
        int[] rowBound = new int[K];
        MatrixKMsUtil.divide(distriInfoMatrix[0], K, maxIteration, diverType, rowAssig, rowBound);

        //divide columns
        LoggerUtil.info(logger, "3.divide columns.");
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        int[] colBound = new int[L];
        MatrixKMsUtil.divide(distriInfoMatrix[1], L, maxIteration, diverType, colAssig, colBound);

        //write cocluster structure
        LoggerUtil.info(logger, "4. write cocluster structure setting file.");
        MatrixFileUtil.writeStructureSetting(settingFile, rowMappingFile, colMappingFile, K, L,
            rowBound, colBound, rowAssig, colAssig);
    }
}
