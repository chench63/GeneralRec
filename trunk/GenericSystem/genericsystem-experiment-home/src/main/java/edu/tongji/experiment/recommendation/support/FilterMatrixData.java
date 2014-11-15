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
    public static String    settingFile    = "E:/MovieLens/ml-10M100K/3/KMeans/Feature/SETTING";

    /** file with row mapping data*/
    public static String    rowMappingFile = "E:/MovieLens/ml-10M100K/3/KMeans/Feature/RM";

    /** file with column mapping data*/
    public static String    colMappingFile = "E:/MovieLens/ml-10M100K/3/KMeans/Feature/CM";

    /** file with training data*/
    public static String    sourceFile     = "E:/MovieLens/ml-10M100K/3/testingset";

    /** file with testing data*/
    public static String    targetFile     = "E:/MovieLens/ml-10M100K/3/KMeans/Feature/testingset";
    /** the number of rows*/
    public final static int rowCount       = 69878;

    /** the number of columns*/
    public final static int colCount       = 10677;

    /** the content parser w.r.t certain dataset*/
    public static Parser    parser         = new MovielensRatingTemplateParser();

    /** the row index of the target block*/
    public static int       iBlock         = 2;

    /** the column index of the target block*/
    public static int       jBlock         = 2;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        for (int i = 0; i < iBlock; i++) {
            for (int j = 0; j < jBlock; j++) {
                SparseMatrix target = MatrixFileUtil.readBlock(sourceFile, settingFile,
                    rowMappingFile, colMappingFile, rowCount, colCount, parser, i, j);
                MatrixFileUtil.write(targetFile + "_" + i + "_" + j, target);
            }
        }
    }
}
