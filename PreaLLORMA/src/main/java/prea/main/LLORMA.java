/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package prea.main;

import java.util.Date;

import org.apache.log4j.Logger;

import prea.data.structure.MatlabFasionSparseMatrix;
import prea.data.structure.SparseRowMatrix;
import prea.recommender.llorma.SingletonParallelLLORMA_RANK;
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
        double maxValue = 5.0;
        double minValue = 1.0;
        double learningRate = 0.01;
        double regularized = 0.001;
        int maxIter = 100;
        int ml = 16;
        int modelCount = 50 + ml;

        System.out.println("1. load trainingset." + new Date());
        String train = rootDir + "trainingset";
        MatlabFasionSparseMatrix trainSeq = MatrixFileUtil.reads(train, 60 * 1000 * 1000);
        int[] anchorUser = new int[modelCount];
        int[] anchorItem = new int[modelCount];
        for (int modelSeq = 0; modelSeq < modelCount; modelSeq++) {
            int seq = (int) (Math.random() * trainSeq.getNnz());
            anchorUser[modelSeq] = trainSeq.getRowIndx()[seq];
            anchorItem[modelSeq] = trainSeq.getColIndx()[seq];
        }

        System.out.println("2. excute SVD to cmp sim. " + new Date());
        RegularizedSVD baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue, 20,
            0.005, 0.2, 0, 30, true);
        baseline.buildModel(trainSeq, null);

        System.out.println("3. load testset." + new Date());
        String test = rootDir + "testingset";
        MatlabFasionSparseMatrix testSeq = MatrixFileUtil.reads(test, 20 * 1000 * 1000);

        SparseRowMatrix ttMatrix = MatrixFileUtil.reads(test, userCount, itemCount);

        for (int featureCount : featureCounts) {
            System.out.println("4. excute LLORMA." + new Date());
            SingletonParallelLLORMA_RANK sgllorma = new SingletonParallelLLORMA_RANK(userCount,
                itemCount, maxValue, minValue, featureCount, learningRate, regularized, maxIter,
                modelCount, KernelSmoothing.EPANECHNIKOV_KERNEL, 0.8, baseline, null, ml, true);
            sgllorma.testSeq = testSeq;
            sgllorma.trainSeq = trainSeq;
            sgllorma.anchorItem = anchorItem;
            sgllorma.anchorUser = anchorUser;
            sgllorma.buildModel(ttMatrix);
        }
    }

}
