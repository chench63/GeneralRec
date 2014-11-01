/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.analysis;

import org.apache.log4j.Logger;

import prea.util.MatrixFileUtil;
import prea.util.MatrixInformationUtil;
import prea.util.SimpleEvaluationMetrics;
import edu.tongji.data.SparseMatrix;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: GlobalAndLocalCompare.java, v 0.1 2014-10-31 上午10:25:31 chench Exp $
 */
public final class GlobalAndLocalCompare {

    /** file with setting data*/
    public static String        settingFile    = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/SETTING";

    /** file with row mapping data*/
    public static String        rowMappingFile = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/RM";

    /** file with column mapping data*/
    public static String        colMappingFile = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/CM";

    /** file with training data*/
    public static String        globalFile     = "E:/MovieLens/ml-10M100K/3/prediction";

    /** file with testing data*/
    public static String        localFile      = "E:/MovieLens/ml-10M100K/3/testingset";

    /** the number of rows*/
    public final static int     rowCount       = 69878;

    /** the number of columns*/
    public final static int     colCount       = 10677;

    /** the content parser w.r.t certain dataset*/
    public static Parser        parser         = new MovielensRatingTemplateParser();

    /** the row index of the target block*/
    public static int           iBlock         = 0;

    /** the column index of the target block*/
    public static int           jBlock         = 0;

    public static double        maxValue       = 5;

    public static double        minValue       = 0.5;

    /** logger */
    private final static Logger logger         = Logger
                                                   .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SparseMatrix predictedMatrix = MatrixFileUtil.read(globalFile, settingFile, rowMappingFile,
            colMappingFile, rowCount, colCount, parser).getBlock(iBlock, jBlock);
        SparseMatrix testMatrix = MatrixFileUtil.read(localFile, settingFile, rowMappingFile,
            colMappingFile, rowCount, colCount, parser).getBlock(iBlock, jBlock);

        SimpleEvaluationMetrics metric = new SimpleEvaluationMetrics(testMatrix, predictedMatrix,
            maxValue, minValue);

        LoggerUtil.info(logger, metric.printOneLine());
        LoggerUtil.info(logger, MatrixInformationUtil.RMSEAnalysis(testMatrix, predictedMatrix));

    }

}
