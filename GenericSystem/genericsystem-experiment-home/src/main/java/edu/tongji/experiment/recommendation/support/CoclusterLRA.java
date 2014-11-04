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
    /** file to store the original data, make sure the data is compact.*/
    public final static String  SOURCE_FILE      = "E:/MovieLens/ml-10M100K/3/trainingset";

    /** file to persist the new data */
    public final static String  SETTING_FILE     = "E:/MovieLens/ml-10M100K/3/Cocluster/EW/SETTING";

    /** file to persist the new data */
    public final static String  ROW_MAPPING_FILE = "E:/MovieLens/ml-10M100K/3/Cocluster/EW/RM";

    /** file to persist the setting data */
    public final static String  COL_MAPPING_FILE = "E:/MovieLens/ml-10M100K/3/Cocluster/EW/CM";

    /** The parser to parse the dataset file  **/
    public final static Parser  parser           = new MovielensRatingTemplateParser();

    /** the number of rows*/
    public final static int     rowCount         = 69878;

    /** the number of columns*/
    public final static int     colCount         = 10677;

    /** Bragman Divergence*/
    public final static int     DIVERGENCE       = CoclusterUtil.EUCLIDEAN_DIVERGENCE;

    /** Constraints*/
    public final static int     CONSTRAINTS      = CoclusterUtil.C_5;

    /** the number of classes*/
    public final static int     K                = 2;

    /** the number of classes*/
    public final static int     L                = 2;

    /** the maximum number of iterations*/
    public final static int     maxIteration     = 8;

    /** logger */
    private final static Logger logger           = Logger
                                                     .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SparseMatrix rateMatrix = MatrixFileUtil.read(SOURCE_FILE, rowCount, colCount, parser);
        Map<Integer, Integer> rowAssign = new HashMap<Integer, Integer>();
        int[] rowBound = new int[K];
        Map<Integer, Integer> colAssign = new HashMap<Integer, Integer>();
        int[][] coclusterStructure = new int[K][L];

        //coclustering
        LoggerUtil.info(logger, "1. start to cocluster.");
        MatrixCoclusterUtil.coclusteringWithConjugateAssumption(rateMatrix, K, L, maxIteration,
            CONSTRAINTS, DIVERGENCE, rowAssign, rowBound, colAssign, coclusterStructure);
        //        MatrixCoclusterUtil.coclustering(rateMatrix, K, L, maxIteration, CONSTRAINTS, DIVERGENCE,
        //            rowAssign, rowBound, colAssign, coclusterStructure);

        //write cocluster structure
        LoggerUtil.info(logger, "2. write cocluster structure setting file.");
        StringBuilder setting = new StringBuilder();
        for (int k = 0; k < K; k++) {
            setting.append(rowBound[k]).append(": ").append(coclusterStructure[k][0]);
            for (int i = 1; i < coclusterStructure[k].length; i++) {
                setting.append(", ").append(coclusterStructure[k][i]);
            }
            setting.append('\n');
        }
        FileUtil.write(SETTING_FILE, setting.toString());
        setting = null;

        //write row mapping
        LoggerUtil.info(logger, "3. write row mapping file.");
        StringBuilder rowMapping = new StringBuilder();
        for (Entry<Integer, Integer> entry : rowAssign.entrySet()) {
            rowMapping.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
        }
        FileUtil.write(ROW_MAPPING_FILE, rowMapping.toString());
        rowMapping = null;

        //write column mapping
        LoggerUtil.info(logger, "4. write column mapping.");
        StringBuilder colMapping = new StringBuilder();
        for (Entry<Integer, Integer> entry : colAssign.entrySet()) {
            colMapping.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
        }
        FileUtil.write(COL_MAPPING_FILE, colMapping.toString());
        colMapping = null;

    }

}
