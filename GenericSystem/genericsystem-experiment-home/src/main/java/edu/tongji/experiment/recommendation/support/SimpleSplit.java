/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.log4j.Logger;

import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseColumnMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;

/**
 * Split dataset into Training dataset and Testing dataset
 * 
 * @author Hanke Chen
 * @version $Id: MovieLensDataSetSpliter.java, v 0.1 2014-10-8 上午10:37:12 chench
 *          Exp $
 */
public class SimpleSplit {

    /** source dataset file input path Netflix MovieLens*/
    protected final static String rootDir               = "E:/Netflix/";

    /** source dataset file input path */
    protected final static String compactDataFile       = "E:/Netflix/r/ratings.dat";

    /** training dataset file output path */
    protected final static String TRAINING_DATASET_FILE = "E:/MovieLens/ml-10M100K/5/trainingset";

    /** testing dataset file output path */
    protected final static String TESTING_DATASET_FILE  = "E:/MovieLens/ml-10M100K/5/testingset";

    /** the number of rows */
    public final static int       rowCount              = 480189;

    /** the number of columns */
    public final static int       colCount              = 17770;

    /** training data/ total data */
    protected final static float  RATIO                 = 0.9f;

    /** logger */
    private final static Logger   logger                = Logger
                                                            .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        filterN(20);
    }

    public static void filterN(int N) {
        //filtering users who has rated less than N items.
        SparseRowMatrix matrix = MatrixFileUtil.reads(compactDataFile, rowCount, colCount, null);
        for (int u = 0; u < rowCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);
            int[] itemList = Fu.indexList();

            if (itemList.length <= N) {
                for (int i : itemList) {
                    matrix.setValue(u, i, 0.0d);
                }
            }
        }

        String file = rootDir + "rating" + N;
        MatrixFileUtil.write(file, matrix);
    }

    public static void compactRowIndex() {
        SparseRowMatrix matrix = MatrixFileUtil.reads(compactDataFile, rowCount, colCount, null);

        //construct new row sequence
        int nextRow = 0;
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        for (int u = 0; u < rowCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);

            if (Fu.itemCount() != 0) {
                rowAssig.put(u, nextRow);
                nextRow++;
            }
        }

        //write to disk
        String file = rootDir + "rating" + "_R";
        for (int u = 0; u < rowCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);
            int[] itemIndex = Fu.indexList();
            if (itemIndex == null) {
                continue;
            }

            StringBuilder content = new StringBuilder();
            for (int i : itemIndex) {
                double val = matrix.getValue(u, i);

                String newElem = rowAssig.get(u) + "::" + i + "::" + String.format("%.1f", val);
                content.append(newElem).append('\n');

                // element in dataset cannot be zero
                if (val == 0.0) {
                    throw new RuntimeException("Dataset must be wrong!");
                } else {
                    matrix.setValue(u, i, 0.0d);
                }
            }
            FileUtil.writeAsAppend(file, content.toString());
        }
    }

    public static void compactColumnIndex() {
        SparseColumnMatrix matrix = MatrixFileUtil.readCols(compactDataFile, rowCount, colCount,
            null);

        //construct new row sequence
        int nextCol = 0;
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        for (int i = 0; i < colCount; i++) {
            SparseVector col = matrix.getColRef(i);

            if (col.indexList() != null) {
                colAssig.put(i, nextCol);
                nextCol++;
            }
        }

        //write to disk
        String file = rootDir + "rating" + "_C";
        for (int i = 0; i < colCount; i++) {
            SparseVector Gi = matrix.getColRef(i);
            int[] userIndex = Gi.indexList();
            if (userIndex == null) {
                continue;
            }

            StringBuilder content = new StringBuilder();
            for (int u : userIndex) {
                double val = matrix.getValue(u, i);

                String newElem = u + "::" + colAssig.get(i) + "::" + String.format("%.1f", val);
                content.append(newElem).append('\n');

                // element in dataset cannot be zero
                if (val == 0.0) {
                    throw new RuntimeException("Dataset must be wrong!");
                } else {
                    matrix.setValue(u, i, 0.0d);
                }
            }
            FileUtil.writeAsAppend(file, content.toString());
        }
    }

    public static void uniformlyFetchN(int N) {
        SparseRowMatrix matrix = MatrixFileUtil.reads(compactDataFile, rowCount, colCount, null);
        SparseRowMatrix rateMatrix = new SparseRowMatrix(rowCount, colCount);

        //uniformly split training and testing set
        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        for (int u = 0; u < rowCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);
            int[] itemList = Fu.indexList();

            if (itemList.length <= N) {
                for (int i : itemList) {
                    matrix.setValue(u, i, 0.0d);
                }
            } else {
                //select N training ratings
                int leftRemoved = N;
                while (leftRemoved > 0) {
                    int pivot = (int) (itemList.length * uniform.sample());

                    //move the pivot rating to training matrix
                    if (itemList[pivot] != -1) {
                        rateMatrix.setValue(u, pivot, matrix.getValue(u, pivot));
                        matrix.setValue(u, pivot, 0.0d);
                        itemList[pivot] = -1;
                        leftRemoved--;
                    }
                }
            }
        }
    }

    public static void ensureOneLimitedForItems() {
        SparseColumnMatrix rateMatrix = MatrixFileUtil.readCols(TRAINING_DATASET_FILE, rowCount,
            colCount, null);
        SparseColumnMatrix testMatrix = MatrixFileUtil.readCols(TESTING_DATASET_FILE, rowCount,
            colCount, null);

        //ensure every item is rated
        for (int v = 0; v < colCount; v++) {
            SparseVector Rv = rateMatrix.getColRef(v);
            if (Rv.indexList() != null) {
                continue;
            }

            SparseVector Tv = testMatrix.getColRef(v);
            int[] indexList = Tv.indexList();
            int index = 0;

            rateMatrix.setValue(indexList[index], v, Tv.getValue(indexList[index]));
            testMatrix.setValue(indexList[index], v, 0.0);
        }

    }

    //    public static void compact() {
    //        //read source file
    //        LoggerUtil.info(logger, "1. read source file.");
    //        SparseRowMatrix matrix = MatrixFileUtil.reads(compactDataFile, rowCount, colCount, null);
    //
    //        //compact rows and columns
    //        int nextRow = 0;
    //        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
    //        for (int i = 0; i < maxRow; i++) {
    //            SparseVector row = matrix.getRowRef(i);
    //
    //            if (row.indexList() != null) {
    //                rowAssig.put(i, nextRow);
    //                nextRow++;
    //            }
    //        }
    //
    //        int nextCol = 0;
    //        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
    //        for (int i = 0; i < maxCol; i++) {
    //            SparseVector col = matrix.getColRef(i);
    //
    //            if (col.indexList() != null) {
    //                colAssig.put(i, nextCol);
    //                nextCol++;
    //            }
    //        }
    //
    //        //iterate the matrix
    //        LoggerUtil.info(logger, "2. iterate the matrix.   RowCount: " + nextRow + " ColCount: "
    //                                + nextCol);
    //        for (int i = 0; i < maxRow; i++) {
    //            SparseVector Mi = matrix.getRowRef(i);
    //            int[] colIndex = Mi.indexList();
    //            if (colIndex == null) {
    //                continue;
    //            }
    //
    //            //write to disk
    //            StringBuilder content = new StringBuilder();
    //            for (int j : colIndex) {
    //                int compactRow = rowAssig.get(i);
    //                //                int compactCol = colAssig.get(j);
    //                int compactCol = colAssig.get(j);
    //                double val = matrix.getValue(i, j);
    //
    //                String newElem = compactRow + "::" + compactCol + "::" + String.format("%.1f", val);
    //                content.append(newElem).append('\n');
    //
    //                // element in dataset cannot be zero
    //                if (val == 0.0) {
    //                    throw new RuntimeException("Dataset must be wrong!");
    //                } else {
    //                    matrix.setValue(i, j, 0.0d);
    //                }
    //            }
    //            FileUtil.writeAsAppend(FILENAME, content.toString());
    //        }
    //    }

    //    public static void split() {
    //        // 1. read data
    //        LoggerUtil.info(logger, "1. Starting to load source file.");
    //        Queue<String> contents = new LinkedList<>(Arrays.asList(FileUtil.readLines(FILENAME)));
    //
    //        // 2. split testset and trainset
    //        LoggerUtil.info(logger, "2. Starting to spliter source file. N：" + contents.size());
    //        SparseMatrix rateMatrix = new SparseMatrix(rowCount, colCount);
    //        SparseMatrix testMatrix = new SparseMatrix(rowCount, colCount);
    //
    //        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
    //        while (!contents.isEmpty()) {
    //            String content = contents.poll();
    //            RatingVO rating = (RatingVO) parser.parse(content);
    //
    //            if (uniform.sample() >= RATIO) {
    //                testMatrix.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
    //            } else {
    //                rateMatrix.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
    //            }
    //        }
    //
    //        // 3. ensure every user has one rating, and every item is rated
    //        LoggerUtil.info(logger, "3. ensure every user has one rating, and every item is rated.");
    //        for (int u = 0; u < rowCount; u++) {
    //            SparseVector Ru = rateMatrix.getRowRef(u);
    //            if (Ru.indexList() != null) {
    //                continue;
    //            }
    //
    //            SparseVector Tu = testMatrix.getRowRef(u);
    //            int[] indexList = Tu.indexList();
    //            int index = 0;
    //            rateMatrix.setValue(u, indexList[index], Tu.getValue(indexList[index]));
    //            testMatrix.setValue(u, indexList[index], 0.0);
    //        }
    //
    //        for (int v = 0; v < colCount; v++) {
    //            SparseVector Rv = rateMatrix.getColRef(v);
    //            if (Rv.indexList() != null) {
    //                continue;
    //            }
    //
    //            SparseVector Tv = testMatrix.getColRef(v);
    //            int[] indexList = Tv.indexList();
    //            int index = 0;
    //
    //            rateMatrix.setValue(indexList[index], v, Tv.getValue(indexList[index]));
    //            testMatrix.setValue(indexList[index], v, 0.0);
    //        }
    //
    //        // 4. write to disk
    //        LoggerUtil.info(logger, "4. write to disk. Train: " + rateMatrix.itemCount() + "\tTest: "
    //                                + testMatrix.itemCount());
    //        MatrixFileUtil.write(TRAINING_DATASET_FILE, rateMatrix);
    //        MatrixFileUtil.write(TESTING_DATASET_FILE, testMatrix);
    //    }

}
