/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.junit.Test;
import org.springframework.util.Assert;

import edu.tongji.data.Cluster;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.ml.KMeansUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: TestKMeansUtil.java, v 0.1 2014-10-14 下午1:23:15 chench Exp $
 */
public class TestKMeansUtil {

    //    @Test
    public void testDivide() {
        SparseMatrix matrix = new SparseMatrix(3, 4);
        matrix.setValue(0, 0, 1);
        matrix.setValue(0, 1, 1);
        matrix.setValue(0, 2, 1);
        matrix.setValue(0, 3, 1);

        matrix.setValue(1, 0, 2);
        matrix.setValue(1, 1, 2);
        matrix.setValue(1, 2, 2);
        matrix.setValue(1, 3, 1);

        matrix.setValue(2, 0, 3);
        matrix.setValue(2, 1, 3);
        matrix.setValue(2, 2, 3);
        matrix.setValue(2, 3, 1);

        int K = 2;
        int maxIteration = 5;
        KMeansUtil.divide(matrix, K, maxIteration, KMeansUtil.SINE_DISTANCE);
    }

//    @Test
    public void testChosenInitilization() {
        int K = 200;
        int pointCount = 3000;
        Cluster[] clusters = new Cluster[K];
        int[] assigmnt = new int[pointCount];
        KMeansUtil.chosenInitilization(clusters, assigmnt, pointCount, K);

        for (int sample = 0; sample < pointCount; sample++) {
            int k = assigmnt[sample];
            List<Integer> elemnts = clusters[k].getList();
            Assert.isTrue(elemnts.indexOf(sample) != -1);
        }

        List<Integer> seq = new ArrayList<Integer>();
        for (int k = 0; k < K; k++) {
            seq.addAll(clusters[k].getList());
        }
        Collections.sort(seq);
        for (int i = 0; i < pointCount; i++) {
            Assert.isTrue(i == seq.get(i));
        }

    }

    @Test
    public void testDistance() {
        SparseVector a = new SparseVector(3);
        SparseVector b = new SparseVector(3);
        UniformRealDistribution uniform = new UniformRealDistribution(0.0, 200);

        int round = 0;
        while (round < 1000) {
            for (int i = 0; i < 3; i++) {
                a.setValue(i, uniform.sample());
                b.setValue(i, uniform.sample());
            }

            double euclidean = 0.0d;
            for (int i = 0; i < 3; i++) {
                euclidean += Math.pow(a.getValue(i) - b.getValue(i), 2.0d);
            }
            euclidean = Math.sqrt(euclidean);
            Assert.isTrue(Math.round(KMeansUtil.distance(a, b,
                KMeansUtil.SQUARE_ROOT_ERROR_DISTANCE)) == Math.round(euclidean));

            round++;
        }

    }

}
