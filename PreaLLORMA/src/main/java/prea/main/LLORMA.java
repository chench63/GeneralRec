/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package prea.main;

import java.util.Date;
import org.apache.log4j.Logger;
import prea.data.structure.SparseRowMatrix;
import prea.recommender.llorma.SingletonParallelLLORMA;
import prea.recommender.matrix.RegularizedSVD;
import prea.util.KernelSmoothing;
import prea.util.LoggerDefineConstant;
import prea.util.MatrixFileUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: LLSVD.java, v 0.1 2014-11-21 上午9:31:06 chench Exp $
 */
public class LLORMA {
    /** training dataset file output path   10M100K*/
    protected final static String rootDir   = "C:/netflix/1/";
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
        doLLORMA(featureCounts);
    }

    public static void doLLORMA(int[] featureCounts) {
        System.out.println("1. load trainingset." + new Date());
        String train = rootDir + "trainingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(train, userCount + 1, itemCount + 1);

        System.out.println("2. load testset." + new Date());
        String test = rootDir + "testingset";
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(test, userCount + 1, itemCount + 1);

        double maxValue = 5.0;
        double minValue = 0.5;
        double learningRate = 0.01;
        double regularized = 0.001;
        int maxIter = 100;
        int modelCount = 54;
        int ml = 4;

        System.out.println("3. excute SVD to cmp sim. " + new Date());
        RegularizedSVD baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue, 20,
            0.005, 0.2, 0, 100, true);
        baseline.buildModel(rateMatrix);

        for (int featureCount : featureCounts) {
            System.out.println("3. excute LLORMA." + new Date());
            SingletonParallelLLORMA sgllorma = new SingletonParallelLLORMA(userCount, itemCount,
                maxValue, minValue, featureCount, learningRate, regularized, maxIter, modelCount,
                KernelSmoothing.EPANECHNIKOV_KERNEL, 0.8, baseline, testMatrix, ml, true);
            sgllorma.buildModel(rateMatrix);
        }
    }

}
