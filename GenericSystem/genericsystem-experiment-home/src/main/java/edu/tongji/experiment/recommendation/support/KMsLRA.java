/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import org.apache.log4j.Logger;

import edu.tongji.data.Cluster;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.ml.KMeansUtil;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

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
    public final static String  SOURCE_FILE      = "E:/MovieLens/ml-10M100K/ratings.dat";

    /** file to persist the new data */
    public final static String  ROW_CLUSTER_FILE = "E:/MovieLens/ml-10M100K/ratings_RowC.dat";

    /** file to persist the new data */
    public final static String  COMPLETE_FILE    = "E:/MovieLens/ml-10M100K/ratings_Complete.dat";

    /** file to persist the setting data */
    public final static String  SETTING_FILE     = "E:/MovieLens/ml-10M100K/ratings_Setting.dat";

    /** The parser to parse the dataset file  **/
    public final static Parser  parser           = new MovielensRatingTemplateParser();

    /** the number of rows*/
    public final static int     rowCount         = 69878;

    /** the number of columns*/
    public final static int     colCount         = 10677;

    /** the type of distance involved*/
    public final static int     DISTANCE_TYPE    = KMeansUtil.SINE_DISTANCE;

    /** the number of classes*/
    public final static int     K_Row            = 2;

    /** the number of classes*/
    public final static int     K_Col            = 2;

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
        //divide rows
        int[] rowBound = rowCluster();
        //divide columns
        int[] colBound = colCluster();

        //write setting to disk
        StringBuilder content = new StringBuilder();
        content.append(rowBound[0]);
        for (int i = 1; i < K_Row; i++) {
            content.append(',').append(rowBound[i]);
        }
        content.append('\n');
        content.append(colBound[0]);
        for (int j = 1; j < K_Col; j++) {
            content.append(',').append(colBound[j]);
        }
        FileUtil.write(SETTING_FILE, content.toString());

    }

    /**
     * cluster rows
     */
    public static int[] rowCluster() {
        //construct matrix
        LoggerUtil.info(logger, "Read data.");
        SparseMatrix matrix = readData(SOURCE_FILE);

        //clustering
        LoggerUtil.info(logger, "Cluster rows.");
        Cluster[] clusters = KMeansUtil.divide(matrix, K_Row, maxIteration, DISTANCE_TYPE);

        //make bounds, lower bound is inclusive, but upper bound is exclusive
        int[] rowBound = new int[K_Row];
        rowBound[0] = clusters[0].getList().size();
        for (int i = 1; i < K_Row; i++) {
            rowBound[i] = rowBound[i - 1] + clusters[i].getList().size();
        }

        //write back to disk
        LoggerUtil.info(logger, "Write to disk.");
        StringBuilder content = new StringBuilder();
        int newRow = 0;
        for (Cluster cluster : clusters) {
            //iterate rows in cluster
            for (int i : cluster) {
                SparseVector Mi = matrix.getRowRef(i);
                int[] colIndex = Mi.indexList();
                if (colIndex == null) {
                    throw new RuntimeException("Matrix is not compact, none element in one row.");
                }

                //iterator elements in row
                for (int j : colIndex) {
                    double val = Mi.getValue(j);
                    String newElem = newRow + "::" + j + "::" + String.format("%.1f", val);
                    content.append(newElem).append('\n');

                    //release memory
                    matrix.setValue(i, j, 0.0d);
                }
                newRow++;
            }
        }
        FileUtil.write(ROW_CLUSTER_FILE, content.toString());
        return rowBound;
    }

    /**
     * cluster column
     */
    public static int[] colCluster() {
        //construct matrix
        LoggerUtil.info(logger, "Read data.");
        SparseMatrix matrix = readData(ROW_CLUSTER_FILE);
        matrix.selfTranspose();

        //clustering
        LoggerUtil.info(logger, "Cluster columns.");
        Cluster[] clusters = KMeansUtil.divide(matrix, K_Col, maxIteration, DISTANCE_TYPE);

        //make bounds, lower bound is inclusive, but upper bound is exclusive
        int[] colBound = new int[K_Col];
        colBound[0] = clusters[0].getList().size();
        for (int i = 1; i < K_Col; i++) {
            colBound[i] = colBound[i - 1] + clusters[i].getList().size();
        }

        //write back to disk
        LoggerUtil.info(logger, "Write to disk.");
        StringBuilder content = new StringBuilder();
        int newCol = 0;
        for (Cluster cluster : clusters) {
            //iterate rows in cluster
            for (int i : cluster) {
                SparseVector Mi = matrix.getRowRef(i);
                int[] colIndex = Mi.indexList();
                if (colIndex == null) {
                    throw new RuntimeException("Matrix is not compact, none element in one row.");
                }

                //iterator elements in row
                for (int j : colIndex) {
                    double val = Mi.getValue(j);
                    //here exchange i and j, cause the matrix is transposed
                    String newElem = j + "::" + newCol + "::" + String.format("%.1f", val);
                    content.append(newElem).append('\n');

                    //release memory
                    matrix.setValue(i, j, 0.0d);
                }
                newCol++;
            }
        }
        FileUtil.write(COMPLETE_FILE, content.toString());
        return colBound;
    }

    /**
     * Read data
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

}
