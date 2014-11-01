/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;

/**
 * 
 * @author Hanke Chen
 * @version $Id: FilterMatrixData.java, v 0.1 2014-10-30 下午10:02:27 chench Exp $
 */
public class FilterMatrixData {

    /** file with setting data*/
    public static String    settingFile    = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/SETTING";

    /** file with row mapping data*/
    public static String    rowMappingFile = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/RM";

    /** file with column mapping data*/
    public static String    colMappingFile = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/CM";

    /** file with training data*/
    public static String    sourceFile     = "E:/MovieLens/ml-10M100K/3/testingset";

    /** file with testing data*/
    public static String    targetFile     = "E:/MovieLens/ml-10M100K/3/testingset_0_0";
    /** the number of rows*/
    public final static int rowCount       = 69878;

    /** the number of columns*/
    public final static int colCount       = 10677;

    /** the content parser w.r.t certain dataset*/
    public static Parser    parser         = new MovielensRatingTemplateParser();

    /** the row index of the target block*/
    public static int       iBlock         = 0;

    /** the column index of the target block*/
    public static int       jBlock         = 0;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SparseMatrix target = MatrixFileUtil.readBlock(sourceFile, settingFile, rowMappingFile,
            colMappingFile, rowCount, colCount, parser, iBlock, jBlock);
        MatrixFileUtil.write(targetFile, target);
    }
}
