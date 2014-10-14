/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.log4j.Logger;

import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

/**
 * Valiate consistency between two row-stable or column-stable transformation dataset 
 * 
 * @author Hanke Chen
 * @version $Id: DatasetValiation.java, v 0.1 2014-10-14 下午4:44:17 chench Exp $
 */
public class DatasetValiation {

    //==========================
    //      Common variable
    //==========================

    /** file to store the original data*/
    //  public final static String  ORIGINAL_FILE  = "E:/MovieLens/ml-1m/r/ratings.dat"; //compact
        public final static String  ORIGINAL_FILE  = "E:/MovieLens/ml-1m/ratings.dat";//rowCluster
//    public final static String  ORIGINAL_FILE  = "E:/MovieLens/ml-1m/ratings_RowC.dat";          //  colCluster

    /** file to persist the new data */
    //    public final static String  ORIGINAL_FILE  = "E:/MovieLens/ml-1m/ratings.dat";//compact
        public final static String  VARIATION_FILE = "E:/MovieLens/ml-1m/ratings_RowC.dat";//rowCluster
//    public final static String  VARIATION_FILE = "E:/MovieLens/ml-1m/ratings_Complete.dat";      // colCluster

    /** The parser to parse the dataset file  **/
    public final static Parser  parser         = new MovielensRatingTemplateParser();

    /** the number of rows*/
    public final static int     rowCount       = 10000;

    /** the number of columns*/
    public final static int     colCount       = 10000;

    /** the dimention of the compact row or column*/
//    public final static int     compactCount   = 6040 - 1;//compact or colCluster
        public final static int     compactCount   = 3706 - 1;// rowCluster

    /** row is unchanged during the tranformation*/
//    public final static boolean rowStable      = true;
        public final static boolean rowStable      = false;

    /** the offset during the transformation, Orignal[i] = Variation[i - offset]*/
    //    public final static int     offset         = 1;//compact
    public final static int     offset         = 0;                                              //KMsLRA clustering

    /** logger */
    private final static Logger logger         = Logger
                                                   .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
    * 
    * @param args
    */
    public static void main(String[] args) {
        // read data
        SparseMatrix original = readData(ORIGINAL_FILE);
        SparseMatrix variation = readData(VARIATION_FILE);

        if (!rowStable) {
            original.selfTranspose();
            variation.selfTranspose();
        }

        if (original.itemCount() != variation.itemCount()) {
            throw new RuntimeException("Some elements lost during the transform");
        }
        LoggerUtil.info(logger, "\tItemCount Check Passed! ");

        int testTimes = 10000;
        int round = 0;
        UniformIntegerDistribution uniform = new UniformIntegerDistribution(0, compactCount);
        while (round < testTimes) {
            int row1InVariation = uniform.sample();
            int row2InVariation = uniform.sample();
            SparseVector Vr1 = variation.getRowRef(row1InVariation);
            SparseVector Vr2 = variation.getRowRef(row2InVariation);
            double[] v = distance(Vr1, Vr2);

            int row1InOriginal = row1InVariation + offset;
            int row2InOriginal = row2InVariation + offset;
            SparseVector Or1 = original.getRowRef(row1InOriginal);
            SparseVector Or2 = original.getRowRef(row2InOriginal);
            double[] o = distance(Or1, Or2);

            for (int i = 0; i < 2; i++) {
                if (v[i] != o[i]) {
                    throw new RuntimeException("The relationship changes during the transform");
                }
            }

            round++;
        }
        LoggerUtil.info(logger, "\tRelationshp Check Passed! \n\t all test passed");

    }

    public static SparseMatrix readData(String file) {
        SparseMatrix result = new SparseMatrix(rowCount, colCount);
        String[] lines = FileUtil.readLines(file);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            result.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
        }
        return result;
    }

    public static double[] distance(SparseVector a1, SparseVector a2) {
        double[] result = new double[2];
        result[0] = a1.minus(a2).norm();
        result[1] = a1.innerProduct(a2) / (a1.norm() * a2.norm());
        return result;
    }

}
