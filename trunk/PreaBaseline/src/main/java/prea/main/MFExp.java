/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package prea.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import prea.data.structure.SparseMatrix;
import prea.recommender.matrix.BayesianPMF;
import prea.recommender.matrix.NMF;
import prea.recommender.matrix.PMF;
import prea.recommender.matrix.RegularizedSVD;
import prea.util.EvaluationMetrics;
import prea.util.FileUtil;
import prea.util.LoggerDefineConstant;
import prea.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: RSVD.java, v 0.1 2014-10-15 下午9:13:00 chench Exp $
 */
public class MFExp {

    /** training dataset file output path 1m 10M100K*/
    protected final static String[] rootDirs  = { "E:/MovieLens/ml-10M100K/fetch20/1/" };
    /** the number of rows      6040    69878*/
    public final static int         userCount = 69878;
    /** the number of cloumns   3706    10677*/
    public final static int         itemCount = 10677;
    public final static double      maxValue  = 5.0;
    public final static double      minValue  = 0.5;
    protected final static Logger   logger    = Logger
                                                  .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    public final static String      resultDir = "E:/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        int[] featureCounts = { 20, 20, 20 };
        for (int i = 0; i < rootDirs.length; i++) {
            String train = rootDirs[i] + "trainingset";
            String test = rootDirs[i] + "testingset";

            //            DescriptiveStatistics stat = doNMF(train, test, featureCounts);
            //            DescriptiveStatistics stat = doRSVD(train, test, featureCounts);
            DescriptiveStatistics stat = doBPMF(train, test, featureCounts);
            LoggerUtil.info(
                logger,
                "Mean: " + stat.getMean() + "\tSD:"
                        + String.format("%.6f", stat.getStandardDeviation()) + "\n");
        }

    }

    public static DescriptiveStatistics doNMF(String train, String test, int[] featureCounts) {
        System.out.println("1.a load netflix trainingset." + new Date());
        SparseMatrix rateMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        read(train, rateMatrix);

        System.out.println("1.b load netflix testset." + new Date());
        SparseMatrix testMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        read(test, testMatrix);

        double lrate = 0.005;
        double regularized = 0.02;
        int maxIter = 200;

        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (int featureCount : featureCounts) {
            System.out.println("2. excute baseline. " + new Date());
            NMF baseline = new NMF(userCount, itemCount, maxValue, minValue, featureCount, 0,
                regularized, 0, maxIter, lrate, true);
            baseline.buildModel(rateMatrix);
            EvaluationMetrics metric = baseline.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            LoggerUtil.info(logger,
                "Train: " + train + "\tTest: " + test + "\n" + metric.printMultiLine());
            stat.addValue(metric.getRMSE());
        }
        FileUtil.writeAsAppend(
            resultDir + "zNMF",
            "Mean: " + stat.getMean() + "\tSD:"
                    + String.format("%.6f", stat.getStandardDeviation()) + "\n");
        return stat;
    }

    public static DescriptiveStatistics doPMF(String train, String test, int[] featureCounts) {
        System.out.println("1.a load netflix trainingset." + new Date());
        SparseMatrix rateMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        read(train, rateMatrix);

        System.out.println("1.b load netflix testset." + new Date());
        SparseMatrix testMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        read(test, testMatrix);

        int maxIter = 200;

        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (int featureCount : featureCounts) {
            PMF baseline = new PMF(userCount, itemCount, maxValue, minValue, featureCount, 50, 0.4,
                0.8, maxIter, true);
            baseline.buildModel(rateMatrix);
            EvaluationMetrics metric = baseline.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            LoggerUtil.info(logger,
                "Train: " + train + "\tTest: " + test + "\n" + metric.printMultiLine());
            System.out.println("3. excute local low rank." + new Date());
            stat.addValue(metric.getRMSE());
        }
        FileUtil.writeAsAppend(
            resultDir + "zPMF",
            "Mean: " + stat.getMean() + "\tSD:"
                    + String.format("%.6f", stat.getStandardDeviation()) + "\n");
        return stat;
    }

    public static DescriptiveStatistics doBPMF(String train, String test, int[] featureCounts) {
        System.out.println("1.a load netflix trainingset." + new Date());
        SparseMatrix rateMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        read(train, rateMatrix);

        System.out.println("1.b load netflix testset." + new Date());
        SparseMatrix testMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        read(test, testMatrix);

        int maxIter = 10;

        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (int featureCount : featureCounts) {
            BayesianPMF baseline = new BayesianPMF(userCount, itemCount, maxValue, minValue,
                featureCount, 0, 0, 0, maxIter, true);
            baseline.buildModel(rateMatrix);
            EvaluationMetrics metric = baseline.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            LoggerUtil.info(logger,
                "Train: " + train + "\tTest: " + test + "\n" + metric.printMultiLine());
            stat.addValue(metric.getRMSE());
        }
        FileUtil.writeAsAppend(
            resultDir + "zBPMF",
            "Mean: " + stat.getMean() + "\tSD:"
                    + String.format("%.6f", stat.getStandardDeviation()) + "\n");
        return stat;
    }

    public static DescriptiveStatistics doRSVD(String train, String test, int[] featureCounts) {
        System.out.println("1.a load netflix trainingset." + new Date());
        SparseMatrix rateMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        read(train, rateMatrix);

        System.out.println("1.b load netflix testset." + new Date());
        SparseMatrix testMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        read(test, testMatrix);

        double learningRate = 0.001;
        double regularized = 0.08;
        int maxIter = 250;

        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (int featureCount : featureCounts) {
            System.out.println("2. excute baseline. " + new Date());
            RegularizedSVD baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
                featureCount, learningRate, regularized, 0, maxIter, true);
            baseline.buildModel(rateMatrix);
            EvaluationMetrics metric = baseline.evaluate(testMatrix);
            System.out.println("Rank: " + featureCount + '\n' + metric.printMultiLine());
            LoggerUtil.info(logger,
                "Train: " + train + "\tTest: " + test + "\n" + metric.printMultiLine());
            stat.addValue(metric.getRMSE());
        }
        FileUtil.writeAsAppend(
            resultDir + "zRSVD",
            "Mean: " + stat.getMean() + "\tSD:"
                    + String.format("%.6f", stat.getStandardDeviation()) + "\n");
        return stat;
    }

    public static void read(String path, SparseMatrix matrix) {
        File file = new File(path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String context = null;
            while ((context = reader.readLine()) != null) {
                String[] elems = context.split("\\::");
                int row = Integer.valueOf(elems[0]) + 1;
                int col = Integer.valueOf(elems[1]) + 1;
                double val = Double.valueOf(elems[2]);
                matrix.setValue(row, col, val);
            }
            reader.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
        }
    }
}
