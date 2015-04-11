/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.log4j.Logger;

import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseColumnMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;

/**
 * Split dataset into Training dataset and Testing dataset
 * 
 * @author Hanke Chen
 * @version $Id: MovieLensDataSetSpliter.java, v 0.1 2014-10-8 上午10:37:12 chench
 *          Exp $
 */
public class DataSetSpliter {

    /*========================================
     * Common parameters
     *========================================*/
    /** source dataset file input path Netflix MovieLens*/
    protected final static String   rootDir     = "E:/Netflix/";
    /** source dataset file input path */
    protected final static String   unifiedFile = "E:/Netflix/r/ratings.dat";
    /** the number of rows */
    public final static int         rowCount    = 480189;
    /** the number of columns */
    public final static int         colCount    = 17770;
    /** the time of repeat */
    public final static int         numRepeat   = 5;
    /** logger */
    private final static Logger     logger      = Logger
                                                    .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /*========================================
     * Cold-start train and test dataset spliting
     *========================================*/
    protected final static int[]    Ns          = {};

    /*========================================
     * Simple train and test dataset spliting
     *========================================*/
    /** training data/ total data */
    protected final static double[] rations     = {};

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //simple split
        //        simpleSplit();

        //fetchN
        netflix();
    }

    public static void simpleSplit() {
        for (double ratio : rations) {
            //e.g, $DIR/fetchN/1/
            String dir = rootDir + "normal" + Double.valueOf(ratio * 10).intValue() + "/";
            for (int repeat = 0; repeat < numRepeat; repeat++) {
                String trainFile = dir + repeat + "/trainingset";
                String testFile = dir + repeat + "/testingset";
                simpleSplit(trainFile, testFile, ratio);
            }
        }
    }

    public static void movieLens() {
        for (int N : Ns) {
            //e.g, $DIR/fetchN/1/
            String dir = rootDir + "fetch" + N + "/";
            String filteringFile = dir + "ratings.dat";
            netflixNGenerator(unifiedFile, N, filteringFile);
            for (int repeat = 0; repeat < numRepeat; repeat++) {
                String trainFile = dir + repeat + "/trainingset";
                String testFile = dir + repeat + "/testingset";
                netflixTestTrainSetGenerator(filteringFile, N, trainFile, testFile);
            }
        }
    }

    public static void netflix() {
        for (int N : Ns) {
            //e.g, $DIR/fetchN/1/
            String dir = rootDir + "fetch" + N + "/";
            String filteringFile = dir + "ratings.dat";
            netflixNGenerator(unifiedFile, N, filteringFile);
            for (int repeat = 0; repeat < numRepeat; repeat++) {
                String trainFile = dir + repeat + "\trainingset";
                String testFile = dir + repeat + "\testingset";
                netflixTestTrainSetGenerator(filteringFile, N, trainFile, testFile);
            }
        }
    }

    /*========================================
     * Generalized simple split
     *========================================*/
    public static void simpleSplit(String trainFile, String testFile, double ratio) {
        //read data
        LoggerUtil.info(logger, "1. starting to load source file.\n\t" + unifiedFile);
        SparseMatrix rateMatrix = MatrixFileUtil.read(unifiedFile, rowCount, colCount, null);
        SparseMatrix testMatrix = new SparseMatrix(rowCount, colCount);

        //uniformly divide into train and test dataset
        LoggerUtil.info(logger, "2. uniformly divide into train and test dataset.");
        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        for (int u = 0; u < rowCount; u++) {
            int[] itemList = rateMatrix.getRowRef(u).indexList();
            if (itemList == null) {
                throw new RuntimeException("\nThis Dataset must be compact!\n" + unifiedFile);
            }

            for (int i : itemList) {
                if (uniform.sample() >= ratio) {
                    testMatrix.setValue(u, i, rateMatrix.getValue(u, i));
                    rateMatrix.setValue(u, i, 0.0d);

                }
            }

        }

        //ensure every user has one rating, and every item is rated
        LoggerUtil.info(logger, "3. ensure every user has one rating, and every item is rated.");
        ensureOneLimited(rateMatrix, testMatrix);

        // write to disk
        LoggerUtil.info(logger, "4. write to disk. Train: " + rateMatrix.itemCount() + "\tTest: "
                                + testMatrix.itemCount());
        MatrixFileUtil.write(trainFile, rateMatrix);
        MatrixFileUtil.write(testFile, testMatrix);
    }

    /*========================================
     * Generalized fetchN split
     *========================================*/
    public static void fetchN(String trainFile, String testFile, int N) {
        //read data
        LoggerUtil.info(logger, "1. starting to load source file.\n\t" + unifiedFile);
        SparseMatrix rateMatrix = new SparseMatrix(rowCount, colCount);
        SparseMatrix testMatrix = MatrixFileUtil.read(unifiedFile, rowCount, colCount, null);

        //uniformly divide into train dataset of N and test dataset of rest
        LoggerUtil.info(logger, "2. uniformly construct trainset with N items.");
        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        for (int u = 0; u < rowCount; u++) {
            SparseVector Fu = testMatrix.getRowRef(u);
            int[] itemList = Fu.indexList();

            if (itemList.length <= N) {
                throw new RuntimeException("The input matrix is not compact!");
            } else {
                //select N training ratings
                int leftRemoved = N;
                while (leftRemoved > 0) {
                    int pivot = (int) (itemList.length * uniform.sample());

                    //move the pivot rating to training matrix
                    if (itemList[pivot] != -1) {
                        rateMatrix.setValue(u, pivot, testMatrix.getValue(u, pivot));
                        testMatrix.setValue(u, pivot, 0.0d);
                        itemList[pivot] = -1;
                        leftRemoved--;
                    }
                }
            }
        }

        //ensure every user has one rating, and every item is rated
        LoggerUtil.info(logger, "3. ensure every user has one rating, and every item is rated.");
        ensureOneLimited(rateMatrix, testMatrix);

        // write to disk
        LoggerUtil.info(logger, "4. write to disk. Train: " + rateMatrix.itemCount() + "\tTest: "
                                + testMatrix.itemCount());
        MatrixFileUtil.write(trainFile, rateMatrix);
        MatrixFileUtil.write(testFile, testMatrix);

    }

    public static void ensureOneLimited(SparseMatrix rateMatrix, SparseMatrix testMatrix) {
        for (int u = 0; u < rowCount; u++) {
            SparseVector Ru = rateMatrix.getRowRef(u);
            if (Ru.indexList() != null) {
                continue;
            }

            SparseVector Tu = testMatrix.getRowRef(u);
            int[] indexList = Tu.indexList();
            int index = 0;
            rateMatrix.setValue(u, indexList[index], Tu.getValue(indexList[index]));
            testMatrix.setValue(u, indexList[index], 0.0d);
        }

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

    /*========================================
     * Netflix Operation with memory saving methods
     *========================================*/
    public static void netflixNGenerator(String netflixFile, int N, String filteringFile) {
        filterN(netflixFile, N, filteringFile);
        compactRowIndex(filteringFile);
        compactColumnIndex(filteringFile);
    }

    public static void netflixTestTrainSetGenerator(String filteringFile, int N, String trainFile,
                                                    String testFile) {
        uniformlyFetchN(filteringFile, N, trainFile, testFile);
        ensureOneLimitedForItems(trainFile, testFile);
    }

    public static void filterN(String datasetFile, int N, String filteringFile) {
        //filtering users who has rated less than N items.
        SparseRowMatrix matrix = MatrixFileUtil.reads(datasetFile, rowCount, colCount, null);
        for (int u = 0; u < rowCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);
            int[] itemList = Fu.indexList();

            if (itemList.length <= N) {
                for (int i : itemList) {
                    matrix.setValue(u, i, 0.0d);
                }
            }
        }

        MatrixFileUtil.write(filteringFile, matrix);
    }

    public static void compactRowIndex(String filteringFile) {
        SparseRowMatrix matrix = MatrixFileUtil.reads(filteringFile, rowCount, colCount, null);

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
        FileUtil.delete(filteringFile);
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
            FileUtil.writeAsAppend(filteringFile, content.toString());
        }
    }

    public static void compactColumnIndex(String filteringFile) {
        SparseColumnMatrix matrix = MatrixFileUtil
            .readCols(filteringFile, rowCount, colCount, null);

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
        FileUtil.delete(filteringFile);
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
            FileUtil.writeAsAppend(filteringFile, content.toString());
        }
    }

    public static void uniformlyFetchN(String compactFilteringNFile, int N, String trainFile,
                                       String testFile) {
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(compactFilteringNFile, rowCount,
            colCount, null);
        SparseRowMatrix rateMatrix = new SparseRowMatrix(rowCount, colCount);

        //uniformly split training and testing set
        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        for (int u = 0; u < rowCount; u++) {
            SparseVector Fu = testMatrix.getRowRef(u);
            int[] itemList = Fu.indexList();

            if (itemList.length <= N) {
                throw new RuntimeException("The input matrix is not compact!");
            } else {
                //select N training ratings
                int leftRemoved = N;
                while (leftRemoved > 0) {
                    int pivot = (int) (itemList.length * uniform.sample());

                    //move the pivot rating to training matrix
                    if (itemList[pivot] != -1) {
                        rateMatrix.setValue(u, pivot, testMatrix.getValue(u, pivot));
                        testMatrix.setValue(u, pivot, 0.0d);
                        itemList[pivot] = -1;
                        leftRemoved--;
                    }
                }
            }
        }

        //write to disk
        MatrixFileUtil.write(testFile, testMatrix);
        MatrixFileUtil.write(trainFile, rateMatrix);
    }

    public static void ensureOneLimitedForItems(String trainFile, String testFile) {
        SparseColumnMatrix rateMatrix = MatrixFileUtil
            .readCols(trainFile, rowCount, colCount, null);
        SparseColumnMatrix testMatrix = MatrixFileUtil.readCols(testFile, rowCount, colCount, null);

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

        //write to disk
        MatrixFileUtil.write(testFile, testMatrix);
        MatrixFileUtil.write(trainFile, rateMatrix);

    }

}
