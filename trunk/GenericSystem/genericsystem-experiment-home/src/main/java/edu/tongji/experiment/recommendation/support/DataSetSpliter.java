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
    protected final static String   rootDir     = "E:/MovieLens/ml-10M100K/";
    /** source dataset file input path */
    protected final static String   unifiedFile = "E:/MovieLens/ml-10M100K/ratings.dat";
    /** the number of rows     943 6040 69878 480189*/
    public final static int         userCount   = 69878;
    /** the number of columns 1682 3706 10677 17770*/
    public final static int         itemCount   = 10677;
    /** the time of repeat */
    public final static int         numRepeat   = 2;
    /** logger */
    private final static Logger     logger      = Logger
                                                    .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /*========================================
     * Cold-start train and test dataset spliting
     *========================================*/
    protected final static int[]    Ns          = { 10, 20, 50 };

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
        //        netflix();
        movieLens();
    }

    public static void simpleSplit() {
        for (double ratio : rations) {
            //e.g, $DIR/fetchN/1/
            String dir = rootDir + "normal" + Double.valueOf(ratio * 10).intValue() + "/";
            for (int repeat = 1; repeat <= numRepeat; repeat++) {
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
            int[] newDim = filterN(unifiedFile, N, filteringFile);
            for (int repeat = 1; repeat <= numRepeat; repeat++) {
                String trainFile = dir + repeat + "/trainingset";
                String testFile = dir + repeat + "/testingset";
                fetchN(filteringFile, N, trainFile, testFile, newDim);
            }
        }
    }

    public static void netflix() {
        for (int N : Ns) {
            //e.g, $DIR/fetchN/1/
            String dir = rootDir + "fetch" + N + "/";
            String filteringFile = dir + "ratings.dat";
            int[] newDim = netflixFilterN(unifiedFile, N, filteringFile);
            for (int repeat = 1; repeat <= numRepeat; repeat++) {
                String trainFile = dir + repeat + "/trainingset";
                String testFile = dir + repeat + "/testingset";
                netflixTestTrainSetGenerator(filteringFile, N, trainFile, testFile, newDim);
            }
        }
    }

    /*========================================
     * Generalized simple split
     *========================================*/
    public static void simpleSplit(String trainFile, String testFile, double ratio) {
        //read data
        LoggerUtil.info(logger, "1. starting to load source file.\n\t" + unifiedFile);
        SparseMatrix rateMatrix = MatrixFileUtil.read(unifiedFile, userCount, itemCount, null);
        SparseMatrix testMatrix = new SparseMatrix(userCount, itemCount);

        //uniformly divide into train and test dataset
        LoggerUtil.info(logger, "2. uniformly divide into train and test dataset.");
        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        for (int u = 0; u < userCount; u++) {
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

    public static void ensureOneLimited(SparseMatrix rateMatrix, SparseMatrix testMatrix) {
        int rowNum = rateMatrix.length()[0];
        for (int u = 0; u < rowNum; u++) {
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

        int colNum = rateMatrix.length()[1];
        for (int v = 0; v < colNum; v++) {
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
     * Generalized fetchN split
     *========================================*/
    public static int[] filterN(String datasetFile, int N, String filteringFile) {
        //filtering users who has rated less than N items.
        LoggerUtil.info(logger, "0a. starting to filter file. Remove users whose item less than "
                                + N);
        SparseMatrix matrix = MatrixFileUtil.read(datasetFile, userCount, itemCount, null);
        for (int u = 0; u < userCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);
            int[] itemList = Fu.indexList();

            if (itemList.length <= N) {
                for (int i : itemList) {
                    matrix.setValue(u, i, 0.0d);
                }
            }
        }

        LoggerUtil.info(logger, "0b. reindex the filtered file.");
        //construct new row sequence
        int nextRow = 0;
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        for (int u = 0; u < userCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);

            if (Fu.itemCount() != 0) {
                rowAssig.put(u, nextRow);
                nextRow++;
            }
        }

        //construct new row sequence
        int nextCol = 0;
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        for (int i = 0; i < itemCount; i++) {
            SparseVector Gi = matrix.getColRef(i);

            if (Gi.indexList() != null) {
                colAssig.put(i, nextCol);
                nextCol++;
            }
        }

        LoggerUtil.info(logger, "0c. write filtered file to disk. \nUsers: " + rowAssig.size()
                                + "\tItems: " + colAssig.size() + "\tTotal: " + matrix.itemCount());
        MatrixFileUtil.write(filteringFile, matrix, rowAssig, colAssig, false);

        //update dimension
        int[] newDim = new int[2];
        newDim[0] = rowAssig.size();
        newDim[1] = colAssig.size();
        return newDim;
    }

    public static void fetchN(String filteringFile, int N, String trainFile, String testFile,
                              int[] newDim) {
        //read data
        LoggerUtil.info(logger, "\t\t1. starting to load filtered file.\n\t" + filteringFile);
        SparseMatrix rateMatrix = new SparseMatrix(newDim[0], newDim[1]);
        SparseMatrix testMatrix = MatrixFileUtil.read(filteringFile, newDim[0], newDim[1], null);

        //uniformly divide into train dataset of N and test dataset of rest
        LoggerUtil.info(logger, "\t\t2. uniformly construct trainset with " + N + " items.");
        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        for (int u = 0; u < newDim[0]; u++) {
            SparseVector Fu = testMatrix.getRowRef(u);
            int[] itemList = Fu.indexList();

            if (itemList == null) {
                continue;
            } else if (itemList.length <= N) {
                throw new RuntimeException("The input matrix is not filtered!");
            } else {
                //select N training ratings
                int leftRemoved = N;
                while (leftRemoved > 0) {
                    int pivot = (int) (itemList.length * uniform.sample());

                    //move the pivot rating to training matrix
                    if (itemList[pivot] != -1) {
                        rateMatrix.setValue(u, itemList[pivot],
                            testMatrix.getValue(u, itemList[pivot]));
                        testMatrix.setValue(u, itemList[pivot], 0.0d);
                        itemList[pivot] = -1;
                        leftRemoved--;
                    }
                }
            }
        }

        //ensure every user has one rating, and every item is rated
        LoggerUtil
            .info(logger, "\t\t3. ensure every user has one rating, and every item is rated.");
        ensureOneLimited(rateMatrix, testMatrix);

        // write to disk
        LoggerUtil.info(logger, "\t\t4. write to disk. Train: " + rateMatrix.itemCount()
                                + "\tTest: " + testMatrix.itemCount());
        MatrixFileUtil.write(trainFile, rateMatrix);
        MatrixFileUtil.write(testFile, testMatrix);

    }

    /*========================================
     * Netflix Operation with memory saving methods
     *========================================*/
    public static int[] netflixFilterN(String netflixFile, int N, String filteringFile) {
        LoggerUtil.info(logger, "0a. starting to filter file. Remove users whose item less than "
                                + N);
        stepFilterN(netflixFile, N, filteringFile);

        LoggerUtil.info(logger, "0b. reindex the filtered file.");
        int[] newDim = new int[2];
        newDim[0] = compactRowIndex(filteringFile);
        newDim[1] = compactColumnIndex(filteringFile);
        return newDim;
    }

    public static void netflixTestTrainSetGenerator(String filteringFile, int N, String trainFile,
                                                    String testFile, int[] newDim) {
        LoggerUtil.info(logger, "\t\t1. uniformly construct trainset with " + N + " items.");
        uniformlyFetchN(filteringFile, N, trainFile, testFile, newDim);
        LoggerUtil
            .info(logger, "\t\t2. ensure every user has one rating, and every item is rated.");
        ensureOneLimitedForItems(trainFile, testFile, newDim);
    }

    public static void stepFilterN(String datasetFile, int N, String filteringFile) {
        //filtering users who has rated less than N items.
        SparseRowMatrix matrix = MatrixFileUtil.reads(datasetFile, userCount, itemCount, null);
        for (int u = 0; u < userCount; u++) {
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

    public static int compactRowIndex(String filteringFile) {
        SparseRowMatrix matrix = MatrixFileUtil.reads(filteringFile, userCount, itemCount, null);

        //construct new row sequence
        int nextRow = 0;
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        for (int u = 0; u < userCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);

            if (Fu.itemCount() != 0) {
                rowAssig.put(u, nextRow);
                nextRow++;
            }
        }

        //write to disk
        FileUtil.delete(filteringFile);
        for (int u = 0; u < userCount; u++) {
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

        return rowAssig.size();
    }

    public static int compactColumnIndex(String filteringFile) {
        SparseColumnMatrix matrix = MatrixFileUtil.readCols(filteringFile, userCount, itemCount,
            null);

        //construct new row sequence
        int nextCol = 0;
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        for (int i = 0; i < itemCount; i++) {
            SparseVector col = matrix.getColRef(i);

            if (col.indexList() != null) {
                colAssig.put(i, nextCol);
                nextCol++;
            }
        }

        //write to disk
        FileUtil.delete(filteringFile);
        for (int i = 0; i < itemCount; i++) {
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

        return colAssig.size();
    }

    public static void uniformlyFetchN(String compactFilteringNFile, int N, String trainFile,
                                       String testFile, int[] newDim) {
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(compactFilteringNFile, newDim[0],
            newDim[1], null);
        SparseRowMatrix rateMatrix = new SparseRowMatrix(newDim[0], newDim[1]);

        //uniformly split training and testing set
        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        for (int u = 0; u < newDim[0]; u++) {
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

    public static void ensureOneLimitedForItems(String trainFile, String testFile, int[] newDim) {
        SparseColumnMatrix rateMatrix = MatrixFileUtil.readCols(trainFile, newDim[0], newDim[1],
            null);
        SparseColumnMatrix testMatrix = MatrixFileUtil.readCols(testFile, newDim[0], newDim[1],
            null);

        //ensure every item is rated
        for (int v = 0; v < newDim[1]; v++) {
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
