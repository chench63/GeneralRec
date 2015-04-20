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

import org.apache.log4j.Logger;

import prea.data.structure.SparseRowMatrix;
import prea.recommender.llorma.SingletonParallelLLORMA;
import prea.recommender.matrix.RegularizedSVD;
import prea.util.KernelSmoothing;
import prea.util.LoggerDefineConstant;

/**
 * 
 * @author Hanke Chen
 * @version $Id: LLSVD.java, v 0.1 2014-11-21 上午9:31:06 chench Exp $
 */
public class LLSVD {

    /** training dataset file output path   10M100K*/
    protected final static String rootDir   = "E:/MovieLens/ml-10M100K/fetch20/1/";

    /** the number of rows      6040 69878*/
    public static int             userCount = 69878;

    /** the number of cloumns   3706 10677*/
    public static int             itemCount = 10677;

    /** logger */
    protected final static Logger logger    = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        int[] featureCounts = { 20, 20, 20 };
        LLORMA(featureCounts);
    }

    public static void LLORMA(int[] featureCounts) {
        System.out.println("1.a load netflix trainingset." + new Date());
        SparseRowMatrix rateMatrix = new SparseRowMatrix(userCount + 1, itemCount + 1);
        String trainingFile = rootDir + "trainingset";
        read(trainingFile, rateMatrix);

        System.out.println("1.b load netflix testset." + new Date());
        SparseRowMatrix testMatrix = new SparseRowMatrix(userCount + 1, itemCount + 1);
        String testingFile = rootDir + "testingset";
        read(testingFile, testMatrix);

        double maxValue = 5.0;
        double minValue = 0.5;
        double learningRate = 0.01;
        double regularized = 0.001;
        int maxIter = 100;
        int modelCount = 54;
        int ml = 4;

        System.out.println("2. excute baseline. " + new Date());
        RegularizedSVD baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue, 20,
            0.005, 0.2, 0, 100, true);
        baseline.buildModel(rateMatrix);

        for (int featureCount : featureCounts) {
            System.out.println("3. excute local low rank." + new Date());
            SingletonParallelLLORMA sgllorma = new SingletonParallelLLORMA(userCount, itemCount,
                maxValue, minValue, featureCount, learningRate, regularized, maxIter, modelCount,
                KernelSmoothing.EPANECHNIKOV_KERNEL, 0.8, baseline, testMatrix, ml, true);
            sgllorma.buildModel(rateMatrix);
        }
    }

    public static void read(String path, SparseRowMatrix matrix) {
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
