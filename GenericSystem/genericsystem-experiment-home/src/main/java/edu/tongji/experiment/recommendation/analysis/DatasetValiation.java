/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.analysis;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.log4j.Logger;

import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

/**
 * Valiate consistency between two row-stable or column-stable transformation
 * dataset
 * 
 * @author Hanke Chen
 * @version $Id: DatasetValiation.java, v 0.1 2014-10-14 下午4:44:17 chench Exp $
 */
public class DatasetValiation {

    // ==========================
    // Common variable
    // ==========================

    /** file to store the original data */
    public final static String  ORIGINAL_FILE  = "E:/MovieLens/ml-1m/r/ratings.dat";

    /** file to persist the new data */
    public final static String  VARIATION_FILE = "E:/MovieLens/ml-1m/ratings.dat";

    /** file to store training set*/
    public final static String  TRAINING_FILE  = "C:/Netflix/1/trainingset";

    /** file to store testing set*/
    public final static String  TESTING_FILE   = "C:/Netflix/1/testingset";

    /** The parser to parse the dataset file **/
    public final static Parser  parser         = new MovielensRatingTemplateParser();

    /** the number of rows */
    public final static int     rowCount       = 480189;

    /** the number of columns */
    public final static int     colCount       = 17770;

    /** the dimention of the compact row or column */
    public final static int     compactCount   = 6040 - 1;                                       // compact or colCluster
                                                                                                  // public final static
                                                                                                  // int compactCount =
                                                                                                  // 3706 - 1;//
                                                                                                  // rowCluster

    /** row is unchanged during the tranformation */
    public final static boolean rowStable      = true;
    // public final static boolean rowStable = false;

    /** the offset during the transformation, Orignal[i] = Variation[i - offset] */
    public final static int     offset         = 1;                                              // compact
    // public final static int offset = 0; //KMsLRA clustering

    /** logger */
    private final static Logger logger         = Logger
                                                   .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        checkCross();
    }

    /**
     * Check whether cross field exists
     */
    public static void checkCross() {
        SparseRowMatrix rateMatrix = MatrixFileUtil
            .reads(TRAINING_FILE, rowCount, colCount, parser);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(TESTING_FILE, rowCount, colCount, parser);

        for (int row = 0; row < rowCount; row++) {
            int[] indexList = testMatrix.getRowRef(row).indexList();
            if (indexList == null) {
                continue;
            }

            for (int col : indexList) {
                double val = rateMatrix.getValue(row, col);
                if (val != 0.0d) {
                    throw new RuntimeException("Cross districts between rateMatrix and testMatrix.");
                }

            }
        }
        LoggerUtil.info(logger, "\tCross Check Passed! ");
    }

    /**
     * Check the relationship between original and compact rating files
     */
    public static void checkCosine() {
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

    /**
     * reading data as SparseRowMatrix
     * 
     * @param file
     * @return
     */
    public static SparseMatrix readData(String file) {
        SparseMatrix result = new SparseMatrix(rowCount, colCount);
        String[] lines = FileUtil.readLines(file);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            result.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
        }
        return result;
    }

    /**
     * Cosine distance
     * 
     * @param a1
     * @param a2
     * @return
     */
    public static double[] distance(SparseVector a1, SparseVector a2) {
        double[] result = new double[2];
        result[0] = a1.minus(a2).norm();
        result[1] = a1.innerProduct(a2) / (a1.norm() * a2.norm());
        return result;
    }

}
