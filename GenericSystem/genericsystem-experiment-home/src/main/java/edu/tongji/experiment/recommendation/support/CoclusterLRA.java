/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import prea.util.MatrixFileUtil;
import recommender.dataset.MatrixCoclusterUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.ml.CoclusterUtil;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: CoclusterLRA.java, v 0.1 2014-10-28 下午12:37:36 chench Exp $
 */
public class CoclusterLRA {

    //==========================
    //      Common variable
    //==========================
    /** file to store the original data and cocluster directory, make sure the data is compact.*/
    public final static String[] ROOTDIRS     = { "E:/MovieLens/ml-10M100K/1/",
            "E:/MovieLens/ml-10M100K/2/", "E:/MovieLens/ml-10M100K/3/",
            "E:/MovieLens/ml-10M100K/5/"     };

    /** The parser to parse the dataset file  **/
    public final static Parser   parser       = new MovielensRatingTemplateParser();

    public final static int[]    DIVERGENCE   = { CoclusterUtil.EUCLIDEAN_DIVERGENCE,
            CoclusterUtil.I_DIVERGENCE       };

    public final static String[] DIR          = { "EW", "IW" };

    public final static int[]    CONSTRAINTS  = { CoclusterUtil.C_1, CoclusterUtil.C_3,
            CoclusterUtil.C_4, CoclusterUtil.C_6 };

    public final static int[]    K            = { 2, 3 };

    public final static int[]    L            = { 2 };

    /** the number of rows*/
    public final static int      rowCount     = 69878;

    /** the number of columns*/
    public final static int      colCount     = 10677;

    /** the maximum number of iterations*/
    public final static int      maxIteration = 6;

    /** logger */
    private final static Logger  logger       = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        // coclustering
        for (String rootDir : ROOTDIRS) {
            // load dataset
            String sourceFile = rootDir + "trainingset";
            String targetCoclusterRoot = rootDir + "Cocluster/";
            SparseMatrix rateMatrix = MatrixFileUtil.read(sourceFile, rowCount, colCount, parser);
            LoggerUtil.info(logger, (new StringBuilder("0. load dataset: ")).append(sourceFile));

            for (int diverIndx = 0; diverIndx < DIVERGENCE.length; diverIndx++) {
                for (int consts : CONSTRAINTS) {
                    for (int k : K) {
                        for (int l : L) {
                            String settingFile = (new StringBuilder(targetCoclusterRoot))
                                .append(DIR[diverIndx]).append(consts).append('_').append(k)
                                .append('_').append(l).append(FileUtil.UNION_DIR_SEPERATOR)
                                .append("SETTING").toString();
                            String rowMappingFile = (new StringBuilder(targetCoclusterRoot))
                                .append(DIR[diverIndx]).append(consts).append('_').append(k)
                                .append('_').append(l).append(FileUtil.UNION_DIR_SEPERATOR)
                                .append("RM").toString();
                            String colMappingFile = (new StringBuilder(targetCoclusterRoot))
                                .append(DIR[diverIndx]).append(consts).append('_').append(k)
                                .append('_').append(l).append(FileUtil.UNION_DIR_SEPERATOR)
                                .append("CM").toString();

                            LoggerUtil.info(logger, (new StringBuilder("1. start to cocluster. "))
                                .append(DIR[diverIndx]).append(consts).append('_').append(k)
                                .append('_').append(l));
                            doCocluster(rateMatrix, settingFile, rowMappingFile, colMappingFile,
                                DIVERGENCE[diverIndx], consts, k, l);
                        }
                    }
                }
            }
        }

    }

    public static void doCocluster(SparseMatrix rateMatrix, String settingFile,
                                   String rowMappingFile, String colMappingFile, int diverType,
                                   int constrains, int K, int L) {
        Map<Integer, Integer> rowAssign = new HashMap<Integer, Integer>();
        int[] rowBound = new int[K];
        Map<Integer, Integer> colAssign = new HashMap<Integer, Integer>();
        int[][] coclusterStructure = new int[K][L];

        //coclustering
        LoggerUtil.info(logger, "\ta. start to cocluster.");
        MatrixCoclusterUtil.coclusteringWithConjugateAssumption(rateMatrix, K, L, maxIteration,
            constrains, diverType, rowAssign, rowBound, colAssign, coclusterStructure);
        //        MatrixCoclusterUtil.coclustering(rateMatrix, K, L, maxIteration, CONSTRAINTS, DIVERGENCE,
        //            rowAssign, rowBound, colAssign, coclusterStructure);

        //write cocluster structure
        LoggerUtil.info(logger, "\tb. write cocluster structure setting file.");
        StringBuilder setting = new StringBuilder();
        for (int k = 0; k < K; k++) {
            setting.append(rowBound[k]).append(": ").append(coclusterStructure[k][0]);
            for (int i = 1; i < coclusterStructure[k].length; i++) {
                setting.append(", ").append(coclusterStructure[k][i]);
            }
            setting.append('\n');
        }
        FileUtil.write(settingFile, setting.toString());
        setting = null;

        //write row mapping
        LoggerUtil.info(logger, "\tc. write row mapping file.");
        StringBuilder rowMapping = new StringBuilder();
        for (Entry<Integer, Integer> entry : rowAssign.entrySet()) {
            rowMapping.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
        }
        FileUtil.write(rowMappingFile, rowMapping.toString());
        rowMapping = null;

        //write column mapping
        LoggerUtil.info(logger, "\td. write column mapping.");
        StringBuilder colMapping = new StringBuilder();
        for (Entry<Integer, Integer> entry : colAssign.entrySet()) {
            colMapping.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
        }
        FileUtil.write(colMappingFile, colMapping.toString());
        colMapping = null;
    }

}
