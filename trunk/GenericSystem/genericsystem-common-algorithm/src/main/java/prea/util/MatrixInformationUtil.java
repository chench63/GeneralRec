/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package prea.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import edu.tongji.data.BlockMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.ml.matrix.MatrixFactorizationRecommender;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.vo.RatingVO;

/**
 * Matrix information utility
 * 
 * @author Hanke Chen
 * @version $Id: MatrixInformationUtil.java, v 0.1 2014-10-28 下午2:55:50 chench Exp $
 */
public final class MatrixInformationUtil {

    /**
     * forbid construction
     */
    private MatrixInformationUtil() {

    }

    /**
     * compute the sparsity message about the integrated matrix
     * 
     * @param rateMatrices
     * @return the sparsity message about the integrated matrix
     */
    public static String sparsity(BlockMatrix rateMatrices) {
        StringBuilder msg = new StringBuilder("\n==========================================");
        int itemTotalCount = rateMatrices.itemCount();
        int[][] structure = rateMatrices.structure();
        for (int i = 0; i < structure.length; i++) {
            for (int j = 0; j < structure[i].length; j++) {
                msg.append("\n******************************************\n")
                    .append(
                        "Matrix[" + i + ", " + j + "]   S: "
                                + String.format("%.5f", rateMatrices.getSparsity(i, j)))
                    .append(" R: ")
                    .append(
                        String.format("%.5f", rateMatrices.getBlock(i, j).itemCount() * 1.0
                                              / itemTotalCount))
                    .append(hierarchy(rateMatrices.getBlock(i, j)));
            }
        }
        msg.append('\n').append("Block Matrix : " + rateMatrices.getSparsity());
        return msg.toString();
    }

    /**
     * compute the rating distribution w.r.t the given matrix
     * 
     * @param rateMatrix    the matrix to compute
     * @return the rating distribution
     */
    public static String hierarchy(SparseMatrix rateMatrix) {
        int[] countTable = new int[10];
        int M = rateMatrix.length()[0];
        for (int u = 0; u < M; u++) {
            SparseVector Ru = rateMatrix.getRowRef(u);
            int[] indexList = Ru.indexList();
            if (indexList == null) {
                continue;
            }

            for (int v : indexList) {
                double val = Ru.getValue(v);
                int pivot = Double.valueOf(val / 0.5 - 1).intValue();
                countTable[pivot]++;
            }
        }

        StringBuilder msg = new StringBuilder();
        int itemCount = rateMatrix.itemCount();
        for (int i = 0; i < 10; i++) {
            msg.append("\n\t").append(String.format("%.1f", (i + 1) * 0.5)).append('\t')
                .append(countTable[i] * 1.0 / itemCount);
        }

        return msg.toString();
    }

    /**
     * compute the rating distribution w.r.t the given matrix
     * 
     * @param rateMatrix    the matrix to compute
     * @return the rating distribution
     */
    public static String hierarchy(SparseRowMatrix rateMatrix) {
        int[] countTable = new int[10];
        int M = rateMatrix.length()[0];
        for (int u = 0; u < M; u++) {
            SparseVector Ru = rateMatrix.getRowRef(u);
            int[] indexList = Ru.indexList();
            if (indexList == null) {
                continue;
            }

            for (int v : indexList) {
                double val = Ru.getValue(v);
                int pivot = Double.valueOf(val / 0.5 - 1).intValue();
                countTable[pivot]++;
            }
        }

        StringBuilder msg = new StringBuilder();
        int itemCount = rateMatrix.itemCount();
        for (int i = 0; i < 10; i++) {
            msg.append("\n\t").append(String.format("%.1f", (i + 1) * 0.5)).append('\t')
                .append(countTable[i] * 1.0 / itemCount);
        }

        return msg.toString();
    }

    /**
     * analyze the error distribution
     * 
     * @param testMatrix        
     * @param predictedMatrix
     * @return
     */
    public static String RMSEAnalysis(SparseRowMatrix testMatrix, SparseRowMatrix predictedMatrix) {
        int[] countTable = new int[10];
        double[] rmseTable = new double[10];
        double[] distanceTable = new double[10];

        int rowCount = testMatrix.length()[0];
        for (int u = 0; u < rowCount; u++) {
            SparseVector Ru = testMatrix.getRowRef(u);
            int[] indexList = Ru.indexList();
            if (indexList == null) {
                continue;
            }

            for (int v : indexList) {
                double RuvReal = Ru.getValue(v);
                double RuvEsitm = predictedMatrix.getValue(u, v);

                //RMSE
                int pivot = Double.valueOf(RuvReal / 0.5 - 1).intValue();
                countTable[pivot]++;
                rmseTable[pivot] += Math.pow(RuvReal - RuvEsitm, 2.0d);

                //Distance
                for (int r = 0; r < 10; r++) {
                    distanceTable[r] += Math.pow(RuvEsitm - 0.5 * (r + 1), 2.0d);
                }
            }
        }

        double rmseTotal = 0.0d;
        double countTotal = 0.0d;
        for (int i = 0; i < 10; i++) {
            rmseTotal += rmseTable[i];
            countTotal += countTable[i];
        }

        // message
        double globalRMSE = Math.sqrt(rmseTotal / countTotal);
        StringBuilder msg = new StringBuilder();
        msg.append("RMSE: ").append(String.format("%.6f", globalRMSE) + "\t\t[*]");
        for (int i = 0; i < 10; i++) {
            if (countTable[i] == 0) {
                continue;
            }
            double RMSE = Math.sqrt(rmseTable[i] / countTable[i]);
            double Dis = Math.sqrt(distanceTable[i] / countTotal);
            msg.append("\n\t").append((i + 1) * 0.5).append('[')
                .append(String.format("%.5f", countTable[i] / countTotal)).append("]\t")
                .append(String.format("%.6f", RMSE)).append('\t')
                .append(String.format("%.6f", Dis));
        }
        return msg.toString();
    }

    /**
     * analyze the error distribution
     * 
     * @param testMatrix        
     * @param predictedMatrix
     * @return
     */
    public static String RMSEAnalysis(SparseMatrix testMatrix, SparseMatrix predictedMatrix) {
        int[] countTable = new int[10];
        double[] rmseTable = new double[10];

        int rowCount = testMatrix.length()[0];
        for (int u = 0; u < rowCount; u++) {
            SparseVector Ru = testMatrix.getRowRef(u);
            int[] indexList = Ru.indexList();
            if (indexList == null) {
                continue;
            }

            for (int v : indexList) {
                double RuvReal = Ru.getValue(v);
                double RuvEsitm = predictedMatrix.getValue(u, v);

                int pivot = Double.valueOf(RuvReal / 0.5 - 1).intValue();
                countTable[pivot]++;
                rmseTable[pivot] += Math.pow(RuvReal - RuvEsitm, 2.0d);
            }
        }

        double rmseTotal = 0.0d;
        double countTotal = 0.0d;
        for (int i = 0; i < 10; i++) {
            rmseTotal += rmseTable[i];
            countTotal += countTable[i];
        }

        // message
        StringBuilder msg = new StringBuilder("\n");
        for (int i = 0; i < 10; i++) {
            if (countTable[i] == 0) {
                continue;
            }
            double RMSE = Math.sqrt(rmseTable[i] / countTable[i]);
            msg.append("\t").append((i + 1) * 0.5).append('\t')
                .append(String.format("%.5f", countTable[i] / countTotal)).append("\t\t")
                .append(String.format("%.6f", RMSE)).append(" [")
                .append(String.format("%.5f", rmseTable[i] / rmseTotal)).append("]\n");
        }
        return msg.toString();
    }

    public static String PredictionReliabilityAnalysis(SparseRowMatrix testMatrix,
                                                       SparseRowMatrix weightedTestMatrix,
                                                       SparseRowMatrix weightedPredictedMatrix) {
        int[] countTable = new int[10];
        double[] rmseTable = new double[10];

        int rowCount = testMatrix.length()[0];
        for (int u = 0; u < rowCount; u++) {
            SparseVector Ru = testMatrix.getRowRef(u);
            int[] indexList = Ru.indexList();
            if (indexList == null) {
                continue;
            }

            for (int v : indexList) {
                double RuvReal = testMatrix.getValue(u, v);
                double wReal = weightedTestMatrix.getValue(u, v);
                double wEsitm = weightedPredictedMatrix.getValue(u, v);

                //RMSE
                int pivot = Double.valueOf(RuvReal / 0.5 - 1).intValue();
                countTable[pivot]++;
                rmseTable[pivot] += Math.pow((wReal - wEsitm), 2.0d);
            }
        }

        double rmseTotal = 0.0d;
        double countTotal = 0.0d;
        for (int i = 0; i < 10; i++) {
            rmseTotal += rmseTable[i];
            countTotal += countTable[i];
        }

        // message
        double globalRMSE = Math.sqrt(rmseTotal / countTotal);
        StringBuilder msg = new StringBuilder("RMSE: " + String.format("%.6f", globalRMSE) + "\n");
        for (int i = 0; i < 10; i++) {
            if (countTable[i] == 0) {
                continue;
            }
            double RMSE = Math.sqrt(rmseTable[i] / countTable[i]);
            msg.append("\t").append((i + 1) * 0.5).append('[')
                .append(String.format("%.5f", countTable[i] / countTotal)).append("]\t")
                .append(String.format("%.6f", RMSE)).append('\n');
        }
        return msg.toString();
    }

    public static double[] ratingDistribution(SparseMatrix matrix, double maxValue, double minValue) {
        int len = (int) (maxValue / minValue);
        int[] countTable = new int[len];

        int M = matrix.length()[0];
        for (int u = 0; u < M; u++) {
            SparseVector Ru = matrix.getRowRef(u);
            int[] indexList = Ru.indexList();
            if (indexList == null) {
                continue;
            }

            for (int v : indexList) {
                double val = Ru.getValue(v);
                int pivot = Double.valueOf(val / minValue - 1).intValue();
                countTable[pivot]++;
            }
        }

        double[] result = new double[len];
        int itemCount = matrix.itemCount();
        for (int i = 0; i < len; i++) {
            result[i] = countTable[i] * 1.0 / itemCount;
        }

        return result;
    }

    public static double offlineRMSE(MatrixFactorizationRecommender recmmd, String testFile,
                                     int rowCount, int colCount, Parser parser) {
        if (parser == null) {
            parser = new MovielensRatingTemplateParser();
        }

        double RMSE = 0.0d;
        int itemCount = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(testFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                RatingVO rating = (RatingVO) parser.parse(line);
                int u = rating.getUsrId();
                int i = rating.getMovieId();
                double AuiReal = rating.getRatingReal();
                double AuiEst = recmmd.getPredictedRating(u, i);
                RMSE += Math.pow(AuiReal - AuiEst, 2.0d);
                itemCount++;
            }

            return Math.sqrt(RMSE / itemCount);
        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + testFile);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return 0.0d;
    }

    public static double[] RMSEAnalysisOfObservedRatingsPerUser(SparseMatrix rateMatrix,
                                                                SparseMatrix testMatrix,
                                                                MatrixFactorizationRecommender recmmd,
                                                                double[] threshholds) {
        double[] SSEs = new double[threshholds.length];
        int[] itemCounts = new int[threshholds.length];

        int usrCount = rateMatrix.length()[0];
        for (int u = 0; u < usrCount; u++) {
            SparseVector Fu = testMatrix.getRowRef(u);
            int[] itemList = Fu.indexList();
            if (itemList == null) {
                continue;
            }

            // compute corresponding RMSE group
            int obsrvdRatingCount = rateMatrix.getRowRef(u).itemCount();
            int pivot = -1;
            for (int k = 0; k < threshholds.length; k++) {
                if (obsrvdRatingCount <= threshholds[k]) {
                    pivot = k;
                    break;
                }
            }

            // compute Sum Square Error
            for (int i : itemList) {
                double AuiReal = testMatrix.getValue(u, i);
                double AuiEstm = recmmd.getPredictedRating(u, i);
                SSEs[pivot] += Math.pow(AuiReal - AuiEstm, 2.0d);
                itemCounts[pivot]++;
            }
        }

        //compute Root Sum Square Error
        for (int k = 0; k < threshholds.length; k++) {
            if (itemCounts[k] != 0) {
                SSEs[k] = Math.sqrt(SSEs[k] / itemCounts[k]);
            }
        }
        return SSEs;
    }

    public static double[] RMSEAnalysisOfObservedRatingsPerItem(SparseMatrix rateMatrix,
                                                                SparseMatrix testMatrix,
                                                                MatrixFactorizationRecommender recmmd,
                                                                double[] threshholds) {
        double[] SSEs = new double[threshholds.length];
        int[] itemCounts = new int[threshholds.length];

        int itemCount = rateMatrix.length()[1];
        for (int i = 0; i < itemCount; i++) {
            SparseVector Gi = testMatrix.getColRef(i);
            int[] usrList = Gi.indexList();
            if (usrList == null) {
                continue;
            }

            // compute corresponding RMSE group
            int obsrvdRatingCount = rateMatrix.getColRef(i).itemCount();
            int pivot = -1;
            for (int k = 0; k < threshholds.length; k++) {
                if (obsrvdRatingCount <= threshholds[k]) {
                    pivot = k;
                    break;
                }
            }

            // compute Sum Square Error
            for (int u : usrList) {
                double AuiReal = testMatrix.getValue(u, i);
                double AuiEstm = recmmd.getPredictedRating(u, i);
                SSEs[pivot] += Math.pow(AuiReal - AuiEstm, 2.0d);
                itemCounts[pivot]++;
            }
        }

        //compute Root Sum Square Error
        for (int k = 0; k < threshholds.length; k++) {
            if (itemCounts[k] != 0) {
                SSEs[k] = Math.sqrt(SSEs[k] / itemCounts[k]);
            }
        }
        return SSEs;
    }
}
