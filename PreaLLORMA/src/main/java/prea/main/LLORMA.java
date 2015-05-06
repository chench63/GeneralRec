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
    protected final static String[] rootDirs  = { "C:/netflix/1/" };
    /** the number of rows      6040 69878 480189*/
    public static int               userCount = 480189;
    /** the number of cloumns   3706 10677 17770*/
    public static int               itemCount = 17770;
    /** logger */
    protected final static Logger   logger    = Logger
                                                  .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        int[] featureCounts = { 20 };
        for (String rootDir : rootDirs) {
            doLLORMA(rootDir, featureCounts);

            if (userCount >= 100 * 1000) {
                System.gc();
            }
        }
    }

    public static void doLLORMA(String rootDir, int[] featureCounts) {
        System.out.println("1. load trainingset." + new Date());
        String train = rootDir + "trainingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(train, userCount + 1, itemCount + 1);

        double maxValue = 5.0;
        double minValue = 0.5;
        double learningRate = 0.01;
        double regularized = 0.001;
        int maxIter = 100;
        int modelCount = 50;
        int ml = 4;

        System.out.println("2. excute SVD to cmp sim. " + new Date());
        RegularizedSVD baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue, 20,
            0.005, 0.2, 0, 100, true);
        baseline.buildModel(rateMatrix);

        System.out.println("3. load testset." + new Date());
        String test = rootDir + "testingset";
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(test, userCount + 1, itemCount + 1);

        for (int featureCount : featureCounts) {
            System.out.println("4. excute LLORMA." + new Date());
            SingletonParallelLLORMA sgllorma = new SingletonParallelLLORMA(userCount, itemCount,
                maxValue, minValue, featureCount, learningRate, regularized, maxIter, modelCount,
                KernelSmoothing.EPANECHNIKOV_KERNEL, 0.8, baseline, testMatrix, ml, true);
            sgllorma.buildModel(rateMatrix);
        }
    }

}
